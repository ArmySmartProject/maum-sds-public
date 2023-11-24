package maum.brain.sds.frontend.service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;
import maum.brain.sds.frontend.common.CommonCode;
import maum.brain.sds.frontend.model.FrontDao;
import net.sf.javainetlocator.InetAddressLocator;
import net.sf.javainetlocator.InetAddressLocatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;


import javax.servlet.http.HttpServletRequest;

import static maum.brain.sds.frontend.common.CommonCode.*;

@Service
public class SourceService {

    @Autowired
    RootService rootService;

    @Autowired
    private FrontDao frontDao;

    @Value("${show.previous.session}")
    private String previousSess;

    @Value("${chat.server.url}")
    private String chatServerURL;

    @Value("${css.route}")
    private String cssRoute;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public String buildHostJs(String host, String lang, String qrLocation, HttpServletRequest request) throws Exception {
        if (frontDao.selectHostAvailable(host)){
            host = frontDao.selectHostName(host);
            String ip = request.getHeader("X-FORWARDED-FOR");

            if(ip == null || ip.length() == 0) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if(ip == null || ip.length() == 0) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if(ip == null || ip.length() == 0) {
                ip = request.getRemoteAddr();
            }

            String country = "";

            try {
                Locale locale = InetAddressLocator.getLocale(ip);
                country = locale.getCountry();
//            System.out.println(locale.getDisplayCountry());
//            System.out.println(locale.getCountry());
//            System.out.println(locale.getDisplayLanguage());
//            System.out.println(locale.getDisplayName());

            } catch (InetAddressLocatorException e) {
                e.printStackTrace();
            }

            StringBuffer stringBuffer = buildHostHead(host, lang, qrLocation.toUpperCase(), country, CommonCode.getServerUrl(request));
            return getHostResource(stringBuffer, HOST_JS_PATH).toString();
        }
        return "console.log('Chatbot License Unavailable')";
    }

    public String buildNoFloatHostJs(String host, String lang, String qrLocation, HttpServletRequest request) throws Exception {
        if (frontDao.selectHostAvailable(host)){
            host = frontDao.selectHostName(host);
            String ip = request.getHeader("X-FORWARDED-FOR");

            if(ip == null || ip.length() == 0) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if(ip == null || ip.length() == 0) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if(ip == null || ip.length() == 0) {
                ip = request.getRemoteAddr();
            }

            String country = "";

            try {
                Locale locale = InetAddressLocator.getLocale(ip);
                country = locale.getCountry();
//            System.out.println(locale.getDisplayCountry());
//            System.out.println(locale.getCountry());
//            System.out.println(locale.getDisplayLanguage());
//            System.out.println(locale.getDisplayName());

            } catch (InetAddressLocatorException e) {
                e.printStackTrace();
            }

            StringBuffer stringBuffer = buildHostHead(host, lang, qrLocation.toUpperCase(), country, CommonCode.getServerUrl(request));
            return getHostResource(stringBuffer, HOST_JS_PATH + "_nofloat.js").toString();
        }
        return "console.log('Chatbot License Unavailable')";
    }

    public String checkStyleCSS(String host, String filename) throws Exception{
        String retStr = "";
        String nowHostNo = frontDao.selectHostCode(host);
        logger.info("checkStyleCSS : " + cssRoute + "/" + nowHostNo + "/" + filename);
        try{
            String nowPath = cssRoute + "/" + nowHostNo + "/" + filename;
            if(new File(nowPath).exists()){
                byte[] encoded = Files.readAllBytes(Paths.get(nowPath));
                retStr = new String(encoded);
            } else {
                logger.info("NOT FOUND FILE : {}", filename);
                return "";
            }
        }catch (Exception e){
            logger.error(e.toString());
            return "";
        }
        return retStr;
    }

    public String checkStyleCSSByNo(String host, String filename) throws Exception{
        String retStr = "";
        String nowHostNo = host;
        logger.info("checkStyleCSSByNo : " + cssRoute + "/" + nowHostNo + "/" + filename);
        try{
            String nowPath = cssRoute + "/" + nowHostNo + "/" + filename;
            if(new File(nowPath).exists()){
                byte[] encoded = Files.readAllBytes(Paths.get(nowPath));
                retStr = new String(encoded);
            } else {
                logger.info("NOT FOUND FILE : {}", filename);
                return "";
            }
        }catch (Exception e){
            logger.error(e.toString());
            return "";
        }
        return retStr;
    }

    public byte[] buildeLogoIconResources(String host, String filename) throws Exception{
        String nowHostNo = host;
        InputStream is;
        try{
            File file = new File(cssRoute + "/" + nowHostNo + "/" + filename.replace("-","."));
            return Files.readAllBytes(file.toPath());
        }catch (Exception e){
            logger.error(e.toString());
            File file = new File(cssRoute + "/Default/" + filename.replace("-","."));
            return Files.readAllBytes(file.toPath());
        }
    }

    public byte[] buildeCounselorImgResources(String host) throws Exception{
        String nowHostNo = host;
        InputStream is;
        try{
            File file = new File(cssRoute + "/" + nowHostNo + "/counselorImg");
            return Files.readAllBytes(file.toPath());
        }catch (Exception e){
            return buildeLogoIconResources(host, "IconImg");
        }
    }

    public String buildHostCss(String host) throws Exception {
        String cssPath = HOST_CSS_PATH;
        if (!host.isEmpty()) {
            cssPath = String.format("%s/%s.css", CSS_DIR, host);
        }
        try {
            return getHostResource(cssPath).toString();
        } catch (FileNotFoundException e) {
            return getHostResource(String.format("%s/%s.css", CSS_DIR, "host")).toString();
        }
    }

    private StringBuffer buildHostHead(String host, String lang, String qrLocation, String country, String serverUrl) {

        CommonCode commonCode = new CommonCode();

        Map hostInfo = frontDao.selectHostInfo(host);
        if (hostInfo == null) {
            logger.warn("WARN: No Host Information.");
        } else {
            logger.info(String.format("Host %s's Info : %s", host, hostInfo.toString()));
        }

        String hostCode = hostInfo.get("No").toString();
        String langCode = commonCode.getLangCode(lang);
        String supportedLangs = "1";

        // LANGUAGE
        if (hostInfo.get("Language") != null) {
            supportedLangs = hostInfo.get("Language").toString();
            try{// 1 언어를 사용하지 않을 경우
                if(!supportedLangs.contains(langCode)){
                    if(supportedLangs.contains(",")){
                        langCode = supportedLangs.split(",")[0];
                    }
                }
            }catch (Exception e){
                // DB : Lang 에러
                logger.info("Lang DB Error Host %s",host);
            }
        }

        String hostAdd = "";
        try {
            hostAdd = checkStyleCSSByNo(hostCode,"host_add.js");
        } catch (Exception e) { }

        String supplier = frontDao.selectSupplier(hostCode);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("var nowSupplier = '"+supplier+"';\n");
        String hostMeta = String.format(HOST_META_FORMAT, hostCode, host, langCode, supportedLangs,
                qrLocation, country, serverUrl, previousSess, chatServerURL,
                "".equals(hostAdd) ? hostAdd : "hostAddJs");
        stringBuffer.append(hostMeta);

        return stringBuffer;
    }


    private StringBuffer getHostResource(StringBuffer stringBuffer, String filePath) throws Exception {
        InputStream inputStream = null;
        BufferedInputStream bufferedInputStream;
        try {
            ClassPathResource jsResource = new ClassPathResource(filePath);
            inputStream = jsResource.getInputStream();
            bufferedInputStream = new BufferedInputStream(inputStream);
            byte[] readBuffer = new byte[bufferedInputStream.available()];
            while (bufferedInputStream.read(readBuffer) != -1) {
                stringBuffer.append(new String(readBuffer));
            }
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            if (inputStream != null) {
                inputStream.close();
            }
            throw e;
        }

        if (inputStream != null) {
            inputStream.close();
        }

        return stringBuffer;
    }

    private StringBuffer getHostResource(String filePath) throws Exception {
        StringBuffer stringBuffer = new StringBuffer();
        return getHostResource(stringBuffer, filePath);
    }

    public boolean checkResourceFile(String host) {
        boolean exist = true;
        try {
            buildHostCss(host);
        } catch (Exception e) {
            if (e instanceof FileNotFoundException) {
                exist = false;
            }
        }
        return exist;
    }


}
