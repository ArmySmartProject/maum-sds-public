package first.builder.vo;

public class AnswerVO {

  private String answer;
  private String account;
  private String language;
  private int num;
  private String serverUrl;
  private int No;


  public String getAnswer() {
    return answer;
  }

  public String getServerUrl() {
    return serverUrl;
  }

  public String getAccount() {
    return account;
  }

  public int getNum() {
    return num;
  }

  public String getLanguage() {
    return language;
  }

  public int getNo() { return No; }

  public void setLanguage(String language) {
    this.language = language;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  public void setNum(int num) {
    this.num = num;
  }

  public void setServerUrl(String serverUrl) {
    this.serverUrl = serverUrl;
  }
  
  public void setNo(int No){ this.No = No; }
}
