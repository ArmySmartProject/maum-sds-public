package first.builder.vo;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class IndexStatusEntity implements Serializable {
  private boolean status; // 색인상태
  private int total; // 총 색인할 기존 문장 갯수
  private int fetched; // 색인된 기존문장 갯수
  private int processed; // 색인된 문장 갯수

  private String message; // 상태 메세지

  private int collectionType; // 0 질문 1 답변

  private IndexingHistoryEntity latestIndexingHistory; // 마지막 indexinghistory 정보
}
