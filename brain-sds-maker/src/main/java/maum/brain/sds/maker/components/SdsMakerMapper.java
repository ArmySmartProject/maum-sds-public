package maum.brain.sds.maker.components;

import maum.brain.sds.data.dto.db.SdsReqEntityDto;
import maum.brain.sds.maker.data.DbResponseDto;
import maum.brain.sds.maker.data.IntentRelDto;
import maum.brain.sds.maker.data.MakerDatabaseDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SdsMakerMapper {
    public List<DbResponseDto> getResponse(MakerDatabaseDto dto);
    public String getReqEntityIds(SdsReqEntityDto dto);
    public List<Map> getReqEntity(Map param);
    public String getRequestAnswer(Map param);
    public String getEntityName(int num);
    public int checkIntent(SdsReqEntityDto dto);
    public List<IntentRelDto> getIntentRel(@Param("srcIntent") String srcIntent,
                                     @Param("destIntent") String bertIntent,
                                     @Param("host") String host,
                                     @Param("lang") String lang,
                                     @Param("bert") String bert);
    public String getConditionAnswer(@Param("num") String num, @Param("host") String host, @Param("lang") String lang);
    public List<Map> getEntityList(@Param("host") String host, @Param("lang") String lang);
}
