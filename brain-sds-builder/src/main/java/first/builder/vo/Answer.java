package first.builder.vo;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import maum.brain.qa.nqa.Admin.NQaAdminAnswer;

@Data
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class Answer implements Serializable {

  private int id;
  private int copyId;
  private String answer;
  private String answerView;
  private String source;
  private String summary;
  private int categoryId;
  private String attr1;
  private String attr2;
  private String attr3;
  private String attr4;
  private String attr5;
  private String attr6;
  private Layer layer1;
  private Layer layer2;
  private Layer layer3;
  private Layer layer4;
  private Layer layer5;
  private Layer layer6;
  private String creatorId;
  private String updaterId;
  private Date createDtm;
  private Date updateDtm;
  private int listCount;
  private int questionCount;
  private int rownum;

  private List<String> tags;

  private List<Question> questions;

  private List<Question> addedQuestions;
  private List<Question> editedQuestions;
  private List<Question> removedQuestions;

  public NQaAdminAnswer makeProto() {
    NQaAdminAnswer.Builder answerProto = NQaAdminAnswer.newBuilder();

    answerProto.setId(this.id);
    answerProto.setCopyId(this.copyId);
    answerProto.setAnswer(this.answer);
    if (this.answerView != null) {
      answerProto.setAnswerView(this.answerView);
    }
    if (this.source != null) {
      answerProto.setSrc(this.source);
    }
    if (this.summary != null) {
      answerProto.setSummary(this.summary);
    }
    answerProto.setCategoryId(this.categoryId);

    if (this.attr1 != null) {
      answerProto.setAttr1(this.attr1);
    }
    if (this.attr2 != null) {
      answerProto.setAttr2(this.attr2);
    }
    if (this.attr3 != null) {
      answerProto.setAttr3(this.attr3);
    }
    if (this.attr4 != null) {
      answerProto.setAttr4(this.attr4);
    }
    if (this.attr5 != null) {
      answerProto.setAttr5(this.attr5);
    }
    if (this.attr6 != null) {
      answerProto.setAttr6(this.attr6);
    }

    if (this.layer1 != null) {
      answerProto.setLayer1(this.layer1.makeProto());
    }
    if (this.layer2 != null) {
      answerProto.setLayer2(this.layer2.makeProto());
    }
    if (this.layer3 != null) {
      answerProto.setLayer3(this.layer3.makeProto());
    }
    if (this.layer4 != null) {
      answerProto.setLayer4(this.layer4.makeProto());
    }
    if (this.layer5 != null) {
      answerProto.setLayer5(this.layer5.makeProto());
    }
    if (this.layer6 != null) {
      answerProto.setLayer6(this.layer6.makeProto());
    }

    if (this.tags != null && this.tags.size() != 0) {
      for (String tag : this.tags) {
        answerProto.addTags(tag);
      }
    }

    if (this.creatorId != null) {
      answerProto.setCreatorId(this.creatorId);
    }
    if (this.updaterId != null) {
      answerProto.setUpdaterId(this.updaterId);
    }
    if (this.createDtm != null) {
      answerProto.setCreateDtm(this.createDtm.toString());
    }
    if (this.updateDtm != null) {
      answerProto.setUpdateDtm(this.updateDtm.toString());
    }
    return answerProto.build();
  }

  public Answer makeEntityByProto(NQaAdminAnswer answerProto) {
    this.id = answerProto.getId();
    this.copyId = answerProto.getCopyId();
    this.answer = answerProto.getAnswer();
    this.answerView = answerProto.getAnswerView();
    this.source = answerProto.getSrc();
    this.summary = answerProto.getSummary();
    // TODO 시간
    this.categoryId = answerProto.getCategoryId();
    this.attr1 = answerProto.getAttr1();
    this.attr2 = answerProto.getAttr2();
    this.attr3 = answerProto.getAttr3();
    this.attr4 = answerProto.getAttr4();
    this.attr5 = answerProto.getAttr5();
    this.attr6 = answerProto.getAttr6();
    Layer layer1 = new Layer();
    layer1.setId(answerProto.getLayer1().getId());
    layer1.setName(answerProto.getLayer1().getName());
    this.layer1 = layer1;
    // TODO 시간
    Layer layer2 = new Layer();
    layer2.setId(answerProto.getLayer2().getId());
    layer2.setName(answerProto.getLayer2().getName());
    this.layer2 = layer2;

    Layer layer3 = new Layer();
    layer3.setId(answerProto.getLayer3().getId());
    layer3.setName(answerProto.getLayer3().getName());
    this.layer3 = layer3;

    Layer layer4 = new Layer();
    layer4.setId(answerProto.getLayer4().getId());
    layer4.setName(answerProto.getLayer4().getName());
    this.layer4 = layer4;

    Layer layer5 = new Layer();
    layer5.setId(answerProto.getLayer5().getId());
    layer5.setName(answerProto.getLayer5().getName());
    this.layer5 = layer5;

    Layer layer6 = new Layer();
    layer6.setId(answerProto.getLayer6().getId());
    layer6.setName(answerProto.getLayer6().getName());
    this.layer6 = layer6;

    this.tags = new ArrayList<>();
    for (String tagProto : answerProto.getTagsList()) {
      this.tags.add(tagProto);
    }

    this.creatorId = answerProto.getCreatorId();
    this.updaterId = answerProto.getUpdaterId();
    this.rownum = answerProto.getRownum();

    return this;
  }

  public void setLayerNameAndSection(int categoryId, int section, String name) {
    Layer layer = new Layer();
    layer.setLayerSection(section);
    layer.setName(name);
    layer.setCategoryId(categoryId);

    switch (section) {
      case 1:
        setLayer1(layer);
        break;
      case 2:
        setLayer2(layer);
        break;
      case 3:
        setLayer3(layer);
        break;
      case 4:
        setLayer4(layer);
        break;
      case 5:
        setLayer5(layer);
        break;
      case 6:
        setLayer6(layer);
        break;
    }
  }
}
