package maum.brain.sds.logdb.data;

public class LogEntityDetailDto {
    private int entityLogId;
    private String entityName;
    private String entityValue;
    private int host;

    public LogEntityDetailDto(int entityLogId, String entityName, String entityValue) {
        this.entityLogId = entityLogId;
        this.entityName = entityName;
        this.entityValue = entityValue;
    }

    public LogEntityDetailDto(int entityLogId, String entityName, String entityValue, int host) {
        this.entityLogId = entityLogId;
        this.entityName = entityName;
        this.entityValue = entityValue;
        this.host = host;
    }

    public int getEntityLogId() {
        return entityLogId;
    }

    public void setEntityLogId(int entityLogId) {
        this.entityLogId = entityLogId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityValue() {
        return entityValue;
    }

    public void setEntityValue(String entityValue) {
        this.entityValue = entityValue;
    }

    public int getHost() {
        return host;
    }

    public void setHost(int host) {
        this.host = host;
    }

    @Override
    public String toString() {
        return "LogEntityDetailDto{" +
                "entityLogId=" + entityLogId +
                ", entityName='" + entityName + '\'' +
                ", entityValue='" + entityValue + '\'' +
                ", host=" + host +
                '}';
    }
}
