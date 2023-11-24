package maum.brain.sds.util.logger;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LogIntentRequest implements Serializable {
    private String session;
    private String utter;
    private String intent;
    private int host;
    private String lang;
    private double prob;
    private String prevIntentUtter;
    private String jsonData;
    private String jsonDebugData;
    private String answer;
    private String sessionID;

    public LogIntentRequest() {
    }

    public LogIntentRequest(String session, String utter, String intent, int host, String lang,
        double prob, String jsonData, String jsonDebugData) {
        this.session = session;
        this.utter = utter;
        this.intent = intent;
        this.host = host;
        this.lang = lang;
        this.prob = prob;
        this.jsonData = jsonData;
        this.jsonDebugData = jsonDebugData;
    }

    public LogIntentRequest(String session, String utter, String intent, int host, String sessionID,
        String lang, double prob, String jsonData,
        String jsonDebugData, String answer) {
        this.session = session;
        this.utter = utter;
        this.intent = intent;
        this.host = host;
        this.lang = lang;
        this.prob = prob;
        this.jsonData = jsonData;
        this.jsonDebugData = jsonDebugData;
        this.sessionID = sessionID;
        this.answer = answer;
    }

    public LogIntentRequest(String session, String utter, String intent, int host,
        String lang, double prob, String prevIntentUtter, String jsonData,
        String jsonDebugData, String answer, String sessionID) {
        this.session = session;
        this.utter = utter;
        this.intent = intent;
        this.host = host;
        this.lang = lang;
        this.prob = prob;
        this.prevIntentUtter = prevIntentUtter;
        this.jsonData = jsonData;
        this.jsonDebugData = jsonDebugData;
        this.answer = answer;
        this.sessionID = sessionID;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getUtter() {
        return utter;
    }

    public void setUtter(String utter) {
        this.utter = utter;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public int getHost() {
        return host;
    }

    public void setHost(int host) {
        this.host = host;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public double getProb() {
        return prob;
    }

    public void setProb(double prob) {
        this.prob = prob;
    }

    public String getPrevIntentUtter() {
        return prevIntentUtter;
    }

    public void setPrevIntentUtter(String prevIntentUtter) {
        this.prevIntentUtter = prevIntentUtter;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public String getJsonDebugData() {
        return jsonDebugData;
    }

    public void setJsonDebugData(String jsonDebugData) {
        this.jsonDebugData = jsonDebugData;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    @Override
    public String toString() {
        return "LogIntentRequest{" +
            "session='" + session + '\'' +
            ", utter='" + utter + '\'' +
            ", intent='" + intent + '\'' +
            ", host=" + host +
            ", lang='" + lang + '\'' +
            ", prob=" + prob +
            ", prevIntentUtter='" + prevIntentUtter + '\'' +
            ", jsonData='" + jsonData + '\'' +
            ", jsonDebugData='" + jsonDebugData + '\'' +
            ", answer='" + answer + '\'' +
            ", sessionID='" + sessionID + '\'' +
            '}';
    }
}
