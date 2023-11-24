package first.builder.controller;

import first.builder.service.NQAGrpcService;
import first.builder.vo.Category;
import java.util.HashMap;
import java.util.List;

import first.common.util.PropInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/nqa/categories")
public class NQACategoryController {

  private static final Logger logger = LoggerFactory.getLogger(NQACategoryController.class);

  @Autowired
  private NQAGrpcService nqaGrpcService;

  /**
   * 채널별 카테고리 전체 목록 조회
   * @return
   */
  @RequestMapping(
      value = "/channel-id/{channelId}",
      method = RequestMethod.GET)
  public ResponseEntity<?> getCategoryListByChannelId(@PathVariable int channelId) {
    if(PropInfo.nqaYN.equals("N")){
      HashMap<String, String> result = new HashMap<>();
      result.put("message", "noNQA");
      return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }else{
      logger.info("===== call api POST [[/api/nqa/categories/channel-id/{channelId}]] :: channelId {}", channelId);
      try {
        List<Category> result = nqaGrpcService.getCategoryListByChannelId(channelId);
        return new ResponseEntity<>(result, HttpStatus.OK);
      } catch (Exception e) {
        logger.error("getCategoryListByChannelId e : " , e);
        HashMap<String, String> result = new HashMap<>();
        result.put("message", "FAIL");
        result.put("error", e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  /**
   * 카테고리 전체 목록 조회
   * @return
   */
  @RequestMapping(
      value = "/name",
      method = RequestMethod.POST)
  public ResponseEntity<?> getCategoryListByName(@RequestBody Category category) {
    if(PropInfo.nqaYN.equals("N")){
      HashMap<String, String> result = new HashMap<>();
      result.put("message", "noNQA");
      return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }else {
      logger.info("===== call api POST [[/api/nqa/categories/name]] getCategoryListByName");
      try {
        List<Category> result = nqaGrpcService.getCategoryListByName(category);
        return new ResponseEntity<>(result, HttpStatus.OK);
      } catch (Exception e) {
        logger.error("getCategoryListByName e : ", e);
        HashMap<String, String> result = new HashMap<>();
        result.put("message", "FAIL");
        result.put("error", e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }


  /**
   * 카테고리 ID 로 카테고리 정보 조회
   * @param categoryId
   * @return
   */
  @RequestMapping(
      value = "/{categoryId}",
      method = RequestMethod.GET)
  public ResponseEntity<?> getCategoryById(@PathVariable int categoryId) {
    if(PropInfo.nqaYN.equals("N")){
      HashMap<String, String> result = new HashMap<>();
      result.put("message", "noNQA");
      return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }else {
      logger.info(
              "===== call api GET [[/api/nqa/categories/{categoryId}]] getCategoryById :: categoryId {}",
              categoryId);
      try {
        Category result = nqaGrpcService.getCategoryById(categoryId);
        return new ResponseEntity<>(result, HttpStatus.OK);
      } catch (Exception e) {
        logger.error("getCategoryById e : ", e);
        HashMap<String, String> result = new HashMap<>();
        result.put("message", "FAIL");
        result.put("error", e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  /**
   * 카테고리 추가
   * @param category
   * @return
   */
  @RequestMapping(
      value = "/add",
      method = RequestMethod.POST)
  public ResponseEntity<?> addCategory(@RequestBody Category category) {
    if(PropInfo.nqaYN.equals("N")){
      HashMap<String, String> result = new HashMap<>();
      result.put("message", "noNQA");
      return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }else {
      logger.info("===== call api POST [[/api/nqa/categories/add]] addCategory :: category {}",
              category);
      try {
        // todo: channelId, name, creatorId check.
        Category result = nqaGrpcService.addCategory(category);
        return new ResponseEntity<>(result, HttpStatus.OK);
      } catch (Exception e) {
        logger.error("addCategory e : ", e);
        HashMap<String, String> result = new HashMap<>();
        result.put("message", "FAIL");
        result.put("error", e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  /**
   * 카테고리 수정
   * @param category
   * @return
   */
  @RequestMapping(
      value = "/edit",
      method = RequestMethod.POST)
  public ResponseEntity<?> editCategory(@RequestBody Category category) {
    if(PropInfo.nqaYN.equals("N")){
      HashMap<String, String> result = new HashMap<>();
      result.put("message", "noNQA");
      return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }else {
      logger
              .info("===== call api PUT [[/api/nqa/categories/edit]] editCategory :: category {}",
                      category);
      try {
        // todo : name, updaterId 필요
        Category result = nqaGrpcService.editCategory(category);
        return new ResponseEntity<>(result, HttpStatus.OK);
      } catch (Exception e) {
        logger.error("editCategory e : ", e);
        HashMap<String, String> result = new HashMap<>();
        result.put("message", "FAIL");
        result.put("error", e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  /**
   * 카테고리 삭제
   * @param categoryIds
   * @return
   */
  @RequestMapping(
      value = "/remove",
      method = RequestMethod.POST)
  public ResponseEntity<?> removeCategory(@RequestBody List<Integer> categoryIds) {
    if(PropInfo.nqaYN.equals("N")){
      HashMap<String, String> result = new HashMap<>();
      result.put("message", "noNQA");
      return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    } else {
      logger.info("===== call api DELETE [[/api/nqa/categories/remove]] :: categoryIds {}",
              categoryIds);
      try {
        int result = nqaGrpcService.removeCategory(categoryIds);
        return new ResponseEntity<>(result, HttpStatus.OK);
      } catch (Exception e) {
        logger.error("removeCategory e : ", e);
        HashMap<String, String> result = new HashMap<>();
        result.put("message", "FAIL");
        result.put("error", e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }
}
