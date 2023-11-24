package maum.brain.sds.data.vo.engines.spec;

public class SdsSpecCustomServerMap {
  private String customKey;
  private String customValue;

  public String getCustomKey() {
    return customKey;
  }

  public void setCustomKey(String customKey) {
    this.customKey = customKey;
  }

  public String getCustomValue() {
    return customValue;
  }

  public void setCustomValue(String customValue) {
    this.customValue = customValue;
  }


  public SdsSpecCustomServerMap(String customKey, String customValue) {
    this.customKey = customKey;
    this.customValue = customValue;
  }


  public SdsSpecCustomServerMap() {
  }

  @Override
  public String toString() {
    return "SdsSpecCustomServerMap{" +
        "customKey='" + customKey + '\'' +
        ", customValue='" + customValue + '\'' +
        '}';
  }
}
