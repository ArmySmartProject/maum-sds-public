package maum.brain.sds.collector.component;

import maum.brain.bert.intent.Itc.Intent;
import maum.brain.sds.collector.data.SdsBackendDto;
import maum.brain.sds.data.dto.db.SdsReqEntityDto;
import maum.brain.sds.data.vo.SdsEntity;

import java.util.List;
import java.util.Map;

public interface SdsCollectorMapper {
    public String getReqEntity(SdsReqEntityDto dto);
    public String getEntityName(int num);
    public SdsBackendDto getBackendDestination(Map<String, String> params);
    public int checkIntent(SdsReqEntityDto dto);
    public int getIntentNum(SdsReqEntityDto dto);
    public Integer getBertIntentNum(Map<String, Object> map);
    public List<Map> getDestIntent(Map<String, Object> map);
    public String getFallbackIntent(Map<String, Object> map);
    public List<Map<String, Object>> getRegexList(Map map);
    public List<Map<String, Object>> getReplaceDict(Map map);
    public String getDestByBertName(Map<String, Object> map);
    public List<SdsEntity> getEntitiesDefault(Map<String, Object> map);
}
