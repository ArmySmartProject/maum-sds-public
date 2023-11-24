package maum.brain.sds.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public interface SdsRequest extends Serializable {
    public String toString();
}
