package maum.brain.sds.data.dto.maker;

import maum.brain.sds.data.dto.SdsRequest;
import maum.brain.sds.data.vo.SdsEntity;
import maum.brain.sds.data.vo.SdsIntent;
import maum.brain.sds.data.vo.SdsMemory;
import maum.brain.sds.data.vo.SdsMeta;
import maum.brain.sds.data.vo.SdsUtter;

import java.util.ArrayList;
import java.util.List;

public class SdsMakerRequest implements SdsRequest {
    private String host;
    private String session;
    private String srcIntent;
    private SdsIntent intent;
    private SdsUtter utter;
    private List<SdsEntity> entities;
    private List<SdsMemory> memories;
    private String lang;
    private String bertIntent;
    private SdsMeta meta;

    public SdsMakerRequest() {
        this.entities = new ArrayList<>();
        this.memories = new ArrayList<>();
    }

    public SdsMakerRequest(String host, String session, SdsIntent intent, SdsUtter utter, List<SdsEntity> entities, List<SdsMemory> memories, String lang) {
        this.host = host;
        this.session = session;
        this.intent = intent;
        this.utter = utter;
        this.entities = entities;
        this.memories = memories;
        this.lang = lang;
    }

    public SdsMakerRequest(String host, String session, String srcIntent, SdsIntent intent, SdsUtter utter, List<SdsEntity> entities, List<SdsMemory> memories, String lang) {
        this.host = host;
        this.session = session;
        this.srcIntent = srcIntent;
        this.intent = intent;
        this.utter = utter;
        this.entities = entities;
        this.memories = memories;
        this.lang = lang;
    }

    public SdsMakerRequest(String host, String session, String srcIntent, SdsIntent intent, SdsUtter utter, List<SdsEntity> entities, List<SdsMemory> memories, String lang, String bertIntent, SdsMeta meta) {
        this.host = host;
        this.session = session;
        this.srcIntent = srcIntent;
        this.intent = intent;
        this.utter = utter;
        this.entities = entities;
        this.memories = memories;
        this.lang = lang;
        this.bertIntent = bertIntent;
        this.meta = meta;
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

    public String getSrcIntent() {
        return srcIntent;
    }

    public void setSrcIntent(String srcIntent) {
        this.srcIntent = srcIntent;
    }

    public SdsIntent getIntent() {
        return intent;
    }

    public void setIntent(SdsIntent intent) {
        this.intent = intent;
    }

    public List<SdsEntity> getEntities() {
        return entities;
    }

    public void setEntities(List<SdsEntity> entities) {
        this.entities = entities;
    }

    public SdsUtter getUtter() {
        return utter;
    }

    public void setUtter(SdsUtter utter) {
        this.utter = utter;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public List<SdsMemory> getMemories() {
        return memories;
    }

    public void setMemories(List<SdsMemory> memories) {
        this.memories = memories;
    }

    public String getBertIntent() {
        return bertIntent;
    }

    public void setBertIntent(String bertIntent) { this.bertIntent = bertIntent; }

    public SdsMeta getMeta() {
        return meta;
    }

    public void setMeta(SdsMeta meta) { this.meta = meta; }

    @Override
    public String toString() {
        return "SdsMakerRequest{" +
                "host='" + host + '\'' +
                ", session='" + session + '\'' +
                ", srcIntent='" + srcIntent + '\'' +
                ", intent=" + intent +
                ", utter=" + utter +
                ", entities=" + entities +
                ", memories=" + memories +
                ", lang='" + lang + '\'' +
                ", bertIntent='" + bertIntent + '\'' +
                ", meta=" + meta +
                '}';
    }
}
