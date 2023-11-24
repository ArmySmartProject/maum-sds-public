package maum.brain.sds.util.logger;

public class LogSessionCounselorRequest {
  private String sessionID;
  private int counselorsCnt;
  private String counselorID;

  public String getSessionID() {
    return sessionID;
  }

  public void setSessionID(String sessionID) {
    this.sessionID = sessionID;
  }

  public int getCounselorsCnt() {
    return counselorsCnt;
  }

  public void setCounselorsCnt(int counselorsCnt) {
    this.counselorsCnt = counselorsCnt;
  }

  public String getCounselorID() {
    return counselorID;
  }

  public void setCounselorID(String counselorID) {
    this.counselorID = counselorID;
  }

  public LogSessionCounselorRequest() {
  }

  public LogSessionCounselorRequest(String sessionID, int counselorsCnt, String counselorID) {
    this.sessionID = sessionID;
    this.counselorsCnt = counselorsCnt;
    this.counselorID = counselorID;
  }

  @Override
  public String toString() {
    return "LogSessionCounselorRequest{" +
        "sessionID='" + sessionID + '\'' +
        ", counselorsCnt=" + counselorsCnt +
        ", counselorID='" + counselorID + '\'' +
        '}';
  }
}
