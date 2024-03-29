package maum.brain.sds.analysis.model;

public class AnalysisTimeDto {
    private int hour;
    private int count;

    public AnalysisTimeDto() {
    }

    public AnalysisTimeDto(int hour, int count) {
        this.hour = hour;
        this.count = count;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "AnalysisTimeDto{" +
                "hour=" + hour +
                ", count=" + count +
                '}';
    }
}
