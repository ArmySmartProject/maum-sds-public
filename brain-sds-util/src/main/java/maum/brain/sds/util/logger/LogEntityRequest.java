package maum.brain.sds.util.logger;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LogEntityRequest implements Serializable {
    private String session;
    private String utter;
    private List<SdsEntity> entities;
    private int host;
    private String lang;

    public LogEntityRequest() {
    }

    public LogEntityRequest(String utter, List<SdsEntity> entities) {
        this.utter = utter;
        this.entities = entities;
    }

    public LogEntityRequest(String utter, List<SdsEntity> entities, int host) {
        this.utter = utter;
        this.entities = entities;
        this.host = host;
    }

    public LogEntityRequest(String session, String utter, List<SdsEntity> entities, int host) {
        this.session = session;
        this.utter = utter;
        this.entities = entities;
        this.host = host;
    }

    public LogEntityRequest(String session, String utter, List<SdsEntity> entities, int host, String lang) {
        this.session = session;
        this.utter = utter;
        this.entities = entities;
        this.host = host;
        this.lang = lang;
    }

    public String getUtter() {
        return utter;
    }

    public void setUtter(String utter) {
        this.utter = utter;
    }

    public List<SdsEntity> getEntities() {
        return entities;
    }

    public void setEntities(List<SdsEntity> entities) {
        this.entities = entities;
    }

    public int getHost() {
        return host;
    }

    public void setHost(int host) {
        this.host = host;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    @Override
    public String toString() {
        return "LogEntityRequest{" +
                "session='" + session + '\'' +
                ", utter='" + utter + '\'' +
                ", entities=" + entities +
                ", host=" + host +
                ", lang='" + lang + '\'' +
                '}';
    }
}
