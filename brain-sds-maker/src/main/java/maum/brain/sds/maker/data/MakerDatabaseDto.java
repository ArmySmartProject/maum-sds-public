package maum.brain.sds.maker.data;

public class MakerDatabaseDto {
    private String account;
    private String session;
    private String intent;
    private String entityNameCs;
    private String entityValuesCs;
    private int lang;

    public MakerDatabaseDto(String account, String session, String intent, String entityNameCs, String entityValuesCs) {
        this.account = account;
        this.session = session;
        this.intent = intent;
        this.entityNameCs = entityNameCs;
        this.entityValuesCs = entityValuesCs;
        this.lang = 1;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getEntityNameCs() {
        return entityNameCs;
    }

    public void setEntityNameCs(String entityNameCs) {
        this.entityNameCs = entityNameCs;
    }

    public String getEntityValuesCs() {
        return entityValuesCs;
    }

    public void setEntityValuesCs(String entityValuesCs) {
        this.entityValuesCs = entityValuesCs;
    }

    public int getLang() {
        return lang;
    }

    public void setLang(int lang) {
        this.lang = lang;
    }

    @Override
    public String toString() {
        return "MakerDatabaseDto{" +
                "account=" + account +
                ", session='" + session + '\'' +
                ", intent='" + intent + '\'' +
                ", entityNameCs='" + entityNameCs + '\'' +
                ", entityValuesCs='" + entityValuesCs + '\'' +
                ", lang=" + lang +
                '}';
    }
}
