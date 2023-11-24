package maum.brain.cache.data;

import maum.brain.sds.data.dto.cache.SdsAddCacheRequest;

public class ChatbotCache {
  private int host;
  private String prevIntent;
  private String bertIntent;
  private String answer;
  private int isUtter;
  private String lang;
  private String isCommonIntent;


  public int getHost() {
    return host;
  }

  public void setHost(int host) {
    this.host = host;
  }

  public String getPrevIntent() {
    return prevIntent;
  }

  public void setPrevIntent(String prevIntent) {
    this.prevIntent = prevIntent;
  }

  public String getBertIntent() {
    return bertIntent;
  }

  public void setBertIntent(String bertIntent) {
    this.bertIntent = bertIntent;
  }

  public String getAnswer() {
    return answer;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }

  public int getIsUtter() {
    return isUtter;
  }

  public void setIsUtter(int isUtter) {
    this.isUtter = isUtter;
  }

  public String getLang() { return lang; }

  public void setLang(String lang) { this.lang = lang; }

  public String getIsCommonIntent() {
    return isCommonIntent;
  }

  public void setIsCommonIntent(String isCommonIntent) {
    this.isCommonIntent = isCommonIntent;
  }

  public ChatbotCache() {
  }

  public ChatbotCache(int host, String prevIntent, String bertIntent, String answer, int isUtter, String lang) {
    this.host = host;
    this.prevIntent = prevIntent;
    this.bertIntent = bertIntent;
    this.answer = answer;
    this.isUtter = isUtter;
    this.lang = lang;
  }

  public ChatbotCache(SdsAddCacheRequest sdsAddCacheRequest){
    this.host = Integer.valueOf(sdsAddCacheRequest.getHost());
    this.prevIntent = sdsAddCacheRequest.getPrevIntent();
    this.bertIntent = sdsAddCacheRequest.getBertIntent();
    this.answer = sdsAddCacheRequest.getAnswer();
    if(sdsAddCacheRequest.isNowUtter()){
      this.isUtter = 1;
    }else{
      this.isUtter = 0;
    }
    this.lang = sdsAddCacheRequest.getLang();
  }


  @Override
  public String toString() {
    return "ChatbotCache{" +
            "host=" + host +
            ", prevIntent='" + prevIntent + '\'' +
            ", bertIntent='" + bertIntent + '\'' +
            ", answer='" + answer + '\'' +
            ", isUtter=" + isUtter +
            ", lang='" + lang + '\'' +
            ", isCommonIntent='" + isCommonIntent + '\'' +
            '}';
  }
}
