package maum.brain.sds.data.dto.cache;

public class SdsAddCacheRequest {
  private String host;
  private String prevIntent;
  private String bertIntent;
  private String answer;
  private boolean nowUtter;
  private String lang;


  public String getHost() {
    return host;
  }

  public void setHost(String host) {
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

  public boolean isNowUtter() {
    return nowUtter;
  }

  public void setNowUtter(boolean nowUtter) {
    this.nowUtter = nowUtter;
  }

  public String getLang() { return lang; }

  public void setLang(String lang) { this.lang = lang; }

  public SdsAddCacheRequest() { }

  public SdsAddCacheRequest(String host, String prevIntent, String bertIntent,
      String answer, boolean nowUtter, String lang) {
    this.host = host;
    this.prevIntent = prevIntent;
    this.bertIntent = bertIntent;
    this.answer = answer;
    this.nowUtter = nowUtter;
    this.lang = lang;
  }

  @Override
  public String toString() {
    return "SdsAddCacheRequest{" +
        "host='" + host + '\'' +
        ", prevIntent='" + prevIntent + '\'' +
        ", bertIntent='" + bertIntent + '\'' +
        ", answer='" + answer + '\'' +
        ", nowUtter=" + nowUtter +
        ", lang='" + lang + '\'' +
        '}';
  }
}
