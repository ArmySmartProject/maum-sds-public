package maum.brain.sds.data.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.annotation.Nonnull;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SdsIntent implements SdsData {

    @Nonnull
    private String intent;

    private int hierarchy;
    private boolean entityFlag;

    private String displayName;
    private String displayType;
    private String displayUrl;
    private String displayText;
    private String h_task;
    private String h_item;
    private String h_param;

    public SdsIntent() {
    }

    public SdsIntent(String intent) {
        this.intent = intent;
        this.hierarchy = this.intent.split("\\.").length;
    }

    public SdsIntent(@Nonnull String intent, String displayName, String displayType, String displayUrl, String displayText, String h_task, String h_item, String h_param) {
        this.intent = intent;
        this.hierarchy = this.intent.split("\\.").length;
        this.displayName = displayName;
        this.displayType = displayType;
        this.displayUrl = displayUrl;
        this.displayText = displayText;
        this.h_task = h_task;
        this.h_item = h_item;
        this.h_param = h_param;
    }

    @Nonnull
    public String getIntent() {
        return intent;
    }

    public void setIntent(@Nonnull String intent) {
        this.intent = intent;
    }

    public int getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(int hierarchy) {
        this.hierarchy = hierarchy;
    }

    public boolean isEntityFlag() {
        return entityFlag;
    }

    public void setEntityFlag(boolean entityFlag) {
        this.entityFlag = entityFlag;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayType() {
        return displayType;
    }

    public void setDisplayType(String displayType) {
        this.displayType = displayType;
    }

    public String getDisplayUrl() {
        return displayUrl;
    }

    public void setDisplayUrl(String displayUrl) {
        this.displayUrl = displayUrl;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    @Override
    public String toString() {
        return "SdsIntent{" +
                "intent='" + intent + '\'' +
                ", hierarchy=" + hierarchy +
                ", entityFlag=" + entityFlag +
                ", displayName='" + displayName + '\'' +
                ", displayType='" + displayType + '\'' +
                ", displayUrl='" + displayUrl + '\'' +
                ", displayText='" + displayText + '\'' +
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
