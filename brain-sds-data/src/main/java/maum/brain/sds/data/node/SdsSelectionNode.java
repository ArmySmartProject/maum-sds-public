package maum.brain.sds.data.node;

import maum.brain.sds.data.vo.SdsIntent;

public class SdsSelectionNode extends SdsScenarioNode {
    private SdsIntent intent;

    public SdsSelectionNode(String display) {
        super(display);
    }

    public SdsSelectionNode(String display, SdsIntent intent) {
        super(display);
        this.intent = intent;
    }

    public SdsIntent getIntent() {
        return intent;
    }

    public void setIntent(SdsIntent intent) {
        this.intent = intent;
    }

    @Override
    public String toString() {
        return "SdsSelectionNode{" +
                "intent=" + intent +
                "} " + super.toString();
    }
}