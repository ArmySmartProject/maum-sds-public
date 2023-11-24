package maum.brain.sds.logdb.data;

public class LogEntityDto {
    private String session;
    private int id;
    private String utter;
    private int host;
    private String lang;

    public LogEntityDto(String utter) {
        this.utter = utter;
    }

    public LogEntityDto(String utter, int host) {
        this.utter = utter;
        this.host = host;
    }

    public LogEntityDto(String session, String utter, int host){
        this.session = session;
        this.utter = utter;
        this.host = host;
    }

    public LogEntityDto(String session, int id, String utter, int host) {
        this.session = session;
        this.id = id;
        this.utter = utter;
        this.host = host;
    }

    public LogEntityDto(String session, String utter, int host, String lang) {
        this.session = session;
        this.utter = utter;
        this.host = host;
        this.lang = lang;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUtter() {
        return utter;
    }

    public void setUtter(String utter) {
        this.utter = utter;
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
        return "LogEntityDto{" +
                "session='" + session + '\'' +
                ", id=" + id +
                ", utter='" + utter + '\'' +
                ", host=" + host +
                ", lang='" + lang + '\'' +
                '}';
    }
}
