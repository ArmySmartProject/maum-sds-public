package first.builder.vo;

import java.io.Serializable;
import java.sql.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import maum.brain.qa.nqa.Admin.NQaAdminQuestion;

@Data
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class Question implements Serializable {
  private int id;
  private int answerId;
  private String question;
  private String creatorId;
  private String updaterId;
  private Date createDtm;
  private Date updateDtm;


  public NQaAdminQuestion makeProto() {
    NQaAdminQuestion.Builder questionProto = NQaAdminQuestion.newBuilder();

    questionProto.setId(this.id);

    if (this.question != null) {
      questionProto.setQuestion(this.question);
    }
    if (this.creatorId != null) {
      questionProto.setCreatorId(this.creatorId);
    }
    if (this.updaterId != null) {
      questionProto.setUpdaterId(this.updaterId);
    }
    if (this.createDtm != null) {
      questionProto.setCreateDtm(this.createDtm.toString());
    }
    if (this.updateDtm != null) {
      questionProto.setUpdateDtm(this.updateDtm.toString());
    }

    return questionProto.build();
  }

  public Question makeEntityByProto(NQaAdminQuestion questionProto) {
    this.id = questionProto.getId();
    this.question = questionProto.getQuestion();
    // TODO 시간
    this.creatorId = questionProto.getCreatorId();
    this.updaterId = questionProto.getUpdaterId();

    return this;
  }
}
