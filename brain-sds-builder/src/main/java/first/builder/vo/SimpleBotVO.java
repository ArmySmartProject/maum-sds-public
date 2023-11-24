package first.builder.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class SimpleBotVO {

  private int id;
  private int host;
  private String name;
  private String userId;
  private String companyId;
  // 1: 한국어
  private int lang;
  private String description;
  private char appliedYn;
  private Date appliedAt;
  private Date createdAt;
  private Date updatedAt;
  private Scenario scenario;
  private String testContractNo;
  private String testCustData;
  private String testTelNo;

  // temp String for scenario
  private String scenarioJson;

  // Intent, Answer map
  private Map<String, String> iaMap = new HashMap<>();
  private String[] intentList = {"YES", "NO", "REPEAT", "UNKNOWN"};
  private String[] intentDepth = {"ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT",
      "NINE", "TEN"};
  private List<String> nodeIdHistory = new ArrayList<>();

  @Data
  public class Scenario {

    private List<Node> nodes;
    private List<Edge> edges;
    private Map<String, String> resultMap;
  }

  @Data
  public static class nodeUtter{
    public nodeUtter(Node.SystemUtter s){
      utter = s.getUtter();
      utterY = s.getUtterY();
      utterN = s.getUtterN();
      utterU = s.getUtterU();
      utterR = s.getUtterR();
    }
    private String utter;
    private int utterAnswerKey;
    private String utterY;
    private int utterYAnswerKey;
    private String utterN;
    private int utterNAnswerKey;
    private String utterU;
    private int utterUAnswerKey;
    private String utterR;
    private int utterRAnswerKey;
    private int IntentKey;
  }

  @Data
  public static class nodeUtterV2{
    public nodeUtterV2(Node.SystemUtter s){
      utter = s.getUtter();
      intentList = s.intentList;
    }
    private String utter;
    private int utterAnswerKey;
    private List<intentList> intentList;
    private int IntentKey;
    private String destAnsScope;
  }

  @Data
  public static class intentList{
    public intentList(){
      this.intent = "";
      this.answer = "";
      this.info = "";
    }
    public intentList(String intent, String answer, String info, int answerKey){
      this.intent = intent;
      this.answer = answer;
      this.info = info;
      this.answerKey = answerKey;
    }
    private String intent;
    private String answer;
    private String info;
    private int answerKey;
  }

  @Data
  public static class specialAnswerKey{
    public specialAnswerKey(int uKey, int rKey){
      unknownAnsKey = uKey;
      repeatAnsKey = rKey;
    }
    private int unknownAnsKey;
    private int repeatAnsKey;
  }

  @Data
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  public class Node {

    private String id;
    private String type;
    private String label;
    private int left;
    private int top;
    // w, h could be 'auto'
    private String w;
    private String h;

    private List<SystemUtter> attr;
    private List<Map<String,Object>> attrMap;
    private String taskGroup;
    private List<Regex> regexList;
    private String successYn;

    @Data
    public class SystemUtter {

      /*SystemUtter 관련 변수들*/
      // 시스템 메인 발화
      private String utter;
      // 조건 타입  0: intent , 1: entity
      private int conditionType;
      // intent가 YES일 경우 조건 발화
      private String utterY;
      // intent가 NO일 경우 조건 발화
      private String utterN;
      // intent가 UNKNOWN일 경우 조건 발화
      private String utterU;
      // intent가 REPEAT일 경우 조건 발화
      private String utterR;

      private List<intentList> intentList;

      // Intent, Utter 세트
      private Map<String, String> conditionUtter;

      /*Meta Data 관련 변수들*/
      // 사용자 input type
      // 0: 음성발화, 1: 다이얼, 2: 음성발화 or 다이얼
      private String inputType;
      // task의 최대 반복 횟수, default는 0(무한대반복)
      private int maxTurn;
      // task가 최대 반복된 후의 이동 task명
      private String taskOverMax;
      // 'utter' 에서 수신자가 반드시 들어야 하는 문장의 index list
      // default가 "" (모든 문장 not listen, not accept)
      private String acceptSttStcIdx;
      // 'utter' 에서 task가 반복 될 경우 반복될 문장의 index list
      // default가 "" (모든 문장 반복)
      private String repeatAnswerStcIdx;
      // 현대해상용 테스트별 인텐트에서 제외되는 인텐트
      private String exceptIntent;
    }
  }

  @Data
  public class Edge {

    private String source;
    private String target;
    private EdgeData data;

    @Data
    public class EdgeData {

      private String id;
      private String type;
      private String label;
      private List<String> attr;
    }
  }

  @Data
  public class Regex {

    private String intent;
    private String regex;
  }

  @Data
  public class ResultMap {

    private String resultKey;
    private String resultValue;
  }

}
