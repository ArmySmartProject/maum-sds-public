package maum.brain.sds.logdb.data;

public class LogMessageDto {
    private String level;
    private String msg;

    public LogMessageDto() {
    }

    public LogMessageDto(String level, String msg) {
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
