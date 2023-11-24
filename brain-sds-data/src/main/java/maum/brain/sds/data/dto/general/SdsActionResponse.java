package maum.brain.sds.data.dto.general;

import maum.brain.sds.data.dto.SdsResponse;
import maum.brain.sds.data.vo.SdsAnswer;
import maum.brain.sds.data.vo.SdsIntent;
import maum.brain.sds.data.vo.SdsMemory;

import java.util.ArrayList;
import java.util.List;
import maum.brain.sds.data.vo.SdsMeta;
import maum.brain.sds.data.vo.engines.SdsEngines;
import maum.brain.sds.data.vo.engines.spec.SdsSpecCustomServerMap;
import maum.brain.sds.data.vo.relations.SdsRelation;

public class SdsActionResponse implements SdsResponse {
    private SdsAnswer answer;
    private SdsAnswer farewell;
    private List<SdsIntent> expectedIntents;
    private SdsMemory setMemory;
    private String bertIntent;
    private String display;
    private SdsMeta meta;
    private String jsonDebug;
    private String responseOrder;
    private SdsEngines engines;
    private SdsRelation relation;
    private String customServerEngine;
    private String customServerTime;
    private List<SdsSpecCustomServerMap> customServerAnswer;
    private String h_task;
    private String h_item;
    private String h_param;

    public SdsActionResponse() {
        expectedIntents = new ArrayList<>();
        engines = new SdsEngines();
        customServerAnswer = new ArrayList<>();
    }

    public SdsActionResponse(SdsAnswer answer, List<SdsIntent> nextIntents) {
        this.answer = answer;
        this.expectedIntents = nextIntents;
        engines = new SdsEngines();
        customServerAnswer = new ArrayList<>();
    }

    public SdsActionResponse(SdsAnswer answer, SdsAnswer farewell, List<SdsIntent> expectedIntents) {
        this.answer = answer;
        this.farewell = farewell;
        this.expectedIntents = expectedIntents;
        engines = new SdsEngines();
        customServerAnswer = new ArrayList<>();
    }

    public SdsActionResponse(SdsAnswer answer, SdsAnswer farewell, List<SdsIntent> expectedIntents, SdsMemory setMemory) {
        this.answer = answer;
        this.farewell = farewell;
        this.expectedIntents = expectedIntents;
        this.setMemory = setMemory;
        engines = new SdsEngines();
        customServerAnswer = new ArrayList<>();
    }

  public SdsActionResponse(SdsAnswer answer, SdsAnswer farewell,
      List<SdsIntent> expectedIntents, SdsMemory setMemory, String bertIntent,
      String display, SdsMeta meta, String jsonDebug, String responseOrder,
      SdsEngines engines, SdsRelation relation, String customServerEngine, String customServerTime,
      List<SdsSpecCustomServerMap> customServerAnswer) {
    this.answer = answer;
    this.farewell = farewell;
    this.expectedIntents = expectedIntents;
    this.setMemory = setMemory;
    this.bertIntent = bertIntent;
    this.display = display;
    this.meta = meta;
    this.jsonDebug = jsonDebug;
    this.responseOrder = responseOrder;
    this.engines = engines;
    this.relation = relation;
    this.customServerEngine = customServerEngine;
    this.customServerTime = customServerTime;
    this.customServerAnswer = customServerAnswer;
  }

  //ActionResponse 에서 반환할 값만 솎아내는 constructor
    public SdsActionResponse(SdsActionResponse sdsActionResponse){
      this.answer = sdsActionResponse.answer;
      this.farewell = sdsActionResponse.farewell;
      this.expectedIntents = sdsActionResponse.expectedIntents;
      this.setMemory = sdsActionResponse.setMemory;
      this.bertIntent = sdsActionResponse.bertIntent;
      this.display = sdsActionResponse.display;
      this.meta = sdsActionResponse.meta;
      this.jsonDebug = sdsActionResponse.jsonDebug;
      this.responseOrder = sdsActionResponse.responseOrder;
      this.engines = sdsActionResponse.engines;
      this.relation = sdsActionResponse.relation;
      this.h_task = sdsActionResponse.h_task;
      this.h_item = sdsActionResponse.getH_item();
      this.h_param = sdsActionResponse.getH_param();
    }

    public void setBertIntent(String bertIntent) {
        this.bertIntent = bertIntent;
    }

    public SdsAnswer getAnswer() {
        return answer;
    }

    public void setAnswer(SdsAnswer answer) {
        this.answer = answer;
    }

    public SdsIntent getExpectedIntent(int index){
        return expectedIntents.get(index);
    }

    public void setExpectedIntent(SdsIntent nextIntent){
        this.expectedIntents.add(nextIntent);
    }

    public List<SdsIntent> getExpectedIntents() {
        return expectedIntents;
    }

    public void setExpectedIntents(List<SdsIntent> expectedIntents) {
        this.expectedIntents = expectedIntents;
    }

    public SdsAnswer getFarewell() {
        return farewell;
    }

    public void setFarewell(SdsAnswer farewell) {
        this.farewell = farewell;
    }

    public SdsMemory getSetMemory() {
        return setMemory;
    }

    public void setSetMemory(SdsMemory setMemory) {
        this.setMemory = setMemory;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getResponseOrder() {
      return responseOrder;
    }

    public void setResponseOrder(String responseOrder) {
    this.responseOrder = responseOrder;
  }

    public SdsMeta getMeta() {
        return meta;
    }

    public void setMeta(SdsMeta meta) {
        this.meta = meta;
    }

    public String getJsonDebug() { return jsonDebug; }

    public void setJsonDebug(String jsonDebug) { this.jsonDebug = jsonDebug; }

    public SdsEngines getEngines() {
        return engines;
    }

    public void setEngines(SdsEngines engines) {
        this.engines = engines;
    }

    public SdsRelation getRelation() {
        return relation;
    }

    public void setRelation(SdsRelation relation) {
        this.relation = relation;
    }

    public String getCustomServerEngine() { return customServerEngine; }

    public void setCustomServerEngine(String customServerEngine) { this.customServerEngine = customServerEngine; }

    public String getCustomServerTime() { return customServerTime; }

    public void setCustomServerTime(String customServerTime) { this.customServerTime = customServerTime; }

    public String getBertIntent() {
      return bertIntent;
    }

    public List<SdsSpecCustomServerMap> getCustomServerAnswer() {
      return customServerAnswer;
    }

    public void setCustomServerAnswer(
        List<SdsSpecCustomServerMap> customServerAnswer) {
      this.customServerAnswer = customServerAnswer;
    }

    @Override
    public String toString() {
      return "SdsActionResponse{" +
          "answer=" + answer +
          ", farewell=" + farewell +
          ", expectedIntents=" + expectedIntents +
          ", setMemory=" + setMemory +
          ", bertIntent='" + bertIntent + '\'' +
          ", display='" + display + '\'' +
          ", meta=" + meta +
          ", jsonDebug='" + jsonDebug + '\'' +
          ", responseOrder='" + responseOrder + '\'' +
          ", engines=" + engines +
          ", relation=" + relation +
          ", customServerEngine='" + customServerEngine + '\'' +
          ", customServerTime=" + customServerTime +
          ", customServerAnswer=" + customServerAnswer +
          ", h_task=" + h_task +
          ", h_item=" + h_item +
          ", h_param=" + h_param +
          '}';
    }

    public String getH_task() {
        return h_task;
    }

    public void setH_task(String h_task) {
        this.h_task = h_task;
    }

    public String getH_item() {
        return h_item;
    }

    public void setH_item(String h_item) {
        this.h_item = h_item;
    }

    public String getH_param() {
        return h_param;
    }

    public void setH_param(String h_param) {
        this.h_param = h_param;
    }
}
