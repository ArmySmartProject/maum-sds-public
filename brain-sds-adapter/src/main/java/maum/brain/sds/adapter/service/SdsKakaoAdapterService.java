package maum.brain.sds.adapter.service;

import maum.brain.sds.adapter.client.SdsCollectorClient;
import maum.brain.sds.data.dto.SdsResponse;
import maum.brain.sds.data.dto.adapter.SdsKakaoAdapterRequest;
import maum.brain.sds.data.dto.adapter.SdsKakaoAdapterResponse;
import maum.brain.sds.data.dto.adapter.SdsKakaoAdapterResponse.SkillTemplate;
import maum.brain.sds.data.dto.adapter.SdsKakaoAdapterResponse.SkillTemplate.*;
import maum.brain.sds.data.dto.general.SdsActionResponse;
import maum.brain.sds.data.dto.general.SdsErrorResponse;
import maum.brain.sds.data.vo.SdsData;
import maum.brain.sds.data.vo.SdsIntent;
import maum.brain.sds.data.vo.SdsUtter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

@Service
public class SdsKakaoAdapterService {

    private Logger logger = LoggerFactory.getLogger(SdsKakaoAdapterService.class);

    @Value("${brain.sds.port.offset}")
    private int portOffset;

    @Autowired
    private SdsCollectorClient client;

    // (2020.03) 한국어만 지원
    private String chatStartLine = "처음으로";

    @Autowired
    public SdsKakaoAdapterService() {
        // do nothing, members autowired.
    }

    public SdsResponse adapt(String host, SdsKakaoAdapterRequest request) {

        String utter = request.getUserRequest().getUtterance();
        String session = request.getUserRequest().getUser().getProperties().getPlusfriendUserKey();
        String staticsData = "{\"device\": \"MOBILE\", \"channel\" : \"KAKAOTALK\"}";

        logger.info("[host] " + host);
        logger.info("[utter] " + utter);
        logger.info("[session] " + session);

        if (session == null || session.isEmpty()) {
            session = "TEST";
            logger.info("No [plus friend user key] from Kakao req. Set session to \"" + session + "\"");
        }

        return this.getResponse(host, session, new SdsUtter(utter), "1", staticsData);
    }

    private SdsResponse getResponse(String host, String session, SdsData data, String lang) {
        SdsResponse response = client.getAction(host, session, data, lang);
        if (response.getClass().equals(SdsErrorResponse.class))
            return response;
        else {
            return this.makeKakaoResponse((SdsActionResponse) response);
        }
    }

    private SdsResponse getResponse(String host, String session, SdsData data, String lang, String jsonData) {
        SdsResponse response = client.getAction(host, session, data, lang, jsonData);
        if (response.getClass().equals(SdsErrorResponse.class))
            return response;
        else return this.makeKakaoResponse((SdsActionResponse) response);
    }

    private SdsResponse makeKakaoResponse(SdsActionResponse responseObject) {
        SdsKakaoAdapterResponse response = new SdsKakaoAdapterResponse();
        SkillTemplate template = response.new SkillTemplate();
        response.setTemplate(template);

        // '처음으로' set
//        setChatStartLine(template);

        if (responseObject.getAnswer() == null) {
            return response;
        }

        setSimpleText(template, responseObject.getAnswer().getAnswer());

        for (SdsIntent expected : responseObject.getExpectedIntents()) {
            switch (expected.getDisplayType()) {
                case "B":
                    setQuickReply(template, expected);
                    break;
                case "L":
                    // (2020.03) 리스트 사용하지 않음.
                    break;
                case "I":
                    setCarousel(template, expected);
                    break;
                default:
                    break;
            }
        }

        return response;
    }

    // maum-SDS의 text answer
    private void setSimpleText(SkillTemplate template, String answer) {
        answer = adaptTextAnswer(template, answer);
        if (answer.isEmpty()) return;
        SimpleText simpleText = template.new SimpleText(answer);
        template.getOutputs().add(template.new Component(simpleText));
    }

    private String adaptTextAnswer(SkillTemplate template, String answer) {
        try {
            if (answer.contains("|||MAP|||") || answer.contains("|||INQUIRY|||")) {
                // not supported yet. todo
            } else if (answer.contains("|||PROMOTION|||")) {
                adaptPromotion(template, answer.split("\\|\\|\\|PROMOTION\\|\\|\\|")[1]);
            } else if (answer.contains("|||IMG_CAROUSEL|||")) {
                // 이미지 캐로셀의 경우 textAnswer가 나중에 출력되었으면 좋겠다는 요청 반영되지 않음. todo
                adaptImgCarousel(template, answer.split("\\|\\|\\|IMG_CAROUSEL\\|\\|\\|")[1]);
            }

            answer = answer.split("\\|\\|\\|")[0];

            // HTML 식 코드 처리
            // br 태그 제거
            answer = answer.replaceAll("<br>", "\n");
            answer = answer.replaceAll("<br/>", "\n");

            // a 태그  -> basicCard 로 변환
            if (answer.indexOf("<a") != -1 && answer.indexOf("</a>") != -1) {
                adaptATag(template, answer);
                answer = "";
            }

        } catch (Exception e) {
            logger.error("Text Answer parse ERR. {}", e.getMessage(), e);
        }
        return answer;
    }

    // '처음으로' 예약어 quickReply set
    private void setChatStartLine(SkillTemplate template) {
        QuickReply quickReply = template.new QuickReply();
        quickReply.setLabel(chatStartLine);
        quickReply.setMessageText(chatStartLine);
        template.getQuickReplies().add(quickReply);
    }

    // maum-SDS의 Button
    private void setQuickReply(SkillTemplate template, SdsIntent expected) {
        QuickReply quickReply = template.new QuickReply();
        quickReply.setLabel(expected.getDisplayName());
        quickReply.setMessageText(expected.getIntent());
        template.getQuickReplies().add(quickReply);
    }

    // maum-SDS의 Carousel
    private void setCarousel(SkillTemplate template, SdsIntent expected) {
        Carousel carousel = null;
        boolean needInit = true;
        for (Component output : template.getOutputs()) {
            if (output.getCarousel() != null) {
                carousel = output.getCarousel();
                needInit = false;
                break;
            }
        }

        if (needInit) {
            carousel = template.new Carousel();
            template.getOutputs().add(template.new Component(carousel));
        }

        BasicCard basicCard = template.new BasicCard();
        basicCard.setTitle(expected.getDisplayName());
        basicCard.setDescription(expected.getDisplayText());
        basicCard.setThumbnail(basicCard.new Thumbnail(expected.getDisplayUrl()));
        // 캐로셀에 클릭 기능을 대체하기 위해 버튼 생성.
        BasicCard.Button btn = basicCard.new Button("내용보기", "message");
        btn.setMessageText(expected.getIntent());
        basicCard.setButtons(new ArrayList<>());
        basicCard.getButtons().add(btn);
        carousel.getItems().add(basicCard);
    }

    private void adaptATag(SkillTemplate template, String answer) throws Exception {
        BasicCard basicCard = template.new BasicCard();
        basicCard.setButtons(new ArrayList<>());

        while (true) {
            int aTagStart = answer.indexOf("<a");
            int aTagEnd = answer.indexOf("</a>");
            // <a href=\"https://www.youtube.com/watch?v=gRkv5u2daxU&t=2s\">스타일러 안내</a>
            if (aTagStart != -1 && aTagEnd != -1) {
                String aTagStr = answer.substring(aTagStart, aTagEnd + 4);
                String url = aTagStr.substring(aTagStr.indexOf("href=") + 6,
                        aTagStr.indexOf(">") - 1);
                String label = aTagStr.substring(aTagStr.indexOf(">") + 1, aTagStr.lastIndexOf("<"));
                answer = answer.replace(aTagStr, "");

                BasicCard.Button btn = basicCard.new Button(label, "webLink");
                btn.setWebLinkUrl(url);
                basicCard.getButtons().add(btn);
            } else {
                break;
            }
        }

        basicCard.setDescription(answer);
        template.getOutputs().add(template.new Component(basicCard));
    }

    private void adaptPromotion(SkillTemplate template, String answer) {
        // {"title":"[프로모션]","img":"https://post-phinf.pstatic.net/MjAyMDAyMjBfNjAg/MDAxNTgyMTkyMjAxNzI3.J49RNTJ-SfO1X6N9akeyBkImUWQYRzLQje-AKf2FGPAg.7O3YlMaNI1W3-HrQJHFv7ulVi_J7XT506ToyPX7nqZYg.JPEG/movie_image_%281%29.jpg?type=w1200",
        // "comment":"호텔에서 제공하는 'winter vacation 프로모션'을 확인하세요 <a href='#'>프로모션 확인</a>"}

        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject promotion = (JSONObject) jsonParser.parse(answer);
            String title = (String) promotion.get("title");
            String imgUrl = (String) promotion.get("img");
            String comment = (String) promotion.get("comment");

            BasicCard basicCard = template.new BasicCard();
            basicCard.setButtons(new ArrayList<>());

            // title
            basicCard.setTitle(title);
            // img Url
            basicCard.setThumbnail(basicCard.new Thumbnail(imgUrl));

            // button
            int aTagStart = comment.indexOf("<a");
            int aTagEnd = comment.indexOf("</a>");
            if (aTagStart != -1 && aTagEnd != -1) {
                String aTagStr = comment.substring(aTagStart, aTagEnd + 4);
                String url = aTagStr.substring(aTagStr.indexOf("href=") + 6,
                        aTagStr.indexOf(">") - 1);
                String label = aTagStr.substring(aTagStr.indexOf(">") + 1, aTagStr.lastIndexOf("<"));
                comment = comment.replace(aTagStr, "");

                BasicCard.Button btn = basicCard.new Button(label, "webLink");
                btn.setWebLinkUrl(url);
                basicCard.getButtons().add(btn);

                // description
                basicCard.setDescription(comment);
            }

            template.getOutputs().add(template.new Component(basicCard));

        } catch (Exception e) {
            logger.error("adaptPromotion() ERR. ", e);
        }
    }

    private void adaptImgCarousel(SkillTemplate template, String answer) {
        // |||IMG_CAROUSEL|||
        // {"imgList":[{"title":"test","src":"https://user-images.githubusercontent.com/31176781/75150477-cbb2ab00-5747-11ea-9e6e-8dc66d4d360f.png"},
        // {"title":"부엉이눈","src":"https://post-phinf.pstatic.net/MjAxNzA5MThfMjE4/MDAxNTA1NzE4NDk4MjY4.BNUB-Ful9pV2hcg_5hdJzyPRyXPnk0C6KGjcok__QkAg.9onUKAl7MLep59zVlYS6o3J0lx_16yo_AYonupDADdcg.JPEG/20170915150632943.jpg?type=w1200"},
        // {"title":"올빼미","src":"https://i.pinimg.com/originals/87/1f/35/871f3544dd365e831b8d9e4130fda96a.jpg"},
        // {"title":"내친구 올라프","src":"https://t1.daumcdn.net/cfile/tistory/2564A950531830CD0B"}]}

        try {
            JSONParser jsonParser = new JSONParser();
            JSONArray imgList = (JSONArray) ((JSONObject) jsonParser.parse(answer)).get("imgList");

            Carousel carousel = template.new Carousel();

            for (Object img : imgList) {

                BasicCard basicCard = template.new BasicCard();
                basicCard.setButtons(new ArrayList<>());

                JSONObject imgJson = (JSONObject) img;
                basicCard.setThumbnail(basicCard.new Thumbnail((String) imgJson.get("src")));

                carousel.getItems().add(basicCard);
            }

            template.getOutputs().add(template.new Component(carousel));

        } catch (Exception e) {
            logger.error("adaptImgCarousel() ERR. ", e);
        }
    }

}
