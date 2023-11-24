package maum.brain.sds.util.logger;

import java.io.*;

import maum.brain.sds.data.vo.SdsAnswer;
import maum.brain.sds.data.vo.SdsEntityList;
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
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SdsLogger {
    private String session;
    private String host;
    private int port;
    private Gson gson;

    public SdsLogger(){
        this.host = "localhost";
        this.port = 6950;
        this.session = "test";
        this.gson = new Gson();
    }

    public void setPortOffset(int portOffset){
        this.port += portOffset;
    }

    //sendToEmail & sendToOrder
    public void intent(String session, SdsUtter utter, SdsIntent intent, int host, String lang, double prob, String jsonData, String jsonDebugData){
        CloseableHttpClient client = HttpClientBuilder.create().build();
        CloseableHttpResponse response = null;
        HttpPost post = new HttpPost(String.format("http://localhost:%d/logger/addIntent", this.port));

        try {
            post.setEntity(
                new StringEntity(
                    gson.toJson(
                        new LogIntentRequest(
                            session,
                            utter.getUtter(),
                            intent.getIntent(),
                            host,
                            lang,
                            prob,
                            jsonData,
                            jsonDebugData
                        )
                    ),
                    Consts.UTF_8
                )
            );
            post.setHeader("Content-type", "application/json;charset=UTF-8");
            response = client.execute(post);
            System.out.println(response.getStatusLine());
        } catch (IOException e){
            System.out.println(e.getMessage());
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
    }

    // collect (Intent & Utter)
    public void intent(String sessionID, String session, SdsUtter utter, SdsIntent intent, int host, String lang, double prob, String jsonData, String jsonDebugData, String logAnswer){
        CloseableHttpClient client = HttpClientBuilder.create().build();
        CloseableHttpResponse response = null;
        HttpPost post = new HttpPost(String.format("http://localhost:%d/logger/addIntent", this.port));

        try {
            post.setEntity(
                new StringEntity(
                    gson.toJson(
                        new LogIntentRequest(
                            session,
                            utter.getUtter(),
                            intent.getIntent(),
                            host,
                            sessionID,
                            lang,
                            prob,
                            jsonData,
                            jsonDebugData,
                            logAnswer
                        )
                    ),
                    Consts.UTF_8
                )
            );
            post.setHeader("Content-type", "application/json;charset=UTF-8");
            response = client.execute(post);
            System.out.println(response.getStatusLine());
        } catch (IOException e){
            System.out.println(e.getMessage());
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
    }



    public String sessionCountAdd(int host, String lang, String session, String channel){
        CloseableHttpClient client = HttpClientBuilder.create().build();
        CloseableHttpResponse response = null;
        HttpPost post = new HttpPost(String.format("http://localhost:%d/logger/sessionCount", this.port));
        String nowReturn = "";

        try {
            post.setEntity(
                new StringEntity(
                    gson.toJson(
                        new LogSessionCountRequest(
                            host,
                            lang,
                            session,
                            channel
                        )
                    ),
                    Consts.UTF_8
                )
            );
            post.setHeader("Content-type", "application/json;charset=UTF-8");
            response = client.execute(post);
            System.out.println(response.getStatusLine());

            InputStream inputStream = response.getEntity().getContent();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            byte[] buffer = new byte[1024];
            int length = 0;
            while((length=inputStream.read(buffer))!=-1){
                try{
                    byteArrayOutputStream.write(buffer, 0 , length);
                    nowReturn += byteArrayOutputStream.toString("UTF-8");
                }catch (Exception e2){
                    break;
                }
            }
            return nowReturn;

        } catch (IOException e){
            System.out.println(e.getMessage());
            if(nowReturn.length()!=0) return nowReturn;
            return "-1";

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

    }

    public void entity(SdsUtter utter, SdsEntityList entityList){
        List<SdsEntity> entities = new ArrayList<>();
        for(maum.brain.sds.data.vo.SdsEntity entity: entityList.getEntities()){
            entities.add(
                new SdsEntity(entity.getEntityName(), entity.getEntityValue())
            );
        }
        CloseableHttpClient client = HttpClientBuilder.create().build();
        CloseableHttpResponse response = null;
        HttpPost post = new HttpPost(String.format("http://localhost:%d/logger/addEntity", this.port));
        try {
            post.setEntity(
                new StringEntity(
                    gson.toJson(
                        new LogEntityRequest(
                            utter.getUtter(),
                            entities
                        )
                    ),
                    Consts.UTF_8
                )
            );
            post.setHeader("Content-type", "application/json;charset=UTF-8");
            response = client.execute(post);
            System.out.println(response.getStatusLine());
        } catch (IOException e){
            e.printStackTrace();
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
    }

    public void entity(String session, SdsUtter utter, SdsEntityList entityList, int host){
        List<SdsEntity> entities = new ArrayList<>();
        for(maum.brain.sds.data.vo.SdsEntity entity: entityList.getEntities()){
            entities.add(
                new SdsEntity(entity.getEntityName(), entity.getEntityValue())
            );
        }
        CloseableHttpClient client = HttpClientBuilder.create().build();
        CloseableHttpResponse response = null;
        HttpPost post = new HttpPost(String.format("http://localhost:%d/logger/addEntity", this.port));
        try {
            post.setEntity(
                new StringEntity(
                    gson.toJson(
                        new LogEntityRequest(
                            session,
                            utter.getUtter(),
                            entities,
                            host
                        )
                    ),
                    Consts.UTF_8
                )
            );
            post.setHeader("Content-type", "application/json;charset=UTF-8");
            response = client.execute(post);
            System.out.println(response.getStatusLine());
        } catch (IOException e){
            e.printStackTrace();
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
    }

    public void entity(String session, SdsUtter utter, SdsEntityList entityList, int host, String lang){
        List<SdsEntity> entities = new ArrayList<>();
        for(maum.brain.sds.data.vo.SdsEntity entity: entityList.getEntities()){
            entities.add(
                new SdsEntity(entity.getEntityName(), entity.getEntityValue())
            );
        }
        CloseableHttpClient client = HttpClientBuilder.create().build();
        CloseableHttpResponse response = null;
        HttpPost post = new HttpPost(String.format("http://localhost:%d/logger/addEntity", this.port));
        try {
            post.setEntity(
                new StringEntity(
                    gson.toJson(
                        new LogEntityRequest(
                            session,
                            utter.getUtter(),
                            entities,
                            host,
                            lang
                        )
                    ),
                    Consts.UTF_8
                )
            );
            post.setHeader("Content-type", "application/json;charset=UTF-8");
            response = client.execute(post);
            System.out.println(response.getStatusLine());
        } catch (IOException e){
            e.printStackTrace();
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
    }

    public void answer(SdsUtter utter, SdsAnswer answer){
        CloseableHttpClient client = HttpClientBuilder.create().build();
        CloseableHttpResponse response = null;
        HttpPost post = new HttpPost(String.format("http://localhost:%d/logger/addAnswer", this.port));
        try {
            post.setEntity(
                new StringEntity(
                    gson.toJson(
                        new LogAnswerRequest(
                            utter.getUtter(),
                            answer.getAnswer()
                        )
                    ),
                    Consts.UTF_8
                )
            );
            post.setHeader("Content-type", "application/json;charset=UTF-8");
            response = client.execute(post);
            System.out.println(response.getStatusLine());
        } catch (IOException e){
            e.printStackTrace();
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

    }

    public void answer(SdsUtter utter, SdsAnswer answer, int host){
        CloseableHttpClient client = HttpClientBuilder.create().build();
        CloseableHttpResponse response = null;
        HttpPost post = new HttpPost(String.format("http://localhost:%d/logger/addAnswer", this.port));
        try {
            post.setEntity(
                new StringEntity(
                    gson.toJson(
                        new LogAnswerRequest(
                            utter.getUtter(),
                            answer.getAnswer(),
                            host
                        )
                    ),
                    Consts.UTF_8
                )
            );
            post.setHeader("Content-type", "application/json;charset=UTF-8");
            response = client.execute(post);
            System.out.println(response.getStatusLine());
        } catch (IOException e){
            e.printStackTrace();
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

    }

    public void addStats(int host, String session,String utter){

    }

    public void debug(Object msg){
        this.sendLog(SdsLoggerCodes.LOG_LEVEL_DEBUG, msg.toString());
    }

    public void info(Object msg){
        this.sendLog(SdsLoggerCodes.LOG_LEVEL_INFO, msg.toString());
    }

    public void warn(Object msg){
        this.sendLog(SdsLoggerCodes.LOG_LEVEL_WARN, msg.toString());
    }

    public void error(Object msg){
        this.sendLog(SdsLoggerCodes.LOG_LEVEL_ERROR, msg.toString());
    }

    private void sendLog(String level, String message) {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(String.format("http://localhost:%d/logger/log", this.port));
        try {
            post.setEntity(
                new StringEntity(gson.toJson(new LogMessageRequest(level, message)), Consts.UTF_8)
            );
            post.setHeader("Content-type", "application/json;charset=UTF-8");
            client.execute(post);
        } catch (IOException e) {
            e.printStackTrace();
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
        }

    }
}
