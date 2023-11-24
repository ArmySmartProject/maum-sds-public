package maum.brain.sds.frontend.model;

import java.io.IOException;
import java.util.Map;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FrontDao {
    private SqlSessionFactory sessionFactory;

    @Autowired
    public FrontDao() throws IOException{
        sessionFactory = new SqlSessionFactoryBuilder().build(
                Resources.getResourceAsReader("mybatis/frontendMapConfig.xml")
        );
    }

    public String selectHostCode(String host){
        try (SqlSession session = sessionFactory.openSession()){
            String res = session.getMapper(FrontMapper.class).selectHostCode(host);
            if (res == null) {
                res = "2";
            }
            return res;
        }
    }

    public String selectHostName(String host){
        try (SqlSession session = sessionFactory.openSession()){
            String res = session.getMapper(FrontMapper.class).selectHostName(host);
            if (res == null) {
                res = "chatbot";
            }
            return res;
        }
    }

    public Map selectHostInfo(String host){
        try (SqlSession session = sessionFactory.openSession()){
            Map res = session.getMapper(FrontMapper.class).selectHostInfo(host);
            return res;
        }
    }

    public boolean selectHostAvailable(String host){
        try (SqlSession session = sessionFactory.openSession()){
            Map res = session.getMapper(FrontMapper.class).selectHostInfo(host);
            if (res == null) { return false; }
            if (res.containsKey("Available")){
                if((int)res.get("Available") == 0){
                    return false;
                }else{
                    return true;
                }
            }
            return true;
        }
    }

    public String selectSupplier(String hostNo){
        try (SqlSession session = sessionFactory.openSession()){
            Map res = session.getMapper(FrontMapper.class).selectSupplier(hostNo);
            return (String) res.get("supplier");
        }catch (Exception e){
            return "";
        }
    }
    public int addLinkLog(Map<String, Object> param){
        try (SqlSession session = sessionFactory.openSession()){
            int res = session.insert("addLinkLog",param);
            session.commit();
            return res;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

}
