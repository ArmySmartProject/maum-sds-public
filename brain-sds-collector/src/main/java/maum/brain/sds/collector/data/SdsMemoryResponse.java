package maum.brain.sds.collector.data;

import maum.brain.sds.data.dto.SdsResponse;
import maum.brain.sds.data.vo.SdsEntity;

import java.util.List;

public class SdsMemoryResponse implements SdsResponse {
    private List<String> utters;
    private List<String> intents;
    private List<SdsEntity> entities;

    public SdsMemoryResponse() {
    }

    public SdsMemoryResponse(List<String> utters, List<String> intents, List<SdsEntity> entities) {
        this.utters = utters;
        this.intents = intents;
        this.entities = entities;
    }

    public List<String> getUtters() {
        return utters;
    }

    public void setUtters(List<String> utters) {
        this.utters = utters;
    }

    public List<String> getIntents() {
        return intents;
    }

    public void setIntents(List<String> intents) {
        this.intents = intents;
    }

    public List<SdsEntity> getEntities() {
        return entities;
    }

    public void setEntities(List<SdsEntity> entities) {
        this.entities = entities;
    }

    @Override
    public String toString() {
        return "SdsMemoryResponse{" +
                "utters=" + utters +
                ", intents=" + intents +
                ", entities=" + entities +
                '}';
    }
}
