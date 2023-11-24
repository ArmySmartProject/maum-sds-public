package maum.brain.sds.util.logger;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LogSessionCountRequest implements Serializable {
  private int host;
  private String lang;
  private String sessionId;
  private String channel;

  public LogSessionCountRequest() {
    this.sessionId = "";
  }

  public LogSessionCountRequest(String sessionId) {
    this.sessionId = sessionId;
  }

  public LogSessionCountRequest(int host, String sessionId, String channel) {
    this.host = host;
    this.sessionId = sessionId;
    this.channel = channel;
  }

  public LogSessionCountRequest(int host, String lang, String sessionId, String channel) {
    this.host = host;
    this.lang = lang;
    this.sessionId = sessionId;
    this.channel = channel;
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public int getHost() { return host; }

  public void setHost(int host) { this.host = host; }

  public String getChannel() { return channel; }

  public void setChannel(String channel) { this.channel = channel; }

  public String getLang() { return lang; }

  public void setLang(String lang) { this.lang = lang; }

  @Override
  public String toString() {
    return "LogSessionCountRequest{" +
        "host=" + host +
        ", lang='" + lang + '\'' +
        ", sessionId='" + sessionId + '\'' +
        ", channel='" + channel + '\'' +
        '}';
  }
}
