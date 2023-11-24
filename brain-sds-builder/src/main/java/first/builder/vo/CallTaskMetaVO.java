package first.builder.vo;

import lombok.Data;

// TABLE: SPB_TASK_CALL_META
@Data
public class CallTaskMetaVO {

  // SIMPLEBOT_ID
  private int simplebotId;
  // TASK
  private String task;
  // ACCEPT_STT_STC_IDX
  private String acceptSttStcIdx;
  // MAX_TURN
  private int maxTurn;
  // TASK_OVER_MAX
  private String taskOverMax;
  // DTMF
  private String dtmf;

}
