package maum.brain.cache.service;

import maum.brain.cache.component.SdsCacheDao;
import maum.brain.cache.data.ChatbotCache;
import maum.brain.sds.data.dto.cache.SdsAddCacheRequest;
import maum.brain.sds.data.dto.cache.SdsGetCacheResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SdsCacheService {
  @Autowired
  private SdsCacheDao dao;

  public void addCache(SdsAddCacheRequest sdsAddCacheRequest){
    try{
      dao.addCache(new ChatbotCache(sdsAddCacheRequest));
    }catch (Exception e){
      System.out.println("[ERROR] SdsCacheService :: addCache :: " + e);
    }

  }

  public void delCache(Integer num){
    try{
      dao.delCache(num);
    }catch (Exception e){
      System.out.println("[ERROR] SdsCacheService :: delCache :: " + e);
    }
  }

  public SdsGetCacheResponse getCache(SdsAddCacheRequest sdsAddCacheRequest){
    SdsGetCacheResponse returnGetCacheData = new SdsGetCacheResponse();
    returnGetCacheData.setCacheNo(-1);
    try{
      returnGetCacheData = dao.getCache(new ChatbotCache(sdsAddCacheRequest));
    }catch (Exception e){
      System.out.println("[ERROR] SdsCacheService :: getCache :: " + e);
    }
    return returnGetCacheData;
  }

}
