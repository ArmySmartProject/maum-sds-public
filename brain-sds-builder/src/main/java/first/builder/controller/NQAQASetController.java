package first.builder.controller;

import first.builder.service.BuilderService;
import first.builder.service.NQAGrpcService;
import first.builder.vo.*;
import first.common.util.MSExcelFileParser;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.annotation.Resource;

import first.common.util.NqaUploadStatusList;
import first.common.util.PropInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("api/nqa/qa-sets")
@PropertySource("classpath:nqa/config/nqa_config.properties")
public class NQAQASetController {

  private static final Logger logger = LoggerFactory.getLogger(NQAQASetController.class);

  @Autowired
  private NQAGrpcService nqaGrpcService;

  @Value("${excel.nqa.item.filename}")
  private String nqaFileName;

  @Value("#{'${excel.nqa.item.headers}'.split(',')}")
  private String[] nqaHeaders;

  @Resource(name = "builderService")
  private BuilderService builderService;

  /**
   * Index 된 keyword 조회
   */
  @RequestMapping(
      value = "/indexed-keywords/{channelId}/{categoryId}",
      method = RequestMethod.GET)
  public ResponseEntity<?> getIndexedKeywords(@PathVariable int channelId,
      @PathVariable int categoryId) {
    if(PropInfo.nqaYN.equals("N")){
      HashMap<String, String> result = new HashMap<>();
      result.put("message", "noNQA");
      return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    } else {
      logger
              .info("===== call api GET [[/api/nqa/qa-sets/indexed-keywords]] getIndexedKeywords");
      try {
        HashMap<String, Object> result = nqaGrpcService.getIndexedKeywords(channelId, categoryId);
        return new ResponseEntity<>(result, HttpStatus.OK);
      } catch (Exception e) {
        logger.error("getIndexedKeywords e : ", e);
        HashMap<String, String> result = new HashMap<>();
        result.put("message", "FAIL");
        result.put("error", e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  /**
   * 전체 Layer를 조회.
   */
  @RequestMapping(
      value = "/layers/category-id/{categoryId}",
      method = RequestMethod.GET)
  public ResponseEntity<?> getLayerList(@PathVariable int categoryId) {
    if(PropInfo.nqaYN.equals("N")){
      HashMap<String, String> result = new HashMap<>();
      result.put("message", "noNQA");
      return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    } else {
      logger.info("===== call api GET [[/api/nqa/qa-sets/layers/category-id]] categoryId {}",
              categoryId);
      try {
        List<Layer> result = nqaGrpcService.getLayerListByCategory(categoryId);
        return new ResponseEntity<>(result, HttpStatus.OK);
      } catch (Exception e) {
        logger.error("getLayerList e : ", e);
        HashMap<String, String> result = new HashMap<>();
        result.put("message", "FAIL");
        result.put("error", e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  /**
   * 카테고리 ID로 answer 목록을 조회
   */
  @RequestMapping(
      value = "/answers/category-id/{categoryId}/{startRow}/{endRow}/{sortModel}/{sortType}",
      method = RequestMethod.POST)
  public ResponseEntity<?> getAnswerList(@PathVariable int categoryId, @PathVariable int startRow,
      @PathVariable int endRow,
      @PathVariable String sortModel, @PathVariable String sortType,
      @RequestBody List<HashMap<String, String>> searchList) {
    if(PropInfo.nqaYN.equals("N")){
      HashMap<String, String> result = new HashMap<>();
      result.put("message", "noNQA");
      return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    } else {

      logger.info("===== call api [[/api/nqa/qa-sets/answers/category-id]] :: categoryId {}"
      );
      try {
        List<Answer> list = nqaGrpcService
                .getAnswerList(categoryId, startRow, endRow, sortModel, sortType, searchList);
        return new ResponseEntity<>(list, HttpStatus.OK);
      } catch (Exception e) {
        logger.error("getAnswerList e : ", e);
        HashMap<String, String> result = new HashMap<>();
        result.put("message", "FAIL");
        result.put("error", e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  /**
   * 카테고리 ID로 Q&A 수 조회
   */
  @RequestMapping(
      value = "/answers/qaCount",
      method = RequestMethod.POST)
  public ResponseEntity<?> getQaCount(@RequestBody Map<String, Integer> request) {
    if(PropInfo.nqaYN.equals("N")){
      HashMap<String, String> result = new HashMap<>();
      result.put("message", "noNQA");
      return new ResponseEntity<>(result, HttpStatus.NON_AUTHORITATIVE_INFORMATION);
    } else {
      logger.info("===== call api [[/api/nqa/qa-sets/answers/qaCount]]");
      try {
        List<Category> list = nqaGrpcService.getCategoryListByChannelId(request.get("channelId"));
        int categoryId = list.size() > 0 ? list.get(0).getId() : 0;
        Map<String, Integer> map = new HashMap<>();
        if(categoryId > 0) {
          map = nqaGrpcService.getQaCount(categoryId);
        } else {
          map.put("message", 0);
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
      } catch (Exception e) {
        logger.error("getAnswerList e : ", e);
        HashMap<String, String> result = new HashMap<>();
        result.put("message", "FAIL");
        result.put("error", e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  /**
   * channel 정보 조회
   */
  @RequestMapping(
      value = "/answers/getChannelInfo",
      method = RequestMethod.POST)
  public ResponseEntity<?> getChannelInfo(@RequestBody Map<String, Integer> request) {
    if(PropInfo.nqaYN.equals("N")){
      HashMap<String, String> result = new HashMap<>();
      result.put("message", "noNQA");
      return new ResponseEntity<>(result, HttpStatus.NON_AUTHORITATIVE_INFORMATION);
    } else {
      logger.info("===== call api [[/api/nqa/qa-sets/answers/getChannelInfo]]");
      try {
        int channelId = request.get("channelId");
        Map result = getChannelInfo(channelId);
        return new ResponseEntity<>(result, HttpStatus.OK);
      } catch (Exception e) {
        logger.error("getAnswerList e : ", e);
        HashMap<String, String> result = new HashMap<>();
        result.put("message", "FAIL");
        result.put("error", e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  /**
   * answerId로 answer 정보 조회
   */
  @RequestMapping(
      value = "/answers/{id}",
      method = RequestMethod.GET)
  public ResponseEntity<?> getAnswerById(@PathVariable int id) {
    if(PropInfo.nqaYN.equals("N")){
      HashMap<String, String> result = new HashMap<>();
      result.put("message", "noNQA");
      return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    } else {
      logger.info("===== call api GET [[/api/nqa/qa-sets/answers/{id}]] :: id {}", id);
      try {
        List<Answer> answerList = nqaGrpcService.getAnswerById(id);
        return new ResponseEntity<>(answerList, HttpStatus.OK);
      } catch (Exception e) {
        logger.error("getAnswerById e : ", e);
        HashMap<String, String> result = new HashMap<>();
        result.put("message", "FAIL");
        result.put("error", e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  /**
   * answer 추가
   */
  @RequestMapping(
      value = "/answers/add",
      method = RequestMethod.POST)
  public ResponseEntity<?> addAnswer(@RequestBody Answer answer) {
    if(PropInfo.nqaYN.equals("N")){
      HashMap<String, String> result = new HashMap<>();
      result.put("message", "noNQA");
      return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    } else {
      logger.info("===== call api [[/api/nqa/qa-sets/answers/add]] :: answer {}", answer);
      try {
        Answer result = nqaGrpcService.addAnswer(answer);
        // todo : id 가 없으면 throw Exception;
        return new ResponseEntity<>(result, HttpStatus.OK);
      } catch (Exception e) {
        logger.error("addAnswer e : ", e);
        HashMap<String, String> result = new HashMap<>();
        result.put("message", "FAIL");
        result.put("error", e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  /**
   * answer 수정
   */
  @RequestMapping(
      value = "/answers/edit",
      method = RequestMethod.POST)
  public ResponseEntity<?> editAnswer(@RequestBody Answer answer) {
    if(PropInfo.nqaYN.equals("N")){
      HashMap<String, String> result = new HashMap<>();
      result.put("message", "noNQA");
      return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    } else {
      logger.info("===== call api [[/api/nqa/qa-sets/answers/edit]] :: answer {}", answer);
      try {
        Answer result = nqaGrpcService.editAnswer(answer);
        return new ResponseEntity<>(result, HttpStatus.OK);
      } catch (Exception e) {
        logger.error("editAnswer e : ", e);
        HashMap<String, String> result = new HashMap<>();
        result.put("message", "FAIL");
        result.put("error", e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  /**
   * answer 삭제
   *
   * @param answerIdSets (answer Id, answer Copy Id 배열)
   */
  @RequestMapping(
      value = "/answers/remove",
      method = RequestMethod.POST)
  public ResponseEntity<?> removeAnswer(@RequestBody List<Map<String, Integer>> answerIdSets) {
    if(PropInfo.nqaYN.equals("N")){
      HashMap<String, String> result = new HashMap<>();
      result.put("message", "noNQA");
      return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    } else {
      logger.info("===== call api [[/api/nqa/qa-sets/answers/remove]] :: answerIdSets {}",
              answerIdSets);
      try {
        int result = nqaGrpcService.removeAnswers(answerIdSets);
        return new ResponseEntity<>(result, HttpStatus.OK);
      } catch (Exception e) {
        logger.error("removeAnswer e : ", e);
        HashMap<String, String> result = new HashMap<>();
        result.put("message", "FAIL");
        result.put("error", e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  /* 카테고리별 업로드 기능 */

  @RequestMapping(
      value = "/upload-files",
      method = RequestMethod.POST)
  public ResponseEntity<?> insertFile(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    if(PropInfo.nqaYN.equals("N")){
      HashMap<String, String> result = new HashMap<>();
      result.put("message", "noNQA");
      return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    } else {
      Map<String, Object> map = new HashMap<>();
      int channelId = Integer.parseInt(request.getParameter("host"));
      String hostName = request.getParameter("hostName");

      Map<String, Object> status = new HashMap<>();
      status.put("host", channelId);
      try {
        status.put("nqaUploadStatus", "uploading");
        NqaUploadStatusList.nqaUploadStatusUpdate(status);
      }catch (Exception e){
        status.put("nqaUploadStatus", "fail");
        NqaUploadStatusList.nqaUploadStatusUpdate(status);
      }

      logger.info("======= call api  [[/api/nqa/qa-sets/upload-files]] :: channelId : {}, categoryId : {}", channelId);

      List<String> befReplace = new ArrayList<>();
      List<String> aftReplace = new ArrayList<>();
      try {//치환사전 리스트 가져옴
        Map<String, Object> rDict = new HashMap<>();
        rDict.put("nowHost", channelId);
        List<Map<String, Object>> replaceDictList = builderService.selectReplaceDict(rDict);
        for (int ii = 0; ii < replaceDictList.size(); ii++) {
          String nowBefore = (String) replaceDictList.get(ii).get("Before");
          String nowAfter = (String) replaceDictList.get(ii).get("After");
          if (nowBefore.length() > 0) {
            befReplace.add(nowBefore);
            aftReplace.add(nowAfter);
          }
        }
      } catch (Exception e) {

      }

      MultipartFile excelFile = ((MultipartHttpServletRequest) request).getFile("excelFile");
      System.out.println("엑셀 파일 업로드 컨트롤러");
      if (excelFile == null || excelFile.isEmpty()) {
        throw new RuntimeException("엑셀파일을 선택 해 주세요.");
      }

      int targetSheetNum = 0;
      int categoryId = -1;
      DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      java.sql.Date date = new java.sql.Date(new java.util.Date().getTime());
      String currentDtm = format.format(date);

      try {
        Map channelInfo = getChannelInfo(channelId);

        // channel info 가져오기 실패시 exception 발생시키기
        // 정상적으로 가져온 경우 200
        if (Integer.parseInt(channelInfo.get("status").toString()) == 500) {
          throw new Exception("get channel info failed");
        }

        Channel newChannel = new Channel();
        newChannel.setId(channelId);
        newChannel.setName(hostName);
        newChannel.setUpdateDtm(currentDtm);

        // isValid 값이 true인 경우, DB에 channel이 존재
        if (!Boolean.parseBoolean(channelInfo.get("isValid").toString())) {
          newChannel.setCreateDtm(currentDtm);
          Channel channel = nqaGrpcService.addChannel(newChannel);
        } else {
          Channel channel = nqaGrpcService.editChannel(newChannel);
        }

        List<Category> categoryList = nqaGrpcService.getCategoryListByChannelId(channelId);
        Category category = new Category();
        if (categoryList.size() > 0) {
          categoryId = categoryList.get(0).getId();
        } else {
          category.setChannelId(channelId);
          category.setName("공통");
          category.setCreateDtm(date);
          category.setUpdateDtm(date);
          category = nqaGrpcService.addCategory(category);
          categoryId = category.getId();
        }

        InputStream inputStream = excelFile.getInputStream();

        // 엑셀 파일 파싱
        MSExcelFileParser excelParser = new MSExcelFileParser(inputStream, targetSheetNum,
                nqaHeaders, false);
        // todo: category이름을 key로 쓰는 경우 많으므로, 중복 insert 안나게 추가 처리해줘야 함.
        Map<String, Integer> categoryMap = new HashMap<>();
        List<Map<String, String>> dataList = excelParser.getData();
        List<Map<String, Object>> qaSetList = new ArrayList<>();

        // data : 1 Row
        for (Map<String, String> data : dataList) {
          Answer answer = new Answer();
          Question question = new Question();
          Map<String, Object> qaSetMap = new HashMap<>();

          answer.setCategoryId(categoryId);
          answer.setAnswer(data.get("Intent"));
          question.setQuestion(replaceDictString(data.get("Question"), befReplace, aftReplace));

          qaSetMap.put("answer", answer);
          qaSetMap.put("question", question);
          qaSetList.add(qaSetMap);
        }

        nqaGrpcService.uploadFiles(qaSetList, channelId, categoryId);

        HashMap<String, Object> result = new HashMap<>();
        result.put("status", "success");

        return new ResponseEntity<>(result, HttpStatus.OK);

      } catch (Exception e) {
        logger.error("upload-files: {}", e.getMessage());
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Internal Server Error");
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  @RequestMapping(
      value = "/download-file",
      method = RequestMethod.POST)
  public ResponseEntity<?> downloadFile(@RequestBody Map request) throws IOException {
    if(PropInfo.nqaYN.equals("N")){
      HashMap<String, Object> result = new HashMap<>();
      result.put("message", "noNQA");
      return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    } else {
      logger.info("======= call api  [[/api/nqa/qa-sets/download-file/]] ::: channelId : {}", request.get("channelId"));

      int channelId = Integer.parseInt(request.get("channelId").toString());

      try {
        List<Category> list = nqaGrpcService.getCategoryListByChannelId(channelId);
        if(list.size() < 1) throw new IndexOutOfBoundsException("noNqaData");

        int categoryId = list.get(0).getId();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders
                .add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + nqaFileName
                        + "-" + nqaGrpcService.getCategoryById(categoryId).getName() + ".xlsx");
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");

        List<HashMap<String, String>> searchList = new ArrayList<>();
        HashMap<String, String> map = new HashMap<>();
        if (request.get("searchField") != null && !"".equals(request.get("searchField")) &&
                request.get("searchValue") != null && !"".equals(request.get("searchValue"))) {
          map.put("searchField", request.get("searchField").toString());
          map.put("searchValue", request.get("searchValue").toString());

          searchList.add(map);
        }
        return new ResponseEntity<>(nqaGrpcService.downloadFile(categoryId, -1, "", "", searchList),
                httpHeaders, HttpStatus.OK);
      } catch (IndexOutOfBoundsException e) {
        logger.error("not exist channelId or categoryId e: ",
                    e.getMessage().equals("noNqaData") ?  e.getMessage() : e);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Category or Channel Id Not Found");
        // 이전 요청이 실패하였기 때문에 지금의 요청도 실패하였습니다. (error code = 424)
        // 카테고리 아이디를 못가져와서 에러 발생한 것이기 때문에 FAILED_DEPENDENCY retutn
        return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
      } catch (Exception e) {
        logger.error("downloadFile e : ", e);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Internal Server Error");
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  @ResponseBody
  @RequestMapping(value = "/insertNqaAnswer")
  public Map<String, Object> insertNqaAnswer(@RequestBody Map param, HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    if(PropInfo.nqaYN.equals("N")){
      HashMap<String, Object> result = new HashMap<>();
      result.put("message", "noNQA");
      response.setStatus(404);
      return result;
    } else {
      Map<String, Object> map = new HashMap<>();

      String nqaAnswer = param.get("answer").toString();
      int answerId = 0;
      if (param.get("answerId").toString() != null && !param.get("answerId").toString().equals("")) {
        answerId = Integer.parseInt(param.get("answerId").toString());
      }
      int categoryId = 0;
      if (param.get("categoryId").toString() != null && !param.get("categoryId").toString()
              .equals("")) {
        categoryId = Integer.parseInt(param.get("categoryId").toString());
      }
      int channelId = Integer.parseInt(param.get("channelId").toString());
      String channelName = param.get("channelName").toString();
      String nqaQuestion = param.get("question").toString();
      Answer answer = new Answer();
      List<Question> questionList = new ArrayList<>();
      Question question = new Question();
      question.setQuestion(nqaQuestion);
      questionList.add(question);
      answer.setAnswer(nqaAnswer);
      if (answerId != 0) {
        answer.setId(answerId);
      }
      answer.setAddedQuestions(questionList);
      Channel channel = new Channel();
      Category category;
      channel.setId(channelId);
      channel.setName(channelName);

      try {
        if (categoryId == 0) {
          map = nqaGrpcService.addDefaultChannelCategory(channel);
          category = (Category) map.get("category");
          answer.setCategoryId(category.getId());
        } else {
          answer.setCategoryId(categoryId);
        }
        answer = nqaGrpcService.addAnswer(answer);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.sql.Date date = new java.sql.Date(new java.util.Date().getTime());
        String currentDtm = format.format(date);
        channel.setUpdateDtm(currentDtm);
        channel = nqaGrpcService.editChannel(channel);
        System.out.println("channel : " + channel);
        map.put("result", "success");
      } catch (Exception e) {
        logger.error("addAnswer e : ", e);
        map.put("result", "fail");
      }
      map.put("answerId", answer.getId());
      return map;
    }
  }

  @ResponseBody
  @RequestMapping(value = "/editNqaAnswer")
  public Map<String, Object> editNqaAnswer(@RequestBody Map param, HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    if(PropInfo.nqaYN.equals("N")){
      HashMap<String, Object> result = new HashMap<>();
      result.put("message", "noNQA");
      response.setStatus(404);
      return result;
    } else {
      Map<String, Object> map = new HashMap<>();
      int channelId = Integer.parseInt(param.get("channelId").toString());
      String channelName = param.get("channelName").toString();
      String nqaAnswer = param.get("answer").toString();
      int answerId = Integer.parseInt(param.get("answerId").toString());
      int questionId = Integer.parseInt(param.get("questionId").toString());
      String nqaQuestion = param.get("question").toString();
      Answer answer = new Answer();
      List<Question> emptyList = new ArrayList<>();
      List<Question> questionList = new ArrayList<>();
      Question question = new Question();
      Channel channel = new Channel();
      channel.setId(channelId);
      channel.setName(channelName);
      question.setQuestion(nqaQuestion);
      question.setId(questionId);
      questionList.add(question);
      answer.setAnswer(nqaAnswer);
      answer.setId(answerId);
      answer.setEditedQuestions(questionList);
      answer.setAddedQuestions(emptyList);
      answer.setRemovedQuestions(emptyList);
      Answer result = new Answer();
      try {
        result = nqaGrpcService.editAnswer(answer);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.sql.Date date = new java.sql.Date(new java.util.Date().getTime());
        String currentDtm = format.format(date);
        channel.setUpdateDtm(currentDtm);
        channel = nqaGrpcService.editChannel(channel);
        System.out.println("channel : " + channel);
        // todo : id 가 없으면 throw Exception;
        map.put("result", "success");
      } catch (Exception e) {
        logger.error("addAnswer e : ", e);
        map.put("result", "fail");
      }

      map.put("answerId", result.getId());
      return map;
    }
  }

  @ResponseBody
  @RequestMapping(value = "/deleteNqaAnswer")
  public Map<String, Object> deleteNqaAnswer(@RequestBody Map param, HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    if(PropInfo.nqaYN.equals("N")){
      HashMap<String, Object> result = new HashMap<>();
      result.put("message", "noNQA");
      response.setStatus(404);
      return result;
    } else {
      Map<String, Object> map = new HashMap<>();
      String nqaAnswer = param.get("answer").toString();
      int channelId = Integer.parseInt(param.get("channelId").toString());
      String channelName = param.get("channelName").toString();
      int answerId = Integer.parseInt(param.get("answerId").toString());
      int questionId = Integer.parseInt(param.get("questionId").toString());
      Answer answer = new Answer();
      List<Question> emptyList = new ArrayList<>();
      List<Question> questionList = new ArrayList<>();
      Question question = new Question();
      Channel channel = new Channel();
      channel.setId(channelId);
      channel.setName(channelName);
      question.setId(questionId);
      questionList.add(question);
      answer.setAnswer(nqaAnswer);
      answer.setId(answerId);
      answer.setEditedQuestions(emptyList);
      answer.setAddedQuestions(emptyList);
      answer.setRemovedQuestions(questionList);
      try {
        Answer result = nqaGrpcService.editAnswer(answer);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.sql.Date date = new java.sql.Date(new java.util.Date().getTime());
        String currentDtm = format.format(date);
        channel.setUpdateDtm(currentDtm);
        channel = nqaGrpcService.editChannel(channel);
        System.out.println("channel : " + channel);
        // todo : id 가 없으면 throw Exception;
        map.put("result", "success");
      } catch (Exception e) {
        logger.error("addAnswer e : ", e);
        map.put("result", "fail");
      }
      return map;
    }
  }

  @ResponseBody
  @RequestMapping(value = "/deleteNqaAnswerList")
  public Map<String, Object> deleteNqaAnswerList(@RequestBody List<Map> paramLst, HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    if(PropInfo.nqaYN.equals("N")){
      HashMap<String, Object> result = new HashMap<>();
      result.put("message", "noNQA");
      response.setStatus(404);
      return result;
    } else {
      Map<String, Object> map = new HashMap<>();
      System.out.println("Test delete answerList ~~~~!!!!! ");

      try {

        for (Map param : paramLst) {
          System.out.println("param : " + param);
          String nqaAnswer = param.get("answer").toString();
          int channelId = Integer.parseInt(param.get("channelId").toString());
          String channelName = param.get("channelName").toString();
          int answerId = Integer.parseInt(param.get("answerId").toString());
          int questionId = Integer.parseInt(param.get("questionId").toString());
          Answer answer = new Answer();
          List<Question> emptyList = new ArrayList<>();
          List<Question> questionList = new ArrayList<>();
          Question question = new Question();
          Channel channel = new Channel();
          channel.setId(channelId);
          channel.setName(channelName);
          question.setId(questionId);
          questionList.add(question);
          answer.setAnswer(nqaAnswer);
          answer.setId(answerId);
          answer.setEditedQuestions(emptyList);
          answer.setAddedQuestions(emptyList);
          answer.setRemovedQuestions(questionList);

          Answer result = nqaGrpcService.editAnswer(answer);
          DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          java.sql.Date date = new java.sql.Date(new java.util.Date().getTime());
          String currentDtm = format.format(date);
          channel.setUpdateDtm(currentDtm);
          channel = nqaGrpcService.editChannel(channel);
          System.out.println("channel : " + channel);
        }
        // todo : id 가 없으면 throw Exception;
        map.put("result", "success");
      } catch (Exception e) {
        logger.error("addAnswer e : ", e);
        map.put("result", "fail");
      }
      return map;
    }
  }

  @ResponseBody
  @RequestMapping(value = "/getNqaDetail")
  public Map<String, Object> getNqaDetail(@RequestBody Map param, HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    if(PropInfo.nqaYN.equals("N")){
      HashMap<String, Object> result = new HashMap<>();
      result.put("message", "noNQA");
      response.setStatus(404);
      return result;
    } else {
      Map<String, Object> map = new HashMap<>();
      List<Category> categories;
      List<Answer> answerList;
      List<Answer> finalResult = new ArrayList<>();
      List<HashMap<String, String>> searchList = new ArrayList<>();
      List<Question> questionList = new ArrayList<>();
      try {
        if (param.get("answerId") != null && !param.get("answerId").equals("")) {
          answerList = nqaGrpcService.getAnswerById(Integer.parseInt(param.get("answerId").toString()));
          questionList = answerList.get(0).getQuestions();
        }
      } catch (Exception e) {
        logger.error("{} => ", e.getMessage(), e);
      }
      map.put("questionList", questionList);
      return map;
    }
  }

  private Map getChannelInfo(int channelId) {
    Map result = new HashMap();
    try {
      List<Channel> channelList = nqaGrpcService.getChannelList();

      boolean channelValid = false;
      for (int i = 0; i < channelList.size(); i++) {
        if (channelList.get(i).getId() == channelId) {
          channelValid = true;
          result.put("channelInfo", channelList.get(i));
          break;
        }
      }
      result.put("isValid", channelValid);
      result.put("status", 200);

      return result;
    } catch (Exception e) {
      System.out.println("getChannelInfo error");
      System.out.println(e);
      result.put("status", 500);
      result.put("errorMsg", e.getMessage());
      return result;
    }
  }

  private String replaceDictString(String origText, List<String> beforeList, List<String> afterList){
    String newText = origText;
    newText = addNString(newText);
    try{
      for(int ii = 0; ii<beforeList.size(); ii++){
        String nowBefore = beforeList.get(ii);
        String nowAfter  = afterList.get(ii);
        nowBefore = addNString(nowBefore);
        nowAfter = addYString(nowAfter);
        if(newText.contains(nowBefore)){
          if(newText.indexOf(nowBefore)%2==0){
            newText = newText.replace(nowBefore, nowAfter);
          }
        }
      }
      newText = remYNString(newText);
    }catch (Exception e){
      newText = origText;
    }
    return newText;
  }

  public String addNString(String before){
    String newText = "";
    for(int i = 0; i<before.length(); i++){
      newText += "N" + before.charAt(i);
    }
    return newText;
  }

  public String addYString(String before){
    String newText = "";
    for(int i = 0; i<before.length(); i++){
      newText += "Y" + before.charAt(i);
    }
    return newText;
  }

  public String remYNString(String before){
    String newText = "";
    for(int i = 1; i<before.length(); i = i+2){
      newText += before.charAt(i);
    }
    return newText;
  }

}
