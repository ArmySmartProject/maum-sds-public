package maum.brain.sds.logdb.data;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LogMessageRequest implements Serializable {
    private String level;
    private String msg;

    public LogMessageRequest() {
    }

    public LogMessageRequest(String level, String msg) {
        this.level = level;
        this.msg = msg;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "LogMessageRequest{" +
                "level='" + level + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
