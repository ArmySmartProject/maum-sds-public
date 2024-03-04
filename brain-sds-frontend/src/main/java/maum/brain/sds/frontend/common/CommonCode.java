package maum.brain.sds.frontend.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

public class CommonCode {

    private static Logger logger = LoggerFactory.getLogger(CommonCode.class);

    public static final String CSS_DIR = "static/resources/css";
    public static final String HOST_JS_PATH = "static/resources/js/host.js";
    public static final String HOST_CSS_PATH = "static/resources/css/host.css";
    public static final String HOST_META_FORMAT = "var chat_meta = " +
        "{\"host\" : \"%s\", \"host_name\" : \"%s\", \"lang\" : \"%s\", \"supportedLangs\" : \"%s\","
        + "\"qrLocation\" : \"%s\","
        + " \"country\" : \"%s\", \"sds_url\" : \"%s\", \"previousSess\" : \"%s\", \"chatServerURL\" : \"%s\", "
        + "\"hostAddJs\" : \"%s\"};\n";

    public static final String MEDIA_TYPE_TEXT_CSS = "text/css";
    public static final String MEDIA_TYPE_TEXT_PLAIN = "text/plain";

    public static String getLangCode(String lang) {
        switch (lang) {
            case "kor" :    return "1";
            case "eng" :    return  "2";
            case "jpn" :    return  "3";
            case "chn" :    return  "4";
            default :       return  "1";
        }
    }

    public static String getServerUrl(HttpServletRequest request) {
        String scheme;
        String serverName;
        int serverPort;

        try {
            scheme = request.getScheme();
//            scheme="https";
            serverName = request.getServerName();
            serverPort = request.getServerPort();
            return String.format("%s://%s:%d", scheme, serverName, serverPort);

        } catch (NullPointerException e) {
            logger.warn("[CommonCode getServerUrl NullPointException]");
            return "https://sds.maum.ai/";
        }
    }
}