package maum.brain.sds.data.vo;

import java.util.Map;

public class SdsMemory implements SdsData {
    private SdsUtter utter;
    private SdsIntent intent;
    private SdsEntityList entities;
    private Map entitySet;
    private int lifespan;
    private String host;
    private String session;

    public SdsMemory() {
    }

    public SdsMemory(SdsUtter utter, SdsIntent intent, SdsEntityList entities, int lifespan, String host, String session) {
        this.utter = utter;
        this.intent = intent;
        this.entities = entities;
        this.lifespan = lifespan;
        this.host = host;
        this.session = session;
    }

    public SdsMemory(SdsUtter utter, SdsIntent intent, SdsEntityList entities, Map entitySet, int lifespan, String host, String session) {
        this.utter = utter;
        this.intent = intent;
        this.entities = entities;
        this.entitySet = entitySet;
        this.lifespan = lifespan;
        this.host = host;
        this.session = session;
    }

    public SdsUtter getUtter() {
        return utter;
    }

    public void setUtter(SdsUtter utter) {
        this.utter = utter;
    }

    public SdsIntent getIntent() {
        return intent;
    }

    public void setIntent(SdsIntent intent) {
        this.intent = intent;
    }

    public SdsEntityList getEntities() {
        return entities;
    }

    public void setEntities(SdsEntityList entities) {
        this.entities = entities;
    }

    public Map getEntitySet() {
        return entitySet;
    }

    public void setEntitySet(Map entitySet) {
        this.entitySet = entitySet;
    }

    public int getLifespan() {
        return lifespan;
    }

    public void setLifespan(int lifespan) {
        this.lifespan = lifespan;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    @Override
    public String toString() {
        return "SdsMemory{" +
                "utter=" + utter +
                ", intent=" + intent +
                ", entities=" + entities +
                ", entitySet=" + entitySet +
                ", lifespan=" + lifespan +
                ", host='" + host + '\'' +
                ", session='" + session + '\'' +
                '}';
    }
}
