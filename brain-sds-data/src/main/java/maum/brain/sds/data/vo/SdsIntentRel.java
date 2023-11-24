package maum.brain.sds.data.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SdsIntentRel implements SdsData {
    private String srcIntent;
    private String bertIntent;
    private String destIntent;
    private String metaData;
    private String type;

    public SdsIntentRel() {
    }

    public SdsIntentRel(String srcIntent, String bertIntent, String destIntent, String metaData, String type) {
        this.srcIntent = srcIntent;
        this.bertIntent = bertIntent;
        this.destIntent = destIntent;
        this.metaData = metaData;
        this.type = type;
    }

    public String getSrcIntent() {
        return srcIntent;
    }

    public void setSrcIntent(String srcIntent) {
        this.srcIntent = srcIntent;
    }

    public String getBertIntent() {
        return bertIntent;
    }

    public void setBertIntent(String bertIntent) {
        this.bertIntent = bertIntent;
    }

    public String getDestIntent() {
        return destIntent;
    }

    public void setDestIntent(String destIntent) {
        this.destIntent = destIntent;
    }

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "SdsIntentRel{" +
                "srcIntent=" + srcIntent + '\'' +
                ", bertIntent=" + bertIntent + '\'' +
                ", destIntent=" + destIntent + '\'' +
                ", metaData=" + metaData + '\'' +
                ", type=" + type + '\'' +
                '}';
    }
}
