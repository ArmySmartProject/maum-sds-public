package maum.brain.sds.util.data;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SdsConciergeData implements Serializable {
    private String host;
    private String name;
    private List<String> reqList;
    private List<String> msg;

    public SdsConciergeData() {
        reqList = new ArrayList<>();
        msg = new ArrayList<>();
    }

    public SdsConciergeData(String host, String name, List<String> reqList, List<String> msg) {
        this.host = host;
        this.name = name;
        this.reqList = reqList;
        this.msg = msg;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getReqList() {
        return reqList;
    }

    public void setReqList(List<String> reqList) {
        this.reqList = reqList;
    }

    public void addReq(String req){
        this.reqList.add(req);
    }

    public List<String> getMsg() {
        return msg;
    }

    public void setMsg(List<String> msg) {
        this.msg = msg;
    }

    public void addMsg(String msg) {
        this.msg.add(msg);
    }

    @Override
    public String toString() {
        return "SdsConciergeData{" +
                "host='" + host + '\'' +
                "name='" + name + '\'' +
                ", reqList=" + reqList +
                ", msg=" + msg +
                '}';
    }
}
