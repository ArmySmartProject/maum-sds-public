package first.builder.controller;

import first.builder.service.NQAGrpcService;
import first.builder.vo.Category;
import first.builder.vo.Channel;
import first.builder.vo.IndexStatusEntity;
import first.common.util.PropInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/nqa/indexing")
public class NQAIndexingController {

  private static final Logger logger = LoggerFactory.getLogger(NQAIndexingController.class);

  @Autowired
  private NQAGrpcService nqaGrpcService;

  /**
   * Indexing 페이지에서 채널 전체 목록 을 가져오는 API
   * @return
   */
  @RequestMapping(
      value = "/channels",
      method = RequestMethod.GET)
  public ResponseEntity<?> getChannelList() {
    if(PropInfo.nqaYN.equals("N")){
      HashMap<String, String> result = new HashMap<>();
      result.put("message", "noNQA");
      return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    } else {
      logger.info("===== call api GET [[/api/nqa/indexing/channels]] getChannelList");
      try {
        List<Channel> result = nqaGrpcService.getChannelList();
        return new ResponseEntity<>(result, HttpStatus.OK);
      } catch (Exception e) {
        logger.error("getChannelList e : ", e);
        HashMap<String, String> result = new HashMap<>();
        result.put("message", "FAIL");
        result.put("error", e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  /**
   * Indexing 페이지에서 카테고리 전체 목록 을 가져오는 API
   * @return
   */
  @RequestMapping(
      value = "/categories",
      method = RequestMethod.POST)
  public ResponseEntity<?> getCategoryListByChannelId(@RequestBody Integer channelId) {
    if(PropInfo.nqaYN.equals("N")){
      HashMap<String, String> result = new HashMap<>();
      result.put("message", "noNQA");
      return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    } else {
      logger.info("===== call api GET [[/api/nqa/indexing/categories]] getCategoryListByChannelId");
      try {
        List<Category> result = nqaGrpcService.getCategoryListByChannelId(channelId);
        return new ResponseEntity<>(result, HttpStatus.OK);
      } catch (Exception e) {
        logger.error("getCategoryListByChannelId e : ", e);
        HashMap<String, String> result = new HashMap<>();
        result.put("message", "FAIL");
        result.put("error", e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  /**
   * fullIndexing 요청 API
   */
  @RequestMapping(
      value = "/fullIndexing",
      method = RequestMethod.POST)
  public ResponseEntity<?> fullIndexing(@RequestBody Map<String, Integer> request) throws IOException {
    if(PropInfo.nqaYN.equals("N")){
      HashMap<String, String> result = new HashMap<>();
      result.put("message", "noNQA");
      return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    } else {
      logger.info("======= call api  [[/api/nqa/indexing/fullIndexing]] =======");

      try {
        int channelId = request.get("channelId");
        List<Category> list = nqaGrpcService.getCategoryListByChannelId(channelId);
        int categoryId = list.size() > 0 ? list.get(0).getId() : 0;
        // 0 : Question
        // 1 : Answer
        // 2 : both
        int collectionType = 2;

        IndexStatusEntity result = nqaGrpcService.fullIndexing(channelId, categoryId, collectionType);
        return new ResponseEntity<>(result, HttpStatus.OK);
      } catch (IndexOutOfBoundsException e) {
        logger.error("not exist channelId or categoryId e: ", e);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Category or Channel Id Not Found");
        return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
      } catch (Exception e) {
        logger.error("fullIndexing e : ", e);
        HashMap<String, String> result = new HashMap<>();
        result.put("message", "FAIL");
        result.put("error", e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  /**
   * additionalIndexing 요청
   * @param channelId
   * @param categoryId
   * @param collectionType
   * @return
   */
//  @RequestMapping(
//      value = "/additionalIndexing/{channelId}/{categoryId}/{collectionType}",
//      method = RequestMethod.GET)
//  public ResponseEntity<?> additionalIndexing(@PathVariable Integer channelId,
//      @PathVariable Integer categoryId, @PathVariable Integer collectionType) {
//    logger.info("======= call api  [[/api/nqa/indexing/additionalIndexing/"
//            + "{channelId}/{categoryId}/{collectionType}]] =======");
//    try {
//      IndexStatusEntity result = nqaGrpcService.additionalIndexing(channelId, categoryId, collectionType);
//      return new ResponseEntity<>(result, HttpStatus.OK);
//    } catch (Exception e) {
//      logger.error("additionalIndexing e : " , e);
//      HashMap<String, String> result = new HashMap<>();
//      result.put("message", "FAIL");
//      result.put("error", e.getMessage());
//      return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//  }

  /**
   * 현재 Indexing 정보 조회
   * @return
   */
  @RequestMapping(
      value = "/getIndexingStatus",
      method = RequestMethod.POST)
  public ResponseEntity<?> getIndexingStatus(@RequestBody Map request) {
    if(PropInfo.nqaYN.equals("N")){
      HashMap<String, String> result = new HashMap<>();
      result.put("message", "noNQA");
      return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    } else {
      logger.info("======= call api  [[/api/nqa/indexing/getIndexingStatus]] =======");
      try {
        int channelId = Integer.parseInt(request.get("channelId").toString());
        Boolean checkTrainingHost = Boolean.parseBoolean(request.get("checkTrainingHost").toString());
        List<Category> list = nqaGrpcService.getCategoryListByChannelId(channelId);
        int categoryId = list.size() > 0 ? list.get(0).getId() : 0;
        IndexStatusEntity result = nqaGrpcService.getIndexingStatus(channelId, categoryId, checkTrainingHost);
        result.setLatestIndexingHistory(null);

        return new ResponseEntity<>(result, HttpStatus.OK);
      } catch (Exception e) {
        logger.error("getIndexingStatus e : ", e);
        HashMap<String, String> result = new HashMap<>();
        result.put("message", "FAIL");
        result.put("error", e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  /**
   * abortIndexing 요청
   * @param collectionType
   * @return
   */
//  @RequestMapping(
//      value = "/abortIndexing/{collectionType}",
//      method = RequestMethod.GET)
//  public ResponseEntity<?> abortIndexing(@PathVariable Integer collectionType) {
//    logger.info("======= call api  [[/api/nqa/indexing/abortIndexing/{collectionType}]] =======");
//    try {
//      IndexStatusEntity result = nqaGrpcService.abortIndexing(collectionType);
//      return new ResponseEntity<>(result, HttpStatus.OK);
//    } catch (Exception e) {
//      logger.error("abortIndexing e : " , e);
//      HashMap<String, String> result = new HashMap<>();
//      result.put("message", "FAIL");
//      result.put("error", e.getMessage());
//      return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//  }

  /**
   * fullIndexing 요청 API
   */
  @RequestMapping(
      value = "/getIndexingHistory",
      method = RequestMethod.POST)
  public ResponseEntity<?> indexingHistory(@RequestBody Map<String, Integer> request) throws IOException {
    if(PropInfo.nqaYN.equals("N")){
      HashMap<String, String> result = new HashMap<>();
      result.put("message", "noNQA");
      return new ResponseEntity<>(result, HttpStatus.NON_AUTHORITATIVE_INFORMATION);
    } else {
      logger.info("======= call api  [[/api/nqa/indexing/getIndexingHistory]] =======");

      try {
        int channelId = request.get("channelId");
        List<Category> list = nqaGrpcService.getCategoryListByChannelId(channelId);
        int categoryId = list.size() > 0 ? list.get(0).getId() : 0;
        Map result = nqaGrpcService.getIndexingHistory(channelId, categoryId);
        return new ResponseEntity<>(result, HttpStatus.OK);
      } catch (Exception e) {
        logger.error("getIndexingHistory e : ", e);
        HashMap<String, String> result = new HashMap<>();
        result.put("message", "FAIL");
        result.put("error", e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }
}
