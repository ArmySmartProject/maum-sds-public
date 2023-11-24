package first.builder.vo;

import java.io.Serializable;
import java.sql.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import maum.brain.qa.nqa.Admin.NQaAdminLayer;


@Data
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class Layer implements Serializable {
  private int id;
  private String name;
  private int categoryId;
  private int layerSection;
  private String creatorId;
  private String updaterId;
  private Date createDtm;
  private Date updateDtm;

  public NQaAdminLayer makeProto() {
    NQaAdminLayer.Builder layerProto = NQaAdminLayer.newBuilder();

    layerProto.setId(this.id);
    layerProto.setCategoryId(this.categoryId);
    layerProto.setLayerSection(this.layerSection);
    if (this.name != null) {
      layerProto.setName(this.name);
    }
    if (this.creatorId != null) {
      layerProto.setCreatorId(this.creatorId);
    }
    if (this.updaterId != null) {
      layerProto.setUpdaterId(this.updaterId);
    }
    if (this.createDtm != null) {
      layerProto.setCreateDtm(this.createDtm.toString());
    }
    if (this.updateDtm != null) {
      layerProto.setUpdateDtm(this.updateDtm.toString());
    }
    return layerProto.build();
  }

  public Layer makeEntityByProto(NQaAdminLayer layerProto) {

    this.id = layerProto.getId();
    this.name = layerProto.getName();
    this.categoryId = layerProto.getCategoryId();
    this.layerSection = layerProto.getLayerSection();
    this.creatorId = layerProto.getCreatorId();
    this.updaterId = layerProto.getUpdaterId();

    return this;
  }
}
