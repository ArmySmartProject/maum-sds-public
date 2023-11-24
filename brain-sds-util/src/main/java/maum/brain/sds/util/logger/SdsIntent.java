package maum.brain.sds.util.logger;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SdsIntent implements Serializable {
  private String intentName;
  private String intentValue;

  public SdsIntent() {
  }

  public SdsIntent(String intentName, String intentValue) {
    this.intentName = intentName;
    this.intentValue = intentValue;
  }

  public String getIntentName() {
    return intentName;
  }

  public void setIntentName(String intentName) {
    this.intentName = intentName;
  }

  public String getIntentValue() {
    return intentValue;
  }

  public void setIntentValue(String intentValue) {
    this.intentValue = intentValue;
  }

  @Override
  public String toString() {
    return "SdsIntent{" +
        "intentName='" + intentName + '\'' +
        ", intentValue='" + intentValue + '\'' +
        '}';
  }
}

