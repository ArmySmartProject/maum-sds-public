package maum.brain.sds.collector.data.memory;

import maum.brain.sds.data.vo.*;
import maum.brain.sds.memory.Memory;

import java.util.ArrayList;
import java.util.List;

public class SdsMemoryDataConverter {

    private SdsMemoryDataConverter(){

    }

    public static Memory.MemIntent convert(SdsIntent intent){
        return Memory.MemIntent.newBuilder()
                .setIntent(intent.getIntent())
                .setHierachy(intent.getIntent().split("\\.").length)
                .setEntities(false)
                .build();
    }

    public static Memory.MemIntent convert(SdsIntent intent, boolean entityReq){
        return Memory.MemIntent.newBuilder()
                .setIntent(intent.getIntent())
                .setHierachy(intent.getIntent().split("\\.").length)
                .setEntities(entityReq)
                .build();
    }

    public static Memory.MemEntity convert(SdsEntity entity){
        return Memory.MemEntity.newBuilder()
                .setEntityName(entity.getEntityName())
                .setEntityValue(entity.getEntityValue())
                .build();
    }

    public static List<Memory.MemEntity> convert(List<SdsEntity> entities){
        List<Memory.MemEntity> memEntities = new ArrayList<>();
        for(SdsEntity entity: entities)
            memEntities.add(
                    convert(entity)
            );
        return memEntities;
    }

    public static List<SdsEntity> convertMem(List<Memory.MemEntity> memEntities){
        List<SdsEntity> entities = new ArrayList<>();
        for(Memory.MemEntity memEntity: memEntities){
            entities.add(
                    new SdsEntity(
                            memEntity.getEntityName(),
                            memEntity.getEntityValue()
                    )
            );
        }
        return entities;
    }

    public static SdsIntent convertMem(Memory.MemIntent memIntent){
        return new SdsIntent(
                memIntent.getIntent()
        );
    }

    public static SdsMemory convert(Memory.MemTurn memTurn){
        SdsUtter utter = new SdsUtter();
        utter.setUtter(memTurn.getUtter());

        SdsIntent intent = new SdsIntent();
        intent.setIntent(memTurn.getIntent().getIntent());
        intent.setHierarchy(memTurn.getIntent().getHierachy());
        intent.setEntityFlag(memTurn.getIntent().getEntities());

        SdsEntityList entities = new SdsEntityList();
        List<SdsEntity> entityList = new ArrayList<>();
        for(Memory.MemEntity entity: memTurn.getEntitiesList()){
            entityList.add(
                    new SdsEntity(entity.getEntityName(), entity.getEntityValue())
            );
        }
        entities.setEntities(entityList);

        return new SdsMemory(
                utter, intent, entities,
                memTurn.getLifespan(),
                memTurn.getUser().getHost(),
                memTurn.getUser().getSession()
        );
    }

    public static List<Memory.MemEntity> convert(SdsEntityList entityList){
        List<Memory.MemEntity> memEntities = new ArrayList<>();
        for(SdsEntity entity: entityList.getEntities())
            memEntities.add(
                    convert(entity)
            );
        return memEntities;
    }

    public static Memory.MemUserInfo convert(String host, String session){
        return Memory.MemUserInfo.newBuilder()
                .setHost(host)
                .setSession(session)
                .build();
    }

    public static Memory.MemTurn convert(SdsMemoryDto sdsMemoryDto){
        return Memory.MemTurn.newBuilder()
                .setUtter(sdsMemoryDto.getUtter())
                .setIntent(sdsMemoryDto.getIntent())
                .addAllEntities(sdsMemoryDto.getEntities())
                .setLifespan(sdsMemoryDto.getLifespan())
                .setUser(sdsMemoryDto.getUser())
                .putAllEntitySet(sdsMemoryDto.getEntitySet())
                .build();
    }
}
