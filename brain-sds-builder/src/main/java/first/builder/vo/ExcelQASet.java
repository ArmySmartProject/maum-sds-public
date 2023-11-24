package first.builder.vo;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class ExcelQASet implements Serializable {
  private String Intent;
  private String Question;

  public ExcelQASet getClone() {
    ExcelQASet result = new ExcelQASet();
    result.setIntent(this.getIntent());
    result.setQuestion(this.getQuestion());
    return result;
  }
}
