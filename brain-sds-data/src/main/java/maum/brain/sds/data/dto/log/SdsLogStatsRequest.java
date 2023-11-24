package maum.brain.sds.data.dto.log;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SdsLogStatsRequest {
    private int host;
    private String session;
    private String utter;

    public SdsLogStatsRequest() {
    }

    public SdsLogStatsRequest(int host, String session, String utter) {
        this.host = host;
        this.session = session;
        this.utter = utter;
    }

    public int getHost() {
        return host;
    }

    public void setHost(int host) {
        this.host = host;
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

    @Override
    public String toString() {
        return "SdsLogStatsRequest{" +
                "host=" + host +
                ", session='" + session + '\'' +
                ", utter='" + utter + '\'' +
                '}';
    }
}
