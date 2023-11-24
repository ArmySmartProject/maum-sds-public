package maum.brain.sds.data.dto.adapter;

import maum.brain.sds.data.dto.SdsResponse;
import maum.brain.sds.data.node.*;

import java.util.ArrayList;
import java.util.List;

public class SdsAdapterResponse implements SdsResponse {
    private String answer;
    private String farewell;
    private List<SdsSelectionNode> buttons;
    private List<SdsSelectionNode> list;
    private List<SdsSelectionNode> carousel;
    // 참고 값
    private String intent;
    private String display;
    private String jsonDebug;
    private String responseOrder;

    public SdsAdapterResponse() {
        this.buttons = new ArrayList<>();
        this.list = new ArrayList<>();
        this.carousel = new ArrayList<>();
    }

    public SdsAdapterResponse(
            String answer,
            String farewell,
            List<SdsSelectionNode> buttons,
            List<SdsSelectionNode> list,
            List<SdsSelectionNode> carousel,
            String intent,
            String display,
            String reponseOrder) {
        this.answer = answer;
        this.farewell = farewell;
        this.buttons = buttons;
        this.list = list;
        this.carousel = carousel;
        this.intent = intent;
        this.display = display;
        this.responseOrder = reponseOrder;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getFarewell() {
        return farewell;
    }

    public void setFarewell(String farewell) {
        this.farewell = farewell;
    }

    public List<SdsSelectionNode> getButtons() {
        return buttons;
    }

    public void setButtons(List<SdsSelectionNode> buttons) {
        this.buttons = buttons;
    }

    public void addButton(SdsSelectionNode buttonNode){
        this.buttons.add(buttonNode);
    }

    public List<SdsSelectionNode> getList() {
        return list;
    }

    public void setList(List<SdsSelectionNode> list) {
        this.list = list;
    }

    public void addList(SdsSelectionNode listNode){
        this.list.add(listNode);
    }

    public List<SdsSelectionNode> getCarousel() {
        return carousel;
    }

    public void setCarousel(List<SdsSelectionNode> carousel) {
        this.carousel = carousel;
    }

    public void addCarousel(SdsSelectionNode carouselNode){
        this.carousel.add(carouselNode);
    }


    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getJsonDebug() { return jsonDebug; }

    public void setJsonDebug(String jsonDebug) { this.jsonDebug = jsonDebug; }

    public String getResponseOrder() { return responseOrder; }

    public void setResponseOrder(String responseOrder) { this.responseOrder = responseOrder; }

    @Override
    public String toString() {
        return "SdsAdapterResponse{" +
                "answer='" + answer + '\'' +
                ", farewell='" + farewell + '\'' +
                ", buttons=" + buttons +
                ", list=" + list +
                ", carousel=" + carousel +
                ", intent=" + intent +
                ", display=" + display +
                ", jsonDebug=" + jsonDebug +
                ", responseOrder=" + responseOrder +
                '}';
    }
}
