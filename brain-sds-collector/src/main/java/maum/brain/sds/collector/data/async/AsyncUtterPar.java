package maum.brain.sds.collector.data.async;

import maum.brain.sds.data.dto.SdsResponse;
import maum.brain.sds.data.dto.collect.SdsUtterCollectRequest;
import maum.brain.sds.data.vo.SdsAnswer;
import maum.brain.sds.data.vo.SdsEntityList;
import maum.brain.sds.data.vo.SdsIntent;
import maum.brain.sds.data.vo.SdsUtter;
import org.json.JSONException;
import org.json.JSONObject;


public class AsyncUtterPar {
  SdsUtterCollectRequest request;
  SdsIntent intent;
  double prob;
  JSONObject jsonDebugData;
  String logAnswer;
  SdsEntityList entities;
  boolean nowAnswerLog;
  SdsUtter nowAnswerLogSdsUtter;
  SdsAnswer nowAnswerLogSdsAnswer;
  boolean nowErrorLog;
  SdsResponse nowErrorLogSdsResponse;




  public SdsUtterCollectRequest getRequest() {
    return request;
  }

  public void setRequest(SdsUtterCollectRequest request) {
    this.request = request;
  }

  public SdsIntent getIntent() {
    return intent;
  }

  public void setIntent(SdsIntent intent) {
    this.intent = intent;
  }

  public double getProb() {
    return prob;
  }

  public void setProb(double prob) {
    this.prob = prob;
  }

  public JSONObject getJsonDebugData() {
    return jsonDebugData;
  }

  public void setJsonDebugData(JSONObject jsonDebugData) {
    this.jsonDebugData = jsonDebugData;
  }

  public String getLogAnswer() {
    return logAnswer;
  }

  public void setLogAnswer(String logAnswer) {
    this.logAnswer = logAnswer;
  }

  public SdsEntityList getEntities() {
    return entities;
  }

  public void setEntities(SdsEntityList entities) {
    this.entities = entities;
  }

  public boolean isNowAnswerLog() {
    return nowAnswerLog;
  }

  public void setNowAnswerLog(boolean nowAnswerLog) {
    this.nowAnswerLog = nowAnswerLog;
  }

  public SdsUtter getNowAnswerLogSdsUtter() {
    return nowAnswerLogSdsUtter;
  }

  public void setNowAnswerLogSdsUtter(SdsUtter nowAnswerLogSdsUtter) {
    this.nowAnswerLogSdsUtter = nowAnswerLogSdsUtter;
  }

  public SdsAnswer getNowAnswerLogSdsAnswer() {
    return nowAnswerLogSdsAnswer;
  }

  public void setNowAnswerLogSdsAnswer(SdsAnswer nowAnswerLogSdsAnswer) {
    this.nowAnswerLogSdsAnswer = nowAnswerLogSdsAnswer;
  }

  public boolean isNowErrorLog() {
    return nowErrorLog;
  }

  public void setNowErrorLog(boolean nowErrorLog) {
    this.nowErrorLog = nowErrorLog;
  }

  public SdsResponse getNowErrorLogSdsResponse() {
    return nowErrorLogSdsResponse;
  }

  public void setNowErrorLogSdsResponse(SdsResponse nowErrorLogSdsResponse) {
    this.nowErrorLogSdsResponse = nowErrorLogSdsResponse;
  }

  public AsyncUtterPar() {
  }


  public AsyncUtterPar(SdsUtterCollectRequest request, SdsIntent intent, double prob,
      JSONObject jsonDebugData, String logAnswer, SdsEntityList entities, boolean nowAnswerLog,
      SdsUtter nowAnswerLogSdsUtter, SdsAnswer nowAnswerLogSdsAnswer, boolean nowErrorLog,
      SdsResponse nowErrorLogSdsResponse) throws JSONException {
    this.request = request;
    this.intent = intent;
    this.prob = prob;
    this.jsonDebugData = new JSONObject(jsonDebugData.toString());
    this.logAnswer = logAnswer;
    this.entities = entities;
    this.nowAnswerLog = nowAnswerLog;
    this.nowAnswerLogSdsUtter = nowAnswerLogSdsUtter;
    this.nowAnswerLogSdsAnswer = nowAnswerLogSdsAnswer;
    this.nowErrorLog = nowErrorLog;
    this.nowErrorLogSdsResponse = nowErrorLogSdsResponse;
  }
}
