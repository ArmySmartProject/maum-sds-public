package first.builder.client;

import com.google.gson.Gson;
import first.builder.client.VoiceBotClient.VoiceBotScenarioVO;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

public class DelCacheClient {
  private Gson gson;
  private String host;
  private int port;
  private String method;

  @Autowired
  public DelCacheClient() {
    gson = new Gson();
    host = "localhost";
    port = 11160;
    method = "/cache/delCache";
  }

  public Object sendHttp(Integer delHost) {
    try {
      HttpClient client = HttpClientBuilder.create().build();
      HttpPost post = new HttpPost("http://" + this.host + ":" + this.port + this.method);

      post.setEntity(
          new StringEntity(String.valueOf(delHost), Consts.UTF_8)
      );

      post.setHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);

      HttpResponse response = client.execute(post);
      if (response.getStatusLine().getStatusCode() == 200) {
        return new InputStreamReader(response.getEntity().getContent());
      } else {

      }
    } catch (IOException e) {

    }

    return null;
  }

}
