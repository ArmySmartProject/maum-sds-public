package maum.brain.sds.frontend.model;

public class HostInfoParams {
    private String host;
    private String lang;
    private String startIntent;

    public HostInfoParams() {
        this.startIntent = "default";
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getStartIntent() {
        return startIntent;
    }

    public void setStartIntent(String startIntent) {
        this.startIntent = startIntent;
    }

    @Override
    public String toString() {
        return "HostInfoParams{" +
                "host='" + host + '\'' +
                ", lang='" + lang + '\'' +
                ", startIntent='" + startIntent + '\'' +
                '}';
    }
}
