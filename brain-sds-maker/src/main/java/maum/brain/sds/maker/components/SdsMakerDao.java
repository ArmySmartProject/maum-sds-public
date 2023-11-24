package maum.brain.sds.maker.components;

import maum.brain.sds.data.dto.db.SdsReqEntityDto;
import maum.brain.sds.maker.data.DbResponseDto;
import maum.brain.sds.maker.data.IntentRelDto;
import maum.brain.sds.maker.data.MakerDatabaseDto;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SdsMakerDao {
    private SqlSessionFactory sessionFactory;

    @Autowired
    public SdsMakerDao() throws IOException {
        sessionFactory = new SqlSessionFactoryBuilder().build(
                Resources.getResourceAsReader("mybatis/makerMapConfig.xml")
        );
    }

    public List<DbResponseDto> getResponse(MakerDatabaseDto dto){
        try(SqlSession session = this.sessionFactory.openSession()){

            SdsMakerMapper mapper = session.getMapper(SdsMakerMapper.class);
            return mapper.getResponse(dto);
        }
    }

    public List<Map> getRequiredEntities(String intent, String lang, String host){
        try (SqlSession session = this.sessionFactory.openSession()){
            SdsMakerMapper mapper = session.getMapper(SdsMakerMapper.class);
            // Intent 테이블에서 entity Id 조회 및 ','로 split
            String entityIdsRes = mapper.getReqEntityIds(new SdsReqEntityDto(intent, lang, host));
            List<Integer> entityIdList = new ArrayList<>();
            for (String id : entityIdsRes.split(",")) {
              entityIdList.add(Integer.parseInt(id));
            }

            //Intent 테이블에서 조회해온 entity Id로 Entity 테이블 조회
            Map param = new HashMap();
            param.put("entityIds", entityIdList);
            List<Map> reqEntities = mapper.getReqEntity(param);

            return reqEntities;
        }
    }

    public String getRequestAnswer(String host, String lang, String name){
        try (SqlSession session = this.sessionFactory.openSession()){
            SdsMakerMapper mapper = session.getMapper(SdsMakerMapper.class);
            Map param = new HashMap();
            param.put("host", host);
            param.put("lang", lang);
            param.put("name", name);
            String answer = mapper.getRequestAnswer(param);

            return answer;
        }
    }

    public boolean checkIntent(SdsReqEntityDto dto){
        try (SqlSession session = this.sessionFactory.openSession()){
            SdsMakerMapper mapper = session.getMapper(SdsMakerMapper.class);
            return 0 < mapper.checkIntent(dto);
        }
    }

    public List<IntentRelDto> getIntentRel(String srcIntent, String destIntent, String host, String lang, String bertIntent) {
        try (SqlSession session = this.sessionFactory.openSession()) {
            SdsMakerMapper mapper = session.getMapper(SdsMakerMapper.class);
            return mapper.getIntentRel(srcIntent, destIntent, host, lang, bertIntent);
        }
    }

    public String getConditionAnswer(String num, String host, String lang) {
        try (SqlSession session = this.sessionFactory.openSession()) {
            SdsMakerMapper mapper = session.getMapper(SdsMakerMapper.class);
            return mapper.getConditionAnswer(num, host, lang);
        }
    }

    public List<Map> getEntityList(String host, String lang) {
        try (SqlSession session = this.sessionFactory.openSession()) {
            SdsMakerMapper mapper = session.getMapper(SdsMakerMapper.class);
            return mapper.getEntityList(host, lang);
        }
    }
}
