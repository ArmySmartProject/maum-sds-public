package maum.brain.sds.data.vo.engines.spec;

public class SdsSpecFallback implements SdsSpec{
    private String intent;

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    @Override
    public String toString() {
        return "SdsSpecFallback{" +
                "intent='" + intent + '\'' +
                '}';
    }

    public SdsSpecFallback(String intent) {
        this.intent = intent;
    }

    public SdsSpecFallback() {
    }
}
