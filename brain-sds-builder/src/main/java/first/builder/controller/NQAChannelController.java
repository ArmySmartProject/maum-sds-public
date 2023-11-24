package first.builder.controller;

import first.builder.service.NQAGrpcService;
import first.builder.vo.Channel;
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
@RequestMapping("api/nqa/channels")
public class NQAChannelController {

  private static final Logger logger = LoggerFactory.getLogger(NQAChannelController.class);

  @Autowired
  private NQAGrpcService nqaGrpcService;

  /**
   * 채널 전체 목록 조회
   * @return
   */
  @RequestMapping(
      value = "",
      method = RequestMethod.GET)
  public ResponseEntity<?> getChannelList() {
    if(PropInfo.nqaYN.equals("N")){
      HashMap<String, String> result = new HashMap<>();
      result.put("message", "noNQA");
      return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    } else {
      logger.info("===== call api GET [[/api/nqa/channels]] getChannelList");
      try {
        List<Channel> result = nqaGrpcService.getChannelList();
        return new ResponseEntity<>(result, HttpStatus.OK);
      } catch (Exception e) {
        logger.error("{} => ", e.getMessage(), e);
        HashMap<String, String> result = new HashMap<>();
        result.put("message", "FAIL");
        result.put("error", e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  /**
   * 채널 ID로 채널 조회
   * @return
   */
  @RequestMapping(
      value = "/id/{channelId}",
      method = RequestMethod.GET)
  public ResponseEntity<?> getChannelById(@PathVariable int channelId) {
    if(PropInfo.nqaYN.equals("N")){
      HashMap<String, String> result = new HashMap<>();
      result.put("message", "noNQA");
      return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    } else {
      logger.info("===== call api POST [[/api/nqa/channels]] getChannelById");
      try {
        Channel result = nqaGrpcService.getChannelById(channelId);
        return new ResponseEntity<>(result, HttpStatus.OK);
      } catch (Exception e) {
        logger.error("{} => ", e.getMessage(), e);
        HashMap<String, String> result = new HashMap<>();
        result.put("message", "FAIL");
        result.put("error", e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  /**
   * 채널 추가
   * @param channel
   * @return
   */
  @RequestMapping(
      value = "/add",
      method = RequestMethod.POST)
  public ResponseEntity<?> addChannel(@RequestBody Channel channel) {
    if(PropInfo.nqaYN.equals("N")){
      HashMap<String, String> result = new HashMap<>();
      result.put("message", "noNQA");
      return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    } else {
      logger.info("===== call api POST [[/api/nqa/channels/add]] addChannel :: channel {}",
              channel);
      try {
        Channel result = nqaGrpcService.addChannel(channel);
        return new ResponseEntity<>(result, HttpStatus.OK);
      } catch (Exception e) {
        logger.error("{} => ", e.getMessage(), e);
        HashMap<String, String> result = new HashMap<>();
        result.put("message", "FAIL");
        result.put("error", e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  /**
   * 채널 수정
   * @param channel
   * @return
   */
  @RequestMapping(
      value = "/edit",
      method = RequestMethod.POST)
  public ResponseEntity<?> editChannel(@RequestBody Channel channel) {
    if(PropInfo.nqaYN.equals("N")){
      HashMap<String, String> result = new HashMap<>();
      result.put("message", "noNQA");
      return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    } else {
      logger
              .info("===== call api PUT [[/api/nqa/channels/edit]] editChannel :: channel {}",
                      channel);
      try {
        Channel result = nqaGrpcService.editChannel(channel);
        return new ResponseEntity<>(result, HttpStatus.OK);
      } catch (Exception e) {
        logger.error("{} => ", e.getMessage(), e);
        HashMap<String, String> result = new HashMap<>();
        result.put("message", "FAIL");
        result.put("error", e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  /**
   * 채널 삭제
   * @param channelId
   * @return
   */
  @RequestMapping(
      value = "/remove",
      method = RequestMethod.POST)
  public ResponseEntity<?> removeChannel(@RequestBody Integer channelId) {
    if(PropInfo.nqaYN.equals("N")){
      HashMap<String, String> result = new HashMap<>();
      result.put("message", "noNQA");
      return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    } else {
      logger.info("===== call api DELETE [[/api/nqa/channels/remove]] :: channelId {}", channelId);
      try {
        int result = nqaGrpcService.removeChannel(channelId);
        return new ResponseEntity<>(result, HttpStatus.OK);
      } catch (Exception e) {
        logger.error("{} => ", e.getMessage(), e);
        HashMap<String, String> result = new HashMap<>();
        result.put("message", "FAIL");
        result.put("error", e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }
}
