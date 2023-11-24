package maum.brain.sds.collector.client;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStreamReader;
import maum.brain.sds.data.dto.SdsResponse;
import maum.brain.sds.data.dto.cache.SdsAddCacheRequest;
import maum.brain.sds.data.dto.cache.SdsGetCacheResponse;
import maum.brain.sds.data.dto.general.SdsActionResponse;
import maum.brain.sds.data.dto.general.SdsErrorResponse;
import maum.brain.sds.data.dto.maker.SdsMakerRequest;
import org.apache.http.Consts;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class SdsCacheClient {
  private static final Logger logger = LoggerFactory.getLogger(SdsCacheClient.class);
  private Gson gson;
  private String host;
  private int port;
  private String methodAdd;
  private String methodGet;

  @Autowired
  public SdsCacheClient() {
    gson = new Gson();
    host = "localhost";
    port = 11160;
    methodAdd = "/cache/addCache";
    methodGet = "/cache/getCache";
  }

  public SdsResponse sendHttpAddCache(SdsAddCacheRequest request){
    CloseableHttpClient client = null;
    CloseableHttpResponse response = null;
    SdsResponse result = null;
    HttpPost post = null;
    try {
      client = HttpClientBuilder.create().build();
      post = new HttpPost("http://" + this.host + ":" + this.port + this.methodAdd);

      post.setEntity(
          new StringEntity(gson.toJson(request), Consts.UTF_8)
      );

      post.setHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);

      response = client.execute(post);
      result = gson.fromJson(
          new InputStreamReader(response.getEntity().getContent()),
          SdsErrorResponse.class
      );
    } catch (Exception e){
      return new SdsErrorResponse(500, "Internal Server Error");
    } finally {
      if (post != null) {
        post.releaseConnection();
      }
      if (client != null) {
        try {
          client.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (response != null) {
        try {
          response.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return result;
  }


  public SdsGetCacheResponse sendHttpGetCache(SdsAddCacheRequest request){
    CloseableHttpClient client = null;
    CloseableHttpResponse response = null;
    SdsGetCacheResponse result = null;
    HttpPost post = null;
    try {
      client = HttpClientBuilder.create().build();
      post = new HttpPost("http://" + this.host + ":" + this.port + this.methodGet);

      post.setEntity(
          new StringEntity(gson.toJson(request), Consts.UTF_8)
      );

      post.setHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);

      response = client.execute(post);
      result = gson.fromJson(
          new InputStreamReader(response.getEntity().getContent()),
          SdsGetCacheResponse.class
      );
    } catch (Exception e){
      System.out.println("[CACHE] sendHttpGetCache Error : " + e);
      SdsGetCacheResponse exceptionGetCacheResponse = new SdsGetCacheResponse();
      exceptionGetCacheResponse.setCacheNo(-1);
      return exceptionGetCacheResponse;
    } finally {
      if (post != null) {
        post.releaseConnection();
      }
      if (client != null) {
        try {
          client.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (response != null) {
        try {
          response.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return result;
  }
}
