package maum.brain.sds.collector.component;

import maum.brain.bert.intent.Itc;
import maum.brain.sds.collector.client.BertItfClient;
import maum.brain.sds.collector.data.SdsBackendDto;
import maum.brain.sds.collector.data.SdsIntentData;
import maum.brain.sds.collector.service.SdsCollectorService;
import maum.brain.sds.data.dto.collect.SdsUtterCollectRequest;
import maum.brain.sds.data.dto.db.SdsReqEntityDto;
import maum.brain.sds.data.vo.SdsEntity;
import maum.brain.sds.data.vo.SdsIntentRel;
import maum.brain.sds.data.vo.SdsMeta;
import maum.brain.sds.data.vo.SdsUtter;
import maum.brain.sds.data.vo.relations.SdsRelation;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SdsCollectorDao {
    private SqlSessionFactory sessionFactory;

    @Autowired
    public SdsCollectorDao() throws IOException{
        sessionFactory = new SqlSessionFactoryBuilder().build(
                Resources.getResourceAsReader("mybatis/collectorMapConfig.xml")
        );
    }

    public boolean hasRequiredEntities(String intent, String lang, String host){
        try (SqlSession session = this.sessionFactory.openSession()){
            SdsCollectorMapper mapper = session.getMapper(SdsCollectorMapper.class);
            boolean hasRequiredEntities = !"N".equals(mapper.getReqEntity(new SdsReqEntityDto(intent, lang, host)));
            session.close();
            return hasRequiredEntities;
        }
    }

    public SdsBackendDto getBackendDestination(String host, String service){
        try (SqlSession session = this.sessionFactory.openSession()){
            SdsCollectorMapper mapper = session.getMapper(SdsCollectorMapper.class);
            Map<String, String > params = new HashMap<>();
            params.put("host", host);
            params.put("service", service);
            SdsBackendDto backendDto = mapper.getBackendDestination(params);
            session.close();
            return backendDto;
        }
    }

    public boolean checkIntent(SdsReqEntityDto dto){
        try (SqlSession session = this.sessionFactory.openSession()){
            SdsCollectorMapper mapper = session.getMapper(SdsCollectorMapper.class);
            boolean checkIntent = 0 < mapper.checkIntent(dto);
            session.close();
            return checkIntent;
        }
    }

    public int getIntentNum(SdsReqEntityDto dto){
        try (SqlSession session = this.sessionFactory.openSession()){
            SdsCollectorMapper mapper = session.getMapper(SdsCollectorMapper.class);
            int intentNum = mapper.getIntentNum(dto);
            session.close();
            return intentNum;
        }
    }

    public int getBertIntentNum(Map<String, Object> map){
        try (SqlSession session = this.sessionFactory.openSession()){
            Integer bertIntentNum;
            SdsCollectorMapper mapper = session.getMapper(SdsCollectorMapper.class);
            bertIntentNum = mapper.getBertIntentNum(map);
            if (bertIntentNum == null) {
                bertIntentNum = 0;
            }
            session.close();
            return bertIntentNum;
        }
    }

    public Map<String, Object> getDestIntent(Map<String, Object> map){
        try (SqlSession session = this.sessionFactory.openSession()){
            String destIntent = null;
            String tmpIntent = null;
            String metaData = null;
            String tmpMetaData = null;
            int bertIntentNum = -1;
            int tmpBertIntentNum = -1;
            SdsCollectorMapper mapper = session.getMapper(SdsCollectorMapper.class);
            List<Map> destList = mapper.getDestIntent(map);

            if (destList != null) {
                for (Map logic : destList) {
                    int srcIntentNum = (Integer) logic.get("srcIntentNum");
                    if (srcIntentNum == (Integer) map.get("srcIntentNum")) {
                        if (destIntent != null) {
                            System.out.println("[getDestIntent] 데이터 오류. 로직 여러개 매칭.");
                        }
                        destIntent = logic.get("destIntent").toString();
                        metaData = logic.get("metaData").toString();
                        bertIntentNum = Integer.parseInt(logic.get("bertIntentNum").toString());
                    } else if (srcIntentNum == 0) {
                        tmpIntent = logic.get("destIntent").toString();
                        tmpMetaData = logic.get("metaData").toString();
                    }
                }

                if (destIntent == null) {
                    destIntent = tmpIntent;
                    metaData = tmpMetaData;
                    bertIntentNum = tmpBertIntentNum;
                }
            }
            if (destIntent == null) {
                destIntent = mapper.getFallbackIntent(map);
            }
            session.close();
            map.put("destIntent", destIntent);
            map.put("metaData", metaData);
            map.put("bertIntentNum", bertIntentNum);
            return map;
        }
    }

    public List<Object> getDest(String utter, String lang, String host, String srcIntent, int srcIntentNum,
                                 Map<String, Object> param, Itc.Intent engnIntent, BertItfClient itfClient){
        SdsRelation sdsRelation = new SdsRelation();
        Logger commonLogger = LoggerFactory.getLogger(SdsCollectorDao.class);
        String taskRel = "";
        //param Key : host, lang, intent, bertItfId
        SdsIntentData result = new SdsIntentData("DEFAULT");
        SdsMeta sdsMeta = null;
        String type = "일반";
        String strBertIntent = "";
        int nowBiCheck = 0;
        int bertIntentNum = getBertIntentNum(param);
        if(bertIntentNum!=0){
            try{
                param.put("bertIntentNum", bertIntentNum);
                param.put("srcIntentNum", srcIntentNum);
                Map<String, Object> destInfo = getDestIntent(param);
                Itc.Intent destIntent = Itc.Intent.newBuilder().setIntent(
                    destInfo.get("destIntent").toString()).setProb(engnIntent.getProb()).build();
                // bertIntent = 0 일시 강제테스크로 인지, meta 정보값에 넣어줌
                if (Integer.parseInt(destInfo.get("bertIntentNum").toString()) == 0) {
                    type = "강제";
                }
                commonLogger.info("[getDestIntent] srcIntent: {}({}), bertIntent: {}({}), destIntent: {}, metaData: {}",
                    srcIntent, srcIntentNum, engnIntent.getIntent(), bertIntentNum, destIntent.getIntent(), destInfo.get("metaData").toString());

                // maker에 intentRel 테이블 정보 넣기
                strBertIntent = engnIntent.getIntent();
                SdsIntentRel sdsIntentRel = new SdsIntentRel(srcIntent, engnIntent.getIntent(), destIntent.getIntent(), destInfo.get("metaData").toString(), type);
                sdsMeta = new SdsMeta(sdsIntentRel);
                result = new SdsIntentData(destIntent, String.valueOf(bertIntentNum), sdsMeta);
                taskRel = srcIntent + "->" + engnIntent.getIntent() + " = " + destIntent.getIntent();
                sdsRelation.makeRelation(Integer.parseInt(lang),Integer.toString(srcIntentNum),srcIntent,Integer.toString(bertIntentNum),engnIntent.getIntent(),destIntent.getIntent());
            }catch (Exception e){
                try{ // Intent = BertIntent
                    try (SqlSession session = this.sessionFactory.openSession()){
                        Map<String, Object> map = new HashMap<>();
                        map.put("lang",lang);
                        map.put("host",host);
                        map.put("BIName",engnIntent.getIntent());
                        SdsCollectorMapper mapper = session.getMapper(SdsCollectorMapper.class);
                        String nowDest = mapper.getDestByBertName(map);
                        Itc.Intent dstIntent = Itc.Intent.newBuilder().setIntent(
                            nowDest).setProb(engnIntent.getProb()).build();
                        result = new SdsIntentData(dstIntent, String.valueOf(bertIntentNum), sdsMeta);
                        taskRel = nowDest + "(의도) == " + nowDest + "(답변)";
                        nowBiCheck = 1;
                    }
                }catch(Exception e2){
                    System.out.println(e2);
                }
            }
        }else{
            try{ // Intent = TASK
                param = itfClient.getIntent(host, utter);
                Itc.Intent intent = (Itc.Intent) param.get("intent");
                commonLogger.info("[getDestIntent] bertIntent=destIntent: {}", intent.getIntent());
                result = new SdsIntentData(intent, "", sdsMeta);
                taskRel = param.get("intent") + "(발화) == " + param.get("intent") + "(답변)";
                nowBiCheck = 2;
            }catch (Exception e){}
        }
        if(result.getBertIntent().equals("DEFAULT")){  // fallback intent check
                result = new SdsIntentData(Itc.Intent.newBuilder().build(), String.valueOf(bertIntentNum), sdsMeta);
                taskRel = "";
        }
        List<Object> returnList = new ArrayList<>();
        returnList.add(result);
        returnList.add(taskRel);
        returnList.add(strBertIntent);
        returnList.add(nowBiCheck);
        returnList.add(sdsRelation);
        return returnList;
    }

    public List<Object> getFallback(String lang, String host, String srcIntent){
        String taskRel = "";
        String fallBackIntent = "";
        SdsIntentData result = new SdsIntentData("DEFAULT");
        SdsMeta sdsMeta = null;
        try (SqlSession session = this.sessionFactory.openSession()){
            Map<String, Object> map = new HashMap<>();
            map.put("lang",lang);
            map.put("host",host);
            SdsCollectorMapper mapper = session.getMapper(SdsCollectorMapper.class);
            fallBackIntent = mapper.getFallbackIntent(map);
            Itc.Intent destIntent;

            if(fallBackIntent == null || "".equals(fallBackIntent)){ // fallBack 없을 경우 이전 Task 반복
                destIntent = Itc.Intent.newBuilder().setIntent(srcIntent).setProb(0).build();
            }else{
                destIntent = Itc.Intent.newBuilder().setIntent(fallBackIntent).setProb(0).build();
            }
            result = new SdsIntentData(destIntent, "0", sdsMeta);
            taskRel = "";
        }catch (Exception e){
            e.printStackTrace();
        }
        List<Object> returnList = new ArrayList<>();
        returnList.add(result);
        returnList.add(taskRel);
        return returnList;
    }

    public List<Map<String, Object>> getRegexList(String host, String lang, int taskNum) {
        try (SqlSession session = this.sessionFactory.openSession()){
            Map<String, Object> map = new HashMap<>();
            map.put("host", host);
            map.put("lang", lang);
            map.put("taskNum", taskNum);

            SdsCollectorMapper mapper = session.getMapper(SdsCollectorMapper.class);
            List<Map<String, Object>> regexList = mapper.getRegexList(map);
            session.close();
            return regexList;
        }
    }

    public List<String> replaceDict(int Host, int Lang, String nowBefore, boolean nowCode){//nowCode==true 일 경우 Code 합으로 반환
        Map<String, Object> map = new HashMap<>();
        map.put("host",Host);
        map.put("lang",Lang);
        String beforeStr = addNString(nowBefore);
        String before = "";
        String after = "";
        String replaceStr = "";
        List<String> returnList = new ArrayList<>();
        try (SqlSession session = this.sessionFactory.openSession()){
            SdsCollectorMapper mapper = session.getMapper(SdsCollectorMapper.class);
            List<Map<String, Object>> replaceList = mapper.getReplaceDict(map);
            session.close();
            for(int i = 0; i<replaceList.size(); i++){
                if(nowCode){
                    before = (String) replaceList.get(i).get("Before");
                    after = (String) replaceList.get(i).get("StringCode") + ","
                            + (String) replaceList.get(i).get("IntCode1") + ","
                            + (String) replaceList.get(i).get("IntCode2") + ","
                            + (String) replaceList.get(i).get("IntCode3") + ","
                            + (String) replaceList.get(i).get("IntCode4") + ","
                            + (String) replaceList.get(i).get("IntCode5");
                }else{
                    before = (String) replaceList.get(i).get("Before");
                    after = (String) replaceList.get(i).get("After");
                }
                before = addNString(before);
                after = addYString(after);
                if(beforeStr.contains(before)&&before.length()>0){
                    if(beforeStr.indexOf(before)%2==0){
                        beforeStr = beforeStr.replace(before, after);
                        replaceStr += remYNString(before) + "<split>" + remYNString(after) + "<cell>";
                        System.out.println("REPLACED -: " + remYNString(before) + "<split>" + remYNString(after));
                    }
                }
            }
            beforeStr = remYNString(beforeStr);
            returnList.add(beforeStr);
            returnList.add(replaceStr);
            return returnList;
        }catch (Exception e){
            returnList = new ArrayList<>();
            returnList.add(nowBefore);
            returnList.add("");
            return returnList;
        }
    }

    public List<Object> replaceSdsUtter(SdsUtterCollectRequest request, boolean nowCode){
        try{
            SdsUtter sdsUtter = request.getData();
            String nowUtter = sdsUtter.getUtter();
            int nowHost = Integer.parseInt(request.getHost());
            int nowLang = Integer.parseInt(request.getLang());
            List<String> outputList = replaceDict(nowHost, nowLang, nowUtter, nowCode);
            nowUtter = outputList.get(0);
            sdsUtter.setUtter(nowUtter);
            List<Object> returnList = new ArrayList<>();
            returnList.add(sdsUtter);
            returnList.add(outputList.get(1));
            return returnList;
        }
        catch(Exception e){
            List<Object> returnList = new ArrayList<>();
            returnList.add(request.getData());
            returnList.add("");
            return returnList;
        }
    }

    public String addNString(String before){
        String newText = "";
        for(int i = 0; i<before.length(); i++){
            newText += "N" + before.charAt(i);
        }
        return newText;
    }

    public String addYString(String before){
        String newText = "";
        for(int i = 0; i<before.length(); i++){
            newText += "Y" + before.charAt(i);
        }
        return newText;
    }

    public String remYNString(String before){
        String newText = "";
        for(int i = 1; i<before.length(); i = i+2){
            newText += before.charAt(i);
        }
        return newText;
    }

    public List<SdsEntity> getEntitiesDefault(Map<String, Object> map){
        List<SdsEntity> entities = new ArrayList<>();

        try (SqlSession session = this.sessionFactory.openSession()) {
            SdsCollectorMapper mapper = session.getMapper(SdsCollectorMapper.class);
            entities = mapper.getEntitiesDefault(map);
            session.close();
        } catch(Exception e) {
        }
        return entities;
    }

}
