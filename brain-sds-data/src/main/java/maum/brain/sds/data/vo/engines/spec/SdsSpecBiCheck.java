package maum.brain.sds.data.vo.engines.spec;

public class SdsSpecBiCheck implements SdsSpec{
  private String match;
  private String type;


  public String getMatch() {
    return match;
  }

  public void setMatch(String match) {
    this.match = match;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return "SdsSpecBiCheck{" +
        "match='" + match + '\'' +
        ", type='" + type + '\'' +
        '}';
  }

  public SdsSpecBiCheck() {

  }

  public SdsSpecBiCheck(String match, String type) {
    this.match = match;
    this.type = type;
  }
}
