package first.builder.client;

import com.google.gson.Gson;
import first.builder.vo.IntentVO;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class VoiceBotClient {

  private static final Logger logger = LoggerFactory.getLogger(VoiceBotClient.class);
  private Gson gson;
  private String host;
  private int port;
  private String method;

  @Autowired
  public VoiceBotClient() {
    gson = new Gson();
    host = "10.122.64.116";
    port = 5000;
    method = "/update/scenario";
  }

  @Data
  public class VoiceBotScenarioVO {
    private int campaign_id;
    private List<Data> data;

    @lombok.Data
    public class Data {
      private int no;
      private String intent;
      private String end_state;
      private String task_name;
    }

  }

  public Object sendEndStateToVoiceBot() {
    VoiceBotScenarioVO voiceBot = new VoiceBotScenarioVO();
    voiceBot.setCampaign_id(116);
    List<VoiceBotScenarioVO.Data> dataList = new ArrayList<>();
    VoiceBotScenarioVO.Data data = voiceBot.new Data();
    data.setIntent("TEST");
    data.setNo(1);
    data.setEnd_state("end");
    dataList.add(data);
    voiceBot.setData(dataList);

    return this.sendHttp(voiceBot);
  }

  private Object sendHttp(VoiceBotScenarioVO request) {
    try {
      HttpClient client = HttpClientBuilder.create().build();
      HttpPost post = new HttpPost("http://" + this.host + ":" + this.port + this.method);

      post.setEntity(
          new StringEntity(gson.toJson(request), Consts.UTF_8)
      );

      post.setHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);

      HttpResponse response = client.execute(post);
      if (response.getStatusLine().getStatusCode() == 200) {
        return new InputStreamReader(response.getEntity().getContent());
      } else {
//        return new SdsErrorResponse(
//            response.getStatusLine().getStatusCode(),
//            response.getStatusLine().toString()
//        );
      }
    } catch (IOException e) {
//      return new SdsErrorResponse(500, "Internal Server Error");
    }

    return null;
  }


}
