package maum.brain.sds.data.vo.engines;

import maum.brain.sds.data.vo.SdsData;
import maum.brain.sds.data.vo.engines.spec.SdsSpec;

public class SdsModel implements SdsData {
    private String model;
    private int time;
    private boolean pass;
    private SdsSpec specific;

    public SdsModel() {
    }

    public SdsModel(String model, int time, boolean pass, SdsSpec specific) {
        this.model = model;
        this.time = time;
        this.pass = pass;
        this.specific = specific;
    }

    public SdsModel(String model, int time, boolean pass) {
        this.model = model;
        this.time = time;
        this.pass = pass;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public float getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public boolean isPass() {
        return pass;
    }

    public void setPass(boolean pass) {
        this.pass = pass;
    }

    public SdsSpec getSpecific() {
        return specific;
    }

    public void setSpecific(SdsSpec specific) {
        this.specific = specific;
    }

    @Override
    public String toString() {
        return "SdsModel{" +
                "model='" + model + '\'' +
                ", time=" + time +
                ", pass=" + pass +
                ", specific=" + specific +
                '}';
    }
}