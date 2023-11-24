package maum.brain.sds.util.data;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SdsInquiryData implements Serializable {
    private String host;
    private String intent;
    private Boolean sendResult;

    private String name;
    private String gender;
    private String phone;
    private String email;
    private String datetime;
    private String add;
    private String personCnt;
    private String eventType;
    private String tableStyle;
    private String inquiryMsg;

    public SdsInquiryData() {
    }

    public SdsInquiryData(String name, String gender, String phone, String email) {
        this.name = name;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
    }

    public SdsInquiryData(String host, String name, String gender, String phone, String inquiryMsg) {
        this.host = host;
        this.name = name;
        this.gender = gender;
        this.phone = phone;
        this.inquiryMsg = inquiryMsg;
    }

    public SdsInquiryData(String host, String name, String gender, String phone, String email, String inquiryMsg) {
        this.host = host;
        this.name = name;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.inquiryMsg = inquiryMsg;
    }

    public SdsInquiryData(String host, String name, String gender, String phone, String email, String datetime, String add) {
        this.host = host;
        this.name = name;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.datetime = datetime;
        this.add = add;
    }

    public SdsInquiryData(String host, String name, String gender, String phone, String email, String datetime, String add, String inquiryMsg) {
        this.host = host;
        this.name = name;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.datetime = datetime;
        this.add = add;
        this.inquiryMsg = inquiryMsg;
    }

    public SdsInquiryData(String host, String name, String gender, String phone, String email, String datetime, String add,
                          String inquiryMsg, String personCnt, String eventType, String tableStyle) {
        this.host = host;
        this.name = name;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.datetime = datetime;
        this.add = add;
        this.inquiryMsg = inquiryMsg;
        this.personCnt = personCnt;
        this.eventType = eventType;
        this.tableStyle = tableStyle;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public Boolean getSendResult() {
        return sendResult;
    }

    public void setSendResult(Boolean sendResult) {
        this.sendResult = sendResult;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getAdd() {
        return add;
    }

    public void setAdd(String add) {
        this.add = add;
    }

    public String getInquiryMsg() {
        return inquiryMsg;
    }

    public void setInquiryMsg(String inquiryMsg) {
        this.inquiryMsg = inquiryMsg;
    }

    public String getPersonCnt() {
        return personCnt;
    }

    public void setPersonCnt(String personCnt) {
        this.personCnt = personCnt;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getTableStyle() {
        return tableStyle;
    }

    public void setTableStyle(String tableStyle) {
        this.tableStyle = tableStyle;
    }

    @Override
    public String toString() {
        return "SdsConciergeData{" +
            "host='" + host + '\'' +
            ", intent='" + intent + '\'' +
            ", sendResult='" + sendResult + '\'' +
            ", name='" + name + '\'' +
            ", gender='" + gender + '\'' +
            ", phone='" + phone + '\'' +
            ", email='" + email + '\'' +
            ", datetime='" + datetime + '\'' +
            ", add='" + add + '\'' +
            ", inquiryMsg='" + inquiryMsg + '\'' +
            ", personCnt='" + personCnt + '\'' +
            ", eventType='" + eventType + '\'' +
            ", tableStyle='" + tableStyle + '\'' +
            '}';
    }
}
