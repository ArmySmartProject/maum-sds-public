package maum.brain.sds.analysis.mapper;

import maum.brain.sds.analysis.model.AnalysisAcumDto;
import maum.brain.sds.analysis.model.AnalysisDto;
import maum.brain.sds.analysis.model.AnalysisTimeDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AnalysisMapper {
    public int selectTotalMessages(AnalysisDto dto);
    public int selectTotalUsers(AnalysisDto dto);
    public int selectUsers(@Param("dto") AnalysisDto dto, @Param("key") String key);
    public List<AnalysisTimeDto> selectTotalMsgPerHour(AnalysisDto dto);
    public List<AnalysisTimeDto> selectTotalUserPerHour(AnalysisDto dto);
    public int selectWeakProb(AnalysisDto dto);
    public int selectTotalEmails(AnalysisDto dto);
    public List<AnalysisAcumDto> selectMostIntents(AnalysisDto dto);
    public List<AnalysisAcumDto> selectMostIntentsAll(AnalysisDto dto);
    public List<AnalysisAcumDto> selectUttersFromIntents(AnalysisDto dto);
    public List<AnalysisAcumDto> selectUserCountry(AnalysisDto dto);
    public String selectEmailInfo(String host);
    public void updateEmailInfo(String host, String email);
}
