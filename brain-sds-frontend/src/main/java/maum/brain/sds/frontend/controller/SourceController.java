package maum.brain.sds.frontend.controller;

import maum.brain.sds.frontend.service.SourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static maum.brain.sds.frontend.common.CommonCode.MEDIA_TYPE_TEXT_CSS;
import static maum.brain.sds.frontend.common.CommonCode.MEDIA_TYPE_TEXT_PLAIN;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping("/")
public class SourceController {

    @Autowired
    SourceService service;

    @RequestMapping(
            value = {"/js/{host}","/redtie/js/{host}"}
            , method = GET
            , produces = MEDIA_TYPE_TEXT_PLAIN
    )
    public @ResponseBody
    String buildHostJs(
            @PathVariable(value = "host")                           String host,
            @RequestParam(value = "lang", defaultValue = "kor")     String lang,
            HttpServletRequest request
    ) throws Exception {

        return service.buildHostJs(host, lang, "", request);
    }

    @RequestMapping(
        value = {"/js/{host}/{qrLocation}","redtie/js/{host}/{qrLocation}"}
        , method = GET
        , produces = MEDIA_TYPE_TEXT_PLAIN
    )
    public @ResponseBody
    String buildHostJs(
        @PathVariable(value = "host")                           String host,
        @PathVariable(value = "qrLocation")                     String qrLocation,
        @RequestParam(value = "lang", defaultValue = "kor")     String lang,
        HttpServletRequest request
    ) throws Exception {

        return service.buildHostJs(host, lang, qrLocation, request);
    }

    @RequestMapping(
        value = {"/js/nofloat/{host}/{qrLocation}"}
        , method = GET
        , produces = MEDIA_TYPE_TEXT_PLAIN
    )
    public @ResponseBody
    String buildNoFloatHostJs(
        @PathVariable(value = "host")                           String host,
        @PathVariable(value = "qrLocation")                     String qrLocation,
        @RequestParam(value = "lang", defaultValue = "kor")     String lang,
        HttpServletRequest request
    ) throws Exception {

        return service.buildNoFloatHostJs(host, lang, qrLocation, request);
    }

    @RequestMapping(
            value = {"/css","/redtie/css"}
            , method = GET
            , produces = MEDIA_TYPE_TEXT_CSS
    )
    public @ResponseBody String buildHostMeta(
            @RequestParam(value = "host", defaultValue = "") String host
    ) throws Exception {
        String retStr = service.checkStyleCSS(host,"chatbot_floating.css");
        if(!retStr.equals("")) return retStr;
        return service.buildHostCss(host);
    }

    @RequestMapping(
            value = {"/cssCustom"}
            , method = GET
            , produces = MEDIA_TYPE_TEXT_CSS
    )
    public @ResponseBody String buildCustomCSS(
            @RequestParam(value = "host", defaultValue = "") String host
    ) throws Exception {
        String retStr = service.checkStyleCSS(host,"chatbot_custom.css");
        return retStr;
    }

    @RequestMapping(
            value = {"/imagesLi/logoImg"}
            , method = GET
            , produces = MediaType.IMAGE_JPEG_VALUE
    )
    public @ResponseBody byte[] buildLogo(
            @RequestParam(value = "host", defaultValue = "") String host
    ) throws Exception {
        return service.buildeLogoIconResources(host,"logoImg");
    }

    @RequestMapping(
            value = {"/imagesLi/IconImg"}
            , method = GET
            , produces = MediaType.IMAGE_JPEG_VALUE
    )
    public @ResponseBody byte[] buildIcon(
            @RequestParam(value = "host", defaultValue = "") String host
    ) throws Exception {
        return service.buildeLogoIconResources(host,"IconImg");
    }


    @RequestMapping(
            value = {"/imagesLi/counselorImg"}
            , method = GET
            , produces = MediaType.IMAGE_JPEG_VALUE
    )
    public @ResponseBody byte[] buildCounselorImg(
            @RequestParam(value = "host", defaultValue = "") String host
    ) throws Exception {
        return service.buildeCounselorImgResources(host);
    }

    @RequestMapping(
            value = {"/hostAddJs/{host}"}
            , method = GET
            , produces = MEDIA_TYPE_TEXT_PLAIN
    )
    public @ResponseBody
    String buildHostAddJs(
            @PathVariable(value = "host")                           String host,
            HttpServletRequest request
    ) throws Exception {
        return service.checkStyleCSSByNo(host,"host_add.js");
    }
}
