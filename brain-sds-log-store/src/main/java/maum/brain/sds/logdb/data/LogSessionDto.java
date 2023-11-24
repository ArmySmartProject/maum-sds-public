package maum.brain.sds.logdb.data;

import java.sql.Timestamp;
import java.util.Calendar;
import maum.brain.sds.logdb.components.SessionLogMaxIDChecker;
import maum.brain.sds.util.logger.LogSessionCountRequest;

public class LogSessionDto {
  private int newSessionID;
  private int host;
  private String session;
  private String channel;
  private int botCnt;
  private int consultantCnt;
  private int lang1Cnt;
  private int lang2Cnt;
  private int lang3Cnt;
  private int lang4Cnt;


  public LogSessionDto(int host, String session, String channel) {
    newSessionID = SessionLogMaxIDChecker.getSessionLogID();
    long timestamp = Calendar.getInstance().getTime().getTime();
    this.host = host;
    this.session = session;
    this.channel = channel;
    this.botCnt = 1;
    this.consultantCnt = 0;
  }

  public LogSessionDto(LogSessionCountRequest logSessionCountRequest){
    newSessionID = SessionLogMaxIDChecker.getSessionLogID();
    long timestamp = Calendar.getInstance().getTime().getTime();
    this.host = logSessionCountRequest.getHost();
    this.session = logSessionCountRequest.getSessionId();
    this.channel = logSessionCountRequest.getChannel();
    this.botCnt = 1;
    this.consultantCnt = 0;
  }

  public LogSessionDto(LogSessionCountRequest logSessionCountRequest, int[] langCnt){
    newSessionID = SessionLogMaxIDChecker.getSessionLogID();
    this.host = logSessionCountRequest.getHost();
    this.session = logSessionCountRequest.getSessionId();
    this.channel = logSessionCountRequest.getChannel();
    this.botCnt = 1;
    this.consultantCnt = 0;
    try{
      lang1Cnt = langCnt[0];
      lang2Cnt = langCnt[1];
      lang3Cnt = langCnt[2];
      lang4Cnt = langCnt[3];
    }catch (Exception e){
      lang1Cnt = 0;
      lang2Cnt = 0;
      lang3Cnt = 0;
      lang4Cnt = 0;
    }
  }

  public int getHost() {
    return host;
  }

  public void setHost(int host) {
    this.host = host;
  }

  public String getSession() {
    return session;
  }

  public void setSession(String session) {
    this.session = session;
  }

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public int getBotCnt() {
    return botCnt;
  }

  public void setBotCnt(int botCnt) {
    this.botCnt = botCnt;
  }

  public int getConsultantCnt() {
    return consultantCnt;
  }

  public void setConsultantCnt(int consultantCnt) {
    this.consultantCnt = consultantCnt;
  }

  public int getNewSessionID() {
    return newSessionID;
  }

  public void setNewSessionID(int newSessionID) {
    this.newSessionID = newSessionID;
  }

  public int getLang1Cnt() { return lang1Cnt; }

  public void setLang1Cnt(int lang1Cnt) { this.lang1Cnt = lang1Cnt; }

  public int getLang2Cnt() { return lang2Cnt; }

  public void setLang2Cnt(int lang2Cnt) { this.lang2Cnt = lang2Cnt; }

  public int getLang3Cnt() { return lang3Cnt; }

  public void setLang3Cnt(int lang3Cnt) { this.lang3Cnt = lang3Cnt; }

  public int getLang4Cnt() { return lang4Cnt; }

  public void setLang4Cnt(int lang4Cnt) { this.lang4Cnt = lang4Cnt; }

  @Override
  public String toString() {
    return "LogSessionDto{" +
        "newSessionID=" + newSessionID +
        ", host=" + host +
        ", session='" + session + '\'' +
        ", channel='" + channel + '\'' +
        ", botCnt=" + botCnt +
        ", consultantCnt=" + consultantCnt +
        ", lang1Cnt=" + lang1Cnt +
        ", lang2Cnt=" + lang2Cnt +
        ", lang3Cnt=" + lang3Cnt +
        ", lang4Cnt=" + lang4Cnt +
        '}';
  }
}
