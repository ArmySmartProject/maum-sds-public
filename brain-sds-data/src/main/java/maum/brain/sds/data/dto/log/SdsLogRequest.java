package maum.brain.sds.data.dto.log;

import maum.brain.sds.data.dto.SdsRequest;
import maum.brain.sds.data.vo.SdsData;

public class SdsLogRequest implements SdsRequest {
    private String session;
    private String utter;
    private SdsData data;

    public SdsLogRequest() {
    }

    public SdsLogRequest(String session, String utter, SdsData data) {
        this.session = session;
        this.utter = utter;
        this.data = data;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getUtter() {
        return utter;
    }

    public void setUtter(String utter) {
        this.utter = utter;
    }

    public SdsData getData() {
        return data;
    }

    public void setData(SdsData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "SdsLogRequest{" +
                "session='" + session + '\'' +
                ", utter='" + utter + '\'' +
                '}';
    }
}
