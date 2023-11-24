package maum.brain.cache.component;
import java.util.List;
import maum.brain.cache.data.ChatbotCache;
import maum.brain.sds.data.dto.cache.SdsGetCacheResponse;

public interface SdsCacheMapper {
  public void addCache(ChatbotCache chatbotCache);
  public void delCache(Integer num);
  public List<SdsGetCacheResponse> getCache(ChatbotCache chatbotCache);
  public String getCacheEntityCheck(ChatbotCache chatbotCache);
  public String getBertIntentCommonCheck(ChatbotCache chatbotCache);
  public int getBotTypeCheck(ChatbotCache chatbotCache);
}
