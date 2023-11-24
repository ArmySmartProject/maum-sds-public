package maum.brain.sds.data.dto.adapter;

public class SdsAdapterStringRequest extends SdsAdapterRequest{
    private String input;
    private String type;

    public SdsAdapterStringRequest() {
    }

    public SdsAdapterStringRequest(String input, String type) {
        this.input = input;
        this.type = type;
    }

    public SdsAdapterStringRequest(String session, String host, String input, String type) {
        super(session, host);
        this.input = input;
        this.type = type;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "SdsAdapterStringRequest{" +
                "input='" + input + '\'' +
                ", type='" + type + '\'' +
                "} " + super.toString();
    }
}
