package maum.brain.sds.data.dto.adapter;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SdsKakaoAdapterRequest {

    // 사용자 정보 (발화 포함)
    private UserRequest userRequest;
    // context 정보 (사용x)
    private String[] contexts;
    // 사용자의 발화 받은 봇 정보
    private Bot bot;
    // 실행 스킬 정보 (엔티티 포함)
    private Action action;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public class UserRequest {

        // 사용자의 시간대 (ex. Asia/Seoul)
        private String timezone;
        // 사용자의 발화에 반응한 블록의 정보
        private Block block;
        // 사용자 발화
        private String utterance;
        // 사용자의 언어 (한국이면 kr)
        private String lang;
        // 사용자 정보
        private User user;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public class User {

            private String id;
            private String type;
            private Properties properties;

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            public class Properties {
                // 카카오톡 채널 사용자 id
                private String plusfriendUserKey;
            }
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public class Block {
            private String id;
            private String name;
        }

    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public class Bot {
        // 봇의 식별자
        private String id;
        // 봇의 이름
        private String name;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public class Action {
        // 스킬명
        private String name;
        // 스킬 식별자
        private String id;
    }

}
