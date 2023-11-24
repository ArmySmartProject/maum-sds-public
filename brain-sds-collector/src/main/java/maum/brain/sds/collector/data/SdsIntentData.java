package maum.brain.sds.collector.data;

import maum.brain.bert.intent.Itc;
import maum.brain.sds.data.vo.SdsMeta;
import maum.brain.sds.data.vo.engines.SdsEngines;
import maum.brain.sds.data.vo.relations.SdsRelation;

public class SdsIntentData {
    private Itc.Intent intent;
    private String bertIntent;
    private String engine;
    private String model;
    private String Error;
    private SdsMeta meta;
    private String taskRel;
    private String logString;
    private String strBertIntent;
    private SdsEngines sdsEngines;
    private SdsRelation sdsRelation;

    public SdsIntentData(){
    }

    public SdsIntentData(String bertIntent){
        this.bertIntent = bertIntent;
    }

    public SdsIntentData(Itc.Intent intent, String bertIntent){
        this.intent = intent;
        this.bertIntent = bertIntent;
    }

    public SdsIntentData(Itc.Intent intent, String bertIntent, SdsMeta meta){
        this.intent = intent;
        this.bertIntent = bertIntent;
        this.meta = meta;
    }

    public Itc.Intent getIntent(){
        return this.intent;
    }

    public String getBertIntent(){
        return this.bertIntent;
    }

    public void addEngineModel(String engine, String model){
        this.engine = engine;
        this.model = model;
    }

    public void addError(String error){
        this.Error = error;
    }

    public void setTaskRel(String taskRel) {this.taskRel = taskRel;}

    public void setLogString(String logString) {this.logString = logString;}

    public String getStrBertIntent() {  return strBertIntent;  }

    public void setStrBertIntent(String strBertIntent) { this.strBertIntent = strBertIntent; }

    public String getEngine(){ return this.engine; }

    public String getModel(){ return this.model; }

    public String getError(){ return this.Error; }

    public SdsMeta getMeta(){ return this.meta; }

    public String getTaskRel() {return taskRel;}

    public String getLogString() {return logString;}

    public SdsEngines getSdsEngines() {
        return sdsEngines;
    }

    public void setSdsEngines(SdsEngines sdsEngines) {
        this.sdsEngines = sdsEngines;
    }

    public SdsRelation getSdsRelation() {
        return sdsRelation;
    }

    public void setSdsRelation(SdsRelation sdsRelation) {
        this.sdsRelation = sdsRelation;
    }
}
