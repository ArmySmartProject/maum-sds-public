package maum.brain.sds.adapter.service;

import maum.brain.sds.adapter.client.SdsCollectorClient;
import maum.brain.sds.data.dto.SdsResponse;
import maum.brain.sds.data.dto.adapter.SdsAdapterCoupledRequest;
import maum.brain.sds.data.dto.adapter.SdsAdapterResponse;
import maum.brain.sds.data.dto.adapter.SdsAdapterStringRequest;
import maum.brain.sds.data.dto.general.SdsActionResponse;
import maum.brain.sds.data.dto.general.SdsErrorResponse;
import maum.brain.sds.data.node.SdsSelectionNode;
import maum.brain.sds.data.vo.SdsData;
import maum.brain.sds.data.vo.SdsIntent;
import maum.brain.sds.data.vo.SdsUtter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class SdsAdapterService {

    @Autowired
    private SdsCollectorClient client;

    @Value("${brain.sds.port.offset}")
    private int portOffset;

    @Autowired
    public SdsAdapterService(){
        // do nothing, members autowired.
    }

    @PostConstruct
    private void postConstruct(){
        client.setPortOffset(this.portOffset);
    }

    public SdsResponse fromStringRequest(SdsAdapterStringRequest request){
        System.out.println(request);
        if(request.getType().equals("intent"))
            return this.getResponse(
                    request.getHost(),
                    request.getSession(),
                    new SdsIntent(request.getInput()),
                    request.getLang(),
                    request.getJsonData()
            );
        else
            return this.getResponse(
                    request.getHost(),
                    request.getSession(),
                    new SdsUtter((request.getInput())),
                    request.getLang(),
                    request.getJsonData()
            );
    }

    public SdsResponse fromCoupledRequest(SdsAdapterCoupledRequest request){
        if(request.getIntent() != null)
            return this.getResponse(
                    request.getHost(),
                    request.getSession(),
                    request.getIntent(),
                    request.getLang()
            );
        else if(request.getUtter() != null)
            return this.getResponse(
                    request.getHost(),
                    request.getSession(),
                    request.getUtter(),
                    request.getLang()
            );
        else throw new UnsupportedOperationException();
    }

    private SdsResponse getResponse(String session, SdsData data){
        SdsResponse response = client.getAction(session, data);
        if(response.getClass().equals(SdsErrorResponse.class))
            return response;
        else return this.makeDisplayResponse((SdsActionResponse) response);
    }

    private SdsResponse getResponse(String host, String session, SdsData data, String lang){
        SdsResponse response = client.getAction(host, session, data, lang);
        if(response.getClass().equals(SdsErrorResponse.class))
            return response;
        else return this.makeDisplayResponse((SdsActionResponse) response);
    }

    private SdsResponse getResponse(String host, String session, SdsData data, String lang, String jsonData){
        SdsResponse response = client.getAction(host, session, data, lang, jsonData);
        if(response.getClass().equals(SdsErrorResponse.class))
            return response;
        else return this.makeDisplayResponse((SdsActionResponse) response);
    }

    private SdsResponse makeDisplayResponse(SdsActionResponse responseObject){
        SdsAdapterResponse response = new SdsAdapterResponse();
        if (responseObject.getAnswer() != null) {
            if(responseObject.getJsonDebug() != null){
                response.setJsonDebug(responseObject.getJsonDebug());
            }
            response.setAnswer(responseObject.getAnswer().getAnswer());
            if(!responseObject.getExpectedIntents().isEmpty()){
                for(SdsIntent expected: responseObject.getExpectedIntents()){
                    switch (expected.getDisplayType()) {
                        case "B":
                            response.addButton(
                                    new SdsSelectionNode(expected.getDisplayName(), expected)
                            );
                            break;
                        case "L":
                            response.addList(
                                    new SdsSelectionNode(expected.getDisplayName(), expected)
                            );
                            break;
                        case "I":
                            response.addCarousel(
                                    new SdsSelectionNode(expected.getDisplayName(), expected)
                            );
                            break;
                        default:
                            break;
                    }
                }
            }

            try {
                // response 의 intent 값을 frontend 에 전달
                response.setIntent(responseObject.getSetMemory().getIntent().getIntent());
                response.setDisplay(responseObject.getDisplay());
                response.setResponseOrder(responseObject.getResponseOrder());
            } catch (Exception e) {
                System.out.println("Cannot set meta value 'intent' from setMemory in response." + e.getMessage());
            }
        }

        return response;
    }
}
