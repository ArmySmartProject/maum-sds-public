package maum.brain.sds.logdb.data;

public class LogIntentDto {
    private String session;
    private String utter;
    private String intent;
    private int host;
    private String lang;
    private double prob;
    private String jsonData;
    private String prevIntentUtter;
    private String answer;
    private String jsonDebugData;
    private String sessionID;

    public LogIntentDto(String utter, String intent) {
        this.utter = utter;
        this.intent = intent;
    }

    public LogIntentDto(String utter, String intent, int host) {
        this.utter = utter;
        this.intent = intent;
        this.host = host;
    }

    public LogIntentDto(String session, String utter, String intent, int host) {
        this.session = session;
        this.utter = utter;
        this.intent = intent;
        this.host = host;
    }

    public LogIntentDto(String session, String utter, String intent, int host, String lang) {
        this.session = session;
        this.utter = utter;
        this.intent = intent;
        this.host = host;
        this.lang = lang;
    }

    public LogIntentDto(String session, String utter, String intent, int host, String lang, double prob) {
        this.session = session;
        this.utter = utter;
        this.intent = intent;
        this.host = host;
        this.lang = lang;
        this.prob = prob;
    }

    public LogIntentDto(String session, String utter, String intent, int host, String lang, double prob, String jsonData,
        String prevIntentUtter, String answer) {
        this.session = session;
        this.utter = utter;
        this.intent = intent;
        this.host = host;
        this.lang = lang;
        this.prob = prob;
        this.jsonData = jsonData;
        this.prevIntentUtter = prevIntentUtter;
        this.answer = answer;
    }

    public LogIntentDto(String session, String utter, String intent, int host, String lang, double prob, String jsonData,
        String prevIntentUtter, String answer, String jsonDebugData) {
        this.session = session;
        this.utter = utter;
        this.intent = intent;
        this.host = host;
        this.lang = lang;
        this.prob = prob;
        this.jsonData = jsonData;
        this.prevIntentUtter = prevIntentUtter;
        this.answer = answer;
        this.jsonDebugData = jsonDebugData;
    }

    public LogIntentDto(String session, String utter, String intent, int host, String lang, double prob, String jsonData,
        String prevIntentUtter, String answer, String jsonDebugData, String sessionID) {
        this.session = session;
        this.utter = utter;
        this.intent = intent;
        this.host = host;
        this.lang = lang;
        this.prob = prob;
        this.jsonData = jsonData;
        this.prevIntentUtter = prevIntentUtter;
        this.answer = answer;
        this.jsonDebugData = jsonDebugData;
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

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public String getPrevIntentUtter(){ return this.prevIntentUtter; }

    public void setPrevIntentUtter(String prevIntentUtter){ this.prevIntentUtter = prevIntentUtter; }

    public String getAnswer(){ return this.answer; }

    public void setAnswer(String answer){ this.answer = answer; }

    public String getJsonDebugData(){ return this.jsonDebugData; }

    public void setJsonDebugData(String jsonDebugData){ this.jsonDebugData = jsonDebugData; }

    public String getSessionID() { return sessionID; }

    public void setSessionID(String sessionID) { this.sessionID = sessionID; }

    @Override
    public String toString() {
        return "LogIntentDto{" +
            "session='" + session + '\'' +
            ", utter='" + utter + '\'' +
            ", intent='" + intent + '\'' +
            ", host=" + host +
            ", lang='" + lang + '\'' +
            ", prob=" + prob +
            ", jsonData='" + jsonData + '\'' +
            ", prevIntentUtter='" + prevIntentUtter + '\'' +
            ", answer='" + answer + '\'' +
            ", jsonDebugData='" + jsonDebugData + '\'' +
            ", sessionID=" + sessionID +
            '}';
    }
}
