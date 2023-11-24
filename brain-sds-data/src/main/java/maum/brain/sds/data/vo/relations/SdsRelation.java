package maum.brain.sds.data.vo.relations;

import maum.brain.sds.data.vo.SdsData;
import maum.brain.sds.data.vo.relations.elem.SdsRel;
import maum.brain.sds.data.vo.relations.elem.SdsRelBertIntent;
import maum.brain.sds.data.vo.relations.elem.SdsRelIntent;

public class SdsRelation implements SdsData {
    private SdsRelIntent srcIntent;
    private SdsRelBertIntent bertIntent;
    private SdsRelIntent dstIntent;
    private SdsRel intentRel;

    public SdsRelIntent getSrcIntent() {
        return srcIntent;
    }

    public void setSrcIntent(SdsRelIntent srcIntent) {
        this.srcIntent = srcIntent;
    }

    public SdsRelBertIntent getBertIntent() {
        return bertIntent;
    }

    public void setBertIntent(SdsRelBertIntent bertIntent) {
        this.bertIntent = bertIntent;
    }

    public SdsRelIntent getDstIntent() {
        return dstIntent;
    }

    public void setDstIntent(SdsRelIntent dstIntent) {
        this.dstIntent = dstIntent;
    }

    public SdsRel getIntentRel() {
        return intentRel;
    }

    public void setIntentRel(SdsRel intentRel) {
        this.intentRel = intentRel;
    }

    public SdsRelation() {
    }

    public SdsRelation(SdsRelIntent srcIntent, SdsRelBertIntent bertIntent, SdsRelIntent dstIntent, SdsRel intentRel) {
        this.srcIntent = srcIntent;
        this.bertIntent = bertIntent;
        this.dstIntent = dstIntent;
        this.intentRel = intentRel;
    }

    public void makeRelation(int lang, String srcNo, String srcIntent, String bertNo, String bertIntent, String dstIntent){
        this.srcIntent = new SdsRelIntent(srcNo,srcIntent,lang);
        this.bertIntent = new SdsRelBertIntent(bertNo,bertIntent,lang);
        this.dstIntent = new SdsRelIntent(dstIntent,lang);
        this.intentRel = new SdsRel("IntentRel",srcIntent, bertIntent,dstIntent);
    }

    @Override
    public String toString() {
        return "SdsRelation{" +
                "srcIntent=" + srcIntent +
                ", bertIntent=" + bertIntent +
                ", dstIntent=" + dstIntent +
                ", intentRel=" + intentRel +
                '}';
    }
}
