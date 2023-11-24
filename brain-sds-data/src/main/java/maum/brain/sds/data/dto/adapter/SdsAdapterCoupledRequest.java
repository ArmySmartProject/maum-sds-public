package maum.brain.sds.data.dto.adapter;

import maum.brain.sds.data.vo.SdsIntent;
import maum.brain.sds.data.vo.SdsUtter;

public class SdsAdapterCoupledRequest extends SdsAdapterRequest{
    private SdsIntent intent;
    private SdsUtter utter;

    public SdsAdapterCoupledRequest() {
    }

    public SdsAdapterCoupledRequest(SdsIntent intent) {
        this.intent = intent;
    }

    public SdsAdapterCoupledRequest(String session, String host, SdsIntent intent) {
        super(session, host);
        this.intent = intent;
    }

    public SdsAdapterCoupledRequest(SdsUtter utter) {
        this.utter = utter;
    }

    public SdsAdapterCoupledRequest(String session, String host, SdsUtter utter) {
        super(session, host);
        this.utter = utter;
    }

    public SdsIntent getIntent() {
        return intent;
    }

    public void setIntent(SdsIntent intent) {
        this.intent = intent;
    }

    public SdsUtter getUtter() {
        return utter;
    }

    public void setUtter(SdsUtter utter) {
        this.utter = utter;
    }

    @Override
    public String toString() {
        return "SdsAdapterCoupledRequest{" +
                "intent=" + intent +
                ", utter=" + utter +
                '}';
    }
}
