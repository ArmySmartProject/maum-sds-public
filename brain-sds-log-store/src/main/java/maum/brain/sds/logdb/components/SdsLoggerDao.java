package maum.brain.sds.logdb.components;

import maum.brain.sds.data.vo.SdsAnswer;
import maum.brain.sds.data.vo.SdsEntity;
import maum.brain.sds.logdb.data.LogAnswerDto;
import maum.brain.sds.logdb.data.LogEntityDetailDto;
import maum.brain.sds.logdb.data.LogEntityDto;
import maum.brain.sds.logdb.data.LogIntentDto;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Repository
public class SdsLoggerDao {
    private SqlSessionFactory sessionFactory;

    @Autowired
    public SdsLoggerDao() throws IOException{
        sessionFactory = new SqlSessionFactoryBuilder().build(
                Resources.getResourceAsReader("mybatis/loggerMapConfig.xml")
        );
    }

    public int logIntent(String utter, String intent){
        SqlSession session = sessionFactory.openSession();
        try{
            LoggerMapper mapper = session.getMapper(LoggerMapper.class);
            int rows = mapper.logIntent(
                    new LogIntentDto(utter, intent)
            );

            session.commit();

            return rows;
        } finally {
            LoggingSqlConfig.closeSession(session);
        }
    }

    public int logEntity(String utter, List<SdsEntity> entities){
        SqlSession session = sessionFactory.openSession();
        try {
            LoggerMapper mapper = session.getMapper(LoggerMapper.class);
            int id = mapper.logEntity(
                    new LogEntityDto(utter)
            );

            for (SdsEntity entity: entities)
                mapper.logEntityDetail(
                        new LogEntityDetailDto(
                                id, entity.getEntityName(), entity.getEntityValue()
                        )
                );

            session.commit();
        }finally {
            LoggingSqlConfig.closeSession(session);
        }

        return 0;
    }

    public int logAnswer(String utter, String answer){
        SqlSession session = sessionFactory.openSession();
        try {
            LoggerMapper mapper = session.getMapper(LoggerMapper.class);
            int rows = mapper.logAnswer(
                    new LogAnswerDto(utter, answer)
            );

            session.commit();

            return rows;
        }finally {
            LoggingSqlConfig.closeSession(session);
        }
    }
}
