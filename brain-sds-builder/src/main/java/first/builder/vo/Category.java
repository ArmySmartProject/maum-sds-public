package first.builder.vo;

import java.io.Serializable;
import java.sql.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import maum.brain.qa.nqa.Admin.NQaAdminCategory;


@Data
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class Category implements Serializable {
  private int id;
  private String name;
  private int channelId;
  private String creatorId;
  private String updaterId;
  private Date createDtm;
  private Date updateDtm;
  private int pageIndex;
  private int pageSize;

  public NQaAdminCategory makeProto() {
    NQaAdminCategory.Builder categoryProto = NQaAdminCategory.newBuilder();

    categoryProto.setId(this.id);

    if (this.name != null) {
      categoryProto.setName(this.name);
    }
    if (this.channelId != 0) {
      categoryProto.setChannelId(this.channelId);
    }
    if (this.creatorId != null) {
      categoryProto.setCreatorId(this.creatorId);
    }
    if (this.updaterId != null) {
      categoryProto.setUpdaterId(this.updaterId);
    }
    if (this.createDtm != null) {
      categoryProto.setCreateDtm(this.createDtm.toString());
    }
    if (this.updateDtm != null) {
      categoryProto.setUpdateDtm(this.updateDtm.toString());
    }
    categoryProto.setPageIndex(this.pageIndex);
    categoryProto.setPageSize(this.pageSize);

    return categoryProto.build();
  }

  public Category makeEntityByProto(NQaAdminCategory categoryProto) {

    this.id = categoryProto.getId();
    this.name = categoryProto.getName();
    this.channelId = categoryProto.getChannelId();
    this.creatorId = categoryProto.getCreatorId();
    this.updaterId = categoryProto.getUpdaterId();
    this.pageIndex = categoryProto.getPageIndex();
    this.pageSize = categoryProto.getPageSize();

    return this;
  }
}
