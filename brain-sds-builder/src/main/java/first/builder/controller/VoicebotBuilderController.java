package first.builder.controller;

import first.builder.service.BuilderService;
import first.builder.service.NQAGrpcService;
import first.builder.service.SimpleBotService;
import first.builder.vo.Answer;
import first.builder.vo.Category;
import first.builder.vo.PagingVO;
import first.common.util.PropInfo;
import first.common.util.TimeQuery;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.aspectj.org.eclipse.jdt.internal.core.CreateCompilationUnitOperation;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class VoicebotBuilderController {

    @Resource(name = "builderService")
    private BuilderService builderService;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private NQAGrpcService nqaGrpcService;

    @Resource(name = "simpleBotService")
    private SimpleBotService simpleBotService;


    @ResponseBody
    @RequestMapping(value = "/vbBuilder")
    public ModelAndView voiceBotBuilderCommon(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ModelAndView view = new ModelAndView("voicebotBuilderContents/voicebotBuilderCommon");
        view.addObject("queryString",TimeQuery.getQueryString());
        
        view.addObject("env", PropInfo.env.toLowerCase());
		    view.addObject("m2uUrl", PropInfo.m2uUrl);
        
        return view;
    }

    //maumaiVoicebotForTesting
    @ResponseBody
    @RequestMapping(value = "/maumaiVoicebot")
    public ModelAndView maumaiVoicebotForTesting(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ModelAndView view = new ModelAndView("maumaiVoicebotForTesting/maumaiVoicebotCommon");
        view.addObject("queryString",TimeQuery.getQueryString());

        view.addObject("env", PropInfo.env.toLowerCase());
        view.addObject("m2uUrl", PropInfo.m2uUrl);
        return view;
    }
    @ResponseBody
    @RequestMapping(value = "/maumaiScenario")
    public ModelAndView maumaiVoicebotScenario(HttpServletRequest request, HttpServletResponse response)
            throws  Exception {
        ModelAndView view = new ModelAndView("maumaiVoicebotForTesting/maumaiVoicebotScenario");
        view.addObject("queryString",TimeQuery.getQueryString());

        view.addObject("env", PropInfo.env.toLowerCase());
        view.addObject("m2uUrl", PropInfo.m2uUrl);

        return view;
    }
    // maumaiVoicebotForTesting

    @ResponseBody
    @RequestMapping(value = "/vbBuilderScenario")
    public ModelAndView voiceBotBuilderScenario(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ModelAndView view = new ModelAndView("voicebotBuilderContents/voicebotBuilderScenario");
        view.addObject("queryString",TimeQuery.getQueryString());
        
        view.addObject("env", PropInfo.env.toLowerCase());
		    view.addObject("m2uUrl", PropInfo.m2uUrl);
        
        return view;
    }

    @ResponseBody
    @RequestMapping(value = "/vbBuilderIntention")
    public ModelAndView voiceBotBuilderIntention(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ModelAndView view = new ModelAndView("voicebotBuilderContents/voicebotBuilderIntention");
        view.addObject("queryString",TimeQuery.getQueryString());
        return view;
    }

    @ResponseBody
    @RequestMapping(value = "/vbBuilderTask")
    public ModelAndView voiceBotBuilderTask(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ModelAndView view = new ModelAndView("voicebotBuilderContents/voicebotBuilderTask");
        view.addObject("queryString",TimeQuery.getQueryString());
        return view;
    }

    @ResponseBody
    @RequestMapping(value = "/vbBuilderIntentionDetail")
    public ModelAndView voiceBotBuilderIntentionDetail(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ModelAndView view = new ModelAndView("voicebotBuilderContents/voicebotBuilderIntentionDetail");
        view.addObject("queryString",TimeQuery.getQueryString());
        view.addObject("title", "의도 상세");
        return view;
    }

    @ResponseBody
    @RequestMapping(value = "/vbBuilderIntentionAdd")
    public ModelAndView voiceBotBuilderIntentionAdd(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ModelAndView view = new ModelAndView("voicebotBuilderContents/voicebotBuilderIntentionDetail");
        view.addObject("queryString",TimeQuery.getQueryString());
        view.addObject("title", "의도 추가");
        return view;
    }

    @ResponseBody
    @RequestMapping(value = "/vbBuilderTaskDetail")
    public ModelAndView voiceBotBuilderTaskDetail(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ModelAndView view = new ModelAndView("voicebotBuilderContents/voicebotBuilderTaskDetail");
        view.addObject("queryString",TimeQuery.getQueryString());
        view.addObject("title", "TASK 상세");
        return view;
    }
    
    @ResponseBody
    @RequestMapping(value = "/vbBuilderTaskAdd")
    public ModelAndView voiceBotBuilderTaskAdd(HttpServletRequest request, HttpServletResponse response)
    		throws Exception {
    	ModelAndView view = new ModelAndView("voicebotBuilderContents/voicebotBuilderTaskDetail");
    	view.addObject("queryString",TimeQuery.getQueryString());
    	view.addObject("title", "TASK 추가");
    	return view;
    }

    @ResponseBody
    @RequestMapping(value = "/vbBuilderReplaceDic")
    public ModelAndView voiceBotBuilderReplaceDic(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ModelAndView view = new ModelAndView("voicebotBuilderContents/voicebotBuilderReplaceDic");
        view.addObject("queryString",TimeQuery.getQueryString());
        return view;
    }

    @ResponseBody
    @RequestMapping(value = "/vbBuilderIntentionOrder")
    public ModelAndView voiceBotBuilderIntentionOrder(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
        ModelAndView view = new ModelAndView("voicebotBuilderContents/voiceBotBuilderIntentionOrder");
        view.addObject("queryString",TimeQuery.getQueryString());
        return view;
    }
    
//    음성봇 목록 조회
    @ResponseBody
    @RequestMapping(
            value = "/voiceBot/getSimpleBotList",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public List<Map> getSimpleBotList(@RequestBody Map request) throws Exception {
        List<Map> simpleBotList;
        try {
            String userId = (String) request.get("userId");
            String companyId = (String) request.get("companyId");
            String keyword = (String) request.get("keyword");
            String userAuthTy = (String) request.get("userAuthTy");

            if ( userAuthTy.equals("S") || userAuthTy.equals("A") ) {
                simpleBotList = simpleBotService.getSimpleBotListFromCompanyId(companyId, keyword);

            } else {
                simpleBotList = simpleBotService.getSimpleBotListFromUserId(userId, keyword);
            }

            return simpleBotList;
        } catch (Exception e) {
            logger.error("[getSimpleBotList] Exception:", e);
            return null;
        }
    }

//    음성봇 목록 추가
    @ResponseBody
    @RequestMapping(
            value = "/voiceBot/addScenario",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public int addScenario(@RequestBody Map request) throws Exception {
        int lang = Integer.parseInt((String) request.get("lang"));
        String userId = (String) request.get("userId");
        String companyId = (String) request.get("companyId");
        String name = (String) request.get("name");

        int simplebotId = -1;

        int nowNameCount = 0; //중복체크
        Map<String, Object> nameCountParam = new HashMap<>();
        nameCountParam.put("checkName",name);
        if (companyId.isEmpty()) {
            nameCountParam.put("checkId",userId);
            nameCountParam.put("checkP","USER_ID");
            nowNameCount = simpleBotService.checkScenarioName(nameCountParam);
        }else{
            nameCountParam.put("checkId",companyId);
            nameCountParam.put("checkP","COMPANY_ID");
            nowNameCount = simpleBotService.checkScenarioName(nameCountParam);
        }
        if(nowNameCount==0){ //중복이 없으면
            if (companyId.isEmpty()) {
                simplebotId = simpleBotService.createSimpleBot(userId, "", name, lang);
            } else {
                simplebotId = simpleBotService.createSimpleBot(userId, companyId, name, lang);
            }
        }
        return simplebotId;
    }

//    음성봇 목록 수정
    @ResponseBody
    @RequestMapping(
            value = "/voiceBot/modifyScenario",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public int modifyScenario(@RequestBody Map request) throws Exception {
        String userId = (String) request.get("userId");
        String companyId = (String) request.get("companyId");
        String name = (String) request.get("name"); //바꾸려는 이름
        int simplebotId = Integer.parseInt((String) (request.get("simplebotId"))); //이름을 바꾸려는 시나리오 id

        int nowNameCount = 0; //중복체크
        Map<String, Object> nameCountParam = new HashMap<>();
        nameCountParam.put("checkName",name);
        if (companyId.isEmpty()) {
            nameCountParam.put("checkId",userId);
            nameCountParam.put("checkP","USER_ID");
            nowNameCount = simpleBotService.checkScenarioName(nameCountParam);
        }else{
            nameCountParam.put("checkId",companyId);
            nameCountParam.put("checkP","COMPANY_ID");
            nowNameCount = simpleBotService.checkScenarioName(nameCountParam);
        }
        if(nowNameCount==0){ //중복이 없으면
            Map<String, Object> updateNameParam = new HashMap<>();
            updateNameParam.put("newName", name);
            updateNameParam.put("simplebotId", simplebotId);
            simpleBotService.updateScenarioName(updateNameParam);
        } else { //중복이 있으면
            simplebotId = -1;
        }
        return simplebotId;
    }

    //    음성봇 목록 삭제 (host가 없을 경우)
    @ResponseBody
    @RequestMapping(
            value = "/voiceBot/deleteBySimplebotIdV2",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public void deleteBySimplebotIdV2(@RequestBody Map request) throws Exception {
        try {
            int simplebotId = (Integer) request.get("simplebotId");
            simpleBotService.deleteBySimplebotIdV2(simplebotId);
        } catch (Exception e) {
            logger.error("[deleteBySimplebotIdV2] Exception:", e);
        }
    }

    //    음성봇 목록 삭제 (host가 있을 경우)
    @ResponseBody
    @RequestMapping(
            value = "/voiceBot/deleteByHostV2",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public void deleteByHostV2(@RequestBody Map request) throws Exception {
        try {
            int host = (Integer) request.get("host");
            int simplebotId = (Integer) request.get("simplebotId");
            int lang = (Integer) request.get("lang");

            Map<String, Object> deleteParam = new HashMap<>();
            deleteParam.put("host", host);
            deleteParam.put("simplebotId", request.get("simplebotId"));
            deleteParam.put("lang", request.get("lang"));

            // DELETE Accout, BackendInfo, style_css, ReplaceDict
            simpleBotService.deleteAllByHostV2(deleteParam);

            // @TODO : mysql transaction 없음. -> ChainedTransactionManager
            // DELETE IntentRel, Intent(task), Answer, BertIntent, RegexIntent, RegexRule, SIMPLEBOT
            simpleBotService.deleteByHostV2(host, simplebotId, lang);

        } catch (Exception e) {
            logger.error("[deleteByHostV2] Exception:", e);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/voiceBot/insertIntention")
    public Map<String, Object> insertIntentionV2(@RequestBody Map param, HttpServletRequest request,
                                               HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<>();
        int checkIntent = builderService.checkBertIntent(param);
        if (checkIntent == 0) {
            int newIntentPK = builderService.insertIntentionV2(param);
            map.put("result", "success");
            map.put("intentNo", newIntentPK);

        } else {
            map.put("result", "fail");
        }
        return map;
    }

    @ResponseBody
    @RequestMapping(value = "/voiceBot/getIntention")
    public Map<String, Object> getIntention(@RequestBody Map param, HttpServletRequest request,
                                            HttpServletResponse response)
            throws Exception {

        // 챗봇목록 선택된 챗봇 세션에 저장
        HttpSession session = request.getSession();
        session.setAttribute("host", param.get("host"));
        Map<String, Object> map = new HashMap<>();
        int channelId = Integer.parseInt(param.get("host").toString());
        List<Map<String, Object>> intentStcList = builderService.getIntentStcListV2(param);
        List<Category> categories;
        List<Answer> answerList;
        List<Answer> finalResult = new ArrayList<>();
        List<HashMap<String, String>> searchList = new ArrayList<>();

        if(!PropInfo.nqaYN.equals("N")) {
            Category category = new Category();
            try {
                category.setChannelId(channelId);
                category.setName("공통");
                categories = nqaGrpcService.getCategoryListByName(category);
                if (categories.size() > 0) {
                    int categoryId = categories.get(0).getId();
                    answerList = nqaGrpcService
                            .getQAsetByCategory(categoryId);
                    for (int j = 0; j < intentStcList.size(); j++) {
                        for (int k = 0; k < answerList.size(); k++) {
                            Map<String, Object> questionMap;
                            questionMap = intentStcList.get(j);
                            questionMap.put("categoryId", categoryId);
                            intentStcList.set(j, questionMap);
                            if (intentStcList.get(j).get("Name").equals(answerList.get(k).getAnswer()) &&
                                    answerList.get(k).getQuestions().size() != 0
                                    && answerList.get(k).getQuestions().get(0).getQuestion() != null) {
                                questionMap = intentStcList.get(j);
                                questionMap.put("question", answerList.get(k).getQuestions().get(0).getQuestion());
                                questionMap.put("answerId", answerList.get(k).getId());
                                questionMap.put("categoryId", categoryId);
                                intentStcList.set(j, questionMap);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("{} => ", e.getMessage(), e);
            }
        }

        map.put("intentStcList", intentStcList);
        map.put("searchIntent", param.get("searchIntent"));

        return map;
    }

    @ResponseBody
    @RequestMapping(value = "/voiceBot/getIntentNo")
    public int voiceBotGetIntentNo(@RequestBody Map param, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        return builderService.getIntentNo(param);
    }

    @ResponseBody
    @RequestMapping(value = "/voiceBot/selectIntent")
    public List<Map<String, Object>> voiceBotSelectIntent(@RequestBody Map param, HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        return builderService.selectIntention(param);
    }

    @ResponseBody
    @RequestMapping(value = "/voiceBot/deleteByIntentNo")
    public void deleteByIntentNo(@RequestBody Map param, HttpServletRequest request, HttpServletResponse response)
    throws Exception {
        int no = builderService.getIntentNo(param);

        try {
            int host = Integer.parseInt((String) param.get("host"));
            int lang = (Integer) param.get("lang");

            simpleBotService.deleteByIntentNo(host, lang, no);

        } catch (Exception e) {
            logger.error("[deleteByIntentNo] Exception:", e);
        }

    }
    
    @ResponseBody
    @RequestMapping(
            value = "/voiceBot/saveScenario",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity saveScenario(@RequestBody Map request) throws Exception {
        	int simplebotId = Integer.parseInt(request.get("simplebotId").toString());
        	String userId = (String) request.get("userId");
    		String companyId = (String) request.get("companyId");
        String scenarioJson = (String)request.get("scenarioJson");
        String isExcelUpload = (String)request.get("isExcelUpload");
        String campaignInfoObj = (String)request.get("campaignInfoObj") != null ? (String)request.get("campaignInfoObj")  : "{'updateCheck':'','oldTaskName':'','newTaskName':'','successYn':''}";

    		ResponseEntity response =
    				new ResponseEntity(
    						simpleBotService.applyScenarioV2(simplebotId, userId, companyId, scenarioJson, isExcelUpload, campaignInfoObj),
    						HttpStatus.OK);

        return response;
    }
    
    // 치환 사전 List 조회
    @ResponseBody
    @RequestMapping(
    		value = "/voiceBot/getReplaceDictList",
    		method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public Map<String, Object> getReplaceDictList(@RequestBody Map param, HttpServletRequest request,HttpServletResponse response) throws Exception{
    	
    	
    	PagingVO pagingVO = new PagingVO();
        pagingVO.setCOUNT_PER_PAGE("100");
        pagingVO.setTotalCount(builderService.getReplaceDictCount(param));
        pagingVO.setCurrentPage(param.get("cp").toString());
        param.put("startRow", pagingVO.getStartRow());
        param.put("endRow", pagingVO.getCOUNT_PER_PAGE());
    	
        List<Map<String,Object>> replaceDictList = builderService.getReplaceDictLst(param);
    	
    	Map<String, Object> replaceDictMap = new HashMap<>();
    	
    	replaceDictMap.put("replaceDictList", replaceDictList);
    	replaceDictMap.put("paging", pagingVO);
    	
    	return replaceDictMap;
    }
    	
    //  치환사전 추가
    @ResponseBody
    @RequestMapping(
    		value = "/voiceBot/addReplaceDict",
    		method = RequestMethod.POST,
    		consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
    		produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public int addReplaceDict(@RequestBody Map param) throws Exception {
    	
    	
    	int addReplaceDictResult = builderService.addReplaceDict(param);

    	return addReplaceDictResult;
    }
    //  치환사전 수정
    @ResponseBody
    @RequestMapping(
    		value = "/voiceBot/updateReplaceDict",
    		method = RequestMethod.POST,
    		consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
    		produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    		)
    public int updateReplaceDict(@RequestBody Map param) throws Exception {
    	
    	int updateReplaceDictResult = builderService.updateReplaceDict(param);
    	
    	return updateReplaceDictResult;
    }
    
    //  치환사전 삭제
    @ResponseBody
    @RequestMapping(
    		value = "/voiceBot/deleteReplaceDict",
    		method = RequestMethod.POST,
    		consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
    		produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    		)
    public int deleteReplaceDict(@RequestBody Map param) throws Exception {
    	
    	int deleteReplaceDictResult = builderService.deleteReplaceDict(param);
    	
    	return deleteReplaceDictResult;
    }
    
  //  음성봇 목록 삭제
  @ResponseBody
  @RequestMapping(
          value = "/voiceBot/saveCustTestData",
          method = RequestMethod.POST,
          consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
          produces = MediaType.APPLICATION_JSON_UTF8_VALUE
  )
  public void saveCustTestData(@RequestBody Map request) throws Exception {
      try {
    	  int simplebotId = Integer.parseInt(request.get("simplebotId").toString());
          String custData =  request.get("custData").toString();

          simpleBotService.saveTestCustData(simplebotId, custData);

      } catch (Exception e) {
          logger.error("[saveCustTestData] Exception:", e);
      }
  }

  //  음성봇 테스트 디버그
  @ResponseBody
  @RequestMapping(
		  value = "/voiceBot/testLogDebug",
		  method = RequestMethod.POST,
		  consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
		  produces = MediaType.APPLICATION_JSON_UTF8_VALUE
		  )
  public Object testLogDebug(@RequestBody String request) throws Exception {
	Gson gson = new Gson();
	String testLogDebugIntent = "http://localhost:6941/collect/run/intent/detail";
	 try {
 		HttpClient client = HttpClientBuilder.create().build();
 		HttpPost post = new HttpPost(testLogDebugIntent);
 		post.setEntity(
 			new StringEntity(request, Consts.UTF_8)
 		);
 		post.setHeader("Content-Type", "application/json");

 		HttpResponse response = client.execute(post);
 	      if (response.getStatusLine().getStatusCode() == 200) {
 	    	ResponseHandler<String> handler = new BasicResponseHandler();
				String body = handler.handleResponse(response);
 	    }
   } catch (IOException e) {
       logger.info("<err>".concat(e.getMessage()));
       return e;
   }
	
    String testLogDebug = "http://localhost:6941/collect/run/utter/detail";
    try {
    		HttpClient client = HttpClientBuilder.create().build();
    		HttpPost post = new HttpPost(testLogDebug);
    		post.setEntity(
    			new StringEntity(request, Consts.UTF_8)
    		);
    		post.setHeader("Content-Type", "application/json");

    		HttpResponse response = client.execute(post);
    	      if (response.getStatusLine().getStatusCode() == 200) {
    	    	ResponseHandler<String> handler = new BasicResponseHandler();
  				String body = handler.handleResponse(response);
    	        return body;
    	    }
      } catch (IOException e) {
          logger.info("<err>".concat(e.getMessage()));
          return e;
      }
      return null;
  }
  //  음성봇 목록 삭제
  @ResponseBody
  @RequestMapping(
		  value = "/voiceBot/getIntentTaskDetail",
		  method = RequestMethod.POST,
		  consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
		  produces = MediaType.APPLICATION_JSON_UTF8_VALUE
		  )
  public List<Map<String, Object>> getIntentTaskDetail(@RequestBody Map request) throws Exception {
	  List<Map<String,Object>> getIntentTaskDetailList = builderService.getIntentTaskDetail(request);

	  return getIntentTaskDetailList;
  }

    @ResponseBody
    @RequestMapping(value = "/voiceBot/getRegexList")
    public Map<String, Object> getRegexListV2(@RequestBody Map param, HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> regexList = builderService.getRegexListV2(param);

        map.put("regexList", regexList);
        map.put("searchSentence", param.get("searchSentence"));
        return map;

    }

    @ResponseBody
    @RequestMapping(value = "/voiceBot/getRegexDetail")
    public Map<String, Object> getRegexDetail(@RequestBody Map param, HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> regexDetail = builderService.getRegexDetail(param);

        map.put("regexDetail", regexDetail);
        return map;

    }

    @ResponseBody
    @RequestMapping(value = "/voiceBot/insertRegex")
    public Map<String, Object> insertRegex(@RequestBody List<Map<String, Object>> params, HttpServletRequest request, HttpServletResponse response)
    throws Exception {
        Map<String, Object> map = new HashMap<>();

        int dupRegexCnt = 0;
        String regex = "";

        for (Map<String, Object> param : params) {
            int checkRegex = builderService.checkRegex(param);

            if (checkRegex == 0) { //중복 없음
                if (param.get("isInsert").equals("Y")) {
                    builderService.insertRegex(param);
                } else {
                    builderService.updateRegex(param);
                }

            } else { //중복 있음
                regex += (String) param.get("regex") + "//";
                dupRegexCnt += 1;
            }
        }

        map.put("totcnt", params.size());
        map.put("dupRegexCnt", dupRegexCnt);
        map.put("dupRegex", regex);

        return map;
    }

    @ResponseBody
    @RequestMapping(value = "/voiceBot/deleteRegexRule")
    public Map<String, Object> deleteRegexRule(@RequestBody List<Map> params, HttpServletRequest request,
                                               HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<>();

        for (Map<String, Object> param : params){
            builderService.deleteRegexRule(param);
        }

        return map;
    }
    
    @ResponseBody
    @RequestMapping(value = "/voiceBot/getIntentList")
    public List<Map<String, Object>> getIntentList(@RequestBody Map<String,Object> params, HttpServletRequest request,
    		HttpServletResponse response) throws Exception {
    	String host = params.get("host").toString();
    	
    	List<Map<String, Object>> getIntentList = builderService.getIntentList(host);
    	
    	return getIntentList;
    }
    
    @ResponseBody
    @RequestMapping(value = "/voiceBot/getTestIntent")
    public List<Map<String, Object>> getTestIntent(@RequestBody Map<String,Object> params, HttpServletRequest request,
    		HttpServletResponse response) throws Exception {
    	
    	List<Map<String, Object>> getIntentList = builderService.getAllIntents(params);
    	
    	return getIntentList;
    }

    @ResponseBody
    @RequestMapping(value = "/voiceBot/getRegexListAll")
    public Map<String, Object> getRegexListAll(@RequestBody Map param, HttpServletRequest request, HttpServletResponse response)
      throws Exception {

        Map<String, Object> map = new HashMap<>(); //getRegexListV2
        List<Map<String, Object>> regexList = builderService.getRegexListAll(param);

        map.put("regexList", regexList);
        return map;
    }

    @ResponseBody
    @RequestMapping(value = "/voiceBot/updateOrderRegex")
    public Map<String, Object> updateOrderRegex(@RequestBody List<Map<String, Object>> params, HttpServletRequest request, HttpServletResponse response)
      throws Exception {
        Map<String, Object> map = new HashMap<>();

        try {
            map = builderService.deleteRegexAllByHostV2(params);
        } catch (Exception e) {
            logger.error("[updateOrderRegex] Exception:", e);
        }

        return map;
    }
}