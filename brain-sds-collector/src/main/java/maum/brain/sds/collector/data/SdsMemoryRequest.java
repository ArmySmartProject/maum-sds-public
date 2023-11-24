package maum.brain.sds.collector.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import maum.brain.sds.data.vo.SdsEntity;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SdsMemoryRequest implements Serializable {
    private String session;
    private String utter;
    private String intent;
    private List<SdsEntity> entities;

    public SdsMemoryRequest() {
    }

    public SdsMemoryRequest(String session) {
        this.session = session;
    }

    public SdsMemoryRequest(String session, String utter, String intent, List<SdsEntity> entities) {
        this.session = session;
        this.utter = utter;
        this.intent = intent;
        this.entities = entities;
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

    public List<SdsEntity> getEntities() {
        return entities;
    }

    public void setEntities(List<SdsEntity> entities) {
        this.entities = entities;
    }

    @Override
    public String toString() {
        return "SdsMemoryRequest{" +
                "session='" + session + '\'' +
                ", utter='" + utter + '\'' +
                ", intent='" + intent + '\'' +
                ", entities=" + entities +
                '}';
    }
}
