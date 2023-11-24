package maum.brain.sds.data.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public interface SdsData extends Serializable {
    public String toString();
}
