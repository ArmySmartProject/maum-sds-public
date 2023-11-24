package maum.brain.sds.data.dto.db;

public class SdsReqEntityDto {
    private String intent;
    private String lang;
    private String host;

    public SdsReqEntityDto(String intent, String lang) {
        this.intent = intent;
        this.lang = lang;
    }

    public SdsReqEntityDto(String intent, String lang, String host) {
        this.intent = intent;
        this.lang = lang;
        this.host = host;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public String toString() {
        return "SdsReqEntityDto{" +
                "intent='" + intent + '\'' +
                ", lang='" + lang + '\'' +
                ", host='" + host + '\'' +
                '}';
    }
}
