package maum.brain.sds.util.data;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SdsConciergeRequest implements Serializable {
    private SdsConciergeData data;

    public SdsConciergeRequest(SdsConciergeData data) {
        this.data = data;
    }

    public SdsConciergeData getData() {
        return data;
    }

    public void setData(SdsConciergeData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "SdsConciergeRequest{" +
                "data=" + data +
                '}';
    }
}