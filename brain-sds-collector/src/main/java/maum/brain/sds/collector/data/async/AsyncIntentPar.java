package maum.brain.sds.collector.data.async;

import maum.brain.sds.data.dto.SdsResponse;
import maum.brain.sds.data.vo.SdsIntent;
import maum.brain.sds.data.vo.SdsUtter;

public class AsyncIntentPar {
  private String session;
  private SdsUtter sdsUtter;
  private SdsIntent sdsIntent;
  private int host;
  private String lang;
  private double prob;
  private String jsonData;
  private String jsonDebugData;

  private boolean nowErrorLog;
  private SdsResponse nowErrorSdsResponse;

  private String answer;

  public String getSession() {
    return session;
  }

  public void setSession(String session) {
    this.session = session;
  }

  public SdsUtter getSdsUtter() {
    return sdsUtter;
  }

  public void setSdsUtter(SdsUtter sdsUtter) {
    this.sdsUtter = sdsUtter;
  }

  public SdsIntent getSdsIntent() {
    return sdsIntent;
  }

  public void setSdsIntent(SdsIntent sdsIntent) {
    this.sdsIntent = sdsIntent;
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

  public String getJsonDebugData() {
    return jsonDebugData;
  }

  public void setJsonDebugData(String jsonDebugData) {
    this.jsonDebugData = jsonDebugData;
  }

  public boolean isNowErrorLog() {
    return nowErrorLog;
  }

  public void setNowErrorLog(boolean nowErrorLog) {
    this.nowErrorLog = nowErrorLog;
  }

  public SdsResponse getNowErrorSdsResponse() {
    return nowErrorSdsResponse;
  }

  public String getAnswer() { return answer; }

  public void setAnswer(String answer) { this.answer = answer; }

  public void setNowErrorSdsResponse(SdsResponse nowErrorSdsResponse) {
    this.nowErrorSdsResponse = nowErrorSdsResponse;
  }

  public AsyncIntentPar(String session, SdsUtter sdsUtter, SdsIntent sdsIntent, int host,
      String lang, double prob, String jsonData, String jsonDebugData, boolean nowErrorLog,
      SdsResponse nowErrorSdsResponse, String answer) {
    this.session = session;
    this.sdsUtter = sdsUtter;
    this.sdsIntent = sdsIntent;
    this.host = host;
    this.lang = lang;
    this.prob = prob;
    this.jsonData = jsonData;
    this.jsonDebugData = jsonDebugData;
    this.nowErrorLog = nowErrorLog;
    this.nowErrorSdsResponse = nowErrorSdsResponse;
    this.answer = answer;
  }

  @Override
  public String toString() {
    return "AsyncIntentPar{" +
        "session='" + session + '\'' +
        ", sdsUtter=" + sdsUtter +
        ", sdsIntent=" + sdsIntent +
        ", host=" + host +
        ", lang='" + lang + '\'' +
        ", prob=" + prob +
        ", jsonData='" + jsonData + '\'' +
        ", jsonDebugData='" + jsonDebugData + '\'' +
        ", nowErrorLog=" + nowErrorLog +
        ", nowErrorSdsResponse=" + nowErrorSdsResponse +
        '}';
  }
}
