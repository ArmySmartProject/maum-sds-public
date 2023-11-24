package maum.brain.sds.data.dto.collect;

import java.util.List;
import java.util.Map;
import maum.brain.sds.data.dto.SdsRequest;
import maum.brain.sds.data.vo.SdsEntity;
import maum.brain.sds.data.vo.SdsUtter;

public class SdsUtterCollectRequest implements SdsRequest {
    private String host;
    private String session;
    private SdsUtter data;
    private String lang;
    private String jsonData;
    private String prevIntentUtter;
    private List<SdsEntity> entities;

    public SdsUtterCollectRequest() {
    }

    public SdsUtterCollectRequest(String host, String session, SdsUtter data) {
        this.host = host;
        this.session = session;
        this.data = data;
    }

    public SdsUtterCollectRequest(String host, String session, SdsUtter data, String lang) {
        this.host = host;
        this.session = session;
        this.data = data;
        this.lang = lang;
    }

    public SdsUtterCollectRequest(String host, String session, SdsUtter data, String lang, String jsonData) {
        this.host = host;
        this.session = session;
        this.data = data;
        this.lang = lang;
        this.jsonData = jsonData;
    }

    public SdsUtterCollectRequest(String host, String session, SdsUtter data, String lang, String jsonData, List<SdsEntity> entities) {
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

    public SdsUtter getData() {
        return data;
    }

    public void setData(SdsUtter data) {
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

    public String getPrevIntentUtter(){ return this.prevIntentUtter; }

    public void setPrevIntentUtter(String prevIntentUtter){ this.prevIntentUtter = prevIntentUtter; }

    public List<SdsEntity> getEntities() {
        return entities;
    }

    public void setEntities(List<SdsEntity> entities) {
        this.entities = entities;
    }

    @Override
    public String toString() {
        return "SdsUtterCollectRequest{" +
                "host='" + host + '\'' +
                ", session='" + session + '\'' +
                ", data=" + data +
                ", lang='" + lang + '\'' +
                ", jsonData='" + jsonData + '\'' +
                ", entities='" + entities + '\'' +
                '}';
    }
}
