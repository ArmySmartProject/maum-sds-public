package maum.brain.sds.util.handler;

import com.google.gson.Gson;
import maum.brain.sds.util.data.SdsConciergeData;
import maum.brain.sds.util.data.SdsConciergeRequest;
import maum.brain.sds.util.data.SdsOrderData;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SdsConciergeClient {
    private static final Gson gson = new Gson();

    private Map<String, SdsConciergeData> conciergeMemory;

    public SdsConciergeClient(){
        conciergeMemory = new HashMap<>();
    }

    private void setUser(String session){
        if(!conciergeMemory.containsKey(session))
            conciergeMemory.put(session, new SdsConciergeData());
    }

    private void removeUser(String session){
        conciergeMemory.remove(session);
    }

    public void addMessage(String session, String message){
        this.setUser(session);
        SdsConciergeData data = this.conciergeMemory.get(session);
        data.addMsg(message);
        this.conciergeMemory.put(session, data);
    }

    public void addRequest(String session, String request){
        this.setUser(session);
        SdsConciergeData data = this.conciergeMemory.get(session);
        data.addReq(request);
        this.conciergeMemory.put(session, data);
    }

    public void sendToConcierge(String session, Map<String, String> entityMap){
        SdsConciergeData payload = conciergeMemory.get(session);
        payload.setName(entityMap.get("ROOM_NUM").concat(", ".concat(entityMap.get("NAME"))));
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost("http://127.0.0.1:5005/cusReq");
            post.setEntity(
                    new StringEntity(gson.toJson(new SdsConciergeRequest(payload)), Consts.UTF_8)
            );
            post.setHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
            client.execute(post);
        } catch (IOException e){
            System.out.println(e.getMessage());
        } finally {
            this.removeUser(session);
        }
    }

    public Boolean sendToOrder(String data){
        Boolean res = true;
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost("http://127.0.0.1:5005/pavanReq");
            post.setEntity(
                new StringEntity(data, Consts.UTF_8)
            );
            post.setHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);

            HttpResponse response = client.execute(post);
            if(response.getStatusLine().getStatusCode() != 200){
                res =  false;
            }
        } catch (IOException e){
            System.out.println(e.getMessage());
            res =  false;
        } finally {
        }
        return res;
    }

    public Boolean sendToEmail(String data){
        Boolean res = true;
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost("http://127.0.0.1:5005/inquiryMailReq");
            post.setEntity(
                new StringEntity(data, Consts.UTF_8)
            );
            post.setHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);

            HttpResponse response = client.execute(post);
            if(response.getStatusLine().getStatusCode() != 200){
                res =  false;
            }
        } catch (IOException e){
            System.out.println(e.getMessage());
            res =  false;
        } finally {
        }
        return res;
    }
}
