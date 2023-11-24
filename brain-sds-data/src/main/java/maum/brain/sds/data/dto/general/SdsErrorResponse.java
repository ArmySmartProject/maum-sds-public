package maum.brain.sds.data.dto.general;

import maum.brain.sds.data.dto.SdsResponse;

public class SdsErrorResponse implements SdsResponse {
    private int status;
    private String message;

    public SdsErrorResponse() {
    }

    public SdsErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "SdsErrorResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
