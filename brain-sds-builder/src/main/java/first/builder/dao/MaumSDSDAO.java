package first.builder.dao;

import first.builder.vo.SimpleBotVO;
import first.builder.vo.SimpleBotVO.Edge;
import first.common.util.PropInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository("maumSDSDAO")
public class MaumSDSDAO extends AbstractDAO {

  public int insertAccount(String name, String email, String hostName) {
    Map<String, Object> param = new HashMap<>();
    param.put("name", name);
//    param.put("email", email);
    // todo: host 겹치지 않도록
    param.put("host", hostName);

    insert("maumsds.insertAccount", param);

    return Integer.parseInt(param.get("no").toString());
  }

  public void setBackendInfo(int host, String defaultHost) {
    Map<String, Object> param = new HashMap<>();
    param.put("host", host);
    param.put("defaultHost", defaultHost);
    insert("maumsds.insertBackendInfo", param);
  }

  public void deleteOldIntentAnswer(int accountPar, int langPar) {
    Map<String, Integer> param = new HashMap<>();
    param.put("userAccount", accountPar);
    param.put("langPar", langPar);
    delete("maumsds.deleteIntent", param);
    delete("maumsds.deleteAnswer", param);
  }

  public void deleteOldIntentAnswer(int accountPar) {
    Map<String, Integer> param = new HashMap<>();
    param.put("userAccount", accountPar);
    delete("maumsds.deleteIntent", param);
    delete("maumsds.deleteAnswer", param);
  }

  public void deleteBertIntentRegexAll(int accountPar, int langPar) {
    Map<String, Integer> param = new HashMap<>();
    param.put("host", accountPar);
    param.put("lang", langPar);
    delete("maumsds.deleteBertRegexRuleAll", param);
    delete("maumsds.deleteBertRegexIntentAll", param);
    delete("maumsds.deleteBertIntentAll", param);
  }

  public void deleteByIntentNo(int accountPar, int langPar, int no) {
    Map<String, Object> param = new HashMap<>();
    param.put("host", accountPar);
    param.put("lang", langPar);
    param.put("no", no);
    delete("maumsds.deleteBertIntentAnswer", param);
    delete("maumsds.deleteIntentRelByIntentNo", param);
    delete("maumsds.deleteBertIntentRegexRule", param);
    delete("maumsds.deleteBertRegexIntent", param);
    delete("maumsds.deleteBertIntent", param);
  }

  public Map<String, Integer> insertSimpleBotBertIntent(int lang, int bertITFid) {
    String[] SimpleBotBertIntent = {"YES", "NO", "UNKNOWN", "REPEAT"};
    for (int i = 0; i < SimpleBotBertIntent.length; i++) {
      Map<String, Object> param = new HashMap<>();
      param.put("bertIntentName", SimpleBotBertIntent[i]);
      param.put("langPar", lang);
      param.put("BertItfIDPar", bertITFid);
      try {
        Object bertIntentCount = selectOne("maumsds.selectBertIntent", param);
        if (bertIntentCount == null) {
          Map<String, Object> insertParam = new HashMap<>();
          insertParam.put("namePar", SimpleBotBertIntent[i]);
          insertParam.put("langPar", lang);
          insertParam.put("itfid", bertITFid);
          insert("maumsds.insertBertIntent", insertParam);
        }
      } catch (Exception e) {
        continue;
      }
    }

    Map<String, Integer> overallBertIntentMap = new HashMap<>();
    for (int i = 0; i < SimpleBotBertIntent.length; i++) {
      Map<String, Object> selectBertIntent = new HashMap<>();
      selectBertIntent.put("BIName", SimpleBotBertIntent[i]);
      selectBertIntent.put("ItfIDPar", bertITFid);
      selectBertIntent.put("langPar", lang);
      List<Integer> bertlist = selectList("maumsds.selectBertIntentCheck", selectBertIntent);
      overallBertIntentMap.put(SimpleBotBertIntent[i], (Integer) bertlist.get(0));
    }
    return overallBertIntentMap;
  }

  public Map<String, Integer> insertSimpleBotBertIntentV2(int lang, int bertITFid, List<Edge> edges) {
    List<String> simpleBotBertIntent = new ArrayList<>();
    List<String> edgeLabelList = new ArrayList<>();

    for (int i = 0; i < edges.size(); i++) {
      String label = edges.get(i).getData().getLabel();
      String[] splitedLabel = label.split("/");
      Collections.addAll(edgeLabelList, splitedLabel);
    }

    for (int i = 0; i < edgeLabelList.size(); i++) {
      if (!simpleBotBertIntent.contains(edgeLabelList.get(i).trim()) &&
          !edgeLabelList.get(i).trim().equals("ALWAYS")) {
        simpleBotBertIntent.add(edgeLabelList.get(i).trim());
      }
    }

    for (int i = 0; i < simpleBotBertIntent.size(); i++) {
      Map<String, Object> param = new HashMap<>();
      param.put("bertIntentName", simpleBotBertIntent.get(i));
      param.put("langPar", lang);
      param.put("BertItfIDPar", bertITFid);
      try {
        Object bertIntentCount = selectOne("maumsds.selectBertIntent", param);
        if (bertIntentCount == null) {
          Map<String, Object> insertParam = new HashMap<>();
          insertParam.put("namePar", simpleBotBertIntent.get(i));
          insertParam.put("langPar", lang);
          insertParam.put("itfid", bertITFid);
          insert("maumsds.insertBertIntent", insertParam);
        }
      } catch (Exception e) {
        continue;
      }
    }

    Map<String, Integer> overallBertIntentMap = new HashMap<>();
    for (int i = 0; i < simpleBotBertIntent.size(); i++) {
      Map<String, Object> selectBertIntent = new HashMap<>();
      selectBertIntent.put("BIName", simpleBotBertIntent.get(i));
      selectBertIntent.put("ItfIDPar", bertITFid);
      selectBertIntent.put("langPar", lang);
      List<Integer> bertlist = selectList("maumsds.selectBertIntentCheck", selectBertIntent);
      overallBertIntentMap.put(simpleBotBertIntent.get(i), (Integer) bertlist.get(0));
    }
    return overallBertIntentMap;
  }

  // maum-sds DB 에서 사용하는 scope 형식으로 변환
  // index list -> index scope list
  // "0,1,4,5,6" -> "0,1,4,6"
  private String getDestAnsScope(String indexListStr) {
    String repeatAnswerStcIdx = "";

    try {

      String[] idxStrList = indexListStr.split(",");
      List<Integer> idxList = new ArrayList<>();
      for (String idx : idxStrList) {
        if (idx.isEmpty())
          continue;
        idxList.add(Integer.parseInt(idx));
      }
      // 오름차순 정렬
      idxList.sort(null);

      int firstIdx = -1;
      int lastIdx = -1;

      for (int idx : idxList) {
        if (firstIdx == -1) {
          firstIdx = idx;
          lastIdx = idx;
        } else if (lastIdx == (idx - 1)) {
          lastIdx = idx;
        } else if (lastIdx < (idx - 1)) {
          if (!repeatAnswerStcIdx.isEmpty()) {
            repeatAnswerStcIdx = repeatAnswerStcIdx.concat(",");
          }
          // "firstIdx,lastIdx" 를 str에 append
          repeatAnswerStcIdx = repeatAnswerStcIdx.concat(String.valueOf(firstIdx)).concat(",")
              .concat(String.valueOf(lastIdx));
          firstIdx = idx;
          lastIdx = idx;
        }
      }
      if (firstIdx != -1) {
        // "firstIdx,lastIdx" 를 str에 append
        if (!repeatAnswerStcIdx.isEmpty()) {
          repeatAnswerStcIdx = repeatAnswerStcIdx.concat(",");
        }
        repeatAnswerStcIdx = repeatAnswerStcIdx.concat(String.valueOf(firstIdx)).concat(",")
            .concat(String.valueOf(lastIdx));
      }
    } catch (Exception e) {
      System.out.println("[maumSDS DAO] getDestAnsScope. Cannot parse repeatAnsStcIdx : " + indexListStr);
    }

    if (repeatAnswerStcIdx.isEmpty()) {
      repeatAnswerStcIdx = "0,-1";
    }
    return repeatAnswerStcIdx;

  }

  public SimpleBotVO.specialAnswerKey insertUnknownAnswer(int account, int lang) {
    String unknownAnswer = lang != 2 ? "네 또는 아니오로 한 번 더 답변 부탁드립니다. " : "Please answer once more with YES or NO.";
    String repeatAnswer = lang != 2 ? "다시 한 번 들려드리겠습니다. " : "Let me say it again.";
    Map<String, Object> param = new HashMap<>();
    param.put("accountPar", account);
    param.put("langPar", lang);
    param.put("answerStr", unknownAnswer);
    insert("maumsds.insertAnswer", param);
    int unknownAnswerKey = (Integer) param.get("No");
    param.put("answerStr", repeatAnswer);
    insert("maumsds.insertAnswer", param);
    int repeatAnswerKey = (Integer) param.get("No");
    return new SimpleBotVO.specialAnswerKey(unknownAnswerKey, repeatAnswerKey);
  }

  private int insertAnswer(Map<String, Object> insertAnswerParam, String answer, Map<String, Integer> ansKeyMap) {
    int key = 0;
    if (answer == null || answer.isEmpty()) {
      return key;
    }

    if (ansKeyMap.containsKey(answer)) {
      return ansKeyMap.get(answer);
    }

    insertAnswerParam.put("answerStr", answer);
    insert("maumsds.insertAnswer", insertAnswerParam);
    key = Integer.parseInt(insertAnswerParam.get("No").toString());
    ansKeyMap.put(answer, key);
    return key;

  }

  public SimpleBotVO.nodeUtter insertAnswerTask(int account, int lang,
      SimpleBotVO.Node scenarioNode, SimpleBotVO.specialAnswerKey specialAnsKey,
      Map<String, Integer> bertIntentMap, Boolean firstNode, Map<String, Integer> ansKeyMap) {

    SimpleBotVO.nodeUtter nodeUtter = new SimpleBotVO.nodeUtter(scenarioNode.getAttr().get(0));
    Map<String, Object> insertAnswerParam = new HashMap<>();
    insertAnswerParam.put("accountPar", account);
    insertAnswerParam.put("langPar", lang);

    // SystemUtter
    nodeUtter.setUtterAnswerKey(insertAnswer(insertAnswerParam, nodeUtter.getUtter(), ansKeyMap));
    // Yes Answer
    nodeUtter.setUtterYAnswerKey(insertAnswer(insertAnswerParam, nodeUtter.getUtterY(), ansKeyMap));
    // No Answer
    nodeUtter.setUtterNAnswerKey(insertAnswer(insertAnswerParam, nodeUtter.getUtterN(), ansKeyMap));
    // Unknown Answer
    nodeUtter.setUtterUAnswerKey(insertAnswer(insertAnswerParam, nodeUtter.getUtterU(), ansKeyMap));
    if (nodeUtter.getUtterUAnswerKey() == 0) {
      // unknown answer 비어있을 시 default값 설정
      nodeUtter.setUtterUAnswerKey(specialAnsKey.getUnknownAnsKey());
    }
    // Repeat Answer
    nodeUtter.setUtterRAnswerKey(insertAnswer(insertAnswerParam, nodeUtter.getUtterR(), ansKeyMap));

//    nodeUtter. todo

    // task(node) insert
    int intentKey = insertTask(lang, scenarioNode.getLabel(), nodeUtter.getUtterAnswerKey(), account, firstNode);
    nodeUtter.setIntentKey(intentKey);

    // IntentRel의 DestAnsScope 컬럼에 넣을 값
    String destAnsScope = getDestAnsScope(scenarioNode.getAttr().get(0).getRepeatAnswerStcIdx());

    // IntentRel 추가 (Unknown, Repeat 관련)
    insertIntentRel(intentKey, bertIntentMap.get("REPEAT"), intentKey, nodeUtter.getUtterRAnswerKey(), destAnsScope);// Intent -> Intent (Repeat)
    insertIntentRel(intentKey, bertIntentMap.get("UNKNOWN"), intentKey, nodeUtter.getUtterUAnswerKey(), "none");// Intent -> Intent (Unknown)

    return nodeUtter;
  }

  public SimpleBotVO.nodeUtterV2 insertAnswerTaskV2(int account, int lang,
      SimpleBotVO.Node scenarioNode, Boolean firstNode, Map<String, Integer> ansKeyMap) {

    SimpleBotVO.nodeUtterV2 nodeUtter = new SimpleBotVO.nodeUtterV2(scenarioNode.getAttr().get(0));
    Map<String, Object> insertAnswerParam = new HashMap<>();
    insertAnswerParam.put("accountPar", account);
    insertAnswerParam.put("langPar", lang);

    // SystemUtter
    nodeUtter.setUtterAnswerKey(insertAnswer(insertAnswerParam, nodeUtter.getUtter(), ansKeyMap));

    for (int i = 0 ; i < nodeUtter.getIntentList().size(); i++) {
      SimpleBotVO.intentList intent = nodeUtter.getIntentList().get(i);
      intent.setAnswerKey(insertAnswer(insertAnswerParam, intent.getAnswer(), ansKeyMap));
    }

    // task(node) insert
    if (!scenarioNode.getLabel().trim().equals("*")) {
      int intentKey = insertTaskV2(lang, scenarioNode.getLabel(), nodeUtter.getUtterAnswerKey(), account, firstNode, scenarioNode.getTaskGroup());
      nodeUtter.setIntentKey(intentKey);
    }

    // IntentRel의 DestAnsScope 컬럼에 넣을 값
    String destAnsScope = getDestAnsScope(scenarioNode.getAttr().get(0).getRepeatAnswerStcIdx());
    nodeUtter.setDestAnsScope(destAnsScope);

    // IntentRel 추가 (Unknown, Repeat 관련)
//    insertIntentRel(intentKey, bertIntentMap.get("REPEAT"), intentKey, nodeUtter.getUtterRAnswerKey(), destAnsScope);// Intent -> Intent (Repeat)
//    insertIntentRel(intentKey, bertIntentMap.get("UNKNOWN"), intentKey, nodeUtter.getUtterUAnswerKey(), "none");// Intent -> Intent (Unknown)

    return nodeUtter;
  }

  public void insertEdge(int srcIntent, int bertIntent, int destIntent, int conditionAns, String meta, String destAnsScope){
    Map<String, Object> param = new HashMap<>();
    param.put("srcPar", srcIntent);
    param.put("bertPar", bertIntent);
    param.put("destPar", destIntent);
    param.put("condPar", conditionAns);
    
    // 다음 task 넘어갈 시 반복 구간 설정
    if(srcIntent != destIntent) {
    	param.put("destScopePar", "0,-1");
    }else {
    	param.put("destScopePar", destAnsScope);
    }
    
    param.put("metaPar", meta);
    // 값은 src-bert-dest 세트가 이미 있는지 조회
    List<Integer> relList = selectList("maumsds.selectIntentRelCheck", param);
    if (relList.size() == 0) {
      insert("maumsds.insertIntentRel", param);
    }
  }

  public void deleteOldIntentRel(int hostID, int lang){
    Map<String, Object> param = new HashMap<>();
    param.put("hostID", hostID);
    param.put("langPar", lang);
    delete("maumsds.deleteOldIntentRELwithSrc", param);
  }

  public void deleteOldIntentRel(int hostID){
    Map<String, Object> param = new HashMap<>();
    param.put("hostID", hostID);
    delete("maumsds.deleteOldIntentRELwithSrc", param);
  }

  public int insertTask(int lang, String ansLabel, int answerKey, int account, Boolean firstNode) {
    Map<String, Object> param = new HashMap<>();
    param.put("langPar", lang);
    param.put("mainPar", firstNode ? "처음으로" : ansLabel);
    param.put("namePar", ansLabel);
    param.put("entityPar", "N");
    param.put("answerPar", answerKey);
    param.put("accountPar", account);
    insert("maumsds.insertTask", param);
    return Integer.parseInt(param.get("No").toString());
  }

  public int insertTaskV2(int lang, String ansLabel, int answerKey, int account, Boolean firstNode, String taskGroup) {
    Map<String, Object> param = new HashMap<>();
    param.put("langPar", lang);
    param.put("mainPar", firstNode ? "처음으로" : ansLabel);
    param.put("namePar", ansLabel);
    param.put("entityPar", "N");
    param.put("answerPar", answerKey);
    param.put("accountPar", account);
    param.put("taskGroupPar", taskGroup);
    insert("maumsds.insertTaskV2", param);
    return Integer.parseInt(param.get("No").toString());
  }

  public void insertIntentRel(int srcPar, int bertPar, int destPar, int condPar, String destScopePar) {
    Map<String, Object> insertIntentRelParam = new HashMap<>();
    insertIntentRelParam.put("srcPar", srcPar);
    insertIntentRelParam.put("bertPar", bertPar);
    insertIntentRelParam.put("destPar", destPar);
    insertIntentRelParam.put("destScopePar", destScopePar);
    if (condPar != -1) {
      insertIntentRelParam.put("condPar", condPar);
    }
    insert("maumsds.insertIntentRel", insertIntentRelParam);
  }


  public int checkBackendInfo(int host) {
    Map<String, Object> selectBackendInfoParam = new HashMap<>();
    selectBackendInfoParam.put("hostPar", host);
    selectBackendInfoParam.put("servicePar", "ITF");
    List<Integer> backendInfoList = selectList("maumsds.checkMyBackendInfo", selectBackendInfoParam);
    if (backendInfoList.size() == 0) {//ITF BackendInfo 없음
      Map<String, Object> insertBackendInfoParam = new HashMap<>();
      insertBackendInfoParam.put("host", host);
      insertBackendInfoParam.put("service", "ITF");
      insertBackendInfoParam.put("ip", PropInfo.itfIP);
      insertBackendInfoParam.put("port", PropInfo.itfPort);
      insert("maumsds.insertNewBackendInfo", insertBackendInfoParam);
    }
    selectBackendInfoParam.put("servicePar", "NER");
    backendInfoList = selectList("maumsds.checkMyBackendInfo", selectBackendInfoParam);
    if (backendInfoList.size() == 0) {//ITF BackendInfo 없음
      Map<String, Object> insertBackendInfoParam = new HashMap<>();
      insertBackendInfoParam.put("host", host);
      insertBackendInfoParam.put("service", "NER");
      insertBackendInfoParam.put("ip", PropInfo.nerIP);
      insertBackendInfoParam.put("port", PropInfo.nerPort);
      insert("maumsds.insertNewBackendInfo", insertBackendInfoParam);
    }

    Map<String, Object> param = new HashMap<>();
    param.put("hostPar", host);
    param.put("servicePar", "ITF");
    List<Integer> backendList = selectList("maumsds.checkMyBackendInfo", param);
    int returnBertItfID = (Integer) backendList.get(0);

    System.out.println("returnBertItfID = " + returnBertItfID);
    return returnBertItfID;
  }

  public List<Map<String, Object>> selectSrcIList(int bertItfId) {
    List<Map<String, Object>> srcList = selectList("maumsds.selectSrcList", bertItfId);
    return srcList;
  }

  public void updateIntentTask(List<Map<String, Object>> map) {
    for (int i=0; i<map.size(); i++) {
      if (map.get(i).get("Task") != null ) {
        String task = map.get(i).get("Task").toString();
        //task = task.substring(0, task.length()-1);
        map.get(i).put("Task", task);
      }
    }
    update("maumsds.updateIntentTask", map);
  }

  public List<Map<String, Object>> getForceTask(int host) {
    List<Map<String, Object>> taskList = selectList("maumsds.getForceTask", host);
    return taskList;
  }

  public void updateTaskType(List<Map<String, Object>> map) {
    update("maumsds.updateTaskType", map);
  }

  public void deleteRegex(Map<String, Object> map) {
    delete("maumsds.deleteRegex", map);
  }

  public void insertRegex(Map<String, Object> map) {
    insert("maumsds.insertRegex", map);
  }

  public void updateIntentOfTask(Map<String, Object> map) {
    update("maumsds.updateIntentOfTask", map);
  }

    public void deleteChatbotCache(int host) { delete("maumsds.deleteChatbotCache", host);}
}
