package maum.brain.sds.collector.data.memory;

import maum.brain.sds.data.vo.*;
import maum.brain.sds.memory.Memory;

import java.util.List;
import java.util.Map;

public class SdsMemoryDto {
    private String utter;
    private Memory.MemIntent intent;
    private List<Memory.MemEntity> entities;
    private int lifespan;
    private Memory.MemUserInfo user;
    private Map entitySet;

    public SdsMemoryDto() {
    }

    public SdsMemoryDto(SdsMemory memory){
        this.utter = memory.getUtter().getUtter();
        this.intent = SdsMemoryDataConverter.convert(memory.getIntent(), memory.getIntent().isEntityFlag());
        this.entities = SdsMemoryDataConverter.convert(memory.getEntities());
        this.lifespan = memory.getLifespan();
        this.user = SdsMemoryDataConverter.convert(memory.getHost(), memory.getSession());
        this.entitySet = memory.getEntitySet();
    }

    public SdsMemoryDto(String utter, Memory.MemIntent intent, List<Memory.MemEntity> entities, int lifespan, Memory.MemUserInfo user) {
        this.utter = utter;
        this.intent = intent;
        this.entities = entities;
        this.lifespan = lifespan;
        this.user = user;
    }

    public SdsMemoryDto(String utter, SdsIntent intent, SdsEntityList entities, int lifespan, String host, String session){
        this.utter = utter;
        this.intent = SdsMemoryDataConverter.convert(intent);
        this.entities = SdsMemoryDataConverter.convert(entities);
        this.lifespan = lifespan;
        this.user = SdsMemoryDataConverter.convert(host, session);
    }

    public SdsMemoryDto(SdsUtter utter, SdsIntent intent, List<SdsEntity> entities, int lifespan, String host, String session){
        this.utter = utter.getUtter();
        this.intent = SdsMemoryDataConverter.convert(intent);
        this.entities = SdsMemoryDataConverter.convert(entities);
        this.lifespan = lifespan;
        this.user = SdsMemoryDataConverter.convert(host, session);
    }

    public SdsMemoryDto(String utter, Memory.MemIntent intent, SdsEntityList entities, int lifespan, String host, String session) {
        this.utter = utter;
        this.intent = intent;
        this.entities = SdsMemoryDataConverter.convert(entities);
        this.lifespan = lifespan;
        this.user = SdsMemoryDataConverter.convert(host, session);
    }

    public SdsMemoryDto(String utter, Memory.MemIntent intent, SdsEntityList entities, int lifespan, String host, String session, Map entitySet) {
        this.utter = utter;
        this.intent = intent;
        this.entities = SdsMemoryDataConverter.convert(entities);
        this.lifespan = lifespan;
        this.user = SdsMemoryDataConverter.convert(host, session);
        this.entitySet = entitySet;
    }

    public String getUtter() {
        return utter;
    }

    public void setUtter(String utter) {
        this.utter = utter;
    }

    public Memory.MemIntent getIntent() {
        return intent;
    }

    public void setIntent(Memory.MemIntent intent) {
        this.intent = intent;
    }

    public List<Memory.MemEntity> getEntities() {
        return entities;
    }

    public void setEntities(List<Memory.MemEntity> entities) {
        this.entities = entities;
    }

    public int getLifespan() {
        return lifespan;
    }

    public void setLifespan(int lifespan) {
        this.lifespan = lifespan;
    }

    public Memory.MemUserInfo getUser() {
        return user;
    }

    public void setUser(Memory.MemUserInfo user) {
        this.user = user;
    }

    public Map getEntitySet() {
        return entitySet;
    }

    public void setEntitySet(Map user) {
        this.entitySet = entitySet;
    }

    @Override
    public String toString() {
        return "SdsMemoryDto{" +
                "utter='" + utter + '\'' +
                ", intent=" + intent +
                ", entities=" + entities +
                ", lifespan=" + lifespan +
                ", user=" + user +
                ", entitySet=" + entitySet +
                '}';
    }
}
