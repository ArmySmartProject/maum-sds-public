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
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import first.common.util.PropInfo;

@Component
public class CallManagerClient {

  private static final Logger logger = LoggerFactory.getLogger(CallManagerClient.class);
  private Gson gson;
  private String method;

  @Autowired
  public CallManagerClient() {
    gson = new Gson();
    method = "/call";
  }

  @Data
  public class CallManagerVO {
    private String eventType = "STT";
    public String event;
    private String caller = "simplebot";
    private String agent = "simplebot";
    public String contractNo;
    public String campaignId = "116";
  }

  public Object callStart(Map request) {
    CallManagerVO callManager = new CallManagerVO();

    callManager.setEvent("START");
    callManager.setContractNo(request.get("contractNo").toString());
    if (request.get("lang").toString().equals("2")) {
      callManager.setCampaignId("121");
    }

    return this.sendHttp(callManager, "/start");
  }

  private Object sendHttp(CallManagerVO request, String method) {
    logger.debug("CallManagerClient method: {}, request: {}", method, request);
    try {
      HttpClient client = HttpClientBuilder.create().build();
      HttpPost post = new HttpPost("http://" + PropInfo.cmIP + ":" + PropInfo.cmPort + this.method.concat(method));

      post.setEntity(
          new StringEntity(gson.toJson(request), Consts.UTF_8)
      );

      post.setHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);

      HttpResponse response = client.execute(post);
      if (response.getStatusLine().getStatusCode() == 200) {
        return new InputStreamReader(response.getEntity().getContent());
      }
    } catch (IOException e) {
      logger.info("<err>".concat(e.getMessage()));
      return e;
    }
    return null;
  }
}
