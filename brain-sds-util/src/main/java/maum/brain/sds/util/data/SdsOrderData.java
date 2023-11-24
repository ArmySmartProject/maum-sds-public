package maum.brain.sds.util.data;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SdsOrderData implements Serializable {
    private String host;
    private String intent;
    private Boolean sendResult;

    private String name;
    private String phone;
    private String email;
    private String pickupTime;
    private List<String[]> reqList;
    private String msg;
    private String totalPrice;
    private String take;
    private String payment;

    public SdsOrderData() {
    }

    public SdsOrderData(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public SdsOrderData(String name, String phone, String email, String pickupTime, List<String[]> reqList, String totalPrice) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.pickupTime = pickupTime;
        this.reqList = reqList;
        this.totalPrice = totalPrice;
    }

    public SdsOrderData(String name, String phone, String email, String pickupTime, List<String[]> reqList, String msg, String totalPrice) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.pickupTime = pickupTime;
        this.reqList = reqList;
        this.msg = msg;
        this.totalPrice = totalPrice;
    }

    public SdsOrderData(String name, String phone, String email, String pickupTime, List<String[]> reqList,
                        String msg, String totalPrice, String take, String payment) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.pickupTime = pickupTime;
        this.reqList = reqList;
        this.msg = msg;
        this.totalPrice = totalPrice;
        this.take = take;
        this.payment = payment;
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

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
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

    public List<String[]> getReqList() {
        return reqList;
    }

    public void setReqList(List<String[]> reqList) {
        this.reqList = reqList;
    }

    public void addReq(String[] req){
        this.reqList.add(req);
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getTake() {
        return take;
    }

    public void setTake(String take) {
        this.take = take;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    @Override
    public String toString() {
        return "SdsConciergeData{" +
            "host='" + host + '\'' +
            ", intent='" + intent + '\'' +
            ", sendResult='" + sendResult + '\'' +
            ", name='" + name + '\'' +
            ", pickupTime='" + pickupTime + '\'' +
            ", phone='" + phone + '\'' +
            ", email='" + email + '\'' +
            ", reqList='" + reqList + '\'' +
            ", msg='" + msg + '\'' +
            ", totalPrice='" + totalPrice + '\'' +
            ", take='" + take + '\'' +
            ", payment='" + payment + '\'' +
            '}';
    }
}
