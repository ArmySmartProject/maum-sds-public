package maum.brain.sds.data.vo;

import java.util.ArrayList;
import java.util.List;

public class SdsEntityList implements SdsData{
    private List<SdsEntity> entities;

    public SdsEntityList() {
        entities = new ArrayList<>();
    }

    public SdsEntityList(List<SdsEntity> entities) {
        this.entities = entities;
    }

    public List<SdsEntity> getEntities() {
        return entities;
    }

    public void setEntities(List<SdsEntity> entities) {
        this.entities = entities;
    }

    public SdsEntity getEntity(int index){
        return this.entities.get(index);
    }

    public void setEntity(SdsEntity entity){
        this.entities.add(entity);
    }

    @Override
    public String toString() {
        return "SdsEntityList{" +
                "entities=" + entities +
                '}';
    }
}
