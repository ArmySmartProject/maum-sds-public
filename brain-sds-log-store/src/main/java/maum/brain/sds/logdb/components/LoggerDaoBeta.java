package maum.brain.sds.logdb.components;

import java.util.Map;

import com.mysql.jdbc.log.Log;
import maum.brain.sds.data.dto.log.SdsLogStatsRequest;
import maum.brain.sds.logdb.data.*;
import maum.brain.sds.util.logger.LogSessionCounselorRequest;
import maum.brain.sds.util.logger.SdsEntity;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class LoggerDaoBeta {
    private static final Logger logger = LoggerFactory.getLogger(LoggerDaoBeta.class);

    public int logIntent(String utter, String intent){
        SqlSession session = LoggingSqlConfig.getNewSqlSessionInstance();
        try{
            LoggerMapper mapper = session.getMapper(LoggerMapper.class);
            return mapper.logIntent(
                    new LogIntentDto(
                            utter, intent
                    )
            );
        }finally {
            LoggingSqlConfig.closeSession(session);
        }

    }

    public int logIntent(String utter, String intent, int host){
        SqlSession session = LoggingSqlConfig.getNewSqlSessionInstance();

        try{
            LoggerMapper mapper = session.getMapper(LoggerMapper.class);
            return mapper.logIntentHost(
                    new LogIntentDto(utter, intent, host)
            );
        }finally {
            LoggingSqlConfig.closeSession(session);
        }
    }

    public int logIntent(LogIntentDto dto){
        SqlSession session = LoggingSqlConfig.getNewSqlSessionInstance();
        try{
            LoggerMapper mapper = session.getMapper(LoggerMapper.class);
            if(dto.getAnswer()==null || dto.getAnswer().length()==0){
                dto.setAnswer(mapper.selectAnswer(dto));
            }
            return mapper.logIntentWProb(dto);
        }finally {
            LoggingSqlConfig.closeSession(session);
        }
    }

    public int logEntity(String utter, List<SdsEntity> entities){
        SqlSession session = LoggingSqlConfig.getNewSqlSessionInstance();

        try {
            LoggerMapper mapper = session.getMapper(LoggerMapper.class);
            LogEntityDto dto = new LogEntityDto(utter);
            mapper.logEntity(dto);

            int id = dto.getId();
            System.out.println(id);

            for (SdsEntity entity: entities)
                mapper.logEntityDetail(
                    new LogEntityDetailDto(
                        id, entity.getEntityName(), entity.getEntityValue()
                    )
                );

            session.commit();

            return 0;
        } finally {
            LoggingSqlConfig.closeSession(session);
        }
    }

    public int logEntity(String utter, List<SdsEntity> entities, int host){
        SqlSession session = LoggingSqlConfig.getNewSqlSessionInstance();
        try{
            LoggerMapper mapper = session.getMapper(LoggerMapper.class);
            LogEntityDto dto = new LogEntityDto(utter, host);
            mapper.logEntity(dto);

            int id = dto.getId();
            for (SdsEntity entity: entities)
                mapper.logEntityDetailHost(
                    new LogEntityDetailDto(
                        id, entity.getEntityName(), entity.getEntityValue(), host
                    )
                );

            session.commit();

            return 0;
        } finally {
            LoggingSqlConfig.closeSession(session);
        }
    }

    public int logEntity(LogEntityDto entityDto, List<SdsEntity> entities){
        SqlSession session = LoggingSqlConfig.getNewSqlSessionInstance();
        try{
            LoggerMapper mapper = session.getMapper(LoggerMapper.class);
            LogEntityDto dto = entityDto;
            mapper.logEntityHostSession(dto);

            int id = dto.getId();
            for (SdsEntity entity: entities)
                mapper.logEntityDetailHost(
                    new LogEntityDetailDto(
                        id, entity.getEntityName(), entity.getEntityValue(), dto.getHost()
                    )
                );

            session.commit();

            return 0;
        } finally {
            LoggingSqlConfig.closeSession(session);
        }
    }

    public int logAnswer(String utter, String answer){
        SqlSession session = LoggingSqlConfig.getNewSqlSessionInstance();
        try{
            LoggerMapper mapper = session.getMapper(LoggerMapper.class);
            return mapper.logAnswer(
                    new LogAnswerDto(
                            utter, answer
                    )
            );
        }finally {
            LoggingSqlConfig.closeSession(session);
        }
    }

    public int logAnswer(String utter, String answer, int host){
        SqlSession session = LoggingSqlConfig.getNewSqlSessionInstance();
        try{
            LoggerMapper mapper = session.getMapper(LoggerMapper.class);
            return mapper.logAnswerHost(
                    new LogAnswerDto(utter, answer, host)
            );
        }finally {
            LoggingSqlConfig.closeSession(session);
        }
    }

    public void logMessage(String level, String msg){
        SqlSession session = LoggingSqlConfig.getNewSqlSessionInstance();
        try{
            LoggerMapper mapper = session.getMapper(LoggerMapper.class);
            mapper.logMessage(
                    new LogMessageDto(
                            level, msg
                    )
            );
        }finally {
            LoggingSqlConfig.closeSession(session);
        }
    }

    public void logStats(SdsLogStatsRequest request){
        SqlSession session = LoggingSqlConfig.getNewSqlSessionInstance();
        try{
            LoggerMapper mapper = session.getMapper(LoggerMapper.class);
            mapper.logStats(request);
        }finally {
            LoggingSqlConfig.closeSession(session);
        }
    }

    public Map<String, Object> sessionLogCheck(String input){
        SqlSession session = LoggingSqlConfig.getNewSqlSessionInstance();
        try{
            LoggerMapper mapper = session.getMapper(LoggerMapper.class);
            return mapper.sessionLogCheck(input);
        }finally {
            LoggingSqlConfig.closeSession(session);
        }
    }

    public void sessionLogCountUpdate(LogSessionCntDto logSessionCntDto){
        SqlSession session = LoggingSqlConfig.getNewSqlSessionInstance();
        try{
            LoggerMapper mapper = session.getMapper(LoggerMapper.class);
            mapper.sessionLogCountUpdate(logSessionCntDto);
        }finally {
            LoggingSqlConfig.closeSession(session);
        }
    }

    public void sessionLogCountInsert(LogSessionDto logSessionDto){
        SqlSession session = LoggingSqlConfig.getNewSqlSessionInstance();
        try {
            LoggerMapper mapper = session.getMapper(LoggerMapper.class);
            mapper.sessionLogCountInsert(logSessionDto);
        }catch (Exception e){
            e.printStackTrace();
        } finally{
            LoggingSqlConfig.closeSession(session);
        }
    }

    public int sessionMaxIDCheck(){
        SqlSession session = LoggingSqlConfig.getNewSqlSessionInstance();
        try{
            LoggerMapper mapper = session.getMapper(LoggerMapper.class);
            return mapper.sessionMaxIDCheck();
        }finally {
            LoggingSqlConfig.closeSession(session);
        }
    }

    public void updateCounselor(LogSessionCounselorRequest logSessionCounselorRequest){
        SqlSession session = LoggingSqlConfig.getNewSqlSessionInstance();
        try{
            LoggerMapper mapper = session.getMapper(LoggerMapper.class);
            mapper.updateCounselor(logSessionCounselorRequest);
        }finally {
            LoggingSqlConfig.closeSession(session);
        }
    }
}
