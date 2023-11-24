package maum.brain.sds.data.vo.engines;
import maum.brain.sds.data.vo.SdsData;

import java.util.ArrayList;
import java.util.List;

public class SdsEngines implements SdsData {
    private String intent;
    private String model;
    private List<SdsModel> models;

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public SdsEngines() {
        models = new ArrayList<>();
    }

    public void addEngine(SdsModel sdsModel){
        models.add(sdsModel);
    }

    public List<SdsModel> getModels() {
        return models;
    }

    public void setModels(List<SdsModel> sdsModels) {
        this.models = sdsModels;
    }


    @Override
    public String toString() {
        return "SdsEngines{" +
            "intent='" + intent + '\'' +
            ", model='" + model + '\'' +
            ", models=" + models +
            '}';
    }
}
