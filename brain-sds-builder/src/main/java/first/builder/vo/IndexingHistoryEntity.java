package first.builder.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class IndexingHistoryEntity  {
  private Integer id;
  private Integer channel_id;
  private Integer category_id;
  private String type; // fullIndexing OR additionalIndexing
  private Boolean status; // 색인상태
  private Integer total; // 총 색인할 기존 문장 갯수
  private Integer fetched; // 색인된 기존 문장 갯수
  private Integer processed; // 색인된 문장 갯수
  private String message; // 상태 메세지
  private String address; // nqa 엔진 주소 (ip:port) 추후 db 1대 mlt 서버 2대 이상 nqa 2대 이상 일 경우 구분을 위해 필요
  private Boolean stop_yn; // abortIndexing 여부
  private String creator_id;
  private Date created_at;
  private String updater_id;
  private Date updated_at;

  public IndexingHistoryEntity getClone() {
    IndexingHistoryEntity result = new IndexingHistoryEntity();
    result.setId(this.getId());
    result.setChannel_id(this.getChannel_id());
    result.setCategory_id(this.getCategory_id());
    result.setType(this.getType());
    result.setStatus(this.getStatus());
    result.setTotal(this.getTotal());
    result.setFetched(this.getFetched());
    result.setProcessed(this.getProcessed());
    result.setMessage(this.getMessage());
    result.setAddress(this.getAddress());
    result.setStop_yn(this.getStop_yn());
    result.setCreator_id(this.getCreator_id());
    result.setCreated_at(this.getCreated_at());
    result.setUpdater_id(this.getUpdater_id());
    result.setUpdated_at(this.getUpdated_at());
    return result;
  }
}
