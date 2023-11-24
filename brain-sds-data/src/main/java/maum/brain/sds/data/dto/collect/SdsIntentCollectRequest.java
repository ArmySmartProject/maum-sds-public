package maum.brain.sds.data.dto.collect;

import java.util.List;
import java.util.Map;
import maum.brain.sds.data.dto.SdsRequest;
import maum.brain.sds.data.vo.SdsEntity;
import maum.brain.sds.data.vo.SdsIntent;

public class SdsIntentCollectRequest implements SdsRequest {
    private String host;
    private String session;
    private SdsIntent data;
    private String lang;
    private String jsonData;
    private List<SdsEntity> entities;

    public SdsIntentCollectRequest() {
    }

    public SdsIntentCollectRequest(String host, String session, SdsIntent data) {
        this.host = host;
        this.session = session;
        this.data = data;
    }

    public SdsIntentCollectRequest(String host, String session, SdsIntent data, String lang) {
        this.host = host;
        this.session = session;
        this.data = data;
        this.lang = lang;
    }

    public SdsIntentCollectRequest(String host, String session, SdsIntent data, String lang, String jsonData) {
        this.host = host;
        this.session = session;
        this.data = data;
        this.lang = lang;
        this.jsonData = jsonData;
    }

    public SdsIntentCollectRequest(String host, String session, SdsIntent data, String lang, String jsonData, List<SdsEntity> entities) {
        this.host = host;
        this.session = session;
        this.data = data;
        this.lang = lang;
        this.jsonData = jsonData;
        this.entities = entities;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public SdsIntent getData() {
        return data;
    }

    public void setData(SdsIntent data) {
        this.data = data;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getJsonData(){ return this.jsonData; }

    public void setJsonData(String jsonData){ this.jsonData = jsonData; }

    public List<SdsEntity> getEntities() {
        return entities;
    }

    public void setEntities(List<SdsEntity> entities) {
        this.entities = entities;
    }

    @Override
    public String toString() {
        return "SdsIntentCollectRequest{" +
                "host='" + host + '\'' +
                ", session='" + session + '\'' +
                ", data=" + data +
                ", lang='" + lang + '\'' +
                ", jsonData='" + jsonData + '\'' +
                ", entities='" + entities + '\'' +
                '}';
    }
}
