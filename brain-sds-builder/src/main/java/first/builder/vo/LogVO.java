package first.builder.vo;

public class LogVO {

  private int id;
  private String name;
  private String session;
  private int flowNo;
  private String utter;
  private String prev;
  private String buttonYN;
  private String intent;
  private String answer;
  private String prob;
  private String createDate;
  private String prevIntentUtter;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSession() {
    return session;
  }

  public void setSession(String session) {
    this.session = session;
  }

  public int getFlowNo() {
    return flowNo;
  }

  public void setFlowNo(int flowNo) {
    this.flowNo = flowNo;
  }

  public String getUtter() {
    return utter;
  }

  public void setUtter(String utter) {
    this.utter = utter;
  }

  public String getPrev() {
    return prev;
  }

  public void setPrev(String prev) {
    this.prev = prev;
  }

  public String getIntent() {
    return intent;
  }

  public void setIntent(String intent) {
    this.intent = intent;
  }

  public String getAnswer() {
    return answer;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }

  public String getProb() {
    return prob;
  }

  public void setProb(String prob) {
    this.prob = prob;
  }

  public String getCreateDate() {
    return createDate;
  }

  public void setCreateDate(String createDate) {
    this.createDate = createDate;
  }

  public String getButtonYN() {
    return buttonYN;
  }

  public void setButtonYN(String buttonYN) {
    this.buttonYN = buttonYN;
  }

  public String getPrevIntentUtter() {
    return prevIntentUtter;
  }

  public void setPrevIntentUtter(String prevIntentUtter) {
    this.prevIntentUtter = prevIntentUtter;
  }
}
