package maum.brain.sds.analysis.controller;

import maum.brain.sds.analysis.model.AnalysisAcumDto;
import maum.brain.sds.analysis.model.DisplayParams;
import maum.brain.sds.analysis.service.AnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

@Controller
public class AnalysisController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AnalysisService service;

    @RequestMapping("/")
    public String test(HttpServletResponse response) {
        response.setHeader(HttpHeaders.CACHE_CONTROL,
                CacheControl.noCache()
                .cachePrivate()
                .mustRevalidate()
                .getHeaderValue()
        );
        return "analysis";
    }

    @RequestMapping("/auth/redtie")
    public String auth() { return "login.html"; }

    @RequestMapping("/login")
    @ResponseBody
    public String login(
            @RequestParam("id") String id,
            @RequestParam("password") String password,
            HttpServletRequest request
    ) {
        return service.auth(id, password, request);
    }

    @PostMapping("/getDrawDate")
    @ResponseBody
    public DisplayParams getDrawData(
            @RequestParam(value = "hotel", defaultValue = "2") String hotel,
            @RequestParam(value = "lang", defaultValue = "1") String lang,
            @RequestParam(value = "date", defaultValue = "7") int date
    ) {
        return service.getDrawData(hotel, lang, date);
    }

    @PostMapping("/getDrawCustom")
    @ResponseBody
    public DisplayParams getDrawCustomTest(
            @RequestParam(value = "hotel") String hotel,
            @RequestParam(value = "lang") String lang,
            @RequestParam(value = "startDate") String startDate,
            @RequestParam(value = "endDate") String endDate
    ) {
        logger.info(String.format("hotel : %s, language : %s, startDate : %s, endDate : %s", hotel, lang, startDate, endDate));
        return service.getDrawData(hotel, lang, startDate, endDate);
    }

    @RequestMapping("/signOut")
    public String signOut(HttpServletRequest request) {
        service.signOut(request);
        return auth();
    }

    @RequestMapping("/sessionCheck")
    @ResponseBody
    public boolean sessionCheck(HttpServletRequest request) {
        logger.info("SessionCheck!");
        return service.sessionCheck(request);
    }

    @RequestMapping("/getCatCnt")
    @ResponseBody
    public DisplayParams getCc(String host, String lang, String date, String startDate, String endDate, String count){

        boolean all = !count.equals("10");

        if (date == null) {
            DisplayParams result = new DisplayParams();
            result.setCcData(service.getIntentCountData(host, lang, startDate, endDate, all));
            return result;
        }

        DisplayParams result = new DisplayParams();
        result.setCcData(
                service.getIntentCountData(
                        host, lang,
                        LocalDate.now().minusDays(Long.parseLong(date)).toString(),
                        LocalDate.now().toString(),
                        all
                )
        );
        return result;
    }

    @RequestMapping("/getUq")
    @ResponseBody
    public DisplayParams getUq(String category, String host, String date, String startDate, String endDate, String lang) {

        if (date == null) {
            DisplayParams result = new DisplayParams();
            result.setUqData(service.getUtterCountData(category, host, lang, startDate, endDate));
            return result;
        }

        DisplayParams result = new DisplayParams();
        result.setUqData(
                service.getUtterCountData(
                        category, host, lang,
                        LocalDate.now().minusDays(Long.parseLong(date)).toString(),
                        LocalDate.now().toString()
                )
        );
        return result;
    }

    @RequestMapping("/getEmailInfo")
    @ResponseBody
    public String getEmailInfo(String host) {
        return service.getEmailInfo(host);
    }

    @RequestMapping("/setEmailInfo")
    @ResponseBody
    public Boolean setEmailInfo(String host, String email) {
        service.setEmailInfo(host, email);
        return true;
    }
}
