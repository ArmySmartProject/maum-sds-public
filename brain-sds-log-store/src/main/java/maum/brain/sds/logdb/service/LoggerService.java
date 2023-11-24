package maum.brain.sds.logdb.service;

import java.util.Map;
import maum.brain.sds.data.dto.log.SdsLogStatsRequest;
import maum.brain.sds.logdb.components.LoggerDaoBeta;
import maum.brain.sds.logdb.data.LogEntityDto;
import maum.brain.sds.logdb.data.LogIntentDto;
import maum.brain.sds.logdb.data.LogMessageDto;
import maum.brain.sds.logdb.data.LogMessageRequest;
import maum.brain.sds.logdb.data.LogSessionCntDto;
import maum.brain.sds.logdb.data.LogSessionDto;
import maum.brain.sds.util.logger.LogAllRequest;
import maum.brain.sds.util.logger.LogAnswerRequest;
import maum.brain.sds.util.logger.LogEntityRequest;
import maum.brain.sds.util.logger.LogIntentRequest;
import maum.brain.sds.util.logger.LogSessionCounselorRequest;
import maum.brain.sds.util.logger.LogSessionCountRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoggerService {

    private static final Logger logger = LoggerFactory.getLogger(LoggerService.class);

    @Autowired
    private LoggerDaoBeta loggerDao;

    public int logIntent(LogIntentRequest request){
        logger.info(request.toString());
        try {
            loggerDao.logIntent(
                new LogIntentDto(request.getSession(), request.getUtter(), request.getIntent(), request.getHost(), request.getLang(), request.getProb(), request.getJsonData(),
                    request.getPrevIntentUtter(), request.getAnswer(), request.getJsonDebugData(), request.getSessionID())
            );

            return 0;
        } catch (Exception e) {
            System.out.println("logIntent Error : " + e.toString());
            return 1;
        }
    }

    public int logEntities(LogEntityRequest request){
        try {
//            loggerDao.logEntity(request.getUtter(), request.getEntities());
            loggerDao.logEntity(
                new LogEntityDto(request.getSession(), request.getUtter(), request.getHost(), request.getLang()),
                request.getEntities()
            );
            return 0;
        } catch (Exception e) {
            return 1;
        }
    }

    public int logAnswer(LogAnswerRequest request){
        try {
            loggerDao.logAnswer(request.getUtter(), request.getAnswer());

            return 0;
        } catch (Exception e){
            return 1;
        }
    }

    public int logAll(LogAllRequest request){
        try {
            String utter = request.getUtter();
            if(!request.getIntent().equals("null"))
                loggerDao.logIntent(utter, request.getIntent());
            if(request.getEntities() != null)
                loggerDao.logEntity(utter, request.getEntities());
            if(!request.getAnswer().equals("null"))
                loggerDao.logAnswer(utter, request.getAnswer());
            return 0;
        } catch (Exception e){
            return 1;
        }
    }

    public void logMessage(LogMessageRequest request){
        loggerDao.logMessage(request.getLevel(), request.getMsg());
    }

    public void logStats(SdsLogStatsRequest request){
        loggerDao.logStats(request);
    }


    public int sessionCount(LogSessionCountRequest request){
        Map<String, Object> nowSessionLog = loggerDao.sessionLogCheck(request.getSessionId());
        try{//SessionLog Update
            int nowSession = Integer.parseInt(nowSessionLog.get("id").toString());
            int nowBotCnt = (Integer) nowSessionLog.get("botCnt");
            int[] langCountArray ={0,0,0,0};
            for(int langCount = 1; langCount<=4; langCount++){
                if(nowSessionLog.get("lang" + Integer.toString(langCount) + "Cnt")==null){
                    langCountArray[langCount-1] = 0;
                }else{
                    langCountArray[langCount-1] =
                        (Integer) nowSessionLog.get("lang" + Integer.toString(langCount) + "Cnt");
                }
            }
            try{
                langCountArray[Integer.valueOf(request.getLang())-1] += 1;
            }catch (Exception e2){
                //lang이 Integer로 casting이 안 됐을 경우
                System.out.println("Integer type casting Error (Session Update) : " + e2);
            }
            LogSessionCntDto logSessionCntDto = new LogSessionCntDto(nowSession, nowBotCnt, langCountArray);
            loggerDao.sessionLogCountUpdate(logSessionCntDto);
            return nowSession;
        }catch (Exception e){//SessionLog insert
            int[] langCountArray ={0,0,0,0};
            try{
                langCountArray[Integer.valueOf(request.getLang())-1] = 1;
            }catch (Exception e2){
                //lang이 Integer로 casting이 안 됐을 경우
                System.out.println("Integer type casting Error (Session Insert) : " + e2);
            }
            LogSessionDto logSessionDto = new LogSessionDto(request, langCountArray);
            System.out.println(logSessionDto);
            loggerDao.sessionLogCountInsert(logSessionDto);//새로 생긴 SessionLog의 ID 반환
            return logSessionDto.getNewSessionID();
        }
    }

    public void updateCounselor(LogSessionCounselorRequest request){
        loggerDao.updateCounselor(request);
    }
}
