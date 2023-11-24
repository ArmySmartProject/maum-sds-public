package maum.brain.sds.data.vo;

public class SdsUtter implements SdsData {
    private String utter;

    public SdsUtter() {
    }

    public SdsUtter(String utter) {
        this.utter = utter;
    }

    public String getUtter() {
        return utter;
    }

    public void setUtter(String utter) {
        this.utter = utter;
    }

    @Override
    public String toString() {
        return "SdsUtter{" +
                "utter='" + utter + '\'' +
                '}';
    }
}
