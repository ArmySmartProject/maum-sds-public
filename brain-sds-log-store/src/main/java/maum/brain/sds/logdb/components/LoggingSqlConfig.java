package maum.brain.sds.logdb.components;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.io.Reader;

@Configuration
public class LoggingSqlConfig {
    public static final Logger logger = LoggerFactory.getLogger(LoggingSqlConfig.class);
    private static final SqlSessionFactory factory;

    static {
        try {
            String resource = "mybatis/loggerMapConfig.xml";
            Reader reader = Resources.getResourceAsReader(resource);
            factory = new SqlSessionFactoryBuilder().build(reader);

        } catch (Exception e) {
            throw new RuntimeException(("Error initializing AppSqlConfig class. Cause: " + e));
        }
    }

    public static SqlSession getNewSqlSessionInstance() {
        return factory.openSession(true);
    }

    public static void closeSession(SqlSession session){
        session.close();
    }
}
