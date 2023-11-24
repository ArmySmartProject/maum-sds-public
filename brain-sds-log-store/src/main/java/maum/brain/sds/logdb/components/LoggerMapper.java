package maum.brain.sds.logdb.components;


import java.util.Map;
import maum.brain.sds.data.dto.log.SdsLogStatsRequest;
import maum.brain.sds.logdb.data.*;
import maum.brain.sds.util.logger.LogSessionCounselorRequest;

public interface LoggerMapper {
    public int logIntent(LogIntentDto dto);
    public int logEntity(LogEntityDto dto);
    public int logEntityDetail(LogEntityDetailDto dto);
    public int logAnswer(LogAnswerDto dto);
    public void logMessage(LogMessageDto dto);
    public int logIntentHost(LogIntentDto dto);
    public int logEntityHost(LogEntityDto dto);
    public int logEntityDetailHost(LogEntityDetailDto dto);
    public int logAnswerHost(LogAnswerDto dto);
    public int logIntentHostSession(LogIntentDto dto);
    public int logEntityHostSession(LogEntityDto dto);
    public int logIntentWProb(LogIntentDto dto);
    public void logStats(SdsLogStatsRequest dto);
    public String selectAnswer(LogIntentDto dto);
    public Map<String, Object> sessionLogCheck (String input);
    public void sessionLogCountUpdate(LogSessionCntDto logSessionCntDto);
    public void sessionLogCountInsert(LogSessionDto logSessionDto);
    public int sessionMaxIDCheck();
    public void  updateCounselor(LogSessionCounselorRequest logSessionCounselorRequest);
}
