package maum.brain.sds.data.vo;

public class SdsAnswer implements SdsData {
    private String answer;

    public SdsAnswer() {
    }

    public SdsAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "SdsAnswer{" +
                "answer='" + answer + '\'' +
                '}';
    }
}
