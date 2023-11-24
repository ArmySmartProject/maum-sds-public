package maum.brain.sds.analysis.model;

public class AnalysisDto {
    private String intent;
    private String host;
    private String lang;
    private String startDate;
    private String endDate;

    public AnalysisDto() {
    }

    public AnalysisDto(String host, String lang) {
        this.host = host;
        this.lang = lang;
    }

    public AnalysisDto(String intent, String host, String lang) {
        this.intent = intent;
        this.host = host;
        this.lang = lang;
    }

    public AnalysisDto(String host, String lang, String startDate, String endDate) {
        this.host = host;
        this.lang = lang;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public AnalysisDto(String intent, String host, String lang, String startDate, String endDate) {
        this.intent = intent;
        this.host = host;
        this.lang = lang;
        this.startDate = startDate;
        this.endDate = endDate;
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "AnalysisDto{" +
                "host='" + host + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                '}';
    }
}
