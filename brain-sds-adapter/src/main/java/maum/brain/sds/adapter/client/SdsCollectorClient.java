package maum.brain.sds.adapter.client;

import com.google.gson.Gson;
import maum.brain.sds.data.dto.SdsRequest;
import maum.brain.sds.data.dto.SdsResponse;
import maum.brain.sds.data.dto.collect.SdsIntentCollectRequest;
import maum.brain.sds.data.dto.collect.SdsUtterCollectRequest;
import maum.brain.sds.data.dto.general.SdsActionResponse;
import maum.brain.sds.data.dto.general.SdsErrorResponse;
import maum.brain.sds.data.vo.SdsData;
import maum.brain.sds.data.vo.SdsIntent;
import maum.brain.sds.data.vo.SdsUtter;
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
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class SdsCollectorClient {

    private Logger logger = LoggerFactory.getLogger(SdsCollectorClient.class);

    private Gson gson;
    private String host;
    private int port;
    private String intentMethod;
    private String utterMethod;

    public SdsCollectorClient(){
        gson = new Gson();
        host = "localhost";
        port = 6941;
        intentMethod = "/collect/run/intent";
        utterMethod = "/collect/run/utter";
    }

    public void setPortOffset(int portOffset){
        this.port += portOffset;
    }

    public SdsResponse getAction(String session, SdsData data){
        SdsRequest request;
        if(data.getClass().equals(SdsIntent.class))
            request = new SdsIntentCollectRequest(
                    "1",
                    session,
                    (SdsIntent) data
            );
        else if(data.getClass().equals(SdsUtter.class))
            request = new SdsUtterCollectRequest(
                    "1",
                    session,
                    (SdsUtter) data
            );
        else throw new UnsupportedOperationException();

        return this.sendHttp(request);
    }

    public SdsResponse getAction(String host, String session, SdsData data, String lang){
        SdsRequest request;
        if(data.getClass().equals(SdsIntent.class))
            request = new SdsIntentCollectRequest(
                    host,
                    session,
                    (SdsIntent) data,
                    lang
            );
        else if(data.getClass().equals(SdsUtter.class))
            request = new SdsUtterCollectRequest(
                    host,
                    session,
                    (SdsUtter) data,
                    lang
            );
        else throw new UnsupportedOperationException();

        return this.sendHttp(request);
    }

    public SdsResponse getAction(String host, String session, SdsData data, String lang, String jsonData){
        SdsRequest request;
        if(data.getClass().equals(SdsIntent.class))
            request = new SdsIntentCollectRequest(
                host,
                session,
                (SdsIntent) data,
                lang,
                jsonData
            );
        else if(data.getClass().equals(SdsUtter.class))
            request = new SdsUtterCollectRequest(
                host,
                session,
                (SdsUtter) data,
                lang,
                jsonData
            );
        else throw new UnsupportedOperationException();

        return this.sendHttp(request);
    }

    private SdsResponse sendHttp(SdsRequest request){
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        SdsResponse result = null;
        HttpPost post = null;
        try {
//            MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
            client = HttpClientBuilder.create().build();
            if(request.getClass().equals(SdsIntentCollectRequest.class))
                post = new HttpPost("http://" + this.host + ":" + this.port + this.intentMethod);
            else if(request.getClass().equals(SdsUtterCollectRequest.class))
                post = new HttpPost("http://" + this.host + ":" + this.port + this.utterMethod);
            else throw new UnsupportedOperationException();
            post.setEntity(new StringEntity(gson.toJson(request), Consts.UTF_8));
            post.setHeader("Content-type", MediaType.APPLICATION_JSON_UTF8_VALUE);
            response = client.execute(post);
            if(response.getStatusLine().getStatusCode() == 200)
                result = gson.fromJson(
                    new InputStreamReader(response.getEntity().getContent()),
                    SdsActionResponse.class
                );
            else
                result = new SdsErrorResponse(
                    response.getStatusLine().getStatusCode(),
                    response.getStatusLine().getReasonPhrase()
                );
        } catch (IOException e){
            logger.error("Collect server connect ERR. " + e.getMessage());
            result = new SdsErrorResponse(500, "Internal Server Error");
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
