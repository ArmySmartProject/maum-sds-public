package maum.brain.sds.maker.client;

import com.google.gson.Gson;
import java.io.IOException;
import maum.brain.sds.data.vo.RestApiDto;
import maum.brain.sds.data.vo.SdsEntityList;
import org.apache.http.Consts;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class RestApiClient {

    private Logger logger = LoggerFactory.getLogger(RestApiClient.class);

    private Gson gson;
    private String host;
    private int port;

    public RestApiClient(){
        gson = new Gson();
    }

    public RestApiDto sendHttp(RestApiDto request, String url){
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        HttpPost post = null;
        RestApiDto result = new RestApiDto();
        try {
            client = HttpClientBuilder.create().build();
            post = new HttpPost(url);
            post.setEntity(new StringEntity(gson.toJson(request), Consts.UTF_8));
            post.setHeader("Content-type", MediaType.APPLICATION_JSON_UTF8_VALUE);
            response = client.execute(post);
            if(response.getStatusLine().getStatusCode() == 200) {
                String res = EntityUtils.toString(response.getEntity(), "UTF-8");
                result = gson.fromJson(res, RestApiDto.class);
                System.out.println("[CUSTOM-SERVER] response : ".concat(result.toString()));
                if (!result.getResultCode().equals("200")) {
                    System.out.println("[CUSTOM-SERVER] result-code : ".concat(result.getResultCode()));
                    System.out.println("[CUSTOM-SERVER] error msg : ".concat(result.getMessage()));
                }
            }
        } catch (IOException e){
            logger.error("[CUSTOM-SERVER] rest api server ERR. " + e.getMessage());
        }
        return result;
    }
}
