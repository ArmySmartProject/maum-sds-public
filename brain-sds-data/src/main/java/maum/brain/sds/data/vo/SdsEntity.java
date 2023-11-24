package maum.brain.sds.data.vo;

public class SdsEntity implements SdsData {
    private String entityName;
    private String entityValue;

    public SdsEntity() {
    }

    public SdsEntity(String entityName, String entityValue) {
        this.entityName = entityName;
        this.entityValue = entityValue;
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

    @Override
    public String toString() {
        return "SdsEntity{" +
                "entityName='" + entityName + '\'' +
                ", entityValue='" + entityValue + '\'' +
                '}';
    }
}
