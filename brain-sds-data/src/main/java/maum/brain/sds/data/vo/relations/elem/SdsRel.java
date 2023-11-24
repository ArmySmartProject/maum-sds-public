package maum.brain.sds.data.vo.relations.elem;

import maum.brain.sds.data.vo.SdsData;

public class SdsRel implements SdsData {
    private String type;
    private String srcIntent;
    private String bertIntent;
    private String dstIntent;

    public String getType() {
        return type;
    }

    public void setType(String type) {
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

    public String getDstIntent() {
        return dstIntent;
    }

    public void setDstIntent(String dstIntent) {
        this.dstIntent = dstIntent;
    }

    public SdsRel(String type, String srcIntent, String bertIntent, String dstIntent) {
        this.type = type;
        this.srcIntent = srcIntent;
        this.bertIntent = bertIntent;
        this.dstIntent = dstIntent;
    }

    @Override
    public String toString() {
        return "SdsRel{" +
                "type='" + type + '\'' +
                ", srcIntent='" + srcIntent + '\'' +
                ", bertIntent='" + bertIntent + '\'' +
                ", dstIntent='" + dstIntent + '\'' +
                '}';
    }
}
