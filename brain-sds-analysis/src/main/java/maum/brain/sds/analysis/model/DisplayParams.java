package maum.brain.sds.analysis.model;

import java.util.List;
import java.util.Map;

public class DisplayParams {
    private String pageTitle;
    private String domain;
    private int tmNum;
    private int tuNum;
    private int avgNum;
    private int wuNum;
    private int teNum;
    private List<String> dateLabel;
    private List<Integer> tmData;
    private List<Integer> auData;
    private List<Integer> nuData;
    private List<Integer> pmData;
    private List<Integer> hqData;
    private List<AnalysisAcumDto> ccData;
    private List<AnalysisAcumDto> uqData;
    private Map<String, Object> countryData;

    public String getPageTitle() {
        return pageTitle;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public int getTmNum() {
        return tmNum;
    }

    public void setTmNum(int tmNum) {
        this.tmNum = tmNum;
    }

    public int getTuNum() {
        return tuNum;
    }

    public void setTuNum(int tuNum) {
        this.tuNum = tuNum;
    }

    public int getAvgNum() {
        return avgNum;
    }

    public void setAvgNum(int avgNum) {
        this.avgNum = avgNum;
    }

    public int getWuNum() {
        return wuNum;
    }

    public void setWuNum(int wuNum) {
        this.wuNum = wuNum;
    }

    public int getTeNum() {
        return teNum;
    }

    public void setTeNum(int teNum) {
        this.teNum = teNum;
    }

    public List<String> getDateLabel() {
        return dateLabel;
    }

    public void setDateLabel(List<String> dateLabel) {
        this.dateLabel = dateLabel;
    }

    public List<Integer> getTmData() {
        return tmData;
    }

    public void setTmData(List<Integer> tmData) {
        this.tmData = tmData;
    }

    public List<Integer> getAuData() {
        return auData;
    }

    public void setAuData(List<Integer> auData) {
        this.auData = auData;
    }

    public List<Integer> getNuData() {
        return nuData;
    }

    public void setNuData(List<Integer> nuData) {
        this.nuData = nuData;
    }

    public List<Integer> getPmData() {
        return pmData;
    }

    public void setPmData(List<Integer> pmData) { this.pmData = pmData; }

    public List<Integer> getHqData() { return hqData; }

    public void setHqData(List<Integer> hqData) {
        this.hqData = hqData;
    }

    public List<AnalysisAcumDto> getCcData() {
        return ccData;
    }

    public void setCcData(List<AnalysisAcumDto> ccData) {
        this.ccData = ccData;
    }

    public List<AnalysisAcumDto> getUqData() {
        return uqData;
    }

    public void setUqData(List<AnalysisAcumDto> uqData) {
        this.uqData = uqData;
    }

    public Map<String, Object> getCountryData() {
        return countryData;
    }

    public void setCountryData(Map<String, Object> countryData) {
        this.countryData = countryData;
    }

    @Override
    public String toString() {
        return "DisplayParams{" +
                "pageTitle='" + pageTitle + '\'' +
                ", domain='" + domain + '\'' +
                ", tmNum=" + tmNum +
                ", tuNum=" + tuNum +
                ", avgNum=" + avgNum +
                ", wuNum=" + wuNum +
                ", teNum=" + teNum +
                ", dateLabel=" + dateLabel +
                ", tmData=" + tmData +
                ", auData=" + auData +
                ", nuData=" + nuData +
                ", pmData=" + pmData +
                ", hqData=" + hqData +
                ", ccData=" + ccData +
                ", uqData=" + uqData +
                '}';
    }
}
