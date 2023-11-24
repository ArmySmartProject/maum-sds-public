package maum.brain.sds.maker.data;

public class DbResponseDto {
    private String hi;
    private String answer;
    private String next;
    private String name;
    private String type;
    private String url;
    private String ds;
    private String display;
    private String answerUrl;
    private String responseOrder;
    private String h_task;
    private String h_item;
    private String h_param;

    public DbResponseDto() {
    }

    public DbResponseDto(String hi, String answer, String next, String name, String type, String url, String ds, String display, String answerUrl, String responseOrder,String h_task, String h_item, String h_param ) {
        this.hi = hi;
        this.answer = answer;
        this.next = next;
        this.name = name;
        this.type = type;
        this.url = url;
        this.ds = ds;
        this.display = display;
        this.answerUrl = answerUrl;
        this.responseOrder = responseOrder;
        this.h_task = h_task;
        this.h_item = h_item;
        this.h_param= h_param;
    }

    public String getHi() {
        return hi;
    }

    public void setHi(String hi) {
        this.hi = hi;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDs() {
        return ds;
    }

    public void setDs(String ds) {
        this.ds = ds;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getAnswerUrl() {
        return answerUrl;
    }

    public void setAnswerUrl(String answerUrl) {
        this.answerUrl = answerUrl;
    }

    public String getResponseOrder() {
        return responseOrder;
    }

    public void setResponseOrder(String responseOrder) { this.responseOrder = responseOrder; }

    @Override
    public String toString() {
        return "DbResponseDto{" +
                "hi='" + hi + '\'' +
                ", answer='" + answer + '\'' +
                ", next='" + next + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", url='" + url + '\'' +
                ", ds='" + ds + '\'' +
                ", display='" + display + '\'' +
                ", answerUrl='" + answerUrl + '\'' +
                ", responseOrder='" + responseOrder + '\'' +
                ", h_task='" + h_task + '\'' +
                '}';
    }

    public String getH_task() {
        return h_task;
    }

    public void setH_task(String h_task) {
        this.h_task = h_task;
    }

    public String getH_item() {
        return h_item;
    }

    public void setH_item(String h_item) {
        this.h_item = h_item;
    }

    public String getH_param() {
        return h_param;
    }

    public void setH_param(String h_param) {
        this.h_param = h_param;
    }
}
