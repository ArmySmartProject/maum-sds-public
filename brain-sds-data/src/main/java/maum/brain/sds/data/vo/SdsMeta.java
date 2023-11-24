package maum.brain.sds.data.vo;

public class SdsMeta implements SdsData {
    private SdsIntentRel intentRel;
    private RestApiDto restApiDto;

    public SdsMeta() {
    }

    public SdsMeta(SdsIntentRel intentRel) {
        this.intentRel = intentRel;
    }

    public SdsMeta(SdsIntentRel intentRel, RestApiDto restApiDto) {
        this.intentRel = intentRel;
        this.restApiDto = restApiDto;
    }

    public SdsIntentRel getIntentRel() {
        return intentRel;
    }

    public void setIntentRel(SdsIntentRel intentRel) {
        this.intentRel = intentRel;
    }

    public RestApiDto getRestApiDto() {
        return restApiDto;
    }

    public void setRestApiDto(RestApiDto restApiDto) {
        this.restApiDto = restApiDto;
    }

    @Override
    public String toString() {
        return "SdsMeta{" +
                "intentRel=" + intentRel +
                ", restApiDto=" + restApiDto +
                '}';
    }
}
