package maum.brain.sds.maker.data;

public class IntentRelDto {
    private int srcIntent;
    private int bertIntent;
    private int destIntent;
    private int conditionAnswer;
    private String destAnswerScope;

    public IntentRelDto() {
    }

    public IntentRelDto(int srcIntent, int bertIntent, int destIntent, int conditionAnswer) {
        this.srcIntent = srcIntent;
        this.bertIntent = bertIntent;
        this.destIntent = destIntent;
        this.conditionAnswer = conditionAnswer;
    }

    public IntentRelDto(int srcIntent, int bertIntent, int destIntent, int conditionAnswer, String destAnswerScope) {
        this.srcIntent = srcIntent;
        this.bertIntent = bertIntent;
        this.destIntent = destIntent;
        this.conditionAnswer = conditionAnswer;
        this.destAnswerScope = destAnswerScope;
    }

    public int getSrcIntent() {
        return srcIntent;
    }

    public void setSrcIntent(int srcIntent) {
        this.srcIntent = srcIntent;
    }

    public int getBertIntent() {
        return bertIntent;
    }

    public void setBertIntent(int bertIntent) {
        this.bertIntent = bertIntent;
    }

    public int getDestIntent() {
        return destIntent;
    }

    public void setDestIntent(int destIntent) {
        this.destIntent = destIntent;
    }

    public int getConditionAnswer() {
        return conditionAnswer;
    }

    public void setConditionAnswer(int conditionAnswer) {
        this.conditionAnswer = conditionAnswer;
    }

    public String getDestAnswerScope() { return destAnswerScope; }

    public void setDestAnswerScope(String destAnswerScope) { this.destAnswerScope = destAnswerScope; }

    @Override
    public String toString() {
        return "DbResponseDto{" +
                "srcIntent='" + srcIntent + '\'' +
                ", bertIntent='" + bertIntent + '\'' +
                ", destIntent='" + destIntent + '\'' +
                ", conditionAnswer='" + conditionAnswer + '\'' +
                ", destAnswerScope='" + destAnswerScope + '\'' +
                '}';
    }
}
