package maum.brain.sds.data.vo.engines.spec;

import maum.brain.sds.data.vo.SdsData;

public class SdsSpecNqa implements SdsSpec{
    private String intent;
    private float prob;

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public float getProb() {
        return prob;
    }

    public void setProb(float prob) {
        this.prob = prob;
    }

    @Override
    public String toString() {
        return "SdsSpecNqa{" +
                "intent='" + intent + '\'' +
                ", prob=" + prob +
                '}';
    }

    public SdsSpecNqa(String intent, float prob) {
        this.intent = intent;
        this.prob = prob;
    }

    public SdsSpecNqa() {
    }
}
