package maum.brain.sds.analysis.model;

import maum.brain.sds.analysis.mapper.AnalysisMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Repository
public class AnalysisDao {
    private SqlSessionFactory sessionFactory;

    @Autowired
    public AnalysisDao() throws IOException{
        sessionFactory = new SqlSessionFactoryBuilder().build(
                Resources.getResourceAsReader("mybatis/analysisMapConfig.xml")
        );
    }

    public int getTotalMessages(String host, String lang, String startDate, String endDate){
        try (SqlSession session = sessionFactory.openSession()){
            return session.getMapper(AnalysisMapper.class).selectTotalMessages(
                    new AnalysisDto(host, lang, startDate, endDate)
            );
        }
    }

    public int getTotalUsers(String host, String lang, String startDate, String endDate){
        try (SqlSession session = sessionFactory.openSession()){
            return session.getMapper(AnalysisMapper.class).selectTotalUsers(
                    new AnalysisDto(host, lang, startDate, endDate)
            );
        }
    }

    public int getUsers(String host, String lang, String startDate, String endDate, String key){
        try (SqlSession session = sessionFactory.openSession()){
            return session.getMapper(AnalysisMapper.class).selectUsers(
                new AnalysisDto(host, lang, startDate, endDate), key
            );
        }
    }

    public int getWeakProb(String host, String lang, String startDate, String endDate){
        try (SqlSession session = sessionFactory.openSession()){
            return session.getMapper(AnalysisMapper.class).selectWeakProb(
                    new AnalysisDto(host, lang, startDate, endDate)
            );
        }
    }

    public int getTotalEmails(String host, String lang, String startDate, String endDate){
        try (SqlSession session = sessionFactory.openSession()){
            return session.getMapper(AnalysisMapper.class).selectTotalEmails(
                new AnalysisDto(host, lang, startDate, endDate)
            );
        }
    }

    public List<AnalysisTimeDto> getMsgPerHour(String host, String lang, String startDate, String endDate){
        try (SqlSession session = sessionFactory.openSession()){
            return session.getMapper(AnalysisMapper.class).selectTotalMsgPerHour(
                    new AnalysisDto(host, lang, startDate, endDate)
            );
        }
    }

    public List<AnalysisTimeDto> getUserPerHour(String host, String lang, String startDate, String endDate){
        try (SqlSession session = sessionFactory.openSession()){
            return session.getMapper(AnalysisMapper.class).selectTotalUserPerHour(
                    new AnalysisDto(host, lang, startDate, endDate)
            );
        }
    }

    public List<AnalysisAcumDto> getMostIntents(String host, String lang, String startDate, String endDate, boolean all){
        try (SqlSession session = sessionFactory.openSession()){
            if (all){
                return session.getMapper(AnalysisMapper.class).selectMostIntentsAll(
                        new AnalysisDto(host, lang, startDate, endDate)
                );
            }
            else {
                return session.getMapper(AnalysisMapper.class).selectMostIntents(
                        new AnalysisDto(host, lang, startDate, endDate)
                );
            }
        }
    }

    public List<AnalysisAcumDto> getUttersFromIntents(String intent, String host, String lang, String startDate, String endDate){
        try (SqlSession session = sessionFactory.openSession()){
            return session.getMapper(AnalysisMapper.class).selectUttersFromIntents(
                    new AnalysisDto(intent, host, lang, startDate, endDate)
            );
        }
    }

    public List<AnalysisAcumDto> getUserCountry(String host, String lang, String startDate, String endDate){
        try (SqlSession session = sessionFactory.openSession()){
            return session.getMapper(AnalysisMapper.class).selectUserCountry(
                    new AnalysisDto(host, lang, startDate, endDate)
            );
        }
    }

    public String getEmailInfo(String host){
        try (SqlSession session = sessionFactory.openSession()){
            String res = session.getMapper(AnalysisMapper.class).selectEmailInfo(host);
            return res;
        }
    }

    public Boolean setEmailInfo(String host, String email){
        try (SqlSession session = sessionFactory.openSession()){
            session.getMapper(AnalysisMapper.class).updateEmailInfo(host, email);
            session.commit();
            return true;
        } catch (Exception e) {
            System.out.println("SetEmailInfo Error");
            System.out.println(e);
            return false;
        }
    }
}
