package maum.brain.sds.data.dto.cache;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SdsGetCacheResponse implements Serializable {
  Integer cacheNo;
  Integer host;
  String prevIntent;
  String bertIntent;
  String answer;
  Integer isUtter;

  public Integer getCacheNo() {
    return cacheNo;
  }

  public void setCacheNo(Integer cacheNo) {
    this.cacheNo = cacheNo;
  }

  public Integer getHost() {
    return host;
  }

  public void setHost(Integer host) {
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

  public Integer getIsUtter() {
    return isUtter;
  }

  public void setIsUtter(Integer isUtter) {
    this.isUtter = isUtter;
  }

  public SdsGetCacheResponse() { }

  public SdsGetCacheResponse(Integer host, String prevIntent, String bertIntent, String answer,
      Integer isUtter) {
    this.host = host;
    this.prevIntent = prevIntent;
    this.bertIntent = bertIntent;
    this.answer = answer;
    this.isUtter = isUtter;
  }

  public SdsGetCacheResponse(Integer cacheNo, Integer host, String prevIntent,
      String bertIntent, String answer, Integer isUtter) {
    this.cacheNo = cacheNo;
    this.host = host;
    this.prevIntent = prevIntent;
    this.bertIntent = bertIntent;
    this.answer = answer;
    this.isUtter = isUtter;
  }

  @Override
  public String toString() {
    return "SdsGetCacheResponse{" +
        "cacheNo=" + cacheNo +
        ", host=" + host +
        ", prevIntent='" + prevIntent + '\'' +
        ", bertIntent='" + bertIntent + '\'' +
        ", answer='" + answer + '\'' +
        ", isUtter=" + isUtter +
        '}';
  }
}
