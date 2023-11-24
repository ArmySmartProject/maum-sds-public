package maum.brain.sds.frontend.service;

import java.util.Map;
import maum.brain.sds.frontend.model.FrontDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

@Service
public class RootService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SourceService sourceService;

    @Autowired
    FrontDao frontDao;

    public ModelAndView indexView(Map<String, Object> metaInfo){

        ModelAndView modelAndView = new ModelAndView("index");
        String host;
        String qrLocation;
        host = (String) metaInfo.get("host");
        qrLocation = (String) metaInfo.get("qrLocation");

        if (host == null) {
            host = "2";
        }


        // STYLE
        String retStr = "";
        try {
            retStr = sourceService.checkStyleCSS(host,"chatbot_custom.css");
        } catch (Exception e){

        }

        // sds-builder에서 만든 css를 이용하는 case
        if (!retStr.equals("")) {
            modelAndView.addObject("cssInfo", String.format("/cssCustom?host=%s", host));
        }

        // frontend에 포함된 css를 이용하는 case
        else {
            String cssName = frontDao.selectHostName(host);
            if (!sourceService.checkResourceFile(cssName)) {
                String newCssName = "host";
                // Y/N 봇은 pps_ynbot.css를 사용한다.
                if (cssName.startsWith("ynbot_")){
                    newCssName = "pps_ynbot";
                }
                logger.info("No CSS file named '{}.css', change to '{}.css'", cssName, newCssName);
                cssName = newCssName;
            }
            logger.info(String.format("host : %s, cssName : %s", host, cssName));
            modelAndView.addObject("cssInfo", String.format("/resources/css/%s.css", cssName));
        }

        modelAndView.addObject("host", host);
        modelAndView.addObject("qrLocation", qrLocation);

        return modelAndView;
    }


    public ModelAndView noFloatIframeView(Map<String, Object> metaInfo){

        ModelAndView modelAndView = new ModelAndView("noFloatIframe");
        String host;
        String qrLocation;
        host = (String) metaInfo.get("host");
        qrLocation = (String) metaInfo.get("qrLocation");

        if (host == null) {
            host = "2";
        }


        // STYLE
        String retStr = "";
        try {
            retStr = sourceService.checkStyleCSS(host,"chatbot_custom.css");
        } catch (Exception e){

        }

        // sds-builder에서 만든 css를 이용하는 case
        if (!retStr.equals("")) {
            modelAndView.addObject("cssInfo", String.format("/cssCustom?host=%s", host));
        }

        // frontend에 포함된 css를 이용하는 case
        else {
            String cssName = frontDao.selectHostName(host);
            if (!sourceService.checkResourceFile(cssName)) {
                String newCssName = "host";
                // Y/N 봇은 pps_ynbot.css를 사용한다.
                if (cssName.startsWith("ynbot_")){
                    newCssName = "pps_ynbot";
                }
                logger.info("No CSS file named '{}.css', change to '{}.css'", cssName, newCssName);
                cssName = newCssName;
            }
            logger.info(String.format("host : %s, cssName : %s", host, cssName));
            modelAndView.addObject("cssInfo", String.format("/resources/css/%s.css", cssName));
        }

        modelAndView.addObject("host", host);
        modelAndView.addObject("qrLocation", qrLocation);
        modelAndView.addObject("floatCssLocation", String.format("../js/nofloat/{}/qrLocation", host));

        return modelAndView;
    }

    public ModelAndView noFloatIndexView(Map<String, Object> metaInfo){

        ModelAndView modelAndView = new ModelAndView("noFloatIndex");
        String host;
        String qrLocation;
        String lang;
        host = (String) metaInfo.get("host");
        qrLocation = (String) metaInfo.get("qrLocation");
        lang = (String) metaInfo.get("lang");

        if (host == null) {
            host = "2";
        }


        // STYLE
        String retStr = "";
        try {
            retStr = sourceService.checkStyleCSS(host,"chatbot_custom.css");
        } catch (Exception e){

        }

        // sds-builder에서 만든 css를 이용하는 case
        if (!retStr.equals("")) {
            modelAndView.addObject("cssInfo", String.format("/cssCustom?host=%s", host));
        }

        // frontend에 포함된 css를 이용하는 case
        else {
            String cssName = frontDao.selectHostName(host);
            if (!sourceService.checkResourceFile(cssName)) {
                String newCssName = "host";
                // Y/N 봇은 pps_ynbot.css를 사용한다.
                if (cssName.startsWith("ynbot_")){
                    newCssName = "pps_ynbot";
                }
                logger.info("No CSS file named '{}.css', change to '{}.css'", cssName, newCssName);
                cssName = newCssName;
            }
            logger.info(String.format("host : %s, cssName : %s", host, cssName));
            modelAndView.addObject("cssInfo", String.format("/resources/css/%s.css", cssName));
        }

        modelAndView.addObject("host", host);
        modelAndView.addObject("qrLocation", qrLocation);
        modelAndView.addObject("floatCssLocation", "../js/nofloat/"+host+"/qrLocation?lang=" + lang);

        return modelAndView;
    }

    public int addLinkLog(Map<String, Object> param){
        return frontDao.addLinkLog(param);
    };

}
