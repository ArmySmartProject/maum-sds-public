package first.builder.vo;

public class IntentVO {

  private int language;
  private String intent;
  private String display;
  private String entity;
  private String answer;
  private String image;
  private String description;
  private String account;
  private String num;
  private String nextIntent;
  private String hTask;
  private String hItem;
  private String hParam;

  public String getAccount() {
    return account;
  }

  public int getLanguage() {
    return language;
  }

  public String getAnswer() {
    return answer;
  }

  public String getDescription() {
    return description;
  }

  public String getEntity() {
    return entity;
  }

  public String getNum() {
    return num;
  }

  public String getDisplay() {
    return display;
  }

  public String getImage() {
    return image;
  }

  public String getIntent() {
    return intent;
  }

  public String getNextIntent() {
    return nextIntent;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setEntity(String entity) {
    this.entity = entity;
  }

  public void setLanguage(int language) {
    this.language = language;
  }

  public void setDisplay(String display) {
    this.display = display;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public void setIntent(String intent) {
    this.intent = intent;
  }

  public void setNextIntent(String nextIntent) {
    this.nextIntent = nextIntent;
  }

  public void setNum(String num) {
    this.num = num;
  }

  public String gethTask() {
    return hTask;
  }

  public void sethTask(String hTask) {
    this.hTask = hTask;
  }

  public String gethItem() {
    return hItem;
  }

  public void sethItem(String hItem) {
    this.hItem = hItem;
  }

  public String gethParam() {
    return hParam;
  }

  public void sethParam(String hParam) {
    this.hParam = hParam;
  }
}
