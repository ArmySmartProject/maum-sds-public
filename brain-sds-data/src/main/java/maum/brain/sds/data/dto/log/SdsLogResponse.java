package maum.brain.sds.data.dto.log;

public class SdsLogResponse {
    private int status;
    private String message;

    public SdsLogResponse() {
    }

    public SdsLogResponse(int status, String message) {
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
        return "SdsLogResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
