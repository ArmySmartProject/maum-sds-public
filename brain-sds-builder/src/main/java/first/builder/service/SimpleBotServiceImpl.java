package first.builder.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import first.builder.client.DelCacheClient;
import first.builder.client.VoiceBotClient;
import first.builder.dao.BuilderDAO;
import first.builder.dao.MaumSDSDAO;
import first.builder.dao.SimpleBotDAO;
import first.builder.vo.CallTaskMetaVO;
import first.builder.vo.SimpleBotVO;
import first.builder.vo.SimpleBotVO.Edge;
import first.builder.vo.SimpleBotVO.Edge.EdgeData;
import first.builder.vo.SimpleBotVO.Node;
import first.builder.vo.SimpleBotVO.Node.SystemUtter;
import first.builder.vo.SimpleBotVO.Regex;
import first.builder.vo.SimpleBotVO.Scenario;
import first.common.util.ExcelRead;
import first.common.util.PropInfo;
import first.common.util.ReadOption;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;


@Service("simpleBotService")
public class SimpleBotServiceImpl implements SimpleBotService {

  Logger logger = LoggerFactory.getLogger(this.getClass());

  private static final String TASK = "TASK 명";
  private static final String START_ANSWER = "시작 답변";
  private static final String YES_ANSWER = "YES 답변";
  private static final String NO_ANSWER = "NO 답변";
  private static final String UNKNOWN_ANSWER = "UNKNOWN 답변";
  private static final String REPEAT_ANSWER = "REPEAT 답변";
  private static final String YES_NEXT_TASK = "YES 다음 TASK";
  private static final String NO_NEXT_TASK = "NO 다음 TASK";
  private static final String USER_UTTER = "사용자 발화";
  private static final String USER_DIAL = "다이얼";
  private static final String MAX_TURN = "최대 반복 횟수";
  private static final String TASK_OVER_MAX = "반복 초과 시 TASK";
  private static final String ACCEPT_STT_STC_IDX = "사용자 발화 듣는 구간 답변 문장";
  private static final String REPEAT_ANSWER_STC_IDX = "사용자에게 반복할 답변 문장";


  @Resource(name = "builderDAO")
  private BuilderDAO builderDAO;

  @Resource(name = "simpleBotDAO")
  private SimpleBotDAO simpleBotDAO;

  @Resource(name = "maumSDSDAO")
  private MaumSDSDAO maumSDSDAO;

  @Autowired
  private VoiceBotClient VoiceBotClient;

  @Override
  public int createSimpleBot(String userId, String companyId, String name, int lang) {
    List<Map> simpleBotList;

    if (companyId.isEmpty()) {
      simpleBotList = getSimpleBotListFromCompanyId(userId, "");
    } else {
      simpleBotList = getSimpleBotListFromCompanyId(companyId, "");
    }

    for (Map simpleBot : simpleBotList) {
      if (simpleBot.get("name").toString().equals(name)) {
        return -1;
      }
    }

    return simpleBotDAO.insertSimpleBot(name, lang, "", userId, companyId);
  }

  @Transactional
  public int createMaumSDSModel(String accessId, String scenarioname) { // host가 생성되지 않았을 경우 (insert)
    int host = 0;
    String defaultHost = "1";
    String hostName = "ynbot_" + accessId + "_" + scenarioname; // ex) ynbot_comp001_test
    try {
      // TODO : BERT intent 에 Scenario에 있는 것들 넣기
      host = maumSDSDAO.insertAccount("YN봇_maumai", accessId, hostName);
      maumSDSDAO.setBackendInfo(host, defaultHost);
//      simpleBotDAO.fillYNIntent(host, lang, defaultHost);
    } catch (Exception e) {
      logger.error("Create MaumSDS Model ERR:", e);
      throw e;
    }
    System.out.println("HOST : " + Integer.toString(host));
    return host;
  }

  @Override
  public SimpleBotVO getSimplebotById(int simplebotId) throws Exception {
    SimpleBotVO simpleBotVO = simpleBotDAO.getSimpleBotVO(simplebotId);

    if (!ObjectUtils.isEmpty(simpleBotVO)) {
      return simpleBotVO;
    } else { // 삭제된 심플봇의 scenarioJson 조회할 경우, NOTFOUND return
      return null;
    }
  }

  @Override
  public void applyCallMeta(int simplebotId, Scenario scenario) throws Exception {
    // 기존 call task info 삭제
    simpleBotDAO.deleteCallTaskMeta(simplebotId);

    List<CallTaskMetaVO> param = new ArrayList<>();

    for(int i = 0; i < scenario.getNodes().size(); i++){
      Node node = scenario.getNodes().get(i);
      if(!node.getLabel().trim().toString().equals("*")){
          if (node.getAttr().size() <= 0) {
            continue;
          }

          SystemUtter nodeAttr = node.getAttr().get(0);

          CallTaskMetaVO callTaskMeta = new CallTaskMetaVO();
          callTaskMeta.setSimplebotId(simplebotId);
          callTaskMeta.setTask(node.getType().equals("start")? "처음으로" : node.getLabel());
          callTaskMeta.setDtmf(nodeAttr.getInputType());
          callTaskMeta.setMaxTurn(nodeAttr.getMaxTurn());

          if (nodeAttr.getTaskOverMax() != null) {
            callTaskMeta.setTaskOverMax(nodeAttr.getTaskOverMax());
          }
          if (nodeAttr.getAcceptSttStcIdx() != null) {
            callTaskMeta.setAcceptSttStcIdx(nodeAttr.getAcceptSttStcIdx());
          }

          // todo: repeatAnswerStcIdx (-> maumSDS)
          // todo: conditionType (-> maumSDS)

          param.add(callTaskMeta);
      }
    }

    // simplebot 별 call task 정보 저장
    simpleBotDAO.saveCallTaskMeta(param);
  }

  @Override
  @Transactional
  public String applyScenario(int simplebotId, String userId, String companyId, String scenarioJson) {
    Map<String, String> response = new HashMap<>();
    String responseMsg = "Success";

    Gson gson = new Gson();
    SimpleBotVO simpleBot = null;
    int givenHost = 0;

    try {
      simpleBot = simpleBotDAO.getSimpleBotVO(simplebotId);

      int host = simpleBot.getHost();
      int lang = simpleBot.getLang();
      String scenarioName = simpleBot.getName();
      String accessId;

      if (companyId.isEmpty()) {
        accessId = userId;
      } else {
        accessId = companyId;
      }

      // 모델 매핑 정보 없을 경우 생성
      if (host == 0) {
        host = createMaumSDSModel(accessId, scenarioName);

        simpleBot.setHost(host);
        simpleBotDAO.updateHostForSimpleBot(simpleBot); // TABLE : SimpleBot : Update : host
      }
      // 기존 intentRel 삭제
      maumSDSDAO.deleteOldIntentRel(host, lang);
      // 기존 Intent 삭제 & 기존 ANSWER 삭제
      maumSDSDAO.deleteOldIntentAnswer(host, lang);
      // BackendInfo 없을 경우 추가
      int bertITFid = maumSDSDAO.checkBackendInfo(host);
      // BertIntent 확인 후 존재하지 않을 경우 추가
      Map<String, Integer> bertIntentMap = maumSDSDAO.insertSimpleBotBertIntent(lang, bertITFid);

      //  scenarioJson update
      simpleBotDAO.saveScenarioJson(simpleBot.getId(), scenarioJson); // TABLE : SimpleBot : Update : scenarioJson, updatedAt, appliedYN

      // Scenario Json 읽어오기
      if (scenarioJson != null && !scenarioJson.isEmpty()) {
        // todo: entity type일 때 처리 추가
        Scenario scenario = gson.fromJson(scenarioJson, Scenario.class);
        simpleBot.setScenario(scenario);

        // Call Meta Update
        try {
          applyCallMeta(simplebotId, scenario);
        } catch (Exception e) {
          logger.error("Apply Call Meta ERR: ", e);
          responseMsg = "Apply Call Meta ERR";
        }

        // Node ID 보관 MAP
        Map<String, Integer> idMap = new HashMap<>();
        Map<String, SimpleBotVO.nodeUtter> nodeUtterMap = new HashMap<>();
        SimpleBotVO.specialAnswerKey specialAnsKey = maumSDSDAO.insertUnknownAnswer(host, lang);
        int endTaskKey = 0;

        // answer, answerKey map
        Map<String, Integer> ansKeyMap = new HashMap<>();

        for (int i = 0; i < scenario.getNodes().size(); i++) {
          try {
            SimpleBotVO.Node nowNode = scenario.getNodes().get(i);

            // 종료 태스크는 answer 없이 task 만 insert
            if (nowNode.getType().equals("end")) {
              // 종료 테스크 처리 여부 (이미 insert 되었을 때)
              if (endTaskKey != 0) {
                idMap.put(nowNode.getId(), endTaskKey);
                continue;
              }
              // insert TASK
              endTaskKey = maumSDSDAO.insertTask(lang, nowNode.getLabel(), 0, host, false);
              idMap.put(nowNode.getId(), endTaskKey);
              continue;
            }

            // 기타 태스크는 answer와 task 모두 insert
            SimpleBotVO.nodeUtter insertedNodeUtter = maumSDSDAO.insertAnswerTask(
                host, lang, nowNode, specialAnsKey, bertIntentMap, i == 0, ansKeyMap);
            idMap.put(nowNode.getId(), insertedNodeUtter.getIntentKey());
            nodeUtterMap.put(nowNode.getId(), insertedNodeUtter);

          } catch (Exception e) {
            logger.error("Apply SDS Task ERR: ", e);
            responseMsg = "Apply SDS Task ERR";
          }
        }

        for (int i = 0; i < scenario.getEdges().size(); i++) {
          SimpleBotVO.Edge nowEdge = scenario.getEdges().get(i);
          try {
            int source = idMap.get(nowEdge.getSource());
            int dest = idMap.get(nowEdge.getTarget());
            SimpleBotVO.nodeUtter condUtter = nodeUtterMap.get(nowEdge.getSource());
            String nodeLabel = nowEdge.getData().getLabel();
            if (nodeLabel.equals("YES")) {
              maumSDSDAO.insertEdge(source, bertIntentMap.get("YES"), dest, condUtter.getUtterYAnswerKey(), "", "0,-1");
            } else if (nodeLabel.equals("NO")) {
              maumSDSDAO.insertEdge(source, bertIntentMap.get("NO"), dest, condUtter.getUtterNAnswerKey(), "", "0,-1");
            } else { //ALWAYS
              maumSDSDAO.insertEdge(source, bertIntentMap.get("YES"), dest, condUtter.getUtterYAnswerKey(), "", "0,-1");
              maumSDSDAO.insertEdge(source, bertIntentMap.get("NO"), dest, condUtter.getUtterNAnswerKey(), "", "0,-1");
            }
          } catch (Exception e) {
            logger.error("Edge Adding Error: ", e);
          }
        }

      } else {
        responseMsg = "Scenario Json is empty.";
      }
      response.put("host", String.valueOf(host));
      response.put("lang", String.valueOf(simpleBot.getLang()));

      // 적용 dtm 업데이트
      SimpleBotVO newSimpleBot = updateApplied(simpleBot.getId());
      response.put("appliedAt", new SimpleDateFormat("yyyy.MM.dd HH:mm")
              .format(newSimpleBot.getAppliedAt()));
    } catch (JsonSyntaxException e) {
      logger.error("Wrong Scenario Json: " + scenarioJson, e);
      responseMsg = "Wrong Scenario Json.";
    } catch (Exception e) {
      logger.error("Cannot get SimpleBot.", e);
      responseMsg = String.format("No Scenario for userId [%s].", userId);
    }

    response.put("msg", responseMsg);
    return gson.toJson(response);
  }

	@Override
	@Transactional
	public String applyScenarioV2(int simplebotId, String userId, String companyId, String scenarioJson, String isExcelUpload, String campaignInfoObj) {
		Map<String, String> response = new HashMap<>();
		String responseMsg = "Success";

		Gson gson = new Gson();
		SimpleBotVO simpleBot = null;
		int givenHost = 0;

		try {
			simpleBot = simpleBotDAO.getSimpleBotVO(simplebotId);

			int host = simpleBot.getHost();
			int lang = simpleBot.getLang();
			String scenarioName = simpleBot.getName();
			String accessId;

			if (companyId.isEmpty()) {
				accessId = userId;
			} else {
				accessId = companyId;
			}

			// 모델 매핑 정보 없을 경우 생성
			if (host == 0) {
				host = createMaumSDSModel(accessId, scenarioName);

				simpleBot.setHost(host);
				simpleBotDAO.updateHostForSimpleBot(simpleBot); // TABLE : SimpleBot : Update : host
			}
            //ChatbotCache 초기화
            maumSDSDAO.deleteChatbotCache(host);
            // 기존 intentRel 삭제
			maumSDSDAO.deleteOldIntentRel(host, lang);
			// 기존 Intent(task) 삭제 & 기존 ANSWER 삭제 (태스크 답변 & 의도 답변)
			maumSDSDAO.deleteOldIntentAnswer(host, lang);
			if ( isExcelUpload.equals("Y") ) { //엑셀로 업로드 하는 경우
            // 기존 BertIntent(의도) 삭제 & 기존 regex 삭제
            maumSDSDAO.deleteBertIntentRegexAll(host, lang);

        // 기존 학습문장은 삭제하지 않음
      }

			// BackendInfo 없을 경우 추가
			int bertITFid = maumSDSDAO.checkBackendInfo(host);

			//  scenarioJson update
			simpleBotDAO.saveScenarioJson(simpleBot.getId(), scenarioJson); // TABLE : SimpleBot : Update : scenarioJson, updatedAt, appliedYN

			// Scenario Json 읽어오기
			if (scenarioJson != null && !scenarioJson.isEmpty()) {
				// todo: entity type일 때 처리 추가
				Scenario scenario = gson.fromJson(scenarioJson, Scenario.class);
				simpleBot.setScenario(scenario);

				// Call Meta Update
				try {
					applyCallMeta(simplebotId, scenario);
				} catch (Exception e) {
					logger.error("Apply Call Meta ERR: ", e);
					responseMsg = "Apply Call Meta ERR";
				}

				// Node ID 보관 MAP
				Map<String, Integer> idMap = new HashMap<>();
				// TODO nodeUtterMap => 변경
				Map<String, SimpleBotVO.nodeUtterV2> nodeUtterMap = new HashMap<>();
				// unknownUtter, repeatUtter 를 사용자에게서 받으므로 제거
//				SimpleBotVO.specialAnswerKey specialAnsKey = maumSDSDAO.insertUnknownAnswer(host, lang);
				int endTaskKey = 0;

				// answer, answerKey map
				Map<String, Integer> ansKeyMap = new HashMap<>();

				int cnt = 0;
				for (int i = 0; i < scenario.getNodes().size(); i++) {
					try {
						SimpleBotVO.Node nowNode = scenario.getNodes().get(i);

						// 종료 태스크는 answer 없이 task 만 insert
						if (nowNode.getType().equals("end")) {
							// 종료 테스크 처리 여부 (이미 insert 되었을 때)
							if (endTaskKey != 0) {
								idMap.put(nowNode.getId(), endTaskKey);
								continue;
							}
							// insert TASK
							endTaskKey = maumSDSDAO.insertTask(lang, nowNode.getLabel(), 0, host, false);
							idMap.put(nowNode.getId(), endTaskKey);
							continue;
						}

						// 기타 태스크는 answer와 task 모두 insert
						if (!nowNode.getLabel().trim().equals("*")) {
							cnt++;
						}
						SimpleBotVO.nodeUtterV2 insertedNodeUtter = maumSDSDAO.insertAnswerTaskV2(
								host, lang, nowNode, cnt == 1, ansKeyMap);
						idMap.put(nowNode.getId(), insertedNodeUtter.getIntentKey());
						nodeUtterMap.put(nowNode.getId(), insertedNodeUtter);

					} catch (Exception e) {
						logger.error("Apply SDS Task ERR: ", e);
						responseMsg = "Apply SDS Task ERR";
					}
				}

				// BertIntent 확인 후 존재하지 않을 경우 추가
				Map<String, Integer> bertIntentMap = maumSDSDAO.insertSimpleBotBertIntentV2(lang, bertITFid, scenario.getEdges());

				for (int i = 0; i < scenario.getEdges().size(); i++) {
					SimpleBotVO.Edge nowEdge = scenario.getEdges().get(i);
					try {
						int source = idMap.get(nowEdge.getSource());
						int dest = idMap.get(nowEdge.getTarget());
						SimpleBotVO.nodeUtterV2 condUtter = nodeUtterMap.get(nowEdge.getSource());
						String nodeLabel = nowEdge.getData().getLabel();
						String[] nodeLabels = nodeLabel.split("/");
						Map<String, Integer> intentIdx = new HashMap<>();
						for (String label: nodeLabels) {
							for (int k = 0; k < condUtter.getIntentList().size(); k++) {
								if (label.equals(condUtter.getIntentList().get(k).getIntent()) || (
										label.equals("ALWAYS") && condUtter.getIntentList().get(k).getIntent()
												.equals(""))) {
									intentIdx.put(label, k);
								}
							}
						}

						if (!intentIdx.isEmpty()) {
							for (String key : intentIdx.keySet()){
								int bertIntentNo = 0;
								if (!key.equals("ALWAYS")) {
									bertIntentNo = bertIntentMap.get(key);
								}
								maumSDSDAO.insertEdge(source, bertIntentNo, dest,
                    condUtter.getIntentList().get(intentIdx.get(key)).getAnswerKey(),
                    condUtter.getIntentList().get(intentIdx.get(key)).getInfo(),
                    condUtter.getDestAnsScope());
							}
						}
					} catch (Exception e) {
						logger.error("Edge Adding Error: ", e);
					}
				}

        // 엑셀 업로드시 task 별 bert intent 데이터 추가
        List<Map<String, Object>> srcList = maumSDSDAO.selectSrcIList(bertITFid);
        maumSDSDAO.updateIntentTask(srcList);

        if ( isExcelUpload.equals("Y") ) { //엑셀로 업로드 하는 경우
          // 정규식 시트
          if (scenario.getNodes().get(0).getRegexList() != null && scenario.getNodes().get(0).getRegexList().size() != 0) {
            Map<String, Object> param = new HashMap<>();
            List<Regex> regexList = scenario.getNodes().get(0).getRegexList();
            param.put("host", host);
            param.put("lang", lang);
            param.put("bertItfId", bertITFid);

            for(int i = 0; i<regexList.size(); i = i+100){
              if(i>regexList.size()) break;
              List<Regex> tmpList = new ArrayList<>();
              for(int j = i; j<i+100; j++){
                if(j<regexList.size()){
                  tmpList.add(regexList.get(j));
                }else{
                  break;
                }
              }
              param.put("regexList", tmpList);
              maumSDSDAO.insertRegex(param);
            }
          }
        }

        if (scenario.getNodes().get(0).getAttr().get(0).getExceptIntent() != null && !""
            .equals(scenario.getNodes().get(0).getAttr().get(0).getExceptIntent())) {
          Map<String, Object> param = new HashMap<>();
          String exceptIntent = scenario.getNodes().get(0).getAttr().get(0).getExceptIntent();
          String[] exceptIntentList = exceptIntent.split(",");
          param.put("intent", exceptIntentList);
          param.put("bertItfId", bertITFid);
          maumSDSDAO.updateIntentOfTask(param);
        }

        //CM_CAMPAIGN_INFO에 데이터 넣기
        List<String> campaignList = simpleBotDAO.getCampaignList(simpleBot.getId());

        Map<String, Object> campaignInfoObjMap = null;
        JsonParser jp = new JsonParser();
        JsonObject jsonObj = (JsonObject) jp.parse(campaignInfoObj);
        campaignInfoObjMap = new ObjectMapper().readValue(jsonObj.toString(), Map.class);
        if(campaignList.size() > 0 ){
            for(int i = 0; i < campaignList.size(); i++) {
                int campaignInfoSeq = 1;
                int campaignInfoSort = 1;
                String infoTask = "task";
                Map<String, Object> campaignInfoMap = new HashMap<>();
                List<String> campaignInfoTaskList = simpleBotDAO.getCampaignInfoTask(campaignList.get(i).toString());
                if(campaignInfoTaskList.size() > 0){
                    if(campaignInfoObjMap.get("updateCheck").toString().equals("update")){
                        Map<String, Object> updateCampaignInfoMap = new HashMap<>();
                        updateCampaignInfoMap.put("campaignId", campaignList.get(i));
                        updateCampaignInfoMap.put("successYn", campaignInfoObjMap.get("successYn").toString());
                        updateCampaignInfoMap.put("newTaskName", campaignInfoObjMap.get("newTaskName").toString());
                        updateCampaignInfoMap.put("oldTaskName", campaignInfoObjMap.get("oldTaskName").toString());

                        simpleBotDAO.updateOldCampaignInfoTask(updateCampaignInfoMap);
                    }else{
                        campaignInfoSeq = campaignInfoTaskList.size() + 1;
                        campaignInfoMap.put("campaignId", campaignList.get(i));
                        campaignInfoMap.put("useYn", "N");
                        campaignInfoMap.put("sortOrdr","0");

                        simpleBotDAO.updateCampaignInfo(campaignInfoMap);
                    }
                }
                for (int j = 0; j < scenario.getNodes().size(); j++) {
                    if(!campaignInfoObjMap.get("updateCheck").toString().equals("update")){
                        if(!"*".equals(scenario.getNodes().get(j).getLabel().toString().trim()) && !"end".equals(scenario.getNodes().get(j).getType().toString())){
                            if(!campaignInfoTaskList.contains(scenario.getNodes().get(j).getLabel().toString())){
                                campaignInfoMap.put("seq", campaignInfoSeq);
                                campaignInfoMap.put("campaignId", campaignList.get(i));
                                campaignInfoMap.put("category", scenario.getNodes().get(j).getLabel());
                                campaignInfoMap.put("task", (infoTask + campaignInfoSeq).toString());
                                campaignInfoMap.put("taskType", "V");
                                campaignInfoMap.put("taskAnswer", "입력");
                                campaignInfoMap.put("taskInfo", scenario.getNodes().get(j).getLabel());
                                if(scenario.getNodes().get(j).getSuccessYn().toString().equals("") || scenario.getNodes().get(j).getSuccessYn() == null){
                                    campaignInfoMap.put("successYn", "N");
                                }else{
                                    campaignInfoMap.put("successYn", scenario.getNodes().get(j).getSuccessYn());
                                }
                                campaignInfoMap.put("useYn", "Y");
                                campaignInfoMap.put("sortOrdr", campaignInfoSort);

                                simpleBotDAO.insertCampaignInfo(campaignInfoMap);
                                campaignInfoSort++;
                                campaignInfoSeq++;
                            }else {
                                campaignInfoMap.put("category", scenario.getNodes().get(j).getLabel());
                                if(scenario.getNodes().get(j).getSuccessYn().toString().equals("") || scenario.getNodes().get(j).getSuccessYn() == null){
                                    campaignInfoMap.put("successYn", "N");
                                }else{
                                    campaignInfoMap.put("successYn", scenario.getNodes().get(j).getSuccessYn());
                                }
                                campaignInfoMap.put("useYn", "Y");
                                campaignInfoMap.put("sortOrdr", campaignInfoSort);
                                simpleBotDAO.updateCampaignInfoTask(campaignInfoMap);
                                campaignInfoSort++;
                            }
                        }
                    }
                }
            }
        }


//        try {
//          DelCacheClient delCacheClient = new DelCacheClient();
//          delCacheClient.sendHttp(host);
//        } catch (Exception e) {
//          System.out.println(e);
//        }

			} else {
				responseMsg = "Scenario Json is empty.";
			}
			response.put("host", String.valueOf(host));
			response.put("lang", String.valueOf(simpleBot.getLang()));

			// 적용 dtm 업데이트
			SimpleBotVO newSimpleBot = updateApplied(simpleBot.getId());
			response.put("appliedAt", new SimpleDateFormat("yyyy.MM.dd HH:mm")
					.format(newSimpleBot.getAppliedAt()));
		} catch (JsonSyntaxException e) {
			logger.error("Wrong Scenario Json: " + scenarioJson, e);
			responseMsg = "Wrong Scenario Json.";
		} catch (Exception e) {
			logger.error("Cannot get SimpleBot.", e);
			responseMsg = String.format("No Scenario for userId [%s].", userId);
		}

		response.put("msg", responseMsg);
		return gson.toJson(response);
	}

  @Override
  public String uploadScenario(int simplebotId, File destFile) throws Exception {
    logger.debug("Function call track : uploadScenario");

    String scenarioJson = "";

    try {
      SimpleBotVO simpleBotVO = simpleBotDAO.getSimpleBotVO(simplebotId);
      int simpleBotId = simpleBotVO.getId();
      int lang = simpleBotVO.getLang();

      Gson gson = new Gson();
      Scenario scenario = excelToScenario(lang, destFile);
      // scenrioVO.toString -> SimpleBot db update (where host)
      scenarioJson = gson.toJson(scenario);
      saveScenarioJson(simpleBotId, scenarioJson);
    } catch (Exception e) {
      logger.error("ERROR during upload scenario.", e);
      throw e;
    }

    // display UI
    return scenarioJson;
  }

  @Override
  public Map<String, Object> uploadScenarioV2(int simplebotId, File destFile) throws Exception {
	  logger.debug("Function call track : uploadScenario");

    String scenarioJson = "";
    Map<String, Object> result = new HashMap<>();

	  try {
		  SimpleBotVO simpleBotVO = simpleBotDAO.getSimpleBotVO(simplebotId);
		  int simpleBotId = simpleBotVO.getId();
		  int lang = simpleBotVO.getLang();

		  Gson gson = new Gson();
		  Scenario scenario = excelToScenarioCompany(lang, destFile);

      Map<String, String> resultMap = scenario.getResultMap();
      if(resultMap != null){
          if (resultMap.containsKey("checkTask")) {
              result.put("checkTask", resultMap.get("checkTask").toString());
          }else if(resultMap.containsKey("checkRegexIntent")){
              result.put("checkRegexIntent", resultMap.get("checkRegexIntent").toString());
          }
      } else {
        // scenrioVO.toString -> SimpleBot db update (where host)
        scenarioJson = gson.toJson(scenario);
        scenarioJson = scenarioJson.replace("attrMap", "attr");
        saveScenarioJson(simpleBotId, scenarioJson);

        result.put("scenarioJson", scenarioJson);
      }

	  } catch (Exception e) {
		  logger.error("ERROR during upload scenario.", e);
		  throw e;
	  }

	  // display UI
	  return result;
  }

  @Transactional
  public void saveScenarioJson(int simpleBotId, String scenarioJson) {
    logger.debug("Function call track : saveScenarioJson");
    simpleBotDAO.saveScenarioJson(simpleBotId, scenarioJson);
  }

  @Transactional
  public SimpleBotVO updateApplied(int simpleBotId) {
    SimpleBotVO simpleBot = simpleBotDAO.updateApplied(simpleBotId);
    return simpleBot;
  }

  static int getNextFilledRow(Sheet sheet, int columnNum, int startRowNum) {

	  int lastRow = sheet.getLastRowNum();
	  int nextFilledRow = lastRow;

	  for (int r = startRowNum; r <= lastRow; r++) {

	  nextFilledRow = r + 1;
	  Row row = sheet.getRow(nextFilledRow);
	  Cell cell = (row != null)?cell = row.getCell(columnNum):null;
	      if (cell != null && cell.getCellType() != CellType.BLANK) {
	          break;
	      }
	  }
      return nextFilledRow;
  }

  private Scenario excelToScenarioCompany(int lang, File destFile) throws Exception {

	  InputStream is = new FileInputStream(destFile.getAbsolutePath());
	  XSSFWorkbook workbook = new XSSFWorkbook(is);

    Row row = null;
    Cell cell = null;
    int columnNum = -1;
    SimpleBotVO simVO = new SimpleBotVO();
    String exceptIntent = "";

    List<String> checkTaskList = new ArrayList<>();
    List<String> checkNextTaskList = new ArrayList<>();

    List<String> checkIntentRegexList  = new ArrayList<>();

	  Sheet sheet = workbook.getSheetAt(0);
	  int lastRow = sheet.getLastRowNum();
	  int startRow = 2;
	  Cell intentCell = null;
	  Cell intentAnswerCell = null;
	  Cell nextTaskCell = null;
	  Cell infoCell = null;
	  int columNumA = 0; //column A determines the grouping
	  List<Map<String,Object>> listMap = new ArrayList<>();

	  int idx = 0;
	  for (int r = startRow; r <= lastRow; r = getNextFilledRow(sheet, columNumA, r)) {
		  List<Map<String,Object>> intentListMap = new ArrayList<>();
		  Map<String, Object> scenarioMap = new HashMap<>();
		  //r is the first row in this group
		  // (회차)
		  columnNum = 0;
		  row = sheet.getRow(r);
		  cell = (row != null)?cell = row.getCell(columnNum):null;
		  scenarioMap.put("num", cell);
		  if (String.valueOf(cell).equals("-")) {
        exceptIntent = String.valueOf(row.getCell(4));
        break;
      }
		  // (TASK 그룹명)
		  columnNum = 1;
		  row = sheet.getRow(r);
		  cell = (row != null)?cell = row.getCell(columnNum):null;
		  scenarioMap.put("taskGroup",cell);
		  // (TASK명)
		  columnNum = 2;
		  row = sheet.getRow(r);
		  cell = (row != null)?cell = row.getCell(columnNum):null;
		  scenarioMap.put("task",cell);
      checkTaskList.add(cell.toString());

		  // (시작 답변)
		  columnNum = 3;
		  row = sheet.getRow(r);
		  cell = (row != null)?cell = row.getCell(columnNum):null;
		  scenarioMap.put("startAnswer",cell);

		  // 의도, 의도-답변, 다음TASK, 정보/비고
		  int lastRowInGroup1 = getNextFilledRow(sheet, columNumA, r);
		  for (int i = r; i < lastRowInGroup1; i++) {

			  Map<String, Object> intentInfoMap = new HashMap<>();
			  row = sheet.getRow(i);
			  intentCell = (row != null)?intentCell = row.getCell(4):null;
			  infoCell = (row != null)?infoCell = row.getCell(5):null;
			  intentAnswerCell = (row != null)?intentAnswerCell = row.getCell(6):null;
			  nextTaskCell = (row != null)?nextTaskCell = row.getCell(7):null;

			  if(intentCell != null && intentCell.getCellType() != CellType.BLANK) {
				  intentInfoMap.put("intentCell", intentCell);
                  checkIntentRegexList.add(intentCell.toString());
			  }else {
				  intentInfoMap.put("intentCell", "");
			  }
			  if(infoCell != null && infoCell.getCellType() != CellType.BLANK) {
				  intentInfoMap.put("infoCell", infoCell);
			  }else {
				  intentInfoMap.put("infoCell", "");
			  }
			  if(intentAnswerCell != null && intentAnswerCell.getCellType() != CellType.BLANK) {
				  intentInfoMap.put("intentAnswerCell", intentAnswerCell);
			  }else {
				  intentInfoMap.put("intentAnswerCell", "");
			  }

			  if(nextTaskCell != null && nextTaskCell.getCellType() != CellType.BLANK) {
				  if(nextTaskCell.toString().equals("")) {
					  columnNum = 2;
					  row = sheet.getRow(lastRowInGroup1);
					  cell = (row != null)?cell = row.getCell(columnNum):null;
					  if(cell != null && cell.getCellType() != CellType.BLANK) {
						  intentInfoMap.put("nextTaskCell", cell); checkNextTaskList.add(cell.toString());
					  }else {
						  intentInfoMap.put("nextTaskCell", "");
					  }
				  }else {
					  intentInfoMap.put("nextTaskCell", nextTaskCell); checkNextTaskList.add(nextTaskCell.toString());
				  }
			  }else {
				  columnNum = 2;
				  row = sheet.getRow(lastRowInGroup1);
				  cell = (row != null)?cell = row.getCell(columnNum):null;
				  if(cell != null && cell.getCellType() != CellType.BLANK) {
					  intentInfoMap.put("nextTaskCell", cell); checkNextTaskList.add(cell.toString());
				  }else {
					  intentInfoMap.put("nextTaskCell", "");
				  }
			  }
			  intentListMap.add(intentInfoMap);
			  scenarioMap.put("intentInfo", intentListMap);
		  }

		  //column H (사용자INPUT : 사용자 발화)
		  columnNum = 8;
		  row = sheet.getRow(r);
		  cell = (row != null)?cell = row.getCell(columnNum):null;
			cell.setCellType(CellType.STRING);
		  scenarioMap.put("inputUser",cell.getStringCellValue());
		  //column I (사용자INPUT : 다이얼)
		  columnNum = 9;
		  row = sheet.getRow(r);
		  cell = (row != null)?cell = row.getCell(columnNum):null;
			cell.setCellType(CellType.STRING);
		  scenarioMap.put("inputDial",cell.getStringCellValue());
		  //column J (반복 설정 : 최대 반복 횟수)
		  columnNum = 10;
		  row = sheet.getRow(r);
		  cell = (row != null)?cell = row.getCell(columnNum):null;
		  cell.setCellType(CellType.STRING);
		  scenarioMap.put("maxTurn", cell.getStringCellValue());
		  //column K (반복 설정 : 반복 초과 시 TASK)
		  columnNum = 11;
		  row = sheet.getRow(r);
		  cell = (row != null)?cell = row.getCell(columnNum):null;
		  scenarioMap.put("taskOverMax", cell);
		  //column L (답변 무시 구간 / 반복 구간 설정 : 사용자 발화 듣는 구간 답변 문장)
		  columnNum = 12;
		  row = sheet.getRow(r);
		  cell = (row != null)?cell = row.getCell(columnNum):null;
		  cell.setCellType(CellType.STRING);
		  scenarioMap.put("acceptSttStcIdx",cell.getStringCellValue());
		  //column M (답변 무시 구간 / 반복 구간 설정 : 사용자에게 반복할 답변 문장)
		  columnNum = 13;
		  row = sheet.getRow(r);
		  cell = (row != null)?cell = row.getCell(columnNum):null;
		  cell.setCellType(CellType.STRING);
		  scenarioMap.put("repeatAnswerStcIdx",cell.getStringCellValue());
          //column O 시나리오 성공 실패 여부
          columnNum = 14;
          row = sheet.getRow(r);
          cell = (row != null)?cell = row.getCell(columnNum):null;
          cell.setCellType(CellType.STRING);
          scenarioMap.put("successYn", cell.getStringCellValue());
		  listMap.add(idx++,scenarioMap);
	  }

      Sheet sheet2 = workbook.getSheetAt(1);
      int lastRow2 = sheet2.getLastRowNum();
      int startRow2 = 1;
      List<Regex> regexList = new ArrayList<>();
      List<String> regexIntentList = new ArrayList<>();

      for (int i = startRow2; i <= lastRow2; i++) {
          row = sheet2.getRow(i);
          if(row.getCell(0) != null && !"".equals(row.getCell(0))
                  && row.getCell(1) != null && !"".equals(row.getCell(1))) {
              Regex regex = simVO.new Regex();
              cell = (row != null) ? cell = row.getCell(0) : null; // 의도
              regex.setIntent(cell.toString());
              regexIntentList.add(cell.toString());
              cell = (row != null) ? cell = row.getCell(1) : null; // 정규식
              regex.setRegex(cell.toString());
              regexList.add(regex);
          }
      }


	  Scenario scenario = simVO.new Scenario();
	  List<Node> nodeList = new ArrayList();
	  List<Edge> edgeList = new ArrayList();

	  List<UUID> nodeIdList = new ArrayList();

    //등록되지 않은 task 여부(c열 task에 작성이 되어야 task가 등록됨, task와 다음task 비교)
    TreeSet<String> treeSetNextTask = new TreeSet<String>(checkNextTaskList);
    checkNextTaskList = new ArrayList<String>(treeSetNextTask);
    String hasNotTaskNames = ""; //등록되지 않은 task 이름(c열에는 없고 h열에는 있는 task 이름)

    for (int i = 0; i < treeSetNextTask.size(); i++) {
      boolean contain = checkTaskList.contains(checkNextTaskList.get(i));
      if (!contain) {
        if (hasNotTaskNames.equals("")) {
          hasNotTaskNames = checkNextTaskList.get(i);

        } else {
          hasNotTaskNames += ", " + checkNextTaskList.get(i);
        }
      }
    }
      //두번째 시트 정규식
      Set<String> set = new HashSet<String>(checkIntentRegexList);
      List<String> newCheckIntentRegexList = new ArrayList<String>(set);
      newCheckIntentRegexList.removeAll(Arrays.asList("", null));

      Set<String> regexIntentSet = new HashSet<String>(regexIntentList);
      List<String> newRegexIntentList = new ArrayList<String>(regexIntentSet);
      newRegexIntentList.removeAll(Arrays.asList("", null));


      String hasNotRegexIntents = "";
      for(int i = 0; i < newRegexIntentList.size(); i++){
          boolean contain = newCheckIntentRegexList.contains(newRegexIntentList.get(i));
          if (!contain) {
              if (hasNotRegexIntents.equals("")) {
                  hasNotRegexIntents = newRegexIntentList.get(i).toString();

              } else {
                  hasNotRegexIntents += ", " + newRegexIntentList.get(i).toString();
              }
          }
      }

    if ( !hasNotTaskNames.equals("") ) { //등록되지 않은 task가 있는 경우
      Map<String, String> map = new HashMap();
      map.put("checkTask", hasNotTaskNames);
      scenario.setResultMap(map);

    } else if(!hasNotRegexIntents.equals("")){
        Map<String, String> map = new HashMap();
        map.put("checkRegexIntent", hasNotRegexIntents);
        scenario.setResultMap(map);
    } else{
      String endTaskName = "종료";
      // 1: KOR , 2: ENG
      if (lang == 2) {
        endTaskName = "END";
      }
      List<Map<String, Object>> nextTaskListMap = new ArrayList<>();
      int nextTaskCnt = 0;
      int taskCnt = 0;

      // 공통 인텐트 관련 분기
      // listMap안에 시나리오 정보를 담는데 그중 첫번째를 체크해서
      // * 인 공통 인텐트가 있으면 true 아니면 false로 체크
      // (주의) 엑셀 업로드 시 공통인텐트는 첫번째 로우에 넣어야됨.
      // 추후 좋은 방법이 생길시 수정 요망.
      boolean commonIntentCheck;
      if(listMap.get(0).get("task").toString().trim().equals("*")){
          commonIntentCheck = true;
      }else{
          commonIntentCheck = false;
      }

      for (int i = 0; i < listMap.size(); i++) {
        if(!endTaskName.equals(listMap.get(i).get("task").toString())) {

          List<Map<String, Object>> systemUtterList = new ArrayList<>();
          List<Map<String, Object>> systemFalse = new ArrayList<>();
          Node node = simVO.new Node();
          Node nodeEnd = simVO.new Node();
          Map<String, Object> systemUtter = new HashMap<>();
          UUID nodeId = UUID.randomUUID();
          node.setId(nodeId.toString());
          node.setLabel(listMap.get(i).get("task").toString());

          if(listMap.get(i).get("task").toString().trim().equals("*")){
              node.setSuccessYn("");
          }else{
              node.setSuccessYn(listMap.get(i).get("successYn").toString());
          }

          // 공통 인텐트 관련 분기
          if(commonIntentCheck){
              if (listMap.get(i).get("task").toString().trim().equals("*")) {
                  node.setType("task");
                  taskCnt++;
              }else if(i == 1){
                  node.setType("start");
                  taskCnt++;
              }else {
                  node.setType("task");
                  taskCnt++;
              }
          }else {
              if(i == 0){
                  node.setType("start");
                  taskCnt++;
              }else{
                  node.setType("task");
                  taskCnt++;
              }
          }

          //Task 그룹명 셋팅
          if(listMap.get(i).get("taskGroup") != null && !"".equals(listMap.get(i).get("taskGroup"))) {
            node.setTaskGroup(listMap.get(i).get("taskGroup").toString());
          }
          //시작 답변 셋팅
          if(listMap.get(i).get("startAnswer") != null && !"".equals(listMap.get(i).get("startAnswer"))) {
            systemUtter.put("utter", listMap.get(i).get("startAnswer").toString());
          }
          // MaxTurn 셋팅
          if(listMap.get(i).get("maxTurn") != null && !"".equals(listMap.get(i).get("maxTurn"))) {
              if(Integer.parseInt(listMap.get(i).get("maxTurn").toString()) > 9){
                  systemUtter.put("maxTurn", "9");
              }else{
                  systemUtter.put("maxTurn", listMap.get(i).get("maxTurn").toString());
              }
          }else {
            systemUtter.put("maxTurn", "0");
          }
          // 사용자 input 데이터에 따라 0,1,2 값으로 분리
          String[] utters = listMap.get(i).get("inputUser").toString().split(","); // 0
          String[] dials = listMap.get(i).get("inputDial").toString().split(","); // 1,2
          Map<Integer, String> userInputMap = new HashMap<>();
          if (!utters[0].equals("")) {
            for (String utter : utters) {
              // 사용자 발화
              userInputMap.put(Integer.parseInt(utter), "0");
            }
          }

          if (!dials[0].equals("")) {
            for (String dial : dials) {
              if (userInputMap.containsKey(Integer.parseInt(dial))) {
                // 사용자발화 + 다이얼
                userInputMap.put(Integer.parseInt(dial), "2");
              } else {
                // 다이얼
                userInputMap.put(Integer.parseInt(dial), "1");
              }
            }
          }

          if (listMap.get(i).get("maxTurn") != null && !"".equals(listMap.get(i).get("maxTurn"))) {
            String inputType = userInputMap.containsKey(0) ? userInputMap.get(0) : "0";

            for (int j = 1; j < Integer.parseInt(systemUtter.get("maxTurn").toString()) + 1; j++) {
              if (userInputMap.containsKey(j)) {
                inputType = inputType.concat(",").concat(userInputMap.get(j));
              } else {
                // 사용자가 입력하지 않은 경우에는
                inputType = inputType.concat(",").concat("0");
              }
            }
            systemUtter.put("inputType", inputType);
          } else { //빈값이면 예외처리한 maxTurn 만큼 inputType 만듬 (MaxTurn 셋팅 else)
            String inputType = "0";
            int systemMaxTurn = Integer.parseInt(systemUtter.get("maxTurn").toString());
            for (int j = 1; j < systemMaxTurn + 1; j++) {
              inputType = inputType.concat(",").concat("0");
            }
            systemUtter.put("inputType", inputType);
          }

          // taskOverMax 세팅
          if (listMap.get(i).get("taskOverMax") != null && !"".equals(listMap.get(i).get("taskOverMax"))) {
            String systemMaxTurn = systemUtter.get("maxTurn").toString();
            if ( "0".equals(systemMaxTurn) ) {
              systemUtter.put("taskOverMax", ""); //maxTurn이 0 (반복없음) 이면 taskOverMax를 세팅하지 않음
            } else {
              systemUtter.put("taskOverMax",listMap.get(i).get("taskOverMax").toString());
            }
          }
          // acceptSttStcIdx 세팅
          String[] acceptSttStcIdxList = listMap.get(i).get("acceptSttStcIdx").toString().replaceAll("\\p{Z}", "").split(",");
          String acceptSttStcIdx = "";
          // 답변 무시구간, 반복 구간 값의 -1의 데이터 저장
          if (acceptSttStcIdxList.length > 0 && !"".equals(acceptSttStcIdxList[0])) {
            for (int j = 0; j < acceptSttStcIdxList.length; j++) {
              if (j != acceptSttStcIdxList.length - 1) {
                acceptSttStcIdx += Integer.parseInt(acceptSttStcIdxList[j]) - 1 + ",";
              } else {
                acceptSttStcIdx += Integer.parseInt(acceptSttStcIdxList[j]) - 1;
              }
            }
            systemUtter.put("acceptSttStcIdx",acceptSttStcIdx);
          }

          // repeatAnswerStcIdx 세팅
          String[] repeatAnswerStcIdxList = listMap.get(i).get("repeatAnswerStcIdx").toString().replaceAll("\\p{Z}", "").split(",");
          String repeatAnswerStcIdx = "";
          if (repeatAnswerStcIdxList.length > 0 && !"".equals(repeatAnswerStcIdxList[0])) {
            for (int j = 0; j < repeatAnswerStcIdxList.length; j++) {
              if (j != repeatAnswerStcIdxList.length - 1) {
                repeatAnswerStcIdx += Integer.parseInt(repeatAnswerStcIdxList[j]) - 1 + ",";
              } else {
                repeatAnswerStcIdx += Integer.parseInt(repeatAnswerStcIdxList[j]) - 1;
              }
            }
            systemUtter.put("repeatAnswerStcIdx",repeatAnswerStcIdx);
          }


          List<Map<String,Object>> intentInfoListMap = (List<Map<String, Object>>) listMap.get(i).get("intentInfo");
          List<Map<String,Object>> intentList = new ArrayList<>();
          int intentListCnt = 0;
          int endNodeCnt = 0;
          for (int j = 0; j < intentInfoListMap.size(); j++) {
            String intent = "";
            if(intentInfoListMap.get(j).get("intentCell") != null && !"".equals(intentInfoListMap.get(j).get("intentCell").toString())){
              //다음 TASK ListMap 세팅
              for (int k = 0; k < intentInfoListMap.size(); k++) {
                if(intentInfoListMap.get(j).get("nextTaskCell").toString().equals(intentInfoListMap.get(k).get("nextTaskCell").toString())) {
                  intent = intentInfoListMap.get(k).get("intentCell").toString() +"/" + intent.trim();
                  intent = StringUtils.removeEnd(intent, "/");
                }
              }
              Map<String, Object> nextTaskMap = new HashMap<>();
              nextTaskMap.put("TASK", listMap.get(i).get("task"));
              nextTaskMap.put("intent",intent);
              nextTaskMap.put("nextTask", intentInfoListMap.get(j).get("nextTaskCell").toString());
              nextTaskListMap.add(nextTaskCnt++,nextTaskMap);
            }else{
              intent = "ALWAYS";
              Map<String, Object> nextTaskMap = new HashMap<>();
              nextTaskMap.put("TASK", listMap.get(i).get("task"));
              nextTaskMap.put("intent",intent);
              nextTaskMap.put("nextTask", intentInfoListMap.get(j).get("nextTaskCell").toString());
              nextTaskListMap.add(nextTaskCnt++,nextTaskMap);
            }

            Map<String,Object> intentMap = new HashMap<>();
            intentMap.put("intent", intentInfoListMap.get(j).get("intentCell").toString());
            intentMap.put("answer", intentInfoListMap.get(j).get("intentAnswerCell").toString());
            intentMap.put("info", intentInfoListMap.get(j).get("infoCell").toString());
            intentMap.put("nextTask", intentInfoListMap.get(j).get("nextTaskCell").toString());
            intentList.add(intentListCnt++,intentMap);
            // 다음 TASK가 종료일때 종료 테스크 node 생성
            if (intentInfoListMap.get(j).get("nextTaskCell").toString().equals(endTaskName) && !intentInfoListMap.get(j).get("nextTaskCell").toString().equals("") && intentInfoListMap.get(j).get("nextTaskCell") != null) {
              if(endNodeCnt == 0) {
                UUID nodeEndId = UUID.randomUUID();
                nodeEnd.setId(nodeEndId.toString());
                nodeEnd.setType("end");
                nodeEnd.setAttrMap(systemFalse);
                nodeEnd.setLabel(endTaskName);
                endNodeCnt++;
              }
            }
          }
          systemUtter.put("version", "2.0");
          systemUtter.put("intentList", intentList);
          systemUtterList.add(systemUtter);
          node.setAttrMap(systemUtterList);
          if (i == 0) {
            node.setRegexList(regexList);
          }

          nodeList.add(node);
          if(nodeEnd.getId() != null && !nodeEnd.getId().toString().equals("")) {
            nodeList.add(nodeEnd);
          }
        }
      }

      if (!exceptIntent.equals("")) {
        nodeList.get(0).getAttrMap().get(0).put("exceptIntent", exceptIntent);
      }

      int edgeCnt = 0;

      for (int j = 0; j < nodeList.size(); j++) {

        for (int j2 = 0; j2 < nextTaskListMap.size(); j2++) {

          List<String> attrList = new ArrayList<>();
          if(nodeList.get(j).getLabel().toString().equals(nextTaskListMap.get(j2).get("TASK").toString())) {
            UUID edgeId = UUID.randomUUID();
            for (int k = 0; k < nodeList.size(); k++) {
              if(nodeList.get(k).getLabel().toString().equals(nextTaskListMap.get(j2).get("nextTask").toString())) {
                if(!nextTaskListMap.get(j2).get("nextTask").toString().equals(endTaskName)) {
                  Edge edge = simVO.new Edge();
                  EdgeData edgeData = edge.new EdgeData();
                  edgeData.setId(edgeId.toString());
                  edgeData.setType("default");
                  edgeData.setLabel(nextTaskListMap.get(j2).get("intent").toString());
                  edgeData.setAttr(attrList);
                  edge.setData(edgeData);
                  edge.setSource(nodeList.get(j).getId().toString());
                  edge.setTarget(nodeList.get(k).getId().toString());
                  edgeList.add(edgeCnt++, edge);
                }
              }
            }
            if(nextTaskListMap.get(j2).get("nextTask").toString().equals(endTaskName)) {
              Edge edge = simVO.new Edge();
              EdgeData edgeData = edge.new EdgeData();
              edgeData.setId(edgeId.toString());
              edgeData.setType("default");
              edgeData.setLabel(nextTaskListMap.get(j2).get("intent").toString());
              edgeData.setAttr(attrList);
              edge.setData(edgeData);
              edge.setSource(nodeList.get(j).getId().toString());
              edge.setTarget(nodeList.get(j+1).getId().toString());
              edgeList.add(edgeCnt++, edge);

            }
//				  else if(nodeList.size() != j+1 && nextTaskListMap.get(j2).get("nextTask").toString().replaceAll("\\p{Z}", "").equals("") && !nextTaskListMap.get(j2).get("nextTask").toString().replaceAll("\\p{Z}", "").equals(endTaskName)) {
//					  Edge edge = simVO.new Edge();
//					  EdgeData edgeData = edge.new EdgeData();
//					  edgeData.setId(edgeId.toString());
//					  edgeData.setType("default");
//					  edgeData.setLabel(nextTaskListMap.get(j2).get("intent").toString());
//					  edgeData.setAttr(attrList);
//					  edge.setData(edgeData);
//					  edge.setSource(nodeList.get(j).getId().toString());
//					  edge.setTarget(nodeList.get(j+1).getId().toString());
//					  edgeList.add(edgeCnt++, edge);
//				  }

          }
        }

      }

      scenario.setNodes(nodeList);
      scenario.setEdges(edgeList);
    }

	  return scenario;

  }
  private Scenario excelToScenario(int lang, File destFile) throws Exception {
	logger.debug("Function call track : excelToScenario");
    ReadOption excelReadOption = new ReadOption();

    excelReadOption.setFilePath(destFile.getAbsolutePath());
    excelReadOption
        .setOutputColumns("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O");
    excelReadOption.setStartRow(2);
    List<Map<String, String>> excelContent = ExcelRead.read(excelReadOption);
    SimpleBotVO simVO = new SimpleBotVO();
    Scenario scenario = simVO.new Scenario();
    List<Node> nodeList = new ArrayList();
    List<Edge> edgeList = new ArrayList();

    List<UUID> nodeIdList = new ArrayList();
    List<UUID> edgeIdYList = new ArrayList();
    List<UUID> edgeIdNList = new ArrayList();

    String endTaskName = "종료";
    // 1: KOR , 2: ENG
    if (lang == 2) {
      endTaskName = "END";
    }

    Map<String, Object> columnMap = new HashMap<>();
    String[] columnList = {TASK, START_ANSWER, YES_ANSWER, NO_ANSWER, UNKNOWN_ANSWER,
        REPEAT_ANSWER, YES_NEXT_TASK, NO_NEXT_TASK,
        USER_UTTER, USER_DIAL, MAX_TURN, TASK_OVER_MAX, ACCEPT_STT_STC_IDX, REPEAT_ANSWER_STC_IDX};

    // 각 컬럼의 엑셀 위치값 확인
    for (int i = 0; i < columnList.length; i++) {
      for (String key : excelContent.get(0).keySet()) {
        if (excelContent.get(0).get(key).replaceAll("\\p{Z}", "").toUpperCase()
            .equals(columnList[i].replaceAll("\\p{Z}", "").toUpperCase())) {
          columnMap.put(columnList[i], key);
        }
      }
    }

    // 각 task별 nodeId, edgeId 생성
    for (int k = 1; k < excelContent.size(); k++) {
      if ((endTaskName.equals(excelContent.get(k).get(columnMap.get(YES_NEXT_TASK))) || endTaskName.equals(excelContent.get(k).get(columnMap.get(NO_NEXT_TASK))))
          && !endTaskName.equals(excelContent.get(k+1).get(columnMap.get(TASK)))) {
        UUID nodeId = UUID.randomUUID();
        nodeIdList.add(nodeId);
      } if (excelContent.get(k).get(columnMap.get(TASK)) != null
          && !excelContent.get(k).get(columnMap.get(TASK)).equals("")
          && !excelContent.get(k).get(columnMap.get(TASK)).equals("Sheet_Change")
          && !excelContent.get(k).get(columnMap.get(TASK)).replaceAll("\\p{Z}", "").toUpperCase().equals(TASK.replaceAll("\\p{Z}", "").toUpperCase())) {
        UUID nodeId = UUID.randomUUID();
        UUID edgeIdY = UUID.randomUUID();
        UUID edgeIdN = UUID.randomUUID();
        nodeIdList.add(nodeId);
        edgeIdYList.add(edgeIdY);
        edgeIdNList.add(edgeIdN);
      }
    }

    int cnt = 0;
    int k = 0;
    int sheet = 0;
    int taskCnt = 0;
    for (int i = 1; i < excelContent.size(); i++) {
      List<SystemUtter> systemUtterList = new ArrayList<>();
      List<SystemUtter> systemFalse = new ArrayList<>();
      Node node = simVO.new Node();
      Node nodeEnd = simVO.new Node();
      SystemUtter systemUtter = node.new SystemUtter();
      if ("Sheet_Change".equals(excelContent.get(i).get("A"))) {
		  sheet++;
      }
      if(sheet == 2) {
    	  break;
      }
      // task 값이 null, 종료, 컬럼명 값이 아닌경우 attr 데이터 생성
      if (excelContent.get(i).get(columnMap.get(TASK)) != null
          && !excelContent.get(i).get(columnMap.get(TASK)).equals("")
          && !excelContent.get(i).get(columnMap.get(TASK)).equals(endTaskName)
          && !excelContent.get(i).get(columnMap.get(TASK)).equals("Sheet_Change")
          && !excelContent.get(i).get(columnMap.get(TASK)).replaceAll("\\p{Z}", "").toUpperCase().equals(TASK.replaceAll("\\p{Z}", "").toUpperCase())) {
        systemUtter.setUtter(excelContent.get(i).get(columnMap.get(START_ANSWER)));
        systemUtter.setUtterY(excelContent.get(i).get(columnMap.get(YES_ANSWER)));
        systemUtter.setUtterN(excelContent.get(i).get(columnMap.get(NO_ANSWER)));

        if (columnMap.containsKey(UNKNOWN_ANSWER) && columnMap.containsKey(REPEAT_ANSWER) && columnMap
            .containsKey(USER_UTTER) && columnMap.containsKey(USER_DIAL)
            && columnMap.containsKey(MAX_TURN) && columnMap.containsKey(TASK_OVER_MAX)
            && columnMap.containsKey(ACCEPT_STT_STC_IDX) && columnMap
            .containsKey(REPEAT_ANSWER_STC_IDX)
        ) {

          systemUtter.setUtterU(excelContent.get(i).get(columnMap.get(UNKNOWN_ANSWER)));
          systemUtter.setUtterR(excelContent.get(i).get(columnMap.get(REPEAT_ANSWER)));
          String utter = excelContent.get(i).get(columnMap.get(USER_UTTER)).replaceAll("\\p{Z}", "")
              .toUpperCase();
          String dial = excelContent.get(i).get(columnMap.get(USER_DIAL)).replaceAll("\\p{Z}", "")
              .toUpperCase();
          // 사용자 input 데이터에 따라 0,1,2 값으로 분리
          if ("Y".equals(utter) && "Y".equals(dial)) {
            systemUtter.setInputType("2");
          } else if ("".equals(utter) && "Y".equals(dial)) {
            systemUtter.setInputType("1");
          } else if ("Y".equals(utter) && "".equals(dial)) {
            systemUtter.setInputType("0");
          } else {
            systemUtter.setInputType("0");
          }
          // maxTurn 세팅
          if (excelContent.get(i).get(columnMap.get(MAX_TURN)) != null
              && !"".equals(excelContent.get(i).get(columnMap.get(MAX_TURN)))) {
            systemUtter
                .setMaxTurn(Integer.parseInt(excelContent.get(i).get(columnMap.get(MAX_TURN))));
          } else {
            systemUtter.setMaxTurn(0);
          }
          // taskOverMax 세팅
          if (excelContent.get(i).get(columnMap.get(TASK_OVER_MAX)) != null && !""
              .equals(excelContent.get(i)
                  .get(columnMap.get(TASK_OVER_MAX)))) {
            systemUtter.setTaskOverMax(excelContent.get(i).get(columnMap.get(TASK_OVER_MAX)));
          }
          // acceptSttStcIdx 세팅
          String[] acceptSttStcIdxList = excelContent.get(i)
              .get(columnMap.get(ACCEPT_STT_STC_IDX)).replaceAll("\\p{Z}", "").split(",");
          String acceptSttStcIdx = "";
          // 답변 무시구간, 반복 구간 값의 -1의 데이터 저장
          if (acceptSttStcIdxList.length > 0 && !"".equals(acceptSttStcIdxList[0])) {
            for (int j = 0; j < acceptSttStcIdxList.length; j++) {
              if (j != acceptSttStcIdxList.length - 1) {
                acceptSttStcIdx += Integer.parseInt(acceptSttStcIdxList[j]) - 1 + ",";
              } else {
                acceptSttStcIdx += Integer.parseInt(acceptSttStcIdxList[j]) - 1;
              }
            }
            systemUtter.setAcceptSttStcIdx(acceptSttStcIdx);
          }

          // repeatAnswerStcIdx 세팅
          String[] repeatAnswerStcIdxList = excelContent.get(i)
              .get(columnMap.get(REPEAT_ANSWER_STC_IDX)).replaceAll("\\p{Z}", "").split(",");
          String repeatAnswerStcIdx = "";
          if (repeatAnswerStcIdxList.length > 0 && !"".equals(repeatAnswerStcIdxList[0])) {
            for (int j = 0; j < repeatAnswerStcIdxList.length; j++) {
              if (j != repeatAnswerStcIdxList.length - 1) {
                repeatAnswerStcIdx += Integer.parseInt(repeatAnswerStcIdxList[j]) - 1 + ",";
              } else {
                repeatAnswerStcIdx += Integer.parseInt(repeatAnswerStcIdxList[j]) - 1;
              }
            }
            systemUtter.setRepeatAnswerStcIdx(repeatAnswerStcIdx);
          }
        }

        systemUtterList.add(systemUtter);
        node.setAttr(systemUtterList);
        node.setId(nodeIdList.get(i-1 + cnt).toString());
        // task type 구별
        if (i == 1) {
          node.setType("start");
          taskCnt++;
        } else if (sheet == 1){
          node.setType("global");
        } else {
          node.setType("task");
          taskCnt++;
        }
        node.setLabel(excelContent.get(i).get(columnMap.get(TASK)));
        nodeList.add(node);
        if(excelContent.get(i + 1).get(columnMap.get(TASK)) != null) {
        // 종료 테스크 node 생성
        if (i != excelContent.size()-1 && (excelContent.get(i).get(columnMap.get(YES_NEXT_TASK)).equals(endTaskName)
            || excelContent.get(i).get(columnMap.get(NO_NEXT_TASK))
            .equals(endTaskName) || excelContent.get(i + 1).get(columnMap.get(TASK))
            .equals(endTaskName))) {
          cnt++;
          taskCnt++;
          nodeEnd.setId(nodeIdList.get(i-1 + cnt).toString());
          nodeEnd.setType("end");
          nodeEnd.setAttr(systemFalse);
          nodeEnd.setLabel(endTaskName);
          nodeList.add(nodeEnd);
        }
      } else {
        cnt--;
      }
    }
    }

    cnt = 0;
    for (int i = 0; i < nodeList.size(); i++) {
      if (!nodeList.get(i).getLabel().equals(endTaskName) && i < taskCnt) {
        Edge edgeY = simVO.new Edge();
        Edge edgeN = simVO.new Edge();
        EdgeData edgeDataY = edgeY.new EdgeData();
        EdgeData edgeDataN = edgeN.new EdgeData();
        List<String> attrList = new ArrayList<>();
        edgeDataY.setId(edgeIdYList.get(k).toString());
        edgeDataY.setType("default");
        edgeDataY.setLabel("YES");
        edgeDataY.setAttr(attrList);
        edgeY.setData(edgeDataY);
        edgeY.setSource(nodeIdList.get(i).toString());
        // YES 다음 TASK 맵핑, edge target 데이터 생성
        if (excelContent.get(i+1 - cnt).get(columnMap.get(YES_NEXT_TASK)) != null && !excelContent.get(i+1 - cnt).get(columnMap.get(YES_NEXT_TASK))
            .equals("")) {
          if (excelContent.get(i+1 - cnt).get(columnMap.get(YES_NEXT_TASK)).equals(endTaskName)) {
            edgeY.setTarget(nodeIdList.get(i + 1).toString());
          } else {
            for (int j = 0; j < nodeList.size(); j++) {
              if (excelContent.get(i+1 - cnt).get(columnMap.get(YES_NEXT_TASK)).equals(nodeList.get(j).getLabel())) {
                edgeY.setTarget(nodeIdList.get(j).toString());
              }
            }
          }
        } else {
          if (excelContent.get(i+1 - cnt).get(columnMap.get(NO_NEXT_TASK)).equals(endTaskName)) {
            edgeY.setTarget(nodeIdList.get(i + 2).toString());
          } else {
            edgeY.setTarget(nodeIdList.get(i + 1).toString());
          }
        }
        edgeList.add(edgeY);

        edgeDataN.setId(edgeIdNList.get(k).toString());
        edgeDataN.setType("default");
        edgeDataN.setLabel("NO");
        edgeDataN.setAttr(attrList);
        edgeN.setData(edgeDataN);
        edgeN.setSource(nodeIdList.get(i).toString());
        // NO 다음 TASK 맵핑, edge target 데이터 생성
        if (excelContent.get(i+1 - cnt).get(columnMap.get(NO_NEXT_TASK)) != null && !excelContent.get(i+1 - cnt).get(columnMap.get(NO_NEXT_TASK))
            .equals("")) {
          if (excelContent.get(i+1 - cnt).get(columnMap.get(NO_NEXT_TASK)).equals(endTaskName)) {
            edgeN.setTarget(nodeIdList.get(i + 1).toString());
          } else {
            for (int j = 0; j < nodeList.size(); j++) {
              if (excelContent.get(i+1 - cnt).get(columnMap.get(NO_NEXT_TASK)).equals(nodeList.get(j).getLabel())) {
                edgeN.setTarget(nodeIdList.get(j).toString());
              }
            }
          }
        } else {
          if (excelContent.get(i+1 - cnt).get(columnMap.get(YES_NEXT_TASK)).equals(endTaskName)) {
            edgeN.setTarget(nodeIdList.get(i + 2).toString());
          } else {
            edgeN.setTarget(nodeIdList.get(i + 1).toString());
          }
        }
        edgeList.add(edgeN);
        k++;
      } else {
        cnt++;
      }
      if (edgeIdYList.size() == k) {
        break;
      }
    }

    scenario.setNodes(nodeList);
    scenario.setEdges(edgeList);

    return scenario;

  }

  @Override
  public List<Map> getSimpleBotListFromCompanyId(String companyId, String keyword) {
    List<Map> simpleBotList = simpleBotDAO.getSimpleBotListFromCompanyId(companyId, keyword);
    if (simpleBotList == null) {
      logger.info("NO Mapping OR WRONG Mapping SimpleBotList with companyID [{}]", companyId);
    }
    return simpleBotList;
  }

  @Override
  public List<Map> getSimpleBotListFromUserId(String userId, String keyword) {
    List<Map> simpleBotList = simpleBotDAO.getSimpleBotListFromUserId(userId, keyword);
    if (simpleBotList == null) {
      logger.info("NO Mapping OR WRONG Mapping SimpleBotList with userID [{}]", userId);
    }
    return simpleBotList;
  }

  @Override
  @Transactional
  public void deleteScenario(int host, int simplebotId) {
    // 기존 intentRel 삭제
    maumSDSDAO.deleteOldIntentRel(host);
    // 기존 Intent 삭제 & 기존 ANSWER 삭제
    maumSDSDAO.deleteOldIntentAnswer(host);
    simpleBotDAO.deleteSimpleBot(simplebotId);
  }

  @Override
  public void deleteBySimplebotIdV2(int simplebotId) {
    simpleBotDAO.deleteSimpleBot(simplebotId);
  }

  @Override
  @Transactional
  public void deleteByHostV2(int host, int simplebotId, int lang) {
      // 기존 intentRel 삭제
      maumSDSDAO.deleteOldIntentRel(host, lang);
      // 기존 Intent 삭제 & 기존 ANSWER 삭제
      maumSDSDAO.deleteOldIntentAnswer(host, lang);
      // 기존 BertIntent(의도) 삭제 & 기존 regex 삭제
      maumSDSDAO.deleteBertIntentRegexAll(host, lang);

      simpleBotDAO.deleteSimpleBot(simplebotId);
  }

  @Override
  @Transactional
  public void deleteByIntentNo(int host, int lang, int no) {
    // answer, intentRel, regexRule, regexIntent, bertIntent 삭제
    maumSDSDAO.deleteByIntentNo(host, lang, no);
  }

  @Override
  public String getTestCustData(int simplebotId) {
    String customInfos = simpleBotDAO.getTestCustData(simplebotId);
    return customInfos;
  }

  @Override
  @Transactional
  public void saveTestCustData(int simplebotId, String custDataJson) {
    int cnt = simpleBotDAO.saveTestCustData(simplebotId, custDataJson);
    if (cnt == 0) {
      logger.info("Update TestCustomInfos failed with simplebotId [{}]", simplebotId);
    } else if (cnt > 1) {
      logger.info("{} TestCustomInfos Updated with simplebotId [{}]", cnt, simplebotId);
    }
  }

  @Override
  @Transactional
  public long getContractNo(int simplebotId, String telNo) {
    long contractNo = simpleBotDAO.getContractNo(simplebotId, telNo);
    return contractNo;
  }

  @Override
  public int getWaitingCustomer(int contractNo) {
	int waitingCount = simpleBotDAO.getWaitingCustomer(contractNo);
	return waitingCount;
  }

  @Override
  public int checkScenarioName(Map<String, Object> inputParam) {
  	int checkCount = simpleBotDAO.checkScenarioName(inputParam);
  	return checkCount;
  }

  @Override
  public void updateScenarioName(Map<String, Object> inputParam) {
  	simpleBotDAO.updateScenarioName(inputParam);
  }

  @Override
  @Transactional
  public void deleteAllByHostV2(Map<String, Object> map){
      try{
          // 기존 intentRel 삭제
          maumSDSDAO.deleteOldIntentRel(Integer.parseInt(map.get("host").toString()), Integer.parseInt(map.get("lang").toString()));
          // 기존 Intent 삭제 & 기존 ANSWER 삭제
          maumSDSDAO.deleteOldIntentAnswer(Integer.parseInt(map.get("host").toString()), Integer.parseInt(map.get("lang").toString()));
          // 기존 BertIntent(의도) 삭제 & 기존 regex 삭제
          maumSDSDAO.deleteBertIntentRegexAll(Integer.parseInt(map.get("host").toString()), Integer.parseInt(map.get("lang").toString()));
          // DELETE Account, BackendInfo, style_css, ReplaceDict
          builderDAO.deleteAccountByHostV2(map);
      }catch (Exception e){
          e.printStackTrace();
      }
  };
}
