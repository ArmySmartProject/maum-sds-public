package maum.brain.sds.logdb.data;

public class LogAnswerDto {
    private String utter;
    private String answer;
    private int host;

    public LogAnswerDto(String utter, String answer) {
        this.utter = utter;
        this.answer = answer;
    }

    public LogAnswerDto(String utter, String answer, int host) {
        this.utter = utter;
        this.answer = answer;
        this.host = host;
    }

    public String getUtter() {
        return utter;
    }

    public void setUtter(String utter) {
        this.utter = utter;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getHost() {
        return host;
    }

    public void setHost(int host) {
        this.host = host;
    }

    @Override
    public String toString() {
        return "LogAnswerDto{" +
                "utter='" + utter + '\'' +
                ", answer='" + answer + '\'' +
                ", host=" + host +
                '}';
    }
}
