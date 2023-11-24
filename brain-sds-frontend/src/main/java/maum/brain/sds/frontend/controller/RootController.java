package maum.brain.sds.frontend.controller;

import maum.brain.sds.frontend.model.HostInfoParams;
import maum.brain.sds.frontend.service.RootService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping("/")
public class RootController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RootService service;

    @RequestMapping("/")
    public ModelAndView index(HttpServletResponse response) {
        logger.info("Default Connect");
        return index("2", response);
    }

    @RequestMapping("/{host}")
    public ModelAndView index(
            @PathVariable(value = "host", required = false) String host,
            HttpServletResponse response
    ) {
        Map<String, Object> metaInfo = new HashMap<>();
        metaInfo.put("host", host);
        logger.info(String.format("%s Connect", metaInfo.get("host")));
        response.setHeader("Access-Control-Allow-origin","*");
        return service.indexView(metaInfo);
    }

    @RequestMapping("/nofloat/{host}")
    public ModelAndView noFloatIndex(
        @PathVariable(value = "host", required = false) String host,
        @RequestParam(value = "lang", defaultValue = "kor") String lang
    ) {
        Map<String, Object> metaInfo = new HashMap<>();
        metaInfo.put("host", host);
        metaInfo.put("lang",lang);
        logger.info(String.format("%s Connect", metaInfo.get("host")));

        return service.noFloatIndexView(metaInfo);
    }

    @RequestMapping("/{host}/{qrLocation}")
    public ModelAndView index(
        @PathVariable(value = "host", required = false) String host,
        @PathVariable(value = "qrLocation", required = false) String qrLocation
    ) {
        Map<String, Object> metaInfo = new HashMap<>();
        metaInfo.put("host", host);
        metaInfo.put("qrLocation", qrLocation);
        logger.info(String.format("%s Connect", metaInfo.get("host")));
        logger.info(String.format("%s Connect", metaInfo.get("qrLocation")));

        return service.indexView(metaInfo);
    }

    @RequestMapping("/{host}/{qrLocation}/noFloat")
    public ModelAndView noFloatIframe(
        @PathVariable(value = "host", required = false) String host,
        @PathVariable(value = "qrLocation", required = false) String qrLocation
    ) {
        Map<String, Object> metaInfo = new HashMap<>();
        metaInfo.put("host", host);
        metaInfo.put("qrLocation", qrLocation);
        logger.info(String.format("%s Connect", metaInfo.get("host")));
        logger.info(String.format("%s Connect", metaInfo.get("qrLocation")));

        return service.noFloatIframeView(metaInfo);
    }

    @RequestMapping(
            value = "/chatbot"
            , method = GET
    )
    public ModelAndView chatbot(
            String hostNo
            , @RequestParam(value = "lang", defaultValue = "1")         String lang
            , @RequestParam(value = "intent", defaultValue = "default") String intent
            , HttpSession session
        ) {
        logger.info(String.format("[chatbot Connect] host : %s, lang : %s, intent : %s", hostNo, lang, intent));
        ModelAndView modelAndView = new ModelAndView("maumai");
        // 1. DB상에서 해당 host의 ID를 확인 (host가 한글로 등록 되어있어 일단 hostNo로 사용)
        HostInfoParams hostInfo = new HostInfoParams();
        hostInfo.setHost(hostNo);
        hostInfo.setLang(lang);

        if(session.isNew()){
            String sessionStr = UUID.randomUUID().toString().replaceAll("-", "");
            session.setAttribute("session", sessionStr);
        }

        // 사용자 챗봇 접속 시 특정 intent 입력 시 해당 인텐트 발화
        if (!intent.equals("default")) hostInfo.setStartIntent(intent);

        // 2. request에서 해당 ID를 할 수 있도록 모델에 set
        modelAndView.addObject("hostInfo", hostInfo);

        // 3. index에서 해당 session 값이 있으면 바로 챗봇이 풀 사이즈로 열리면서 클로즈 버튼을 삭제
        // 해당 코드는 chatbot.js에 수록 해 둠

        return modelAndView;
    }

    @RequestMapping(
            value = "/aTag/addLinkLog",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ResponseBody
    public void addLinkLog(@RequestBody Map<String, Object> param) {
        try {
            service.addLinkLog(param);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
