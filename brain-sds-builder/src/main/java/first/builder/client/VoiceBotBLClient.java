package first.builder.client;

import com.google.gson.Gson;
import lombok.Data;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

import first.common.util.PropInfo;

@Component
public class VoiceBotBLClient {

  private static final Logger logger = LoggerFactory.getLogger(VoiceBotBLClient.class);
  private Gson gson;
  private String method;

  @Autowired
  public VoiceBotBLClient() {
    gson = new Gson();
    method = "";
  }

  @Data
  public class VoiceBotBlVO {
    private String maum_sds_url = "http://localhost:6941/collect/run/utter";
    public String svc_host;
    private String campaign = "116";
    private String campaign_id = "116";
    public String contract_no;
    public String tester;
    public String call_name;
    public String call_tel;
    public String lang = "1";
  }

  public String findTeseterInfo(Map request) {
    VoiceBotBlVO voiceBotBL = new VoiceBotBlVO();
    
    if (request.get("lang").toString().equals("2")) {
      voiceBotBL.setLang(request.get("lang").toString());
      voiceBotBL.setCampaign("121");
      voiceBotBL.setCampaign_id("121");
    }
    voiceBotBL.setTester(request.get("tester").toString());
    return this.sendHttp(voiceBotBL, "/get/ob/recent", PropInfo.blPort1);
  }

  public String saveTesterInfo(Map request) {
    VoiceBotBlVO voiceBotBL = new VoiceBotBlVO();

    if (request.get("lang").toString().equals("2")) {
      voiceBotBL.setLang(request.get("lang").toString());
      voiceBotBL.setCampaign("121");
      voiceBotBL.setCampaign_id("121");
    }
    voiceBotBL.setSvc_host(request.get("svc_host").toString());
    voiceBotBL.setTester(request.get("tester").toString());
    voiceBotBL.setCall_name(request.get("call_name").toString());
    voiceBotBL.setCall_tel(request.get("call_tel").toString());
    return this.sendHttp(voiceBotBL, "/make/ob/call", PropInfo.blPort1);
  }

  public String callList(Map request) {
    VoiceBotBlVO voiceBotBL = new VoiceBotBlVO();

    voiceBotBL.setContract_no(request.get("contract_no").toString());
    return this.sendHttp(voiceBotBL, "/get/callList", PropInfo.blPort2);
  }

  private String sendHttp(VoiceBotBlVO request, String method, int port) {
    logger.debug("voiceBotBLClient method: {}, request: {}", method, request);
    try {
      HttpClient client = HttpClientBuilder.create().build();
      HttpPost post = new HttpPost("http://" + PropInfo.blIP + ":" + port + this.method.concat(method));

      post.setEntity(
          new StringEntity(gson.toJson(request), Consts.UTF_8)
      );

      post.setHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);

      HttpResponse response = client.execute(post);
      if (response.getStatusLine().getStatusCode() == 200) {
        return EntityUtils.toString(response.getEntity(), "UTF-8");
      } else {
        logger.info("http response isn't 200");
        return "WRONG";
      }
    } catch (IOException e) {
      logger.info("<err>".concat(e.getMessage()));
      return "ERROR";
    }
  }


}
