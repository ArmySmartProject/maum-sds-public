package maum.brain.sds.util.logger;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LogAllRequest implements Serializable {
  private String utter;
  private String intent;
  private String answer;
  private List<SdsEntity> entities;

  public LogAllRequest(String utter, String intent, String answer, List<SdsEntity> entities) {
    this.utter = utter;
    this.intent = intent;
    this.answer = answer;
    this.entities = entities;
  }

  public String getUtter() {
    return utter;
  }

  public void setUtter(String utter) {
    this.utter = utter;
  }

  public String getIntent() {
    return intent;
  }

  public void setIntent(String intent) {
    this.intent = intent;
  }

  public String getAnswer() {
    return answer;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }

  public List<SdsEntity> getEntities() {
    return entities;
  }

  public void setEntities(List<SdsEntity> entities) {
    this.entities = entities;
  }

  @Override
  public String toString() {
    return "LogAllRequest{" +
        "utter='" + utter + '\'' +
        ", intent='" + intent + '\'' +
        ", answer='" + answer + '\'' +
        ", entities=" + entities +
        '}';
  }
}
