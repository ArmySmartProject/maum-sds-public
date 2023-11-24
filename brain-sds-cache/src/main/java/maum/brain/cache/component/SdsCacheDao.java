package maum.brain.cache.component;

import java.io.IOException;
import java.util.List;
import maum.brain.cache.data.ChatbotCache;
import maum.brain.sds.data.dto.cache.SdsGetCacheResponse;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SdsCacheDao {
  private SqlSessionFactory sessionFactory;

  @Autowired
  public SdsCacheDao() throws IOException {
    sessionFactory = new SqlSessionFactoryBuilder().build(
        Resources.getResourceAsReader("mybatis/cacheMapConfig.xml")
    );
  }

  public void addCache(ChatbotCache chatbotCache){
    String answerEntityCheck="";

    if(chatbotCache.getAnswer().trim().length()<1||chatbotCache.getBertIntent().trim().length()<1
    ||chatbotCache.getPrevIntent().trim().length()<1||chatbotCache.getHost()<1){
      // 캐싱 pass부분
    }
    else{
      boolean nowCallCustom = false;
      try{
        JSONObject answerJson = new JSONObject(chatbotCache.getAnswer());
        if(answerJson.getJSONObject("answer").getString("answer").contains("{")){
          nowCallCustom = true;
        }
      }catch (Exception e){
        nowCallCustom = false;
      }

      if(!nowCallCustom){
        try (SqlSession session = this.sessionFactory.openSession(true)) {
          SdsCacheMapper mapper = session.getMapper(SdsCacheMapper.class);

          if(!"".equals(chatbotCache.getBertIntent()) && chatbotCache.getBertIntent() != null){
            int typeInt = mapper.getBotTypeCheck(chatbotCache);

            // 챗봇 이전 태스크가 항상일 경우
            if("-1".equals(chatbotCache.getPrevIntent()) && typeInt == 0){
              mapper.addCache(chatbotCache);
            } else {
              if (typeInt > 0) { // 음성봇
                // 음성봇 공통 인텐트일 경우
                if ("0".equals(mapper.getBertIntentCommonCheck(chatbotCache))) {
                  chatbotCache.setIsCommonIntent("Y");
                } else {
                  chatbotCache.setIsCommonIntent("N");
                }
              } else { // 챗봇
                if ("".equals(chatbotCache.getPrevIntent()) || chatbotCache.getPrevIntent() == null) {
                 chatbotCache.setIsCommonIntent("Y");
                } else {
                 chatbotCache.setIsCommonIntent("N");
                }
              }

              answerEntityCheck = getCacheEntityCheck(chatbotCache);

              if (!answerEntityCheck.contains("{") && !answerEntityCheck.contains("}")) {
                mapper.addCache(chatbotCache);
              }
            }
          }
        }catch (Exception e) {
          System.out.println("[ERROR] SdsCacheDao :: addCache :: " + e);
        }
      }
    }
  }

  public void delCache(Integer num){
    try (SqlSession session = this.sessionFactory.openSession(true)){
      SdsCacheMapper mapper = session.getMapper(SdsCacheMapper.class);
      mapper.delCache(num);
    }catch (Exception e) {
      System.out.println("[ERROR] SdsCacheDao :: delCache :: " + e);
    }
  }

  public SdsGetCacheResponse getCache(ChatbotCache chatbotCache){
    SdsGetCacheResponse getCacheData = new SdsGetCacheResponse();
    if(chatbotCache.getBertIntent().trim().length()<1
        ||chatbotCache.getPrevIntent().trim().length()<1||chatbotCache.getHost()<1){
      // 캐싱 pass부분
    }else{
      try (SqlSession session = this.sessionFactory.openSession()){
        SdsCacheMapper mapper = session.getMapper(SdsCacheMapper.class);
        List<SdsGetCacheResponse> retCacheData = mapper.getCache(chatbotCache);
        getCacheData = retCacheData.get(0);
      }catch (Exception e) {
        System.out.println("[ERROR] SdsCacheDao :: getCache :: " + e);
      }
    }
    return getCacheData;
  }
  public String getCacheEntityCheck(ChatbotCache chatbotCache){
    String isEntityBeforeAnswer = "{}";
    try (SqlSession session = this.sessionFactory.openSession()){
      SdsCacheMapper mapper = session.getMapper(SdsCacheMapper.class);
      isEntityBeforeAnswer = mapper.getCacheEntityCheck(chatbotCache);
    }catch (Exception e) {
      System.out.println("chatbotCache request data ::: " + chatbotCache.toString());
      System.out.println("[ERROR] SdsCacheDao :: getCacheEntityCheck :: " + e);
    }
    return isEntityBeforeAnswer;
  }
}
