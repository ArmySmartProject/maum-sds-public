package maum.brain.sds.analysis.service;

import java.util.HashMap;
import java.util.Map;
import maum.brain.sds.analysis.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.*;

@Service
public class AnalysisService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String LAST_SECOND_PROFIX = " 23:59:59";
    private static final String PREFIX_FORMAT = " %d:00:00";

    @Autowired
    private AnalysisDao dao;

    public String auth(String id, String password, HttpServletRequest request) {
        User user = new User(id, password);
        String result = "Success";
        if (selectUser(user) == 0) {
            result = "False";
        }
        Map<String, String> hostInfo = setHostData(user.getUserId());
        HttpSession session = request.getSession();
        session.setAttribute("user", user);
        session.setAttribute("hotel", user.getUserId());
        session.setAttribute("host", hostInfo.get("host"));

        logger.info("Login User : " + user.toString() + " " + result);

        return result;
    }

    public void signOut(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.invalidate();
    }

    private int selectUser(User user) {
        int result = 0;
        switch(user.getUserId()) {
            case "gt_hud" :
                if (user.getUserPassword().equals("gt_hud")) result = 1;
                break;
            case "mayfield" :
                if (user.getUserPassword().equals("mayfield")) result = 1;
                break;
            case "life_dental" :
                if (user.getUserPassword().equals("life_dental")) result = 1;
                break;
            case "value_busan" :
                if (user.getUserPassword().equals("value_busan")) result = 1;
                break;
            case "active_life" :
                if (user.getUserPassword().equals("active_life")) result = 1;
                break;
            case "mercure" :
                if (user.getUserPassword().equals("mercure")) result = 1;
                break;
            case "ramada" :
                if (user.getUserPassword().equals("ramada")) result = 1;
                break;
            case "westurn" :
                if (user.getUserPassword().equals("westurn")) result = 1;
                break;
            case "sully" :
                if (user.getUserPassword().equals("sully")) result = 1;
                break;
            case "rosana" :
                if (user.getUserPassword().equals("rosana")) result = 1;
                break;
            case "royal_seoul" :
                if (user.getUserPassword().equals("royal_seoul")) result = 1;
                break;
            case "asti_busan" :
                if (user.getUserPassword().equals("asti_busan")) result = 1;
                break;
            case "suite_jeju" :
                if (user.getUserPassword().equals("suite_jeju")) result = 1;
                break;
            case "suite_namwon" :
                if (user.getUserPassword().equals("suite_namwon")) result = 1;
                break;
            case "suite_naksan" :
                if (user.getUserPassword().equals("suite_naksan")) result = 1;
                break;
            case "suite_gyeongju" :
                if (user.getUserPassword().equals("suite_gyeongju")) result = 1;
                break;
            case "haedamchae_gasan" :
                if (user.getUserPassword().equals("haedamchae_gasan")) result = 1;
                break;
            case "chisun" :
                if (user.getUserPassword().equals("chisun")) result = 1;
                break;
            case "soulhada" :
                if (user.getUserPassword().equals("soulhada")) result = 1;
                break;
            case "lavita" :
                if (user.getUserPassword().equals("lavita")) result = 1;
                break;
            case "royal_emporium" :
                if (user.getUserPassword().equals("royal_emporium")) result = 1;
                break;
            case "pension_hellopeace" :
                if (user.getUserPassword().equals("pension_hellopeace")) result = 1;
                break;
            case "hotel_patio7" :
                if (user.getUserPassword().equals("hotel_patio7")) result = 1;
                break;
            case "ramada_namdaemun" :
                if (user.getUserPassword().equals("ramada_namdaemun")) result = 1;
                break;
            case "pension_paradog" :
                if (user.getUserPassword().equals("pension_paradog")) result = 1;
                break;
            case "hotel_brownSuites_seoul" :
                if (user.getUserPassword().equals("hotel_brownSuites_seoul")) result = 1;
                break;
            case "hotel_brownSuites_jeju" :
                if (user.getUserPassword().equals("hotel_brownSuites_jeju")) result = 1;
                break;
            case "clinic_maylin" :
                if (user.getUserPassword().equals("clinic_maylin")) result = 1;
                break;
            case "hotel_koreana" :
                if (user.getUserPassword().equals("hotel_koreana")) result = 1;
                break;
        }

        return result;
    }

    public boolean sessionCheck(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object user = session.getAttribute("user");
        logger.info(user.toString());
        return user != null;
    }

    public DisplayParams getDrawData(String hotel, String lang, int days) {
        LocalDate endDate = LocalDate.now().plusDays(1);
        LocalDate startDate = LocalDate.now().minusDays(days);

        logger.info(String.format("%s to %s", startDate.toString(), endDate.toString()));

        DisplayParams params = new DisplayParams();
        Map<String, String> hostInfo = setHostData(hotel);

        String host = hostInfo.get("host");
        String hostTitle = hostInfo.get("hostTitle");

        params.setPageTitle(hostTitle);

        int totalMsg = dao.getTotalMessages(host, lang, startDate.toString(), endDate.toString());
        int totalUsers = dao.getTotalUsers(host, lang, startDate.toString(), endDate.toString());
        int todayUsers = dao.getTotalUsers(host, lang, endDate.minusDays(1).toString(), endDate.toString());
        int totalEmails = dao.getTotalEmails(host, lang, startDate.toString(), endDate.toString());

        int pcUsers = dao.getUsers(host, lang, startDate.toString(), endDate.toString(), "PC");
        int mobileUsers = dao.getUsers(host, lang, startDate.toString(), endDate.toString(), "MOBILE");
        int hpUsers = dao.getUsers(host, lang, startDate.toString(), endDate.toString(), "HOMEPAGE");
        int qrUsers = dao.getUsers(host, lang, startDate.toString(), endDate.toString(), "QR");
        int kakaoUsers = dao.getUsers(host, lang, startDate.toString(), endDate.toString(), "KAKAOTALK");

        List<Integer> totalMsgPerHourList = new ArrayList<>();
        int i = 0;
        for(AnalysisTimeDto messages: dao.getMsgPerHour(host, lang, startDate.toString(), endDate.toString())){
            while (messages.getHour() > i){
                totalMsgPerHourList.add(0);
                i++;
            }
            totalMsgPerHourList.add(messages.getCount());
            i++;
        }
        for(; i < 24; i++){
            totalMsgPerHourList.add(0);
        }

        List<Integer> totalActUsersPerHourList = new ArrayList<>();
        i = 0;
        for(AnalysisTimeDto users: dao.getUserPerHour(host, lang, startDate.toString(), endDate.toString())){
            while (users.getHour() > i){
                totalActUsersPerHourList.add(0);
                i++;
            }
            totalActUsersPerHourList.add(users.getCount());
            i++;
        }
        for(; i < 24; i++){
            totalActUsersPerHourList.add(0);
        }

        if (totalUsers > 0) {
            params.setAvgNum(totalMsg / totalUsers);
        } else {
            params.setAvgNum(0);
        }
        params.setDomain(hotel);
        params.setTmNum(totalMsg);
        params.setTuNum(totalUsers);
        params.setWuNum(dao.getWeakProb(host, lang, startDate.toString(), endDate.toString()));
        params.setTeNum(totalEmails);
        params.setTmData(totalMsgPerHourList);
        params.setAuData(totalActUsersPerHourList);
        params.setNuData(new ArrayList<>(Arrays.asList((totalUsers-todayUsers), todayUsers)));
        params.setPmData(new ArrayList<>(Arrays.asList(pcUsers, mobileUsers)));
        params.setHqData(new ArrayList<>(Arrays.asList(hpUsers, qrUsers, kakaoUsers)));
        List<AnalysisAcumDto> stats = this.getIntentCountData(host, lang, startDate.toString(), endDate.toString(), false);
        params.setCcData(stats);
        if (stats.size() > 0) {
            params.setUqData(this.getUtterCountData(stats.get(0).getContent(), host,lang, startDate.toString(), endDate.toString()));
        } else {
            AnalysisAcumDto analysisAcumDto = new AnalysisAcumDto();
            analysisAcumDto.setCount(0);
            List<AnalysisAcumDto> analysisAcumDtoList = new ArrayList<>();
            analysisAcumDtoList.add(analysisAcumDto);
            params.setUqData(analysisAcumDtoList);
        }

        List<AnalysisAcumDto> countryList = new ArrayList<>();
        countryList = this.getUserCountry(host, lang, startDate.toString(), endDate.toString());
        List<String> countryKeyList = new ArrayList<>();
        String country = "";
        for (int j=0; j<countryList.size(); j++) {
            country = countryList.get(j).getContent();
            int a = country.indexOf("country");
            countryKeyList.add(country.substring(a+10, a+12));
        }

        Map<String, Object> countryMap = new HashMap<>();
        String countryCheck = "";
        String countryBefore = "";
        int cnt = 1;
        Collections.sort(countryKeyList);
        for (int j=0; j<countryKeyList.size(); j++) {
            countryCheck = countryKeyList.get(j);
            if (!countryCheck.equals(countryBefore)) {
                cnt = 1;
                countryMap.put(countryCheck, cnt);
            } else {
                countryMap.put(countryCheck, ++cnt);
            }
            countryBefore = countryCheck;
        }

        params.setCountryData(countryMap);
        return params;
    }

    public DisplayParams getDrawData(String hotel, String lang, String startDate, String endDate){
        DisplayParams params = new DisplayParams();
        Map<String, String> hostInfo = setHostData(hotel);

        String host = hostInfo.get("host");
        String hostTitle = hostInfo.get("hostTitle");

        params.setPageTitle(hostTitle);

        int totalMsg = dao.getTotalMessages(host, lang, startDate, endDate.concat(LAST_SECOND_PROFIX));
        int totalUsers = dao.getTotalUsers(host, lang, startDate, endDate.concat(LAST_SECOND_PROFIX));
        int totalEmails = dao.getTotalEmails(host, lang, startDate.toString(), endDate.toString());

        int pcUsers = dao.getUsers(host, lang, startDate, endDate.concat(LAST_SECOND_PROFIX), "PC");
        int mobileUsers = dao.getUsers(host, lang, startDate, endDate.concat(LAST_SECOND_PROFIX), "MOBILE");
        int hpUsers = dao.getUsers(host, lang, startDate, endDate.concat(LAST_SECOND_PROFIX), "HOMEPAGE");
        int qrUsers = dao.getUsers(host, lang, startDate, endDate.concat(LAST_SECOND_PROFIX), "QR");
        int kakaoUsers = dao.getUsers(host, lang, startDate, endDate.concat(LAST_SECOND_PROFIX), "KAKAOTALK");

        List<Integer> totalMsgPerHourList = new ArrayList<>();
        int i = 0;
        for(AnalysisTimeDto messages: dao.getMsgPerHour(host, lang, startDate, endDate)){
            while (messages.getHour() > i){
                totalMsgPerHourList.add(0);
                i++;
            }
            totalMsgPerHourList.add(messages.getCount());
            i++;
        }
        for(; i < 24; i++){
            totalMsgPerHourList.add(0);
        }

        List<Integer> totalActUsersPerHourList = new ArrayList<>();
        i = 0;
        for(AnalysisTimeDto users: dao.getUserPerHour(host, lang, startDate, endDate)){
            while (users.getHour() > i){
                totalActUsersPerHourList.add(0);
                i++;
            }
            totalActUsersPerHourList.add(users.getCount());
            i++;
        }
        for(; i < 24; i++){
            totalActUsersPerHourList.add(0);
        }

        if (totalUsers > 0) {
            params.setAvgNum(totalMsg / totalUsers);
        } else {
            params.setAvgNum(0);
        }
        params.setDomain(hotel);
        params.setTmNum(totalMsg);
        params.setTuNum(totalUsers);
        params.setWuNum(dao.getWeakProb(host, lang, startDate, endDate));
        params.setTeNum(totalEmails);
        params.setTmData(totalMsgPerHourList);
        params.setAuData(totalActUsersPerHourList);
        params.setNuData(new ArrayList<>(Arrays.asList(totalUsers, dao.getTotalUsers(host, lang, LocalDate.parse(endDate).minusDays(1).toString(), endDate))));
        params.setPmData(new ArrayList<>(Arrays.asList(pcUsers, mobileUsers)));
        params.setHqData(new ArrayList<>(Arrays.asList(hpUsers, qrUsers, kakaoUsers)));
        List<AnalysisAcumDto> stats = this.getIntentCountData(host, lang, startDate, endDate, false);
        params.setCcData(stats);
        if (stats.size() > 0) {
            params.setUqData(this.getUtterCountData(stats.get(0).getContent(), host,lang, startDate.toString(), endDate.toString()));
        } else {
            AnalysisAcumDto analysisAcumDto = new AnalysisAcumDto();
            analysisAcumDto.setCount(0);
            List<AnalysisAcumDto> analysisAcumDtoList = new ArrayList<>();
            analysisAcumDtoList.add(analysisAcumDto);
            params.setUqData(analysisAcumDtoList);
        }

        List<AnalysisAcumDto> countryList = new ArrayList<>();
        countryList = this.getUserCountry(host, lang, startDate.toString(), endDate.toString());
        List<String> countryKeyList = new ArrayList<>();
        String country = "";
        for (int j=0; j<countryList.size(); j++) {
            country = countryList.get(j).getContent();
            int a = country.indexOf("country");
            countryKeyList.add(country.substring(a+10, a+12));
        }

        Map<String, Object> countryMap = new HashMap<>();
        String countryCheck = "";
        String countryBefore = "";
        int cnt = 1;
        Collections.sort(countryKeyList);
        for (int j=0; j<countryKeyList.size(); j++) {
            countryCheck = countryKeyList.get(j);
            if (!countryCheck.equals(countryBefore)) {
                cnt = 1;
                countryMap.put(countryCheck, cnt);
            } else {
                countryMap.put(countryCheck, ++cnt);
            }
            countryBefore = countryCheck;
        }

        params.setCountryData(countryMap);

        return params;
    }

    public List<AnalysisAcumDto> getIntentCountData(String host, String lang, String startDate, String endDate, boolean all){
        return dao.getMostIntents(host, lang, startDate, endDate, all);
    }

    public List<AnalysisAcumDto> getUserCountry(String host, String lang, String startDate, String endDate){
        return dao.getUserCountry(host, lang, startDate, endDate);
    }

    public List<AnalysisAcumDto> getUtterCountData(String intent, String host, String lang, String startDate, String endDate){
        List<AnalysisAcumDto> utters = dao.getUttersFromIntents(intent, host, lang, startDate, endDate);

        if (intent.equals("문의하기") || intent.equals("예약하기")) {
            List<AnalysisAcumDto> totalUtters = new ArrayList<>();
            AnalysisAcumDto utter = new AnalysisAcumDto();
            utter.setContent(intent);
            int count = 0;
            for(AnalysisAcumDto acumDto : utters) {
                count += acumDto.getCount();
            }
            utter.setCount(count);
            totalUtters.add(utter);
            return totalUtters;
        }

        return utters;
    }

    public String getEmailInfo(String host) {
        return dao.getEmailInfo(host);
    }

    public Boolean setEmailInfo(String host, String email) {
        return dao.setEmailInfo(host, email);
    }

    public Map<String, String> setHostData(String hotel) {
        Map<String, String> hostData = new HashMap<>();

        String host = "2";
        String hostTitle;

        switch (hotel) {
            case "gt_hud":
                hostTitle = "골든튤립 해운대 챗봇 통계(Golden Tulip Hotel Chatbot Data)";
                break;
            case "life_dental":
                hostTitle = "라이프 치과 챗봇 통계(Life Dental Chatbot Data)";
                host = "4";
                break;
            case "mayfield":
                hostTitle = "메이필드 호텔";
                host = "5";
                break;
            case "value_busan":
                hostTitle = "밸류 부산 챗봇 통계(Value Busan Hotel Chatbot Data)";
                host = "8";
                break;
            case "active_life":
                hostTitle = "액티브 라이프 챗봇 통계(Active Life Hotel Chatbot Data)";
                host = "9";
                break;
            case "mercure":
                hostTitle = "머큐어 앰배서더 챗봇 통계(Mercure Ambassador Hotel Chatbot Data";
                host = "10";
                break;
            case "ramada":
                hostTitle = "라마다 챗봇 통계(Ramada Hotel Chatbot Data";
                host = "11";
                break;
            case "westurn":
                hostTitle = "웨스턴 그레이스 챗봇 통계(Weatern Grace Hotel Chatbot Data";
                host = "16";
                break;
            case "sully":
                hostTitle = "설리 챗봇 통계(Sully Chatbot Data";
                host = "17";
                break;
            case "rosana":
                hostTitle = "로사나부띠크호텔 챗봇 통계(Rosana Boutique Hotel Chatbot Data";
                host = "18";
                break;
            case "royal_seoul":
                hostTitle = "로얄호텔서울 챗봇 통계(Royal Hotel Seoul Chatbot Data";
                host = "19";
                break;
            case "asti_busan":
                hostTitle = "아스티호텔부산 챗봇 통계(Asti Hotel Busan Chatbot Data";
                host = "20";
                break;
            case "suite_jeju":
                hostTitle = "스위트 호텔 제주 챗봇 통계(The Suites Hotel Jeju Chatbot Data";
                host = "34";
                break;
            case "suite_namwon":
                hostTitle = "스위트 호텔 남원 챗봇 통계(The Suites Hotel Namwon Chatbot Data";
                host = "35";
                break;
            case "suite_naksan":
                hostTitle = "스위트 호텔 낙산 챗봇 통계(The Suites Hotel Naksan Chatbot Data";
                host = "36";
                break;
            case "suite_gyeongju":
                hostTitle = "스위트 호텔 경주 챗봇 통계(The Suites Hotel Gyeongju Chatbot Data";
                host = "37";
                break;
            case "haedamchae_gasan":
                hostTitle = "호텔해담채가산 챗봇 통계(Hotel Haedamchae Gasan Chatbot Data";
                host = "41";
                break;
            case "chisun":
                hostTitle = "치선호텔서울명동 챗봇 통계(Chisun Hotel Chatbot Data";
                host = "43";
                break;
            case "soulhada":
                hostTitle = "호텔 소울하다 챗봇 통계(Hotel Soulhada Chatbot Data";
                host = "52";
                break;
            case "lavita":
                hostTitle = "라비타 호텔 챗봇 통계(Lavita Hotel Chatbot Data";
                host = "53";
                break;
            case "royal_emporium":
                hostTitle = "로얄 엠포리움 호텔 챗봇 통계(Royal Emporium Hotel Chatbot Data";
                host = "68";
                break;
            case "hotel_brownSuites_seoul":
                hostTitle = "브라운스위트 서울 호텔 챗봇 통계(Brown Suites Seoul Hotel Chatbot Data)";
                host = "69";
                break;
            case "clinic_maylin":
                hostTitle = "메이린 클리닉 압구정점 챗봇 통계(Maylin Clinic Chatbot Data)";
                host = "94";
                break;
            case "hotel_brownSuites_jeju":
                hostTitle = "브라운스위트 제주 호텔 챗봇 통계(Brown Suites Jeju Hotel Chatbot Data)";
                host = "104";
                break;
            case "pension_hellopeace":
                hostTitle = "헬로 피스 펜션 챗봇 통계(Pension Hellopeace Chatbot Data";
                host = "110";
                break;
            case "hotel_patio7":
                hostTitle = "파티오세븐 호텔 챗봇 통계(Hotel Patio7 Chatbot Data";
                host = "125";
                break;
            case "ramada_namdaemun":
                hostTitle = "라마다 호텔앤스위트 남대문 챗봇 통계(Hotel Ramada Namdaemun Chatbot Data";
                host = "119";
                break;
            case "pension_paradog":
                hostTitle = "파라독스 펜션 챗봇 통계(Pension Paradog Chatbot Data";
                host = "124";
                break;
            case "hotel_koreana":
                hostTitle = "코리아나 호텔 챗봇 통계(Koreana Hotel Chatbot Data";
                host = "161";
                break;
            default:
                hostTitle = "골든튤립 해운대 챗봇 통계(Golden Tulip Hotel Chatbot Data";
        }
        hostData.put("host", host);
        hostData.put("hostTitle", hostTitle);

        return hostData;
    }
}
