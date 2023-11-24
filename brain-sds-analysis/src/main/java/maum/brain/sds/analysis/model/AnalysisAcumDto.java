package maum.brain.sds.analysis.model;

public class AnalysisAcumDto {
    private String content;
    private int count;

    public AnalysisAcumDto() {
    }

    public AnalysisAcumDto(String content, int count) {
        this.content = content;
        this.count = count;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
