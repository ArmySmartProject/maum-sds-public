package maum.brain.sds.util.handler;

import com.google.gson.Gson;
import maum.brain.sds.data.dto.SdsRequest;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.InputStreamReader;

public class SdsHttpClient {
    private static final Gson gson = new Gson();
    private static final HttpClient client = HttpClientBuilder.create().build();

    private SdsHttpClient(){

    }

    private static <T> T sendPostRequest(SdsRequest request, int port, String method, Class<T> cls)
    throws IOException {
        HttpPost post = new HttpPost(
                "http://127.0.0.1:" + port + method
        );

        post.setEntity(
                new StringEntity(gson.toJson(request), Consts.UTF_8)
        );
        post.setHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);

        HttpResponse response = client.execute(post);
        if(response.getStatusLine().getStatusCode() == 200)
            return gson.fromJson(
                    new InputStreamReader(response.getEntity().getContent()),
                    cls
            );
        else
            throw new IOException(response.getStatusLine().getReasonPhrase());
    }
}
