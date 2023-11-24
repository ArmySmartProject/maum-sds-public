package first.builder.controller;

import com.google.protobuf.Empty;
import first.builder.client.DelCacheClient;
import first.builder.service.BuilderService;
import first.builder.service.NQAGrpcService;
import first.builder.vo.*;
import first.common.util.*;
import io.grpc.stub.StreamObserver;
import maum.brain.qa.nqa.Admin;
import maum.brain.qa.nqa.Nqa;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;


@Controller
public class BuilderController {

  @Resource(name = "builderService")
  private BuilderService builderService;

  private static final Logger logger = LoggerFactory.getLogger(NQAChannelController.class);

  @Autowired
  private NQAGrpcService nqaGrpcService;

  @ResponseBody
  @RequestMapping(value = "/excel")
  public ModelAndView forwardExcel(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    ModelAndView view = new ModelAndView("/uploadExcel");
    return view;

  }

  @ResponseBody
  @RequestMapping(value = "/image")
  public ModelAndView forwardImage(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    ModelAndView view = new ModelAndView("/uploadImage");
    return view;

  }

  @ResponseBody
  @RequestMapping(value = "/bertExcel")
  public ModelAndView forwardBertExcel(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    ModelAndView view = new ModelAndView("/uploadBertExcel");
    return view;

  }

  @ResponseBody
  @RequestMapping(value = "/nowQueryString")
  public String nowQueryString(HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    return TimeQuery.getQueryString();
  }

  @ResponseBody
  @RequestMapping(value = "/searchChat")
  public Map<String, Object> searchChat(@RequestBody Map param, HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    String botIds = (String) param.get("chatList");
    List<String> list = new ArrayList<>();
    String[] botList = botIds.split("}");
    for (int i=0; i<botList.length; i++) {
      String botId = botList[i].replaceAll("[^0-9]", "");
      if (botId != null && !botId.equals("")) {
        list.add(botId);
      }
    }

    List<Map<String, Object>> chatList = null;
    param.put("chatList", list);
    try{
      chatList = builderService.getChatbotList(param);
    }catch (Exception e){
      System.out.println("E ->" + e);
    }
    param.put("chatList", chatList);
    return param;

  }

  @ResponseBody
  @RequestMapping(value = "/getIntention")
  public Map<String, Object> getIntention(@RequestBody Map param, HttpServletRequest request,
      HttpServletResponse response)
      throws Exception {

    // 챗봇목록 선택된 챗봇 세션에 저장
    HttpSession session = request.getSession();
    session.setAttribute("host", param.get("host"));
    Map<String, Object> map = new HashMap<>();
    PagingVO pagingVO = new PagingVO();
    pagingVO.setCOUNT_PER_PAGE("15");
    pagingVO.setTotalCount(builderService.getIntentCount(param));
    pagingVO.setCurrentPage(param.get("cp").toString());
    param.put("startRow", pagingVO.getStartRow());
    param.put("endRow", pagingVO.getCOUNT_PER_PAGE());
    int channelId = Integer.parseInt(param.get("host").toString());
    List<Map<String, Object>> intentStcList = builderService.getIntentStcList(param);
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
              Map<String, Object> questionMap = new HashMap<>();
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
    map.put("paging", pagingVO);
    map.put("searchIntent", param.get("searchIntent"));

    return map;

  }

  @ResponseBody
  @RequestMapping(value = "/getIntentionDetail")
  public Map<String, Object> intentionDetailMain(@RequestBody Map param, HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    Map<String, Object> map = new HashMap<>();
    PagingVO pagingVO = new PagingVO();
    pagingVO.setCOUNT_PER_PAGE("15");
    pagingVO.setTotalCount(builderService.getSentenceCount(param));
    pagingVO.setCurrentPage(param.get("cp").toString());
    param.put("startRow", pagingVO.getStartRow());
    param.put("endRow", pagingVO.getCOUNT_PER_PAGE());
    List<Map<String, Object>> sentenceList = builderService.getSentenceList(param);

    map.put("sentenceList", sentenceList);
    map.put("paging", pagingVO);
    map.put("searchSentence", param.get("searchSentence"));
    return map;

  }

  @ResponseBody
  @RequestMapping(value = "/deleteIntent")
  public Map<String, Object> deleteIntent(@RequestBody Map param, HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    Map<String, Object> map = new HashMap<>();
    map = builderService.getChatLang(param);
    String lang = (String) map.get("Language");
    String langList[] = lang.split(",");
    param.put("langList", langList);
    builderService.deleteIntention(param);

    return map;

  }

  @ResponseBody
  @RequestMapping(value = "/getRegexList")
  public Map<String, Object> getRegexList(@RequestBody Map param, HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    Map<String, Object> map = new HashMap<>();
    PagingVO pagingVO = new PagingVO();
    pagingVO.setCOUNT_PER_PAGE("15");
    pagingVO.setTotalCount(builderService.getRegexCount(param));
    pagingVO.setCurrentPage(param.get("cp").toString());
    param.put("startRow", pagingVO.getStartRow());
    param.put("endRow", pagingVO.getCOUNT_PER_PAGE());
    List<Map<String, Object>> regexList = builderService.getRegexList(param);

    map.put("regexList", regexList);
    map.put("paging", pagingVO);
    map.put("searchSentence", param.get("searchSentence"));
    return map;

  }

  @ResponseBody
  @RequestMapping(value = "/getRegexDetail")
  public Map<String, Object> getRegexDetail(@RequestBody Map param, HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    Map<String, Object> map = new HashMap<>();
    List<Map<String, Object>> regexDetail = builderService.getRegexDetail(param);

    map.put("regexDetail", regexDetail);
    return map;

  }

  @ResponseBody
  @RequestMapping(value = "/insertRegex")
  public Map<String, Object> insertRegex(@RequestBody Map param, HttpServletRequest request,
      HttpServletResponse response)
      throws Exception {

    Map<String, Object> map = new HashMap<>();
    int checkRegex = builderService.checkRegex(param);
    if (checkRegex == 0) {
      if (param.get("isInsert").equals("Y")) {
        builderService.insertRegex(param);
      } else {
        builderService.updateRegex(param);
      }
      map.put("result", "success");
    } else {
      map.put("result", "fail");
    }
    return map;

  }

  @ResponseBody
  @RequestMapping(value = "/chatLog")
  public ModelAndView chatLogList(Criteria cri) throws Exception {

    ModelAndView mv = new ModelAndView("/chatbotLog");

    PageMaker pageMaker = new PageMaker();
    pageMaker.setCri(cri);
    pageMaker.setPageNum(cri.getPage());
    pageMaker.setTotalCount(builderService.countChatLog());

    cri.setPerPageNum(cri.getPerPageNum() + cri.getPageStart());
    List<LogVO> list = builderService.selectChatLogList(cri);
    for(int i=0; i<list.size(); i++) {
      List<LogVO> flowNo = builderService.selectFlowNo(list.get(i).getSession());
      int cnt = 1;
      for (int j=0; j<flowNo.size(); j++) {
        if (!flowNo.get(j).getCreateDate().equals(list.get(i).getCreateDate())) {
          cnt ++;
        } else {
          list.get(i).setFlowNo(cnt);
        }
      }
    }

    mv.addObject("list", list);
    mv.addObject("pageMaker", pageMaker);

    return mv;

  }

  @ResponseBody
  @RequestMapping(value = "/insertChatExcel", method = RequestMethod.POST)
  public ModelAndView insertChatExcel(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    Map<String, Object> map = new HashMap<>();
    String host = request.getParameter("host");
    map.put("host", host);
    MultipartFile excelFile = ((MultipartHttpServletRequest) request).getFile("excelFile");
    System.out.println("엑셀 파일 업로드 컨트롤러");
    if (excelFile == null || excelFile.isEmpty()) {
      throw new RuntimeException("엑셀파일을 선택 해 주세요.");
    }

    File destFile = new File(
        request.getSession().getServletContext().getInitParameter("excelPath") + excelFile
            .getOriginalFilename());
    try {
      excelFile.transferTo(destFile);
    } catch (IllegalStateException | IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
    String result = builderService.chatExcelUpload(destFile, map);

    destFile.delete();

    ModelAndView view = new ModelAndView("jsonView");
    view.addObject("status", result);
    return view;

  }
  @ResponseBody
  @RequestMapping(value = "/insertChatExcelHTask", method = RequestMethod.POST)
  public ModelAndView insertChatExcelHTask(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    Map<String, Object> map = new HashMap<>();
    String host = request.getParameter("host");
    map.put("host", host);
    MultipartFile excelFile = ((MultipartHttpServletRequest) request).getFile("excelFile");
    System.out.println("엑셀 파일 업로드 컨트롤러");
    if (excelFile == null || excelFile.isEmpty()) {
      throw new RuntimeException("엑셀파일을 선택 해 주세요.");
    }

    File destFile = new File(
        request.getSession().getServletContext().getInitParameter("excelPath") + excelFile
            .getOriginalFilename());
    try {
      excelFile.transferTo(destFile);
    } catch (IllegalStateException | IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
    String result = builderService.chatExcelUploadHTask(destFile, map);

    destFile.delete();

    ModelAndView view = new ModelAndView("jsonView");
    view.addObject("status", result);
    return view;

  }

  @ResponseBody
  @RequestMapping(value = "/insertExcel", method = RequestMethod.POST)
  public ModelAndView insertExcel(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    Map<String, Object> map = new HashMap<>();
    String account = request.getParameter("account");
    map.put("account", account);
    MultipartFile excelFile = ((MultipartHttpServletRequest) request).getFile("excelFile");
    System.out.println("엑셀 파일 업로드 컨트롤러");
    if (excelFile == null || excelFile.isEmpty()) {
      throw new RuntimeException("엑셀파일을 선택 해 주세요.");
    }

    File destFile = new File(
        request.getSession().getServletContext().getInitParameter("excelPath") + excelFile
            .getOriginalFilename());
    try {
      excelFile.transferTo(destFile);
    } catch (IllegalStateException | IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }

    builderService.excelUpload(destFile, map);

    destFile.delete();

    ModelAndView view = new ModelAndView("jsonView");
    view.addObject("status", "OK");
    return view;

  }

  @ResponseBody
  @RequestMapping(value = "/insertBertExcel", method = RequestMethod.POST)
  public ModelAndView insertBertExcel(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    Map<String, Object> param = new HashMap<>();
    param.put("account", request.getParameter("host"));
    MultipartFile excelFile = ((MultipartHttpServletRequest) request).getFile("excelFile");
    System.out.println("엑셀 파일 업로드 컨트롤러");
    if (excelFile == null || excelFile.isEmpty()) {
      throw new RuntimeException("엑셀파일을 선택 해 주세요.");
    }

    File destFile = new File(
        request.getSession().getServletContext().getInitParameter("excelPath") + excelFile
            .getOriginalFilename());
    try {
      excelFile.transferTo(destFile);
    } catch (IllegalStateException | IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }

    builderService.bertExcelUpload(destFile, param);

    destFile.delete();

    ModelAndView view = new ModelAndView("jsonView");
    view.addObject("status", "OK");
    return view;

  }

  @ResponseBody
  @RequestMapping(value = "/insertBackendInfo", method = RequestMethod.POST)
  public ModelAndView insertBackendInfo(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    Map<String, Object> map = new HashMap<>();
    map.put("host", request.getParameter("host"));
    map.put("service", request.getParameter("service"));
    map.put("ip", request.getParameter("ip"));
    map.put("port", request.getParameter("port"));

    builderService.insertBackendInfo(map);

    ModelAndView view = new ModelAndView("jsonView");
    view.addObject("status", "OK");
    return view;

  }

  @ResponseBody
  @RequestMapping(value = "/imageUrl", method = RequestMethod.POST)
  public ModelAndView uploadImage(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    MultipartFile image = ((MultipartHttpServletRequest) request).getFile("imageFile");
    File destFile = new File(
        request.getSession().getServletContext().getInitParameter("imagePath") +
            image.getOriginalFilename());
    try {
      image.transferTo(destFile);
    } catch (IllegalStateException | IOException e) {
      System.out.println(e);
      throw new RuntimeException(e.getMessage(), e);
    }

    String ip = request.getSession().getServletContext().getInitParameter("serverIp");
    int port = request.getServerPort();

    ModelAndView view = new ModelAndView("jsonView");
    view.addObject("status", "OK");
    view.addObject("imgName", image.getOriginalFilename());
    view.addObject("hostAddr", ip + ":" + port);
    return view;

  }

  @RequestMapping(value = "/logExcelDownload")
  public void excel(HttpServletResponse response) throws Exception {
    builderService.selectChatLogListExcel(response);
  }



  @ResponseBody
  @RequestMapping(value = "/answer")
  public ModelAndView forwardAnswer(HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    String host = "";
    host = request.getParameter("host");
    if (host == null) {
      host = "";
    }
    request.setAttribute("host", "");
    HttpSession session = request.getSession();
    List<String> list = (List<String>) session.getAttribute("chatList");
    Map<String, Object> map = new HashMap<>();
    map.put("chatList", list);
    List<Map<String, Object>> chatList = null;
    chatList = builderService.getChatbotList(map);

    ModelAndView view = new ModelAndView("/chatbotBuilderAnswer");
    view.addObject("chatList", chatList);
    view.addObject("host", host);
    
    
    return view;

  }



  @ResponseBody
  @RequestMapping(value = "/getTask")
  public Map<String, Object> getTask(@RequestBody Map param, HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    HttpSession session = request.getSession();
    session.setAttribute("host", param.get("host"));
    Map<String, Object> map = new HashMap<>();
    PagingVO pagingVO = new PagingVO();
    pagingVO.setCOUNT_PER_PAGE("15");
    pagingVO.setTotalCount(builderService.getTaskCount(param));
    pagingVO.setCurrentPage(param.get("cp").toString());
    param.put("startRow", pagingVO.getStartRow());
    param.put("endRow", pagingVO.getCOUNT_PER_PAGE());
    List<Map<String, Object>> answerList = builderService.getAnswerList(param);
    map.put("answerList", answerList);
    map.put("paging", pagingVO);
    map.put("searchSentence", param.get("searchSentence"));
    map.put("host",param.get("host"));


    List<Map<String, Object>> taskList = builderService.getAllIntents(param);
    map.put("taskList", taskList);
    List<Map<String, Object>> bertIntentList = builderService.getAllBertIntents(param);
    map.put("bertIntentList", bertIntentList);
    return map;
  }

  @ResponseBody
  @RequestMapping(value = "/getTaskSearch")
  public Map<String, Object> getTaskSearch(@RequestBody Map param, HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    HttpSession session = request.getSession();
    session.setAttribute("host", param.get("host"));
    Map<String, Object> map = new HashMap<>();
    PagingVO pagingVO = new PagingVO();
    pagingVO.setCOUNT_PER_PAGE("15");
    pagingVO.setTotalCount(builderService.getTaskCountSearch(param));
    pagingVO.setCurrentPage(param.get("cp").toString());
    param.put("startRow", pagingVO.getStartRow());
    param.put("endRow", pagingVO.getCOUNT_PER_PAGE());
    List<Map<String, Object>> answerList = builderService.getAnswerDetailSearch(param);
    map.put("answerList", answerList);
    map.put("paging", pagingVO);
    map.put("searchSentence", param.get("searchSentence"));
    map.put("host",param.get("host"));

    List<Map<String, Object>> taskList = builderService.getAllIntents(param);
    map.put("taskList", taskList);
    List<Map<String, Object>> bertIntentList = builderService.getAllBertIntents(param);
    map.put("bertIntentList", bertIntentList);
    return map;
  }


  @ResponseBody
  @RequestMapping(value = "/getDetailTask")
  public Map<String, Object> getDetailTask(@RequestBody Map param, HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    HttpSession session = request.getSession();
    session.setAttribute("intentNum", param.get("intentNum"));
    Map<String, Object> map = new HashMap<>();
    List<Map<String, Object>> answerList = builderService.getIntentDetail(param);
    map.put("answerList", answerList.get(0));
    param.put("langPara",answerList.get(0).get("Language"));
    param.put("mainPara",answerList.get(0).get("Main"));
    List<Map<String, Object>> otherLangList = builderService.getIntentDetailByMain(param);
    map.put("otherLang", otherLangList);
    param.put("destPar", param.get("intentNum"));
    List<Map<String, Object>> bertIntentList = builderService.getIntentRelInAnsDetail(param);
    map.put("bertIntentList", bertIntentList);
    return map;
  }

  @ResponseBody
  @RequestMapping(value = "/getSetting")
  public Map<String, Object> getSetting(@RequestBody Map param, HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    HttpSession session = request.getSession();
    session.setAttribute("host", param.get("host"));
    Map<String, Object> map = new HashMap<>();
    List<Map<String, Object>> answerList = builderService.getSettingVal(param);
    map.put("answerList", answerList.get(0));
    List<Map<String,Object>> cssList = builderService.getStyleCSS(param);
    if(cssList.size()>0){
      map.put("cssList", cssList.get(0));
    }
    return map;
  }

  @ResponseBody
  @RequestMapping(value = "/updateSupplier")
  public String updateSupplier(@RequestBody Map param, HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    builderService.updateStyleCSSSupplier(param);
    Map<String, Object> map = new HashMap<>();
    map.put("host", param.get("host"));
    List<Map<String,Object>> cssList = builderService.getStyleCSS(map);
    if(cssList.size()!=0) {
      String supplier = (String)cssList.get(0).get("supplier");
      int host = Integer.parseInt((String)cssList.get(0).get("supplier"));
      String textColor = (String)cssList.get(0).get("textColor");
      String mainColor = (String)cssList.get(0).get("mainColor");
      String logoSize = (String)cssList.get(0).get("logoSize");
      String posX = (String)cssList.get(0).get("posX");
      String posY = (String)cssList.get(0).get("posY");

      StyleCssInfo.remakeHostResource(host,textColor,mainColor, logoSize, posX, posY, supplier);
    }else{
      return "Failed";
    }

    return "Success";
  }



  @ResponseBody
  @RequestMapping(value = "/checkIntentRel")
  public Map<String, Object> checkIntentRel(@RequestBody Map param, HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    String[] intentRelText = param.get("intentRelText").toString().split("/");
    String whereStr = "";
    String nowPK = param.get("nowPK").toString();
    System.out.println("nowPK : " + nowPK);
    for(int i = 0; i<intentRelText.length; i++){
      String[] tmpIntentRel = intentRelText[i].split(",");
      whereStr += "(SrcIntent = " + tmpIntentRel[0] + " AND BertIntent = " + tmpIntentRel[1] + " AND DestIntent !="+ nowPK +" ) ";
      if(i!=intentRelText.length-1){whereStr += " OR ";}
    }
    try{
      Map<String, Object> map = new HashMap<>();
      map.put("whereStr",whereStr);
      List<Map<String, Object>> intentRelList = builderService.getIntentRelByAll(map);
      if(intentRelList.size()==0){
        map = new HashMap<>();
        map.put("retDestIntentName", "");
        return map;
      }
      else{
        map = new HashMap<>();
        map.put("intentNo",intentRelList.get(0).get("DestIntent"));
        String retDestIntentName = (String) builderService.getIntentNameByNo(map).get(0).get("Name");
        map = new HashMap<>();
        map.put("retDestIntentName", retDestIntentName);
        return map;
      }
    }catch (Exception e){
      Map<String, Object> maps = new HashMap<>();
      maps.put("retDestIntentName", "");
      return maps;
    }
  }



  @RequestMapping(
          value="/setImage/{host}/{filename}",
          method=RequestMethod.GET,
          produces = MediaType.IMAGE_JPEG_VALUE)
  @ResponseBody
  public byte[]  settingFile(
          @PathVariable("host") String host,
          @PathVariable("filename") String filename){
    InputStream is;
    try{
      File file = new File(StyleCssInfo.resourcesPath + "/" + host + "/" + filename.replace("-","."));
      is = new FileInputStream(file);

      return IOUtils.toByteArray(is);
    }catch (Exception e){
      System.out.println(e);
      try{
        File file = new File(StyleCssInfo.resourcesPath + "/Default/" + filename.replace("-","."));
        is = new FileInputStream(file);

        return IOUtils.toByteArray(is);
      }catch (Exception e2){
        System.out.println(e2);
      }
    }
    return null;
  }






  @ResponseBody
  @RequestMapping(value = "/chatbotBuilder")
  public ModelAndView chatbotBuilderCommon(HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    String host = "";
    host = request.getParameter("host");
    if (host == null) {
      host = "";
    }
    request.setAttribute("host", "");
    ModelAndView view = new ModelAndView("/chatbotBuilderCommon");
    view.addObject("host", host);
    view.addObject("queryString",TimeQuery.getQueryString());
    return view;
  }

  @ResponseBody
  @RequestMapping(value = "/getChatList")
  public Map<String, Object> getChatList(@RequestBody Map param, HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    String botIds = (String) param.get("chatList");
    List<String> list = new ArrayList<>();
    String[] botList = botIds.split("}");
    for (int i=0; i<botList.length; i++) {
      String botId = botList[i].replaceAll("[^0-9]", "");
      if (botId != null && !botId.equals("")) {
        list.add(botId);
      }
    }

    Map<String, Object> map = new HashMap<>();
//    List<Map<String, Object>>
    //TODO: fast와 연동시 iframe으로 account list 받아와야함 현재는 하드코딩
    List<Map<String, Object>> chatList = null;
    map.put("chatList", list);
    try{
      chatList = builderService.getChatbotList(map);
    }catch (Exception e){
      System.out.println("E ->" + e);
    }
    map.put("chatList", chatList);
    return map;
  }

  @ResponseBody
  @RequestMapping(value = "/builderContentsAnswer")
  public ModelAndView builderContentsAnswer(HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    ModelAndView view = new ModelAndView("/chatbotBuilderContents/chatbotBuilderContentsAnswer");
    view.addObject("queryString",TimeQuery.getQueryString());
    return view;
  }

  @ResponseBody
  @RequestMapping(value = "/builderContentsIntention")
  public ModelAndView builderContentsIntention(HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    ModelAndView view = new ModelAndView("/chatbotBuilderContents/chatbotBuilderContentsIntention");
    view.addObject("queryString",TimeQuery.getQueryString());
    return view;
  }

  @ResponseBody
  @RequestMapping(value = "/builderContentsIntentionDetail")
  public ModelAndView builderContentsIntentionDetail(HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    ModelAndView view = new ModelAndView("/chatbotBuilderContents/chatbotBuilderContentsIntentionDetail");
    view.addObject("queryString",TimeQuery.getQueryString());
    return view;
  }

  @ResponseBody
  @RequestMapping(value = "/builderContentsNewChatbot")
  public ModelAndView builderContentsNewChatbot(HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    ModelAndView view = new ModelAndView("/chatbotBuilderContents/chatbotBuilderContentsNewChatbot");
    List<Map<String, Object>> resultList = builderService.getDomain();
    view.addObject("domains",resultList);
    view.addObject("queryString",TimeQuery.getQueryString());
    return view;
  }

  @ResponseBody
  @RequestMapping(value = "/builderContentsSetting")
  public ModelAndView builderContentsSetting(HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    ModelAndView view = new ModelAndView("/chatbotBuilderContents/chatbotBuilderContentsSetting");
    view.addObject("queryString",TimeQuery.getQueryString());
    return view;
  }

  @ResponseBody
  @RequestMapping(value = "/builderContentsDeepLearning")
  public ModelAndView builderContentsDeepLearning(HttpServletRequest request, HttpServletResponse response)
    throws Exception {
    ModelAndView view = new ModelAndView("/chatbotBuilderContents/chatbotBuilderContentsDeepLearning");
    view.addObject("queryString",TimeQuery.getQueryString());
    return view;
  }

  @ResponseBody
  @RequestMapping(value = "/builderBottomAnswer")
  public ModelAndView builderBottomAnswer(HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    ModelAndView view = new ModelAndView("/chatbotBuilderContents/chatbotBuilderBottomAnswer");
    view.addObject("queryString",TimeQuery.getQueryString());
    return view;
  }

  @ResponseBody
  @RequestMapping(value = "/builderBottomNewChatbot")
  public ModelAndView builderBottomnewChatbot(HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    ModelAndView view = new ModelAndView("/chatbotBuilderContents/chatbotBuilderBottomNewChatbot");
    view.addObject("queryString",TimeQuery.getQueryString());
    return view;
  }

  @ResponseBody
  @RequestMapping(value = "/builderBottomIntention")
  public ModelAndView builderBottomIntention(HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    ModelAndView view = new ModelAndView("/chatbotBuilderContents/chatbotBuilderBottomIntention");
    view.addObject("queryString",TimeQuery.getQueryString());
    return view;
  }

  @ResponseBody
  @RequestMapping(value = "/builderBottomIntentionDetail")
  public ModelAndView builderBottomIntentionDetail(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    ModelAndView view = new ModelAndView("/chatbotBuilderContents/chatbotBuilderBottomIntentionDetail");
    view.addObject("queryString",TimeQuery.getQueryString());
    return view;
  }

  @ResponseBody
  @RequestMapping(value = "/builderBottomSetting")
  public ModelAndView builderBottomSetting(HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    ModelAndView view = new ModelAndView("/chatbotBuilderContents/chatbotBuilderBottomSetting");
    view.addObject("queryString",TimeQuery.getQueryString());
    return view;
  }

  @ResponseBody
  @RequestMapping(value = "/builderBottomDeepLearning")
  public ModelAndView builderBottomDeepLearning(HttpServletRequest request, HttpServletResponse response)
    throws Exception {
    ModelAndView view = new ModelAndView("/chatbotBuilderContents/chatbotBuilderBottomDeepLearning");
    view.addObject("queryString",TimeQuery.getQueryString());
    return view;
  }

  @ResponseBody
  @RequestMapping(value = "/getImageCarousel")
  public Map<String, Object> getImageCarousel(@RequestBody Map param, HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    HttpSession session = request.getSession();
    String[] carStrList = param.get("carStr").toString().split(",");
    String sessionTest = "";
    for(int ii = 0; ii<carStrList.length; ii++){
      sessionTest += "I.NO = " + carStrList[ii] + " ";
      if(ii!=carStrList.length-1) sessionTest += " OR ";
    }
    session.setAttribute("carStr", sessionTest);
    param.put("carStr",sessionTest);
    Map<String, Object> map = new HashMap<>();
    if(carStrList.length>0 && carStrList[0] != ""){
      List<Map<String, Object>> answerList = builderService.getImageCarousel(param);
      map.put("answerList", answerList);
    }

    return map;
  }


  @ResponseBody
  @RequestMapping(value = "/startSaveSetting" , consumes ={"multipart/form-data"})
  public String startSaveSetting(
          @RequestParam String host,
          @RequestParam String textColor,
          @RequestParam String mainColor,
          @RequestParam String logoSize,
          @RequestParam String posX,
          @RequestParam String posY
  )
          throws Exception {
    Map<String, Object> map = new HashMap<>();
    map.put("host", host);
    List<Map<String,Object>> cssList = builderService.getStyleCSS(map);
    String supplier = "";
    if(cssList.size()!=0) supplier = (String)cssList.get(0).get("supplier");
    StyleCssInfo.remakeHostResource(Integer.parseInt(host),textColor,mainColor, logoSize, posX, posY, supplier);

    cssList = builderService.getStyleCSS(map);
    map.put("textColor",textColor);
    map.put("mainColor",mainColor);
    map.put("logoSize",logoSize);
    map.put("posX",posX);
    map.put("posY",posY);
    if(cssList.size()>0){
      builderService.updateStyleCSS(map);
    }else{
      builderService.insertStyleCSS(map);
    }
    return "Success to start save ";
  }


  @ResponseBody
  @RequestMapping(value = "/saveSetting" , consumes ={"multipart/form-data"})
  public String saveSetting(
                                           @RequestParam String host,

                                           @RequestParam String name,
                                           @RequestParam String hostName,
                                           @RequestParam String email,
                                           @RequestParam String description
                              )
          throws Exception {


    //Account DB Update
    Map<String, Object> accountMap = new HashMap<>();
    accountMap.put("host",host);
    accountMap.put("name",name);
    accountMap.put("hostName",hostName);
    accountMap.put("email",email);
    accountMap.put("description",description);
    builderService.updateAccount(accountMap);

    return "Success to save";
  }

  @ResponseBody
  @RequestMapping(value = "/saveLogoImgSetting" , consumes ={"multipart/form-data"})
  public String saveLogoImgSetting(
                                           @RequestPart MultipartFile logoImg,
                                           @RequestParam String host
  )
          throws Exception {
    try {
      File dest = new File(StyleCssInfo.resourcesPath + "/" + host + "/logoImg");
      if(dest.exists()){
        dest.delete();
      }
      dest = new File(StyleCssInfo.resourcesPath + "/" + host + "/logoImg");
      logoImg.transferTo(dest);
    }catch (Exception e){System.out.println(e);}
    return "Success to save LogoImg";
  }

  @ResponseBody
  @RequestMapping(value = "/delLogoImgSetting" , consumes ={"multipart/form-data"})
  public String delLogoImgSetting(
          @RequestParam String logoIcon,
          @RequestParam String host
  )
          throws Exception {
    try {
      File dest = new File(StyleCssInfo.resourcesPath + "/" + host + "/" + logoIcon);
      if(dest.exists()){
        dest.delete();
      }
    }catch (Exception e){System.out.println(e);}
    return "Success to delete " + logoIcon;
  }

  @ResponseBody
  @RequestMapping(value = "/saveIconImgSetting" , consumes ={"multipart/form-data"})
  public String saveIconImgSetting(
                                           @RequestPart MultipartFile IconImg,
                                           @RequestParam String host
  )
          throws Exception {
    try {
      File dest = new File(StyleCssInfo.resourcesPath + "/" + host + "/IconImg");
      if(dest.exists()){
        dest.delete();
      }
      dest = new File(StyleCssInfo.resourcesPath + "/" + host + "/IconImg");
      IconImg.transferTo(dest);
    }catch (Exception e){System.out.println(e);}
    return "Success to save IconImg";
  }


  @ResponseBody
  @RequestMapping(value = "/setting")
  public ModelAndView forwardSetting(HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    ModelAndView view = new ModelAndView("/chatbotBuilderSetting");
    return view;
  }

  @ResponseBody
  @RequestMapping(value = "/settingNew")
  public ModelAndView forwardSettingNew(HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    ModelAndView view = new ModelAndView("/chatbotBuilderSettingNew");
    return view;
  }

  @ResponseBody
  @RequestMapping(value = "/intentionRegex")
  public ModelAndView intentionRegex(HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    ModelAndView view = new ModelAndView("/chatbotBuilderIntentionDetailRegex");
    return view;
  }

  @ResponseBody
  @RequestMapping(value = "/insertIntention")
  public Map<String, Object> insertIntention(@RequestBody Map param, HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Map<String, Object> map = new HashMap<>();
    map = builderService.getChatLang(param);
    String lang = (String) map.get("Language");
    String langList[] = lang.split(",");
    param.put("lang", langList[0]);
    param.put("langList", langList);
    int checkIntent = builderService.checkBertIntent(param);
    if (checkIntent == 0) {
      builderService.insertIntention(param);
      map.put("result", "success");
    } else {
      map.put("result", "fail");
    }
    return map;
  }

  @ResponseBody
  @RequestMapping(value = "/deleteRegexRule")
  public Map<String, Object> deleteRegexRule(@RequestBody Map param, HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Map<String, Object> map = new HashMap<>();
    builderService.deleteRegexRule(param);
    return map;
  }


  @ResponseBody
  @RequestMapping(value = "/getTaskCheck")
  public Map<String, Object> getTaskCheck(@RequestBody Map param, HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
    Map<String, Object> map = new HashMap<>();
    List<Map<String, Object>> answerList = builderService.getTaskCheck(param);
    map.put("answerList",answerList);
    return map;
  }

  @ResponseBody
  @RequestMapping(value = "/addModIntent")
  public Map<String, Object> addModIntent(@RequestBody Map param, HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    HttpSession session = request.getSession();
    List<HashMap<String,Object>> paramList = (List<HashMap<String,Object>>) param.get("inputList");
    String nowResponseOrder = (String) param.get("ResponseOrder");
    Map<String, Object> map;
    int insertedAnswer = 0;

    for(int i = 0; i<paramList.size(); i++){
      String main = (String) paramList.get(i).get("main");
      int lang = (Integer) paramList.get(i).get("lang");
      int nowModifyingPK = (Integer) paramList.get(i).get("nowModifyingPK");
      String taskName = (String) paramList.get(i).get("taskName");
      String answerText = ((String) paramList.get(i).get("answerText")).replace("\n","<br>");
      String nextText = (String) paramList.get(i).get("nextText");
      String imgCardImg = (String) paramList.get(i).get("imgCardImg");
      String intentRelText = (String) paramList.get(i).get("intentRelText");
      int host = (Integer) paramList.get(i).get("host");
      String description = (String) paramList.get(i).get("description");
      String customServerUrl = "";
      try{
        customServerUrl = (String) paramList.get(i).get("A_URL");
      }catch (Exception e){ // null일 때에
        customServerUrl = "";
      }

      if(lang==-2){
        continue;
      }else if(lang==-1){
        map = new HashMap<>();
        map.put("nowModifyingPK",nowModifyingPK);
        map.put("host",host);
        map.put("nowResponseOrder",nowResponseOrder);
        builderService.deleteOldIntentByLang(map);
        builderService.deleteOldIntentRel(map);
        continue;
      }


      insertedAnswer = 0;
      boolean nowNew = false;
      if(nowModifyingPK==-1){
        map = new HashMap<>();
        map.put("answerText",answerText);
        map.put("host",host);
        map.put("lang",lang);
        insertedAnswer = builderService.insertAnswerNew(map);
        map = new HashMap<>();
        map.put("lang",lang);
        map.put("taskMain",main);
        map.put("taskName",taskName);
        map.put("answerNoNew",insertedAnswer);
        map.put("nextText",nextText);
        map.put("imgCardImg",imgCardImg.equals("-1") ? "" : imgCardImg);
        map.put("description",description);
        map.put("host",host);
        map.put("Entity","N");
        map.put("greetyn",null);
        map.put("TmpNo", null);
        map.put("TmpNextIntent",null);
        map.put("nowResponseOrder",nowResponseOrder);
        nowModifyingPK = builderService.insertIntentNewAll(map);
        nowNew = true;
      }
      // 기존 Intent No 와 Answer 가져오기
      map = new HashMap<>();
      map.put("nowModifyingPK",nowModifyingPK);
      Map<String, Object> NoAnswer = builderService.selectIntentNoAnswer(map).get(0);
      String URLNew = "";
      try{
        if(imgCardImg.equals("")){
          URLNew = NoAnswer.get("URL").toString();
        }else if(imgCardImg.equals("-1")){
          URLNew = "";
        }else {
          URLNew = imgCardImg;
        }
      }catch(Exception e){
        System.out.println("URLNew Error");
        System.out.println(e);
        URLNew = "";
      }
      List<Map<String, Object>> oldBertIntentList = new ArrayList<>();
      // 상세 -> 수정
      if(!nowNew){
        // Answer 삽입 및 PK 가져오기
        map = new HashMap<>();
        map.put("answerText",answerText);
        map.put("host",host);
        map.put("lang",lang);
        map.put("customServerUrl",customServerUrl);
        insertedAnswer = builderService.insertAnswerNew(map);

        // Intent Update !
        map = new HashMap<>();
        map.put("taskMain",main);
        map.put("taskName",taskName);
        map.put("answerNoNew",insertedAnswer);
        map.put("nextText",nextText);
        map.put("URLNew",URLNew);
        map.put("description",description);
        map.put("nowModifyingPK",nowModifyingPK);
        map.put("nowResponseOrder",nowResponseOrder);
        builderService.updateOldIntent(map);

        // 기존 IntentRel 삭제
        map = new HashMap<>();
        map.put("nowModifyingPK",nowModifyingPK);
        oldBertIntentList = builderService.getBertIntentByDestIntent(map); // 기존 관계 리스트
        builderService.deleteOldIntentRel(map);
      }
      // IntentRel 추가
      if (intentRelText.length() > 1) {
        List<Map<String, Object>> bertLst = new ArrayList<>();
        bertLst.addAll(oldBertIntentList);

        String[] nowIntentRel = intentRelText.split(",");
        for(int ii = 0; ii<nowIntentRel.length; ii = ii + 2){
          Map<String, Object> addIntentRelMap = new HashMap<>();
          addIntentRelMap.put("SrcIntent",nowIntentRel[ii]);
          addIntentRelMap.put("BertIntent",nowIntentRel[ii+1]);
          addIntentRelMap.put("DestIntent",nowModifyingPK);
          builderService.insertIntentRelNew(addIntentRelMap);

          // intentRel update 전후 모든 BertIntent
          for(Map<String,Object> m : oldBertIntentList){
            if(!m.get("BertIntent").toString().equals(nowIntentRel[ii + 1])) bertLst.add(addIntentRelMap);
          }
        }

        // task 별 bert intent 정보 update
        List<Map<String, Object>> srcList = builderService.selectEachSrcList(bertLst);
        if (srcList != null && srcList.size() > 0) builderService.updateIntentTask(srcList);
      }
    }

    // Answer insert -> 태스크가 존재하지 않는 답변삭제
    Map<String, Object> tmpMap = new HashMap<>();
    tmpMap.put("host", paramList.get(0).get("host"));
    if (insertedAnswer > 0) {
      builderService.deleteNoAnswerFromIntent(tmpMap);
    }

    map = new HashMap<>();
    return map;
  }


  @ResponseBody
  @RequestMapping(value = "/delIntent")
  public String delIntent(@RequestBody Map param, HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    HttpSession session = request.getSession();
    Map<String, Object> map = new HashMap<>();
    List<Map<String, Object>> intentList = builderService.selectIntentBeforeDel(param);
    boolean checkIntent = false;
    List<String> delList = new ArrayList<>();
    for(int i = 0; i<intentList.size(); i++){
      if(param.get("intentPK").equals(intentList.get(i).get("No"))){
        checkIntent = true;
      }
      delList.add(intentList.get(i).get("No").toString());
    }

    if(checkIntent){
      for(int i = 0; i<delList.size(); i++){
        map = new HashMap<>();
        map.put("intentPK",delList.get(i));
        builderService.deleteIntentByNo(map);
      }
    }
    else{
      return "FALSE";
    }
    return "TRUE";
  }


  @ResponseBody
  @RequestMapping(value = "/getTestPage")
  public ModelAndView getTestPage(HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    int hostNum = Integer.parseInt(request.getParameter("host"));
    ModelAndView view = new ModelAndView("/chatbotBuilderContents/chatbotTestPage");
    Map<String, Object> accountCheckMap =   new HashMap<>();
    accountCheckMap.put("hostNum",hostNum);
    Map<String, Object> resultMap = builderService.getHostByAccount(accountCheckMap).get(0);
    view.addObject("hostName",resultMap.get("Host"));
    view.addObject("queryString",TimeQuery.getQueryString());
    return view;
  }

  @ResponseBody
  @RequestMapping(value = "/getNofloatTestPage")
  public ModelAndView getNofloatTestPage(HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    int hostNum = Integer.parseInt(request.getParameter("host"));
    ModelAndView view = new ModelAndView("/chatbotBuilderContents/chatbotNofloatTestPage");
    Map<String, Object> accountCheckMap =   new HashMap<>();
    accountCheckMap.put("hostNum",hostNum);
    Map<String, Object> resultMap = builderService.getHostByAccount(accountCheckMap).get(0);
    view.addObject("hostName",resultMap.get("Host"));
    view.addObject("queryString",TimeQuery.getQueryString());
    return view;
  }

  @ResponseBody
  @RequestMapping(value = "/checkHostname")
  public String checkHostname(@RequestBody Map param, HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    HttpSession session = request.getSession();
    Map<String, Object> map = new HashMap<>();
    List<Map<String, Object>> answerList = builderService.getHostName(param);
    return Integer.toString(answerList.size());
  }

  @ResponseBody
  @RequestMapping(value = "/newBertIntent")
  public String newBertIntent(@RequestBody Map param, HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    HttpSession session = request.getSession();
    Map<String, Object> map = new HashMap<>();
    List<Map<String, Object>> answerList = builderService.checkBertItfID(param);
    if(answerList.size()>1){
      return "BertItfID가 2개 이상 할당되어있습니다.";
    }else if(answerList.size()<1){
      return "BertItfID가 할당되지 않았습니다. ";
    }
    String[] langList = param.get("nowLang").toString().split(",");
    String addedBert = "";
    for(int i = 0; i<langList.length; i++){
      map = new HashMap<>();
      map.put("Name",param.get("keyword"));
      map.put("Language",langList[i]);
      map.put("BertItfId",answerList.get(0).get("id"));
      int newBert = builderService.insertBertIntentNow(map);
      addedBert += langList[i] +":" + Integer.toString(newBert);
      if(i!=langList.length-1){
        addedBert += ",";
      }
    }
    return ":TRUE:" + addedBert;

  }

  @RequestMapping(value = "/excelSampleDown")
  public void excelSampleDown(HttpServletRequest request, HttpServletResponse response,@RequestParam String host)
      throws Exception {
      try {
        InputStream downloadfile = new FileInputStream(request.getSession().getServletContext().getInitParameter("excelPath") + "sample/[chatbot] TASK_ANSWER_샘플데이터.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(downloadfile);
        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFSheet sheet2 = workbook.getSheetAt(1);
        XSSFSheet sheet3 = workbook.getSheetAt(2);
        XSSFSheet sheet4 = workbook.getSheetAt(3);
        XSSFSheet sheet5 = workbook.getSheetAt(4);

        Map<String, Object> map = new HashMap<>();
        map.put("host", host);
        map = builderService.getChatLang(map);
        String langList[];
        if (map == null) {
          langList = new String[]{"1","2","3","4"};
        } else {
          String lang = (String) map.get("Language");
          langList = lang.split(",");
        }

        for(int i = 0; i <= 3; i++) {
          int nowLang = 0;

          for(int j = 0; j<langList.length; j++){
            if(i+1 == Integer.parseInt(langList[j])){
              nowLang=0;
              break;
            }else{
              nowLang = i+1;
            }
          }

          if(nowLang == i+1) {
            if(nowLang == 2){
              nowLang = 4;
            }else if(nowLang == 4){
              nowLang = 2;
            }

            sheet.setColumnHidden(nowLang+1, true);
            sheet.setColumnHidden(nowLang+5, true);
            sheet.setColumnHidden(nowLang+9, true);
            sheet.setColumnHidden(nowLang+13, true);
            sheet.setColumnHidden(nowLang+17, true);

            sheet2.setColumnHidden(nowLang-1, true);

            sheet3.setColumnHidden((nowLang-1)*3, true);
            sheet3.setColumnHidden(1 + (nowLang-1)*3, true);
            sheet3.setColumnHidden(2 + (nowLang-1)*3, true);

            sheet4.setColumnHidden(nowLang-1, true);

            sheet5.setColumnHidden(nowLang-1, true);
            sheet5.setColumnHidden(nowLang+3, true);
            sheet5.setColumnHidden(nowLang+7, true);
          }
        }

        response.setContentType("ms-vnd/excel");
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");
        response.setHeader("Content-Disposition", "attachment;filename=[chatbot] TASK_ANSWER_샘플데이터.xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();

        response.getOutputStream().flush();
        response.getOutputStream().close();

        downloadfile.close();
      } catch (Exception e) {
        e.printStackTrace();
        response.setHeader("Set-Cookie", "fileDownload=false; path=/");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Content-Type", "text/html; charset=utf-8");
        OutputStream out = null;
        try {
          out = response.getOutputStream();
          byte[] data = "fail..".getBytes();
          out.write(data, 0, data.length);
        } catch (Exception ignore) {
          ignore.printStackTrace();
        } finally {
          if (out != null) {
            try {
              out.close();
            } catch (Exception ignore) {
            }
          }
        }
      }
  }

  private String check_browser(HttpServletRequest request) {
    String browser = "";
    String header = request.getHeader("User-Agent");
    //신규추가된 indexof : Trident(IE11) 일반 MSIE로는 체크 안됨
    if (header.indexOf("MSIE") > -1 || header.indexOf("Trident") > -1){
      browser = "ie";
    }
    //크롬일 경우
    else if (header.indexOf("Chrome") > -1){
      browser = "chrome";
    }
    //오페라일경우
    else if (header.indexOf("Opera") > -1){
      browser = "opera";
    }
    //사파리일 경우
    else if (header.indexOf("Apple") > -1){
      browser = "sarari";
    } else {
      browser = "firfox";
    }
    return browser;
  }

  private String getDisposition(String down_filename, String browser_check) throws UnsupportedEncodingException {
    String prefix = "attachment;filename=";
    String encodedfilename = null;
    System.out.println("browser_check:"+browser_check);
    if (browser_check.equals("ie")) {
      encodedfilename = URLEncoder.encode(down_filename, "UTF-8").replaceAll("\\+", "%20");
    }else if (browser_check.equals("chrome")) {
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < down_filename.length(); i++){
        char c = down_filename.charAt(i);
        if (c > '~') {
          sb.append(URLEncoder.encode("" + c, "UTF-8"));
        } else {
          sb.append(c);
        }
      }
      encodedfilename = sb.toString();
    }else {
      encodedfilename = "\"" + new String(down_filename.getBytes("UTF-8"), "8859_1") + "\"";
    }
    return prefix + encodedfilename;
  }

  @ResponseBody
  @RequestMapping(value = "/checkNewHostname")
  public String checkNewHostname(@RequestBody Map param, HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    HttpSession session = request.getSession();
    Map<String, Object> map = new HashMap<>();
    List<Map<String, Object>> answerList = builderService.getNewHostName(param);
    return Integer.toString(answerList.size());
  }



  @ResponseBody
  @RequestMapping(value = "/addNewChatbot")
  public int addNewChatbot(@RequestBody Map param, HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    //Account 추가
    int newChatbotPK = builderService.addChatbot(param);
    int nowCategory =  Integer.parseInt(param.get("nowCategory").toString());
    Map<String, Object> map = new HashMap<>();
    map.put("host",nowCategory);
    //BackendInfo 정보 가져오기
    List<Map<String, Object>> backendInfoList = builderService.getBackendInfo(map);
    //BackendInfo 추가
    Map<Integer,Integer> ITFNosList = new HashMap<>();
    for(int ii = 0; ii<backendInfoList.size(); ii++){
      map = new HashMap<>();
      map.put("host",newChatbotPK);
      map.put("service",backendInfoList.get(ii).get("service"));
      map.put("ip",backendInfoList.get(ii).get("ip"));
      map.put("port",backendInfoList.get(ii).get("port"));
      ITFNosList.put(Integer.parseInt(backendInfoList.get(ii).get("id").toString()),builderService.insertBackendInfos(map));
    }
    //Intent & Answer 모두 가져오기
    String[] langParSplit = ((String) param.get("langStr")).split(",");
    String nowLangPar = "";
    for(int ii = 0; ii<langParSplit.length; ii++){
      nowLangPar += "I.Language=" + langParSplit[ii] + " ";
      if(ii!=langParSplit.length-1){
        nowLangPar += " or ";
      }
    }
    map = new HashMap<>();
    map.put("host",nowCategory);
    map.put("langStr",nowLangPar);
    List<Map<String, Object>> intentAnswerList = builderService.getIntentAnswer(map);
    //IntentRel 가져오기
    String whereStr = "";
    for(int ii = 0; ii<intentAnswerList.size(); ii++){
      whereStr += "SrcIntent = "+intentAnswerList.get(ii).get("I_No")+" or DestIntent = " + intentAnswerList.get(ii).get("I_No");
      if(ii!=intentAnswerList.size()-1){
        whereStr += " or ";
      }
    }
    map = new HashMap<>();
    map.put("whereStr",whereStr);
    List<Map<String, Object>> intentRelList = builderService.getIntentRelByAll(map);
    //BertIntent 모두 가져오기
    whereStr = "(";
    for(int ii = 0; ii<backendInfoList.size(); ii++){
      whereStr += "BertItfId = " + backendInfoList.get(ii).get("id");
      if(ii!=backendInfoList.size()-1){
        whereStr += " or ";
      }
    }
    whereStr += ") AND (";
    for(int ii = 0; ii<langParSplit.length; ii++){
      whereStr += "Language=" + langParSplit[ii] + " ";
      if(ii!=langParSplit.length-1){
        whereStr += " or ";
      }
    }
    whereStr += ")";
    map = new HashMap<>();
    map.put("whereStr",whereStr);
    List<Map<String, Object>> bertIntentList = builderService.getBertIntentByNo(map);
    //이전 & 이후 Intent, BertIntent 가진 Map
    Map<Integer, Integer> intentBeforeAfter = new HashMap<>();
    Map<Integer, Integer> bertIntentBeforeAfter = new HashMap<>();
    //Intent & Answer 모두 추가 (각 이전No, 이후 No 변수에 추가)
    for(int ii = 0; ii<intentAnswerList.size(); ii++){
      map = new HashMap<>();
      map.put("answerText",intentAnswerList.get(ii).get("A_Answer"));
      map.put("host",newChatbotPK);
      map.put("lang",intentAnswerList.get(ii).get("A_Language"));
      map.put("TmpNo",intentAnswerList.get(ii).get("A_TmpNo"));
      int newAnswerPK = builderService.insertAnswerByAll(map);
      intentAnswerList.get(ii).put("newAnswerPK",newAnswerPK);
      map = new HashMap<>();
      map.put("lang",intentAnswerList.get(ii).get("I_Language"));
      map.put("taskMain",intentAnswerList.get(ii).get("Main"));
      map.put("taskName",intentAnswerList.get(ii).get("Name"));
      map.put("answerNoNew",newAnswerPK);
      map.put("nextText",intentAnswerList.get(ii).get("Next"));
      map.put("imgCardImg",intentAnswerList.get(ii).get("URL"));
      map.put("description",intentAnswerList.get(ii).get("Description"));
      map.put("host",newChatbotPK);
      map.put("Entity",intentAnswerList.get(ii).get("Entity") != null ? intentAnswerList.get(ii).get("Entity") : "N");
      map.put("greetyn",intentAnswerList.get(ii).get("greetyn"));
      map.put("TmpNo",intentAnswerList.get(ii).get("I_TmpNo"));
      map.put("TmpNextIntent",intentAnswerList.get(ii).get("TmpNextIntent"));
      map.put("nowResponseOrder", map.get("nowResponseOrder") == null ? "0,1,2" : map.get("nowResponseOrder"));
      int newIntentPK = builderService.insertIntentNewAll(map);
      intentAnswerList.get(ii).put("newIntentPK",newIntentPK);
      intentBeforeAfter.put(Integer.parseInt(intentAnswerList.get(ii).get("I_No").toString()),newIntentPK);
    }
    //BertIntent 모두 추가 (각 이전 No, 이후 No 변수에 추가)
    for(int ii = 0; ii<bertIntentList.size(); ii++){
      map = new HashMap<>();
      map.put("Name",bertIntentList.get(ii).get("Name"));
      map.put("Language",bertIntentList.get(ii).get("Language"));
      map.put("BertItfId",ITFNosList.get(Integer.parseInt(bertIntentList.get(ii).get("BertItfId").toString())));
      int newBertIntentPK = builderService.insertBertIntentNew(map);
      bertIntentList.get(ii).put("newBertIntentPK",newBertIntentPK);
      bertIntentBeforeAfter.put(Integer.parseInt(bertIntentList.get(ii).get("No").toString()),newBertIntentPK);
    }
    //ReplaceDict 추가
    Map<String, Object> rDict = new HashMap<>();
    rDict.put("nowHost",nowCategory);
    List<Map<String, Object>> replaceDictList = builderService.selectReplaceDict(rDict);
    String[]  dictDBColumn = {"Lang","Before","After","StringCode","IntCode1","IntCode2","IntCode3","IntCode4","IntCode5"};
    boolean[] dictDBColVal = { false, true,    true,   true,        false,     false,     false,     false,     false};
    List<String> dictQueryValues = new ArrayList<>();
    for(int ii = 0; ii<replaceDictList.size(); ii++){
      String dbValString = "(" + Integer.toString(newChatbotPK) + ",";
      for(int jj = 0; jj<dictDBColumn.length; jj++){

        try{
          if(replaceDictList.get(ii).get(dictDBColumn[jj])==null){
            throw new Exception("null value");
          }
          if(dictDBColVal[jj]){
            dbValString += "N'" + (String)replaceDictList.get(ii).get(dictDBColumn[jj]) + "'";
          }else{
            dbValString += Integer.toString((Integer)replaceDictList.get(ii).get(dictDBColumn[jj]));
          }
        }catch (Exception e){
          dbValString += "NULL";
        }

        if(jj!=dictDBColumn.length-1){
          dbValString += ",";
        }else{
          dbValString += ")";
        }
      }
      dictQueryValues.add(dbValString);
    }
    List<String> dictQuery = new ArrayList<>();
    int dictCount = 0;
    String nowQuery = "";
    for(int ii = 0; ii<dictQueryValues.size(); ii++){
      if(dictCount<10 && ii<dictQueryValues.size()-1){
        nowQuery += dictQueryValues.get(ii) + ",";
        dictCount++;
      }else{
        nowQuery += dictQueryValues.get(ii) + ";";
        dictQuery.add(nowQuery);
        nowQuery = "";
        dictCount = 0;
      }
    }

    for(int ii = 0; ii<dictQuery.size(); ii++){
      Map<String, Object> dictMap = new HashMap<>();
      dictMap.put("valStr",dictQuery.get(ii));
      builderService.insertReplaceDict(dictMap);
    }

    //IntentRel 추가하기
    String valStr = "";
    String srcIntentStr;
    List<String> valStrList = new ArrayList<>();
    int nowStrList = 0;
    for(int ii = 0; ii<intentRelList.size(); ii++){
      try{
        if(intentRelList.get(ii).get("SrcIntent")==null){
          srcIntentStr = "0";
        }else{
          srcIntentStr = intentRelList.get(ii).get("SrcIntent").toString();
        }
        String conditionAnsStr = "NULL";
        if(intentRelList.get(ii).get("ConditionAnswer")!=null) {conditionAnsStr = "'" + intentRelList.get(ii).get("ConditionAnswer").toString() + "'";}
        String destAnswerScope = "NULL";
        if(intentRelList.get(ii).get("DestAnswerScope")!=null) {destAnswerScope = "'" + intentRelList.get(ii).get("DestAnswerScope").toString() + "'";}
        valStr += "(" + srcIntentStr + ","
                + bertIntentBeforeAfter.get(Integer.parseInt(intentRelList.get(ii).get("BertIntent").toString())) + ","
                + intentBeforeAfter.get(Integer.parseInt((intentRelList.get(ii).get("DestIntent").toString()))) + ","
                + conditionAnsStr + "," + destAnswerScope + ")";
        if(nowStrList == 90 || ii==intentRelList.size()-1){
          nowStrList = 0;
          valStrList.add(valStr);
          valStr = "";
        }else{
          nowStrList += 1;
          valStr += ",";
        }
      }catch (Exception e){
      }

    }
    for(int ii = 0; ii<valStrList.size(); ii++){
      if(valStrList.get(ii).equals("")){
        break;
      }
      map = new HashMap<>();
      map.put("valStr", valStrList.get(ii));
      builderService.insertIntentRelByAll(map);
    }
    return newChatbotPK;
  }



  @ResponseBody
  @RequestMapping(value = "/deleteCache")
  public Integer deleteCache(@RequestBody Map param, HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    System.out.println("deleteCache start");
    Integer nowDelCache = -1;
    try{
      nowDelCache  = (Integer) param.get("delHost");
      DelCacheClient delCacheClient = new DelCacheClient();
      delCacheClient.sendHttp(nowDelCache);
    }catch (Exception e){
      System.out.println(e);
    }
    System.out.println("/deleteCache :: DEL HOST :: " + nowDelCache);
    return nowDelCache;
  }

  @ResponseBody
  @RequestMapping(value = "/changeAccountLang")
  public String changeAccountLang(@RequestBody Map param, HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    HttpSession session = request.getSession();
    Map<String, Object> map = new HashMap<>();
    builderService.changeAccountLang(param);
    return "Success";
  }

  @ResponseBody
  @RequestMapping(value = "/delHost")
  public Map<String, Object> delHost(@RequestBody Map param, HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    Map<String, Object> returnMap = new HashMap<>();
    returnMap.put("return","FAILED");
    returnMap.put("nowDelHost",param.get("nowDelHost"));
    HttpSession session = request.getSession();
    Map<String, Object> map = new HashMap<>();

    map.put("nowDelHost",param.get("nowDelHost"));
    List<Map<String, Object>> intentList = builderService.selectIntentForDelete(map);
    List<Map<String, Object>> backendInfoList = builderService.selectBackendInfoForDelete(map);

    // IntentRel    삭제
    int nowIntentCount = 0;
    int nowIntentOverallCount = 0;
    String nowIntentStr = "";
    List<String> nowIntentStrList = new ArrayList<>();
    while(nowIntentOverallCount<intentList.size()){
      String nowIntentNo = intentList.get(nowIntentOverallCount).get("No").toString();
      if(nowIntentCount<100){
        nowIntentStr += "(SrcIntent=" +nowIntentNo+" OR DestIntent=" + nowIntentNo + ")";
        if(nowIntentCount!=99 && nowIntentOverallCount!=intentList.size()-1){
          nowIntentStr += " OR ";
        }
      }
      nowIntentOverallCount++;
      nowIntentCount++;
      if(nowIntentCount==100 || nowIntentOverallCount==intentList.size()){
        nowIntentCount = 0;
        nowIntentStrList.add(nowIntentStr);
        nowIntentStr = "";
      }
    }
    for(int i = 0; i<nowIntentStrList.size(); i++){
      if(nowIntentStrList.get(i).length()<4){
        break;
      }
      map = new HashMap<>();
      map.put("delWhereStr",nowIntentStrList.get(i));
      builderService.deleteIntentRelByHost(map);
    }

    // BertIntent   삭제
    String bertIntentStr = "(";
    for(int ii = 0; ii<backendInfoList.size(); ii++){
      bertIntentStr += "BertItfId = " + backendInfoList.get(ii).get("id");
      if(ii!=backendInfoList.size()-1){bertIntentStr += " OR ";}
      else{bertIntentStr += " )";}
    }
    List<Map<String, Object>> bertIntentList = new ArrayList<>();
    if(! bertIntentStr.equals("(")) {
      map.put("backendInfoStr", bertIntentStr);
      bertIntentList = builderService.selectBertIntentForDelete(map);
    }
    map = new HashMap<>();
    map.put("delWhereStr",bertIntentStr);
    if(bertIntentStr.length()>4){
      builderService.deleteBertIntentByHost(map);
    }


    // RegexRule    삭제
    List<Map<String, Object>> regexIntentList = builderService.selectRegexIntentForDelete(map);
    int nowRegexIntentCount = 0;
    int nowRegexIntentOverallCount = 0;
    String nowRegexIntentStr = "";
    List<String> nowRegexIntentStrList = new ArrayList<>();
    while(nowRegexIntentOverallCount<regexIntentList.size()){
      String nowRegexIntentNo = regexIntentList.get(nowRegexIntentOverallCount).get("No").toString();
      if(nowRegexIntentCount<100){
        nowRegexIntentStr += "( RegexIntentNo=" +nowRegexIntentNo+" )";
        if(nowRegexIntentCount!=99 && nowRegexIntentOverallCount!=regexIntentList.size()-1){
          nowRegexIntentStr += " OR ";
        }
      }
      nowRegexIntentOverallCount++;
      nowRegexIntentCount++;
      if(nowRegexIntentCount==100 || nowRegexIntentOverallCount==regexIntentList.size()){
        nowRegexIntentCount = 0;
        nowRegexIntentStrList.add(nowRegexIntentStr);
        nowRegexIntentStr = "";
      }
    }
    for(int i = 0; i<nowRegexIntentStrList.size(); i++){
      if(nowRegexIntentStrList.get(i).length()<4){
        break;
      }
      map = new HashMap<>();
      map.put("delWhereStr",nowRegexIntentStrList.get(i));
      builderService.deleteRegexRuleByHost(map);
    }

    // BertSentence 삭제
    int nowBertIntentCount = 0;
    int nowBertIntentOverallCount = 0;
    String nowBertIntentStr = "";
    List<String> nowBertIntentStrList = new ArrayList<>();
    while(nowBertIntentOverallCount<bertIntentList.size()){
      String nowBertIntentNo = bertIntentList.get(nowBertIntentOverallCount).get("No").toString();
      if(nowBertIntentCount<100){
        nowBertIntentStr += "(BertIntentNo=" +nowBertIntentNo+")";
        if(nowBertIntentCount!=99 && nowBertIntentOverallCount!=bertIntentList.size()-1){
          nowBertIntentStr += " OR ";
        }
      }
      nowBertIntentOverallCount++;
      nowBertIntentCount++;
      if(nowBertIntentCount==100 || nowBertIntentOverallCount==bertIntentList.size()){
        nowBertIntentCount = 0;
        nowBertIntentStrList.add(nowBertIntentStr);
        nowBertIntentStr = "";
      }
    }

    for(int i = 0; i<nowBertIntentStrList.size(); i++){
      if(nowBertIntentStrList.get(i).length()<4){
        break;
      }
      map = new HashMap<>();
      map.put("delWhereStr",nowBertIntentStrList.get(i));
      builderService.deleteBertSentenceByHost(map);
    }


    // 삭제 : Account, Answer, Intent, BackendInfo, style_css, RegexIntent, ReplaceDict
    map = new HashMap<>();
    map.put("nowDelHost",param.get("nowDelHost"));
    builderService.deleteAccountByHost(map);

    // Entities 삭제
    builderService.deleteEntities(Integer.parseInt(param.get("nowDelHost").toString()));

    returnMap.put("return","SUCCESS");

    return returnMap;
  }

  @ResponseBody
  @RequestMapping(value = "/logDebug")
  public Map<String, Object> logDebug(@RequestBody Map param, HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    Map<String, Object> map = new HashMap<>();

    try {

      if(builderService.replaceDictCnt(param) > 0){
        param.put("utter",builderService.replaceDictgetAfter(param).get("After"));
      }

      if(builderService.logDebug(param) != null && builderService.logDebug(param).size() > 0){
        map = builderService.logDebug(param);
      }

      if(map.size() == 0){
        map.put("logDebugErrLog","실시간 엔진 분석 지연으로 결과를 가져오지 못했습니다. 계속 반복된다면 태스크, 의도 페이지에서 이전 태스크 선택 후 테스트를 진행해주세요. 동일한 분석결과를 확인할 수 있습니다.");
      }
    }catch (Exception e){
      e.printStackTrace();
      map.put("logDebugErrLog","실시간 엔진 분석 지연으로 결과를 가져오지 못했습니다. 계속 반복된다면 태스크, 의도 페이지에서 이전 태스크 선택 후 테스트를 진행해주세요. 동일한 분석결과를 확인할 수 있습니다.");
      return map;
    }

    return map;
  }


  @RequestMapping(value = "/chatExcelDown", method = RequestMethod.GET)
  public void chatExcelDown(HttpServletRequest request, HttpServletResponse response, @RequestParam String host)
      throws Exception {
    XSSFWorkbook workbook = new XSSFWorkbook();
    Map<String, Object> param = new HashMap<>();
    param.put("host", host);

    try {

      /* (2) 새로운 Sheet 생성 */
      XSSFSheet sheet = workbook.createSheet("Intent");
      XSSFSheet sheet2 = workbook.createSheet("BertIntent");
      XSSFSheet sheet3 = workbook.createSheet("Relation");
      XSSFSheet sheet4 = workbook.createSheet("Fallback");
      XSSFSheet sheet5 = workbook.createSheet("Entities");

      Row row = null;
      Cell cell = null;
      int rowNo = 0;
      String[] columnList = {"번호", "이미지", "Description(한)", "Description(중)", "Description(일)",
          "Description(영)", "태스크", "디스플레이명(한)", "디스플레이명(중)", "디스플레이명(일)", "디스플레이명(영)",
          "다음태스크", "답변(한)", "답변(중)", "답변(일)", "답변(영)","CustomURL"
      };
      String[] dataList = {"Row", "URL", "intentDescKr", "intentDescCh", "intentDescJp",
          "intentDescEn", "main", "intentNmKr", "intentNmCh", "intentNmJp", "intentNmEn",
          "TmpNextIntent", "answerKr", "answerCh", "answerJp", "answerEn","customURL"
      };

      Map<String, Object> map = builderService.getChatLang(param);
      String langList[];
      if (map == null) {
        langList = new String[]{"1"};
        param.put("lang", 1);
      } else {
        String lang = (String) map.get("Language");
        langList = lang.split(",");
        param.put("lang", langList[0]);
      }
      List<Map<String, Object>> chatList = builderService.getChatInfo(param);
      Map<String, Object> rowNum = new HashMap<>();
      for (int k=0; k<chatList.size(); k++) {
        rowNum.put(chatList.get(k).get("No").toString(), chatList.get(k).get("Row"));
      }
      for (int i=-1; i<chatList.size(); i++) {
        row = sheet.createRow(rowNo++);
        for (int j=0; j<17; j++) {
          cell = row.createCell(j);
          if (i == -1) {
            cell.setCellValue(columnList[j]);
          } else {
            if (dataList[j].equals("TmpNextIntent")) {
              String nextIntent = chatList.get(i).get(dataList[j]).toString();
              String[] nextIntentList = nextIntent.split(",");
              String tmpNextIntent = "";
              for (int k=0; k<nextIntentList.length; k++) {
                 if (nextIntentList[k] != null && !"".equals(nextIntentList[k])) {
                  String type = nextIntentList[k].substring(0,1);
                  String nextNo = nextIntentList[k].substring(1);

                  if(rowNum.get(nextNo) != null && !"".equals(rowNum.get(nextNo))){
                    String tmpNextNo = rowNum.get(nextNo).toString();
                    tmpNextIntent += type + tmpNextNo;
                    if (k != nextIntentList.length - 1) {
                      tmpNextIntent += ",";
                    }
                  }

                }
              }
              if(!"".equals(tmpNextIntent) && nextIntentList.length > tmpNextIntent.split(",").length){
                tmpNextIntent = tmpNextIntent.substring(0, tmpNextIntent.length()-1);
              }
              if(j == 11){
                cell.setCellValue(tmpNextIntent);
              }
            } else {
              cell.setCellValue(chatList.get(i).get(dataList[j]).toString());
            }
          }
        }
      }

      columnList = new String[]{"BERT_Intent(Kor)", "BERT_Intent(Chn)", "BERT_Intent(Jap)", "BERT_Intent(Eng)"};
      dataList = new String[]{"bertIntentKr", "bertIntentCh", "bertIntentJp", "bertIntentEn"};
      rowNo = 0;
      List<Map<String, Object>> bertIntentList = builderService.getBertIntent(param);
      for (int i=-1; i<bertIntentList.size(); i++) {
        row = sheet2.createRow(rowNo++);
        for (int j=0; j<4; j++) {
          cell = row.createCell(j);
          if (i == -1) {
            cell.setCellValue(columnList[j]);
          } else {
            cell.setCellValue(bertIntentList.get(i).get(dataList[j]).toString());
          }
        }
      }

      columnList = new String[]{"Src_Task(Kor)", "Dest_Task(Kor)", "Bert_Intent(Kor)",
          "Src_Task(Chn)", "Dest_Task(Chn)",
          "Bert_Intent(Chn)", "Src_Task(Jap)", "Dest_Task(Jap)", "Bert_Intent(Jap)",
          "Src_Task(Eng)", "Dest_Task(Eng)", "Bert_Intent(Eng)"
      };
      dataList = new String[]{"Src_Task", "Dest_Task", "Bert_Intent"};
      param.put("lang", 1);
      List<Map<String, Object>> intentRelKr = builderService.getIntentRel(param);
      param.put("lang", 4);
      List<Map<String, Object>> intentRelCh = builderService.getIntentRel(param);
      param.put("lang", 3);
      List<Map<String, Object>> intentRelJp = builderService.getIntentRel(param);
      param.put("lang", 2);
      List<Map<String, Object>> intentRelEn = builderService.getIntentRel(param);
      rowNo = 0;
      row = sheet3.createRow(rowNo++);
      for (int i=0; i<columnList.length; i++) {
        cell = row.createCell(i);
        cell.setCellValue(columnList[i]);
      }
      if (intentRelKr.size() != 0 || intentRelCh.size() !=0 || intentRelJp.size() !=0 || intentRelEn.size() !=0 ) {
        int [] arr = {intentRelKr.size(), intentRelCh.size(), intentRelJp.size(), intentRelEn.size()};
        int max = 0;
        for (int i = 0; i < arr.length; i++) {
          if (arr[i] > max) {
            max = arr[i];
          }
        }
        for (int j=0; j<max; j++) {
          row = sheet3.createRow(rowNo++);
          for (int k=0; k<12; k++) {
            if (k < 3 && intentRelKr.size() > 0 && intentRelKr.size() > j) {
              cell = row.createCell(k);
              cell.setCellValue(intentRelKr.get(j).get(dataList[k]).toString());
            } else if (2 < k && k < 6 && intentRelCh.size() > 0 && intentRelCh.size() > j) {
              cell = row.createCell(k);
              cell.setCellValue(intentRelCh.get(j).get(dataList[k-3]).toString());
            } else if (5 < k && k < 9 && intentRelJp.size() > 0 && intentRelJp.size() > j) {
              cell = row.createCell(k);
              cell.setCellValue(intentRelJp.get(j).get(dataList[k-6]).toString());
            } else if (8 < k && intentRelEn.size() > 0 && intentRelEn.size() > j){
              cell = row.createCell(k);
              cell.setCellValue(intentRelEn.get(j).get(dataList[k-9]).toString());
            }
          }
        }
      }
      rowNo = 0;
      columnList = new String[]{"Default(Kor)", "Default(Chn)", "Default(Jap)", "Default(Eng)"};
      List<Map<String, Object>> fallbackList = builderService.getFallback(param);
      row = sheet4.createRow(rowNo++);
      for (int i=0; i<4; i++) {
        cell = row.createCell(i);
        cell.setCellValue(columnList[i]);
      }

      row = sheet4.createRow(rowNo++);
      for (int i=0; i<fallbackList.size(); i++) {
        int langCode = (int) fallbackList.get(i).get("language");

        if ( langCode == 1 ) cell = row.createCell(0);
        else if ( langCode == 2 ) cell = row.createCell(3);
        else if ( langCode == 3 ) cell = row.createCell(2);
        else if ( langCode == 4 ) cell = row.createCell(1);

        cell.setCellValue(fallbackList.get(i).get("Intent").toString());
      }

      // entities sheet 추가
      rowNo = 0;
      columnList = new String[]{"Entity_Task(Kor)", "Entity_Task(Chn)", "Entity_Task(Jap)", "Entity_Task(Eng)"
                                , "Entity_Name(Kor)", "Entity_Name(Chn)", "Entity_Name(Jap)", "Entity_Name(Eng)"
                                , "Entity_Value(Kor)", "Entity_Value(Chn)", "Entity_Value(Jap)", "Entity_Value(Eng)"};
      param.put("lang", 1);
      List<Map<String, Object>> entitiesKr = builderService.getEntitiesList(param);
      param.put("lang", 4);
      List<Map<String, Object>> entitiesCh = builderService.getEntitiesList(param);
      param.put("lang", 3);
      List<Map<String, Object>> entitiesJp = builderService.getEntitiesList(param);
      param.put("lang", 2);
      List<Map<String, Object>> entitiesEn = builderService.getEntitiesList(param);
      int maxSize = entitiesKr.size();
      if(maxSize < entitiesCh.size()) maxSize = entitiesCh.size();
      if(maxSize < entitiesJp.size()) maxSize = entitiesJp.size();
      if(maxSize < entitiesEn.size()) maxSize = entitiesEn.size();

      row = sheet5.createRow(rowNo++);
      for(int i=0; i < columnList.length; i++){
        cell = row.createCell(i);
        cell.setCellValue(columnList[i]);
      }

      for(int i=0; i < maxSize; i++){
        row = sheet5.createRow(rowNo++);
        for(int j=0; j < 12; j++){
          cell = row.createCell(j);

          if(j==0 && entitiesKr.size() > i) {
            cell.setCellValue(entitiesKr.get(i).get("Main").toString());
          } else if(j==1 && entitiesCh.size() > i) {
            cell.setCellValue(entitiesCh.get(i).get("Main").toString());
          } else if(j==2 && entitiesJp.size() > i) {
            cell.setCellValue(entitiesJp.get(i).get("Main").toString());
          } else if(j==3 && entitiesEn.size() > i) {
            cell.setCellValue(entitiesEn.get(i).get("Main").toString());
          } else if(j==4 && entitiesKr.size() > i) {
            cell.setCellValue(entitiesKr.get(i).get("name").toString());
          } else if(j==5 && entitiesCh.size() > i) {
            cell.setCellValue(entitiesCh.get(i).get("name").toString());
          } else if(j==6 && entitiesJp.size() > i) {
            cell.setCellValue(entitiesJp.get(i).get("name").toString());
          } else if(j==7 && entitiesEn.size() > i) {
            cell.setCellValue(entitiesEn.get(i).get("name").toString());
          }else if(j==8 && entitiesKr.size() > i) {
            cell.setCellValue(entitiesKr.get(i).get("val").toString());
          }else if(j==9 && entitiesCh.size() > i) {
            cell.setCellValue(entitiesCh.get(i).get("val").toString());
          }else if(j==10 && entitiesJp.size() > i) {
            cell.setCellValue(entitiesJp.get(i).get("val").toString());
          }else if(j==11 && entitiesEn.size() > i) {
            cell.setCellValue(entitiesEn.get(i).get("val").toString());
          }
        }
      }

      /* 설정언어 외 숨기기 처리 */
      for(int i = 0; i <= 3; i++) {
        int nowLang = 0;

        for(int j = 0; j<langList.length; j++){
          if(i+1 == Integer.parseInt(langList[j])){
            nowLang=0;
            break;
          }else{
            nowLang = i+1;
          }
        }

        if(nowLang == i+1) {
          if(nowLang == 2){
            nowLang = 4;
          }else if(nowLang == 4){
            nowLang = 2;
          }

          //nowLang 1: 한국어, 2: 중국어, 3: 일본어, 4: 영어
          //컬럼 셀 때는 0부터 시작
          sheet.setColumnHidden(nowLang+1, true);
          sheet.setColumnHidden(nowLang+6, true);
          sheet.setColumnHidden(nowLang+11, true);

          sheet2.setColumnHidden(nowLang-1, true);

          sheet3.setColumnHidden((nowLang-1)*3, true);
          sheet3.setColumnHidden(1 + (nowLang-1)*3, true);
          sheet3.setColumnHidden(2 + (nowLang-1)*3, true);

          sheet4.setColumnHidden(nowLang-1, true);

          sheet5.setColumnHidden(nowLang-1, true);
          sheet5.setColumnHidden(nowLang+3, true);
          sheet5.setColumnHidden(nowLang+7, true);
        }

      }

      /* (8) Excel 파일 생성 */

      response.setContentType("ms-vnd/excel");
      response.setHeader("Set-Cookie", "fileDownload=true; path=/");
      response.setHeader("Content-Disposition", "attachment;filename=[chatbot] TASK_ANSWER_데이터.xlsx");

      workbook.write(response.getOutputStream());
      workbook.close();
    } catch (Exception e) {
      e.printStackTrace();
      response.setHeader("Set-Cookie", "fileDownload=false; path=/");
      response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
      response.setHeader("Content-Type", "text/html; charset=utf-8");

      OutputStream out = null;
      try {
        out = response.getOutputStream();
        byte[] data = "fail..".getBytes();
        out.write(data, 0, data.length);
      } catch (Exception ignore) {
        ignore.printStackTrace();
      } finally {
        if (out != null) {
          try {
            out.close();
          } catch (Exception ignore) {
          }
        }
      }

    }
  }
  @RequestMapping(value = "/chatExcelDownHTask", method = RequestMethod.GET)
  public void chatExcelDownHTask(HttpServletRequest request, HttpServletResponse response, @RequestParam String host)
      throws Exception {
    XSSFWorkbook workbook = new XSSFWorkbook();
    Map<String, Object> param = new HashMap<>();
    param.put("host", host);

    try {

      /* (2) 새로운 Sheet 생성 */
      XSSFSheet sheet = workbook.createSheet("Intent");
      XSSFSheet sheet2 = workbook.createSheet("BertIntent");
      XSSFSheet sheet3 = workbook.createSheet("Relation");
      XSSFSheet sheet4 = workbook.createSheet("Fallback");
      XSSFSheet sheet5 = workbook.createSheet("Entities");

      Row row = null;
      Cell cell = null;
      int rowNo = 0;
      String[] columnList = {"번호", "이미지", "Description(한)", "Description(중)", "Description(일)",
          "Description(영)", "태스크", "디스플레이명(한)", "디스플레이명(중)",
          "디스플레이명(일)", "디스플레이명(영)", "다음태스크", "답변(한)","답변(중)", "답변(일)", "답변(영)",
          "CustomURL", "h_task", "h_item", "h_param"
      };
      String[] dataList = {"Row", "URL", "intentDescKr", "intentDescCh", "intentDescJp",
          "intentDescEn", "main", "intentNmKr", "intentNmCh",
          "intentNmJp", "intentNmEn", "TmpNextIntent", "answerKr", "answerCh", "answerJp", "answerEn",
          "customURL", "hTask", "hItem", "hParam"};
      Map<String, Object> map = builderService.getChatLang(param);
      String langList[];
      if (map == null) {
        langList = new String[]{"1"};
        param.put("lang", 1);
      } else {
        String lang = (String) map.get("Language");
        langList = lang.split(",");
        param.put("lang", langList[0]);
      }
      List<Map<String, Object>> chatList = builderService.getChatInfoHTask(param);
      Map<String, Object> rowNum = new HashMap<>();
      for (int k=0; k<chatList.size(); k++) {
        rowNum.put(chatList.get(k).get("No").toString(), chatList.get(k).get("Row"));
      }
      for (int i=-1; i<chatList.size(); i++) {
        row = sheet.createRow(rowNo++);
        for (int j=0; j<20; j++) {
          cell = row.createCell(j);
          if (i == -1) {
            cell.setCellValue(columnList[j]);
          } else {
            if (dataList[j].equals("TmpNextIntent")) {
              String nextIntent = chatList.get(i).get(dataList[j]).toString();
              String[] nextIntentList = nextIntent.split(",");
              String tmpNextIntent = "";
              for (int k=0; k<nextIntentList.length; k++) {
                 if (nextIntentList[k] != null && !"".equals(nextIntentList[k])) {
                  String type = nextIntentList[k].substring(0,1);
                  String nextNo = nextIntentList[k].substring(1);

                  if(rowNum.get(nextNo) != null && !"".equals(rowNum.get(nextNo))){
                    String tmpNextNo = rowNum.get(nextNo).toString();
                    tmpNextIntent += type + tmpNextNo;
                    if (k != nextIntentList.length - 1) {
                      tmpNextIntent += ",";
                    }
                  }

                }
              }
              if(!"".equals(tmpNextIntent) && nextIntentList.length > tmpNextIntent.split(",").length){
                tmpNextIntent = tmpNextIntent.substring(0, tmpNextIntent.length()-1);
              }
              if(j == 11){
                cell.setCellValue(tmpNextIntent);
              }
            } else {
              cell.setCellValue(chatList.get(i).get(dataList[j]).toString());
            }
          }
        }
      }

      columnList = new String[]{"BERT_Intent(Kor)", "BERT_Intent(Chn)", "BERT_Intent(Jap)", "BERT_Intent(Eng)"};
      dataList = new String[]{"bertIntentKr", "bertIntentCh", "bertIntentJp", "bertIntentEn"};
      rowNo = 0;
      List<Map<String, Object>> bertIntentList = builderService.getBertIntent(param);
      for (int i=-1; i<bertIntentList.size(); i++) {
        row = sheet2.createRow(rowNo++);
        for (int j=0; j<4; j++) {
          cell = row.createCell(j);
          if (i == -1) {
            cell.setCellValue(columnList[j]);
          } else {
            cell.setCellValue(bertIntentList.get(i).get(dataList[j]).toString());
          }
        }
      }

      columnList = new String[]{"Src_Task(Kor)", "Dest_Task(Kor)", "Bert_Intent(Kor)",
          "Src_Task(Chn)", "Dest_Task(Chn)",
          "Bert_Intent(Chn)", "Src_Task(Jap)", "Dest_Task(Jap)", "Bert_Intent(Jap)",
          "Src_Task(Eng)", "Dest_Task(Eng)", "Bert_Intent(Eng)"
      };
      dataList = new String[]{"Src_Task", "Dest_Task", "Bert_Intent"};
      param.put("lang", 1);
      List<Map<String, Object>> intentRelKr = builderService.getIntentRel(param);
      param.put("lang", 4);
      List<Map<String, Object>> intentRelCh = builderService.getIntentRel(param);
      param.put("lang", 3);
      List<Map<String, Object>> intentRelJp = builderService.getIntentRel(param);
      param.put("lang", 2);
      List<Map<String, Object>> intentRelEn = builderService.getIntentRel(param);
      rowNo = 0;
      row = sheet3.createRow(rowNo++);
      for (int i=0; i<columnList.length; i++) {
        cell = row.createCell(i);
        cell.setCellValue(columnList[i]);
      }
      if (intentRelKr.size() != 0 || intentRelCh.size() !=0 || intentRelJp.size() !=0 || intentRelEn.size() !=0 ) {
        int [] arr = {intentRelKr.size(), intentRelCh.size(), intentRelJp.size(), intentRelEn.size()};
        int max = 0;
        for (int i = 0; i < arr.length; i++) {
          if (arr[i] > max) {
            max = arr[i];
          }
        }
        for (int j=0; j<max; j++) {
          row = sheet3.createRow(rowNo++);
          for (int k=0; k<12; k++) {
            if (k < 3 && intentRelKr.size() > 0 && intentRelKr.size() > j) {
              cell = row.createCell(k);
              cell.setCellValue(intentRelKr.get(j).get(dataList[k]).toString());
            } else if (2 < k && k < 6 && intentRelCh.size() > 0 && intentRelCh.size() > j) {
              cell = row.createCell(k);
              cell.setCellValue(intentRelCh.get(j).get(dataList[k-3]).toString());
            } else if (5 < k && k < 9 && intentRelJp.size() > 0 && intentRelJp.size() > j) {
              cell = row.createCell(k);
              cell.setCellValue(intentRelJp.get(j).get(dataList[k-6]).toString());
            } else if (8 < k && intentRelEn.size() > 0 && intentRelEn.size() > j){
              cell = row.createCell(k);
              cell.setCellValue(intentRelEn.get(j).get(dataList[k-9]).toString());
            }
          }
        }
      }
      rowNo = 0;
      columnList = new String[]{"Default(Kor)", "Default(Chn)", "Default(Jap)", "Default(Eng)"};
      List<Map<String, Object>> fallbackList = builderService.getFallback(param);
      row = sheet4.createRow(rowNo++);
      for (int i=0; i<4; i++) {
        cell = row.createCell(i);
        cell.setCellValue(columnList[i]);
      }

      row = sheet4.createRow(rowNo++);
      for (int i=0; i<fallbackList.size(); i++) {
        cell = row.createCell(i);
        cell.setCellValue(fallbackList.get(i).get("Intent").toString());
      }

      // entities sheet 추가
      rowNo = 0;
      columnList = new String[]{"Entity_Task(Kor)", "Entity_Task(Chn)", "Entity_Task(Jap)", "Entity_Task(Eng)"
                                , "Entity_Name(Kor)", "Entity_Name(Chn)", "Entity_Name(Jap)", "Entity_Name(Eng)"
                                , "Entity_Value(Kor)", "Entity_Value(Chn)", "Entity_Value(Jap)", "Entity_Value(Eng)"};
      param.put("lang", 1);
      List<Map<String, Object>> entitiesKr = builderService.getEntitiesList(param);
      param.put("lang", 4);
      List<Map<String, Object>> entitiesCh = builderService.getEntitiesList(param);
      param.put("lang", 3);
      List<Map<String, Object>> entitiesJp = builderService.getEntitiesList(param);
      param.put("lang", 2);
      List<Map<String, Object>> entitiesEn = builderService.getEntitiesList(param);
      int maxSize = entitiesKr.size();
      if(maxSize < entitiesCh.size()) maxSize = entitiesCh.size();
      if(maxSize < entitiesJp.size()) maxSize = entitiesJp.size();
      if(maxSize < entitiesEn.size()) maxSize = entitiesEn.size();

      row = sheet5.createRow(rowNo++);
      for(int i=0; i < columnList.length; i++){
        cell = row.createCell(i);
        cell.setCellValue(columnList[i]);
      }

      for(int i=0; i < maxSize; i++){
        row = sheet5.createRow(rowNo++);
        for(int j=0; j < 12; j++){
          cell = row.createCell(j);

          if(j==0 && entitiesKr.size() > i) {
            cell.setCellValue(entitiesKr.get(i).get("Main").toString());
          } else if(j==1 && entitiesCh.size() > i) {
            cell.setCellValue(entitiesCh.get(i).get("Main").toString());
          } else if(j==2 && entitiesJp.size() > i) {
            cell.setCellValue(entitiesJp.get(i).get("Main").toString());
          } else if(j==3 && entitiesEn.size() > i) {
            cell.setCellValue(entitiesEn.get(i).get("Main").toString());
          } else if(j==4 && entitiesKr.size() > i) {
            cell.setCellValue(entitiesKr.get(i).get("name").toString());
          } else if(j==5 && entitiesCh.size() > i) {
            cell.setCellValue(entitiesCh.get(i).get("name").toString());
          } else if(j==6 && entitiesJp.size() > i) {
            cell.setCellValue(entitiesJp.get(i).get("name").toString());
          } else if(j==7 && entitiesEn.size() > i) {
            cell.setCellValue(entitiesEn.get(i).get("name").toString());
          }else if(j==8 && entitiesKr.size() > i) {
            cell.setCellValue(entitiesKr.get(i).get("val").toString());
          }else if(j==9 && entitiesCh.size() > i) {
            cell.setCellValue(entitiesCh.get(i).get("val").toString());
          }else if(j==10 && entitiesJp.size() > i) {
            cell.setCellValue(entitiesJp.get(i).get("val").toString());
          }else if(j==11 && entitiesEn.size() > i) {
            cell.setCellValue(entitiesEn.get(i).get("val").toString());
          }
        }
      }

      /* 설정언어 외 숨기기 처리 */
      for(int i = 0; i <= 3; i++) {
        int nowLang = 0;

        for(int j = 0; j<langList.length; j++){
          if(i+1 == Integer.parseInt(langList[j])){
            nowLang=0;
            break;
          }else{
            nowLang = i+1;
          }
        }

        if(nowLang == i+1) {
          if(nowLang == 2){
            nowLang = 4;
          }else if(nowLang == 4){
            nowLang = 2;
          }

          //nowLang 1: 한국어, 2: 중국어, 3: 일본어, 4: 영어
          //컬럼 셀 때는 0부터 시작
          sheet.setColumnHidden(nowLang+1, true);
          sheet.setColumnHidden(nowLang+6, true);
          sheet.setColumnHidden(nowLang+11, true);

          sheet2.setColumnHidden(nowLang-1, true);

          sheet3.setColumnHidden((nowLang-1)*3, true);
          sheet3.setColumnHidden(1 + (nowLang-1)*3, true);
          sheet3.setColumnHidden(2 + (nowLang-1)*3, true);

          sheet4.setColumnHidden(nowLang-1, true);

          sheet5.setColumnHidden(nowLang-1, true);
          sheet5.setColumnHidden(nowLang+3, true);
          sheet5.setColumnHidden(nowLang+7, true);
        }

      }

      /* (8) Excel 파일 생성 */

      response.setContentType("ms-vnd/excel");
      response.setHeader("Set-Cookie", "fileDownload=true; path=/");
      response.setHeader("Content-Disposition", "attachment;filename=[chatbot] TASK_ANSWER_데이터.xlsx");

      workbook.write(response.getOutputStream());
      workbook.close();
    } catch (Exception e) {
      e.printStackTrace();
      response.setHeader("Set-Cookie", "fileDownload=false; path=/");
      response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
      response.setHeader("Content-Type", "text/html; charset=utf-8");

      OutputStream out = null;
      try {
        out = response.getOutputStream();
        byte[] data = "fail..".getBytes();
        out.write(data, 0, data.length);
      } catch (Exception ignore) {
        ignore.printStackTrace();
      } finally {
        if (out != null) {
          try {
            out.close();
          } catch (Exception ignore) {
          }
        }
      }

    }
  }

  @ResponseBody
  @RequestMapping(value = "/regexExcelUpload", method = RequestMethod.POST)
  public ModelAndView regexExcelUpload(HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    String result = "";
    Map<String, Object> map = new HashMap<>();
    String host = request.getParameter("host");
    map.put("host", host);
    MultipartFile excelFile = ((MultipartHttpServletRequest) request).getFile("excelFile");
    System.out.println("엑셀 파일 업로드 컨트롤러");
    if (excelFile == null || excelFile.isEmpty()) {
      throw new RuntimeException("엑셀파일을 선택 해 주세요.");
    }

    File destFile = new File(
            request.getSession().getServletContext().getInitParameter("excelPath") + excelFile
                    .getOriginalFilename());
    try {
      excelFile.transferTo(destFile);
      result = builderService.regexExcelUpload(destFile, map);
    } catch (IllegalStateException | IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e.getMessage(), e);
    } catch (RuntimeException e){
      result = e.getMessage();
    }

    destFile.delete();

    ModelAndView view = new ModelAndView("jsonView");
    view.addObject("status", result);
    return view;
  }

  @RequestMapping(value = "/regexExcelDown", method = RequestMethod.GET)
  public void regexExcelDown(HttpServletRequest request, HttpServletResponse response, @RequestParam String host)
          throws Exception {
    XSSFWorkbook workbook = new XSSFWorkbook();
    Map<String, Object> param = new HashMap<>();
    param.put("host", host);

    try {
      XSSFSheet sheet = workbook.createSheet("Regex");
      Row row = null;
      Cell cell = null;
      int rowNo = 0;

      row = sheet.createRow(rowNo++);
      String[] columnList = new String[]{"의도(Kor)", "정규식(Kor)","의도(Chn)", "정규식(Chn)","의도(Jap)", "정규식(Jap)","의도(Eng)", "정규식(Eng)"};
      for (int i=0; i<columnList.length; i++) {
        cell = row.createCell(i);
        cell.setCellValue(columnList[i]);
      }

      param.put("lang", "1");
      List<Map<String, Object>> regexListKor = builderService.getRegexListAll(param);
      param.put("lang", "2");
      List<Map<String, Object>> regexListEng = builderService.getRegexListAll(param);
      param.put("lang", "3");
      List<Map<String, Object>> regexListJap = builderService.getRegexListAll(param);
      param.put("lang", "4");
      List<Map<String, Object>> regexListChn = builderService.getRegexListAll(param);
      List<Integer> tempList = new ArrayList<> (Arrays.asList(regexListKor.size(), regexListChn.size(), regexListJap.size(), regexListEng.size()));

      for(int i = 0; i < Collections.max(tempList); i++) {
        row = sheet.createRow(rowNo++);

        if(i < tempList.get(0)){
          cell = row.createCell(0);
          cell.setCellValue((String) regexListKor.get(i).get("IntentName"));
          cell = row.createCell(1);
          cell.setCellValue((String) regexListKor.get(i).get("Regex"));
        }

        if(i < tempList.get(1)){
          cell = row.createCell(2);
          cell.setCellValue((String) regexListChn.get(i).get("IntentName"));
          cell = row.createCell(3);
          cell.setCellValue((String) regexListChn.get(i).get("Regex"));
        }

        if(i < tempList.get(2)){
          cell = row.createCell(4);
          cell.setCellValue((String) regexListJap.get(i).get("IntentName"));
          cell = row.createCell(5);
          cell.setCellValue((String) regexListJap.get(i).get("Regex"));
        }

        if(i < tempList.get(3)){
          cell = row.createCell(6);
          cell.setCellValue((String) regexListEng.get(i).get("IntentName"));
          cell = row.createCell(7);
          cell.setCellValue((String) regexListEng.get(i).get("Regex"));
        }
      }

      Map<String, Object> map = builderService.getChatLang(param);
      String langList[];
      if (map == null) {
        langList = new String[]{"1"};
      } else {
        langList = ((String) map.get("Language")).split(",");
      }

      for(int i = 1; i<5; i++){
        String tmpLang = Integer.toString(i);
        if(!Arrays.stream(langList).anyMatch(s -> s.equals(tmpLang))){
          if ( "1".equals(tmpLang) ) { //한국어
            sheet.setColumnHidden(0, true);
            sheet.setColumnHidden(1, true);
          } else if ( "2".equals(tmpLang) ) { //영어
            sheet.setColumnHidden(6, true);
            sheet.setColumnHidden(7, true);
          } else if ( "3".equals(tmpLang) ) { //일본어
            sheet.setColumnHidden(4, true);
            sheet.setColumnHidden(5, true);
          } else if ( "4".equals(tmpLang) ) { //중국어
            sheet.setColumnHidden(2, true);
            sheet.setColumnHidden(3, true);
          }
        }
      }

      response.setContentType("ms-vnd/excel");
      response.setHeader("Set-Cookie", "fileDownload=true; path=/");
      response.setHeader("Content-Disposition", "attachment;filename=[chatbot]Regex.xlsx");

      workbook.write(response.getOutputStream());
      workbook.close();
    } catch (Exception e) {
      e.printStackTrace();
      response.setHeader("Set-Cookie", "fileDownload=false; path=/");
      response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
      response.setHeader("Content-Type", "text/html; charset=utf-8");

      OutputStream out = null;
      try {
        out = response.getOutputStream();
        byte[] data = "fail..".getBytes();
        out.write(data, 0, data.length);
      } catch (Exception ignore) {
        ignore.printStackTrace();
      } finally {
        if (out != null) {
          try {
            out.close();
          } catch (Exception ignore) {
          }
        }
      }

    }
  }

  @ResponseBody
  @RequestMapping(value = "/builderContentsReplaceDict")
  public ModelAndView builderContentsReplaceDict(HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    ModelAndView view = new ModelAndView("/chatbotBuilderContents/chatbotBuilderContentsReplaceDict");
    view.addObject("queryString",TimeQuery.getQueryString());
    return view;
  }

  @ResponseBody
  @RequestMapping(value = "/builderBottomReplaceDict")
  public ModelAndView builderBottomReplaceDict(HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    ModelAndView view = new ModelAndView("/chatbotBuilderContents/chatbotBuilderBottomReplaceDict");
    view.addObject("queryString",TimeQuery.getQueryString());
    return view;
  }

  @ResponseBody
  @RequestMapping(value = "/getReplaceDict")
  public Map<String, Object> getReplaceDict(@RequestBody Map param, HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    HttpSession session = request.getSession();
    try{

      session.setAttribute("host", param.get("host"));
      Map<String, Object> map = new HashMap<>();

      // 페이징 setting
      PagingVO pagingVO = new PagingVO();
      pagingVO.setCOUNT_PER_PAGE("100");
      pagingVO.setTotalCount(builderService.getReplaceDictCount(param));
      pagingVO.setCurrentPage(param.get("cp").toString());
      param.put("startRow", pagingVO.getStartRow());
      param.put("endRow", pagingVO.getCOUNT_PER_PAGE());

      List<Map<String, Object>> replaceDictList = builderService.getReplaceDictLst(param);
      map.put("replaceDictList", replaceDictList);
      map.put("paging", pagingVO);
      map.put("searchSentence", param.get("searchSentence"));
      map.put("host", param.get("host"));
      map.put("orderCol", param.get("orderCol"));
      map.put("orderSort", param.get("orderSort"));

      return map;
    }catch (Exception e){
      e.printStackTrace();
    }
    return new HashMap<>();
  }

  @ResponseBody
  @RequestMapping(value = "/addReplaceDict")
  public Map<String, Object> addReplaceDict(@RequestBody Map param, HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
    Map<String, Object> map = new HashMap<>();
    int resultCnt = builderService.addReplaceDict(param);
    map.put("resultCnt", resultCnt);

    return map;
  }
  @ResponseBody
  @RequestMapping(value = "/updateReplaceDict")
  public Map<String, Object> updateReplaceDict(@RequestBody Map param, HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
    Map<String, Object> map = new HashMap<>();
    int resultCnt = builderService.updateReplaceDict(param);
    map.put("resultCnt", resultCnt);

    return map;
  }
  @ResponseBody
  @RequestMapping(value = "/deleteReplaceDict")
  public Map<String, Object> deleteReplaceDict(@RequestBody Map param, HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
    Map<String, Object> map = new HashMap<>();
    int resultCnt = builderService.deleteReplaceDict(param);
    map.put("resultCnt", resultCnt);

    return map;
  }

  @RequestMapping(value = "/replaceDictExcelDown", method = RequestMethod.GET)
  public void ReplaceDictExcelDown(@RequestParam Map param,HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    XSSFWorkbook workbook = new XSSFWorkbook();
    
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("_yyyyMMddHHmmss");
    Calendar calendar = Calendar.getInstance();
    String strToday = simpleDateFormat.format(calendar.getTime());
    
    try {

      /* (2) 새로운 Sheet 생성 */
      XSSFSheet sheet = workbook.createSheet("치환사전");

      Row row = null;
      Cell cell = null;
      int rowNo = 0;
      String[] columnList = {"치환 전", "치환 후"};
      String[] dataList = {"Before", "After"};

      param.put("startRow",0);
      param.put("endRow",builderService.getReplaceDictCount(param));
      List<Map<String, Object>> replaceDict = builderService.getReplaceDictLst(param);

      for (int i=-1; i<replaceDict.size(); i++) {
        row = sheet.createRow(rowNo++);
        for (int j = 0; j < 2; j++) {
          cell = row.createCell(j);
          if (i == -1) {
            cell.setCellValue(columnList[j]);
          } else {
            cell.setCellValue(replaceDict.get(i).get(dataList[j]).toString());
          }
        }
      }

      response.setContentType("ms-vnd/excel");
      response.setHeader("Set-Cookie", "fileDownload=true; path=/");
      response.setHeader("Content-Disposition", "attachment;filename=[chatbot]REPLACE_DICTIONARY_DATA"+strToday+".xlsx");

      workbook.write(response.getOutputStream());
      workbook.close();

    } catch (Exception e) {
      e.printStackTrace();
      response.setHeader("Set-Cookie", "fileDownload=false; path=/");
      response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
      response.setHeader("Content-Type", "text/html; charset=utf-8");

      OutputStream out = null;
      try {
        out = response.getOutputStream();
        byte[] data = "fail..".getBytes();
        out.write(data, 0, data.length);
      } catch (Exception ignore) {
        ignore.printStackTrace();
      } finally {
        if (out != null) {
          try {
            out.close();
          } catch (Exception ignore) { ignore.printStackTrace();
          }
        }
      }

    }
  }
  @ResponseBody
  @RequestMapping(value = "/insertReplaceDictExcel", method = RequestMethod.POST)
  public ModelAndView insertReplaceDictExcel(HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    Map<String, Object> map = new HashMap<>();
    map.put("host", request.getParameter("Host"));
    map.put("lang", request.getParameter("Lang"));

    MultipartFile excelFile = ((MultipartHttpServletRequest) request).getFile("excelFile");

    if (excelFile == null || excelFile.isEmpty()) {
      throw new RuntimeException("엑셀파일을 선택 해 주세요.");
    }
    
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss_");
    Calendar calendar = Calendar.getInstance();
    String strToday = simpleDateFormat.format(calendar.getTime());
    
    File destFile = new File(
            request.getSession().getServletContext().getInitParameter("excelPath") + excelFile
            .getOriginalFilename());
    
    if(destFile.exists()){
        destFile = new File(
                request.getSession().getServletContext().getInitParameter("excelPath") +strToday+excelFile
                        .getOriginalFilename());
    }
    
    try {
      excelFile.transferTo(destFile);
    } catch (IllegalStateException | IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }

    String result = builderService.replaceDictExcelUpload(destFile, map);
    destFile.delete();

    ModelAndView view = new ModelAndView("jsonView");
    view.addObject("status", result);
    return view;

  }

  @ResponseBody
  @RequestMapping(value = "/getNqaStatus")
  public Map<String, Object> getNqaStatus(@RequestBody Map param, HttpServletRequest request, HttpServletResponse response)
          throws Exception {

    return NqaUploadStatusList.getNqaStaus(param);
  }

}
