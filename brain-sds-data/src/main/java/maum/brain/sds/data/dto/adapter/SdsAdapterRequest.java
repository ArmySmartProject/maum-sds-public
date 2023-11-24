package maum.brain.sds.data.dto.adapter;

import maum.brain.sds.data.dto.SdsRequest;

public class SdsAdapterRequest implements SdsRequest {
    private String session;
    private String host;
    private String lang;
    private String jsonData;

    public SdsAdapterRequest() {
    }

    public SdsAdapterRequest(String session, String host) {
        this.session = session;
        this.host = host;
    }

    public SdsAdapterRequest(String session, String host, String lang){
        this.session = session;
        this.host = host;
        this.lang = lang;
    }

    public SdsAdapterRequest(String session, String host, String lang, String jsonData){
        this.session = session;
        this.host = host;
        this.lang = lang;
        this.jsonData = jsonData;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getLang(){
        return this.lang;
    }

    public void setLang(String lang){
        this.lang = lang;
    }

    public String getJsonData(){
        return this.jsonData;
    }

    public void setJsonData(String jsonData){ this.jsonData = jsonData; }

    @Override
    public String toString() {
        return "SdsAdapterRequest{" +
                "session='" + session + '\'' +
                ", host='" + host + '\'' +
                ", lang='" + lang + '\'' +
                ", jsonData='" + jsonData + '\'' +
                '}';
    }
}
