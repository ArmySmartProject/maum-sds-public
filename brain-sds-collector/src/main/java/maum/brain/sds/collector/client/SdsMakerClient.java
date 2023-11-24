package maum.brain.sds.collector.client;

import com.google.gson.Gson;
import maum.brain.sds.data.dto.general.SdsActionResponse;
import maum.brain.sds.data.dto.general.SdsErrorResponse;
import maum.brain.sds.data.dto.maker.SdsMakerRequest;
import maum.brain.sds.data.dto.SdsResponse;
import maum.brain.sds.data.vo.SdsEntity;
import maum.brain.sds.data.vo.SdsIntent;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
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

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@Component
public class SdsMakerClient {
    private static final Logger logger = LoggerFactory.getLogger(SdsMakerClient.class);
    private Gson gson;
    private String host;
    private int port;
    private String method;

    @Autowired
    public SdsMakerClient() {
        gson = new Gson();
        host = "localhost";
        port = 7650;
        method = "/maker/search";
    }

    public void setPortOffset(int portOffset){
        this.port += portOffset;
    }

    public SdsResponse getDecision(SdsMakerRequest request){
        return this.sendHttp(request);
    }

    private SdsResponse sendHttp(SdsMakerRequest request){
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        SdsResponse result = null;
        HttpPost post = null;
        try {
            client = HttpClientBuilder.create().build();
            post = new HttpPost("http://" + this.host + ":" + this.port + this.method);

            post.setEntity(
                    new StringEntity(gson.toJson(request), Consts.UTF_8)
            );

            post.setHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);

            response = client.execute(post);
            if(response.getStatusLine().getStatusCode() == 200)
                result = gson.fromJson(
                        new InputStreamReader(response.getEntity().getContent()),
                        SdsActionResponse.class
                );
            else
                return new SdsErrorResponse(
                    response.getStatusLine().getStatusCode(),
                    response.getStatusLine().toString()
                );
        } catch (IOException e){
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
}
