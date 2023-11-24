package maum.brain.sds.data.dto.adapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import maum.brain.sds.data.dto.SdsResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
// @Getter, @Setter, @RequiredArgsConstructor
public class SdsKakaoAdapterResponse implements SdsResponse {

    @NonNull
    // required
    private String version = "2.0";
    private SkillTemplate template;
    private ContextControl context;
    private Map<String, Object> data;

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public class SkillTemplate {

        @NonNull
        // required
        private List<Component> outputs = new ArrayList<>();
        // optional
        private List<QuickReply> quickReplies = new ArrayList<>();

        @Getter
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public class Component {
            // one of
            private SimpleText simpleText;
            private SimpleImage simpleImage;
            private BasicCard basicCard;
            private CommerceCard commerceCard;
            private ListCard listCard;
            private Carousel carousel;

            public Component(SimpleText simpleText) {
                this.simpleText = simpleText;
            }

            public Component(SimpleImage simpleImage) {
                this.simpleImage = simpleImage;
            }

            public Component(BasicCard basicCard) {
                this.basicCard = basicCard;
            }

            public Component(CommerceCard commerceCard) {
                this.commerceCard = commerceCard;
            }

            public Component(ListCard listCard) {
                this.listCard = listCard;
            }

            public Component(Carousel carousel) {
                this.carousel = carousel;
            }
        }

        @Data
        public class SimpleText {
            @NonNull
            // 텍스트 요소 (required)
            private String text;
        }

        @Data
        public class SimpleImage {
            @NonNull
            // 이미지 url (required)
            private String imageUrl;
            @NonNull
            // url이 유효하지 않을 경우 전달되는 text (required)
            private String altText;
        }

        @Data
        // 캐로셀 형식 가능
        public class BasicCard {

            // 카드의 제목
            private String title;
            // 카드에 대한 상세 설명 (max 76자, 2줄)
            private String description;
            // 카드의 상단 이미지
            private Thumbnail thumbnail;
            /*
            // 카드의 프로필 정보 (미지원)
            private Profile profile;
            // 카드의 소셜 정보 (미지원)
            private Social social;
            */
            private List<Button> buttons;

            @Data
            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            public class Thumbnail {

                @NonNull
                // 이미지의 url (required)
                private String imageUrl;
                // 이미지 클릭시 작동하는 link
                private Link link;
                // 이미지 비율 유지 여부
                // true: 이미지 영역을 1:1 비율로 두고 이미지의 원본 비율 유지
                //      이미지가 없는 영역은 흰색으로 노출
                // false: 이미지 영역을 2:1로 두고 이미지의 가운데를 크롭하여 노출 (default)
                private boolean fixedRatio;
                // 이미지 넓이 (fixedRatio true일 경우 required)
                private int width;
                // 이미지 높이 (fixedRatio true일 경우 required)
                private int height;

                @Data
                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                public class Link {
                    private String mobile;
                    private String ios;
                    private String android;
                    private String pc;
                    private String mac;
                    private String win;
                    private String web;
                }
            }

            @Data
            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            public class Button {

                @NonNull
                // 버튼에 적히는 문구 (required) (max 8자)
                private String label;
                @NonNull
                // 버튼 클릭시 수행될 작업 (required)
                private String action;
                // 웹 브라우저를 열고 webLinkUrl 의 주소로 이동 (required if action==webLink)
                private String webLinkUrl;
                // osLink 값에 따라서 웹의 주소로 이동하거나 앱을 실행 (required if action==osLink)
                private String osLink;
                // message: 사용자의 발화로 messageText를 내보냄 (required if action==message)
                // (바로가기 응답의 메세지 연결 기능과 동일)
                // block: 블록 연결시 사용자의 발화로 노출 (required if action==block)
                private String messageText;
                // phoneNumber에 있는 번호로 전화 (required if action==phone)
                private String phoneNumber;
                // blockId를 갖는 블록을 호출합니다. (required if action==block)
                // (바로가기 응답의 블록 연결 기능과 동일)
                private String blockId;
                // block이나 message action으로 블록 호출시, 스킬 서버에 추가적으로 제공하는 정보
                private Map<String, Object> extra;
            }
        }

        @Data
        public class CommerceCard {
        }

        @Data
        public class ListCard {
        }

        @Data
        public class Carousel {
            // 타입. basicCard or commerceCard (basicCard only for now)
            private String type = "basicCard";
            // 캐로셀 아이템
            private List<BasicCard> items = new ArrayList<>();
            // 캐로셀 커버
            private CarouselHeader header;

            public class CarouselHeader {
                // header는 현재 CommerceCard만 제공됨.
            }
        }

        @Data
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public class QuickReply {
            // 사용자에게 노출될 바로가기 응답의 표시
            private String label;
            // 바로가기 응답의 기능 (message or block)
            private String action = "message";
            // 사용자 측으로 노출될 발화
            private String messageText;
            // 블록 아이디 if action == 'block'
            private String blockId;
            // 추가 정보 if action == 'block'
            private Object extra;

        }
    }

    @Data
    public class ContextControl {

        private ContextValue values;

        public class ContextValue {
            private String name;
            private int lifeSpan;
            private Map<String, String> params;
        }
    }

}
