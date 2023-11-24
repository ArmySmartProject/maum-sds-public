package maum.brain.sds.data.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import maum.brain.sds.data.vo.SdsEntity;
import maum.brain.sds.data.vo.engines.spec.SdsSpecCustomServerMap;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RestApiDto implements Serializable {
    // 결과 코드
    private String resultCode = "200";
    // 에러가 발생한 경우의 에러 메세지
    private String message = "";
    // 사용자 발화
    private String utter;
    // 발화 의도
    private String intent;
    // 답변을 찾은 엔진
    private String engine;
    // 답변 정확도
    private Double probability;
    // return할 entity list
    private List<SdsEntity> entities;
    // meta
    private String meta;
    // CustomServer Key,Value List
    private List<SdsSpecCustomServerMap> customServerMapList;

    public RestApiDto() {
        utter = "";
        intent = "";
        engine = "";
        probability = 0.0;
        entities = new ArrayList<>();
        meta = "";
        customServerMapList = new ArrayList<>();
    }

    public RestApiDto(String utter, String intent, List<SdsEntity> entities) {
        this.utter = utter;
        this.intent = intent;
        this.entities = entities;
        customServerMapList = new ArrayList<>();
    }

    public RestApiDto(String utter, String intent, String engine, Double probability,
        List<SdsEntity> entities, String meta) {
        this.utter = utter;
        this.intent = intent;
        this.engine = engine;
        this.probability = probability;
        this.entities = entities;
        this.meta = meta;
        customServerMapList = new ArrayList<>();
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

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public Double getProbability() {
        return probability;
    }

    public void setProbability(Double probability) {
        this.probability = probability;
    }

    public List<SdsEntity> getEntities() {
        return entities;
    }

    public void setEntities(List<SdsEntity> entities) {
        this.entities = entities;
    }

    public SdsEntity getEntityByIndex(int idx) {
        return entities.get(idx);
    }

    public void addEntity(SdsEntity entity) {
        entities.add(entity);
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public List<SdsSpecCustomServerMap> getCustomServerMapList() {
        return customServerMapList;
    }

    public void setCustomServerMapList(
        List<SdsSpecCustomServerMap> customServerMapList) {
        this.customServerMapList = customServerMapList;
    }

    @Override
    public String toString() {
        return "RestApiDto{" +
            "resultCode='" + resultCode + '\'' +
            ", message='" + message + '\'' +
            ", utter='" + utter + '\'' +
            ", intent='" + intent + '\'' +
            ", engine='" + engine + '\'' +
            ", probability=" + probability +
            ", entities=" + entities +
            ", meta='" + meta + '\'' +
            ", customServerMapList=" + customServerMapList +
            '}';
    }
}
