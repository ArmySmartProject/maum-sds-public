package first.builder.vo;

import java.io.Serializable;
import java.sql.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import maum.brain.qa.nqa.Admin.NQaAdminChannel;


@Data
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class Channel implements Serializable {
  private int id;
  private String name;
  private String creatorId;
  private String updaterId;
  private String createDtm;
  private String updateDtm;

  public NQaAdminChannel makeProto() {
    NQaAdminChannel.Builder channelProto = NQaAdminChannel.newBuilder();

    channelProto.setId(this.id);

    if (this.name != null) {
      channelProto.setName(this.name);
    }
    if (this.creatorId != null) {
      channelProto.setCreatorId(this.creatorId);
    }
    if (this.updaterId != null) {
      channelProto.setUpdaterId(this.updaterId);
    }
    if (this.createDtm != null) {
      channelProto.setCreateDtm(this.createDtm.toString());
    }
    if (this.updateDtm != null) {
      channelProto.setUpdateDtm(this.updateDtm.toString());
    }

    return channelProto.build();
  }

  public Channel makeEntityByProto(NQaAdminChannel channelProto) {

    this.id = channelProto.getId();
    this.name = channelProto.getName();
    this.creatorId = channelProto.getCreatorId();
    this.updaterId = channelProto.getUpdaterId();
    this.createDtm = channelProto.getCreateDtm();
    this.updateDtm = channelProto.getUpdateDtm();

    return this;
  }
}
