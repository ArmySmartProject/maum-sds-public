package maum.brain.sds.frontend.service;

import com.google.gson.Gson;
import maum.brain.sds.data.dto.adapter.SdsAdapterResponse;
import maum.brain.sds.data.dto.adapter.SdsAdapterStringRequest;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class ConnectService {
    private static final Logger logger = LoggerFactory.getLogger(ConnectService.class);
    private Gson gson = new Gson();
    @Value("${brain.sds.port.offset}")
    private int portOffset;

    public String doChat(SdsAdapterStringRequest request) {

        SdsAdapterResponse adapterResponse = connectAdapter(request);

        return gson.toJson(adapterResponse);
    }

    private SdsAdapterResponse connectAdapter(SdsAdapterStringRequest request) {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        CloseableHttpResponse response = null;
        SdsAdapterResponse adapterResponse = null;
        HttpPost post = null;
        System.out.println(String.format("http://localhost:%d/adapter/simple", 7641 + portOffset));
        try {
            client = HttpClientBuilder.create().build();
            post = new HttpPost(String.format("http://localhost:%d/adapter/simple", 7641 + portOffset));
            post.setEntity(
                    new StringEntity(gson.toJson(request), Consts.UTF_8)
            );
            post.setHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
            response = client.execute(post);
            adapterResponse = gson.fromJson(
                    new InputStreamReader(response.getEntity().getContent()),
                    SdsAdapterResponse.class
            );

            logger.info(adapterResponse.toString());
        } catch (IOException e) {
            logger.error(e.getMessage());
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

        return adapterResponse;
    }

}
