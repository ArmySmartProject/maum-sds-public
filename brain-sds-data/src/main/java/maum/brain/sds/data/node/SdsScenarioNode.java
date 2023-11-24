package maum.brain.sds.data.node;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SdsScenarioNode implements Serializable {
    private String display;

    private SdsScenarioNode() {
    }

    protected SdsScenarioNode(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return "SdsScenarioNode{" +
                "display='" + display + '\'' +
                '}';
    }
}
