package maum.brain.sds.logdb.data;

import java.sql.Timestamp;
import java.util.Calendar;

public class LogSessionCntDto {
  private int id; //SessionLogPK
  private int botCnt;
  private int lang1Cnt;
  private int lang2Cnt;
  private int lang3Cnt;
  private int lang4Cnt;

  public LogSessionCntDto(int id, int nowCnt) {
    long timestamp = Calendar.getInstance().getTime().getTime();
    this.id = id;
    this.botCnt = nowCnt+1;
  }

  public LogSessionCntDto(int id, int nowCnt, int[] langCnt) {
    long timestamp = Calendar.getInstance().getTime().getTime();
    this.id = id;
    this.botCnt = nowCnt+1;
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

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getBotCnt() {
    return botCnt;
  }

  public void setBotCnt(int botCnt) {
    this.botCnt = botCnt;
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
    return "LogSessionCntDto{" +
        "id=" + id +
        ", botCnt=" + botCnt +
        ", lang1Cnt=" + lang1Cnt +
        ", lang2Cnt=" + lang2Cnt +
        ", lang3Cnt=" + lang3Cnt +
        ", lang4Cnt=" + lang4Cnt +
        '}';
  }
}
