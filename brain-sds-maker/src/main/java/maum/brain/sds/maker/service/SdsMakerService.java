package maum.brain.sds.maker.service;

import java.util.Iterator;
import maum.brain.sds.data.dto.db.SdsReqEntityDto;
import maum.brain.sds.data.dto.general.SdsActionResponse;
import maum.brain.sds.data.dto.maker.SdsMakerRequest;
import maum.brain.sds.data.vo.*;
import maum.brain.sds.data.vo.engines.spec.SdsSpecCustomServerMap;
import maum.brain.sds.maker.client.RestApiClient;
import maum.brain.sds.maker.components.SdsMakerDao;
import maum.brain.sds.maker.data.DbResponseDto;
import maum.brain.sds.maker.data.IntentRelDto;
import maum.brain.sds.maker.data.MakerDatabaseDto;
import maum.brain.sds.data.vo.RestApiDto;
import maum.brain.sds.util.data.SdsString;
import maum.brain.sds.util.handler.SdsConciergeClient;
import maum.brain.sds.util.logger.SdsLogger;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SdsMakerService {

    private static final SdsLogger logger = new SdsLogger();

    @Autowired
    private SdsMakerDao dao;

    private SdsConciergeClient conciergeClient = new SdsConciergeClient();
    private SqlSessionFactory sessionFactory;

    @Value("${brain.sds.port.offset}")
    private int portOffset;

    public SdsMakerService() throws IOException {
        sessionFactory = new SqlSessionFactoryBuilder().build(
                Resources.getResourceAsReader("mybatis/makerMapConfig.xml")
        );
        logger.setPortOffset(portOffset);
    }

    public SdsActionResponse getResponse(SdsMakerRequest request){
        System.out.println("SdsMakerRequest : " + request);
        MakerDatabaseDto dto = null;
        SdsMemory setMemory = null;
        Map<String, String> entityMap = null;
        List<Map> reqEntities = null;
        int memorySize = request.getMemories().size() - 1;
        Map entitySet = new HashMap();
        SdsMeta sdsMeta = request.getMeta();
        int customServerTime = 0;
        String customServerEngine = "";

//        if (request.getUtter().getUtter() != null && !request.getUtter().getUtter().equals("챗봇공지사항")
//            && !request.getUtter().getUtter().equals("선톡")) {
//            entitySet.put("INPUT", request.getUtter().getUtter());
//        }
//        if (memorySize >= 0) {
//            int cnt = 1;
//            for (int i = 0; i < request.getMemories().size(); i++) {
//                if (request.getMemories().get(i).getUtter().getUtter() != null && !request
//                    .getMemories().get(i)
//                    .getUtter().getUtter().equals("챗봇공지사항") && !request.getMemories().get(i)
//                    .getUtter().getUtter()
//                    .equals("선톡")) {
//                    entitySet.put("INPUT" + cnt, request.getMemories().get(i).getUtter().getUtter());
//                    cnt++;
//                }
//            }
//        }
        try {

            if (memorySize > 0 && request.getMemories().get(memorySize).getEntitySet() != null) {
                entitySet = request.getMemories().get(memorySize).getEntitySet();
            }
//         request로 넘어온 entities entitySet에 넣어주기
            request.getEntities().removeIf(entity -> entity.getEntityValue().equals(""));
            for (SdsEntity reqEntity : request.getEntities()) {
                entitySet.put(reqEntity.getEntityName(), reqEntity.getEntityValue());
            }
            System.out.println("entitySet: " + entitySet);

            // Check current intent requires entities
            if (request.getIntent().isEntityFlag()) {
                // intent에서 필요로 하는 entity 조회해서 entitySet에 넣어주기
                reqEntities = dao.getRequiredEntities(request.getIntent().getIntent(), request.getLang(), request.getHost());
                for (Map reqEntity : reqEntities) {
                    String value = "";
                    if (reqEntity.get("DefaultValue") != null) {
                        value = reqEntity.get("DefaultValue").toString();
                    }
                    if (!entitySet.containsKey(reqEntity.get("Name"))) {
                        entitySet.put(reqEntity.get("Name"), value);
                    }
                }

                setMemory = new SdsMemory(
                        request.getUtter(),
                        request.getIntent(),
                        new SdsEntityList(request.getEntities()),
                        entitySet,
                        3,
                        request.getHost(),
                        request.getSession()
                );

                SdsActionResponse res = this.checkEntityAnswer(setMemory, reqEntities, entitySet, request.getHost(), request.getLang());
                if (res == null) {
                    setMemory.setEntitySet(entitySet);
                } else {
                    return res;
                }
            } else if (entitySet.containsValue("")) {
                // '서울이요' 같은 entity를 채우기 위한 발화가 들어온 경우
                SdsActionResponse res = this.checkEntityAnswer(setMemory, reqEntities, entitySet, request.getHost(), request.getLang());
                if (res == null) {
                    setMemory = new SdsMemory(
                            request.getUtter(),
                            request.getIntent(),
                            new SdsEntityList(request.getEntities()),
                            entitySet,
                            3,
                            request.getHost(),
                            request.getSession()
                    );
                } else {
                    return res;
                }
            }
            // hierarchy comparison
            else {
                setMemory = new SdsMemory(
                        request.getUtter(),
                        request.getIntent(),
                        new SdsEntityList(request.getEntities()),
                        entitySet,
                        3,
                        request.getHost(),
                        request.getSession()
                );
                try {
                    SdsIntent lastIntent = request.getMemories().get(request.getMemories().size() - 1).getIntent();
                    String[] splitIntent = request.getIntent().getIntent().split("\\.");
                    if (dao.checkIntent(
                            new SdsReqEntityDto(
                                    lastIntent.getIntent().concat(".".concat(splitIntent[splitIntent.length - 1])),
                                    request.getLang()
                            )
                    ))
                        dto = this.makeDto(
                                request.getHost(),
                                request.getSession(),
                                new SdsIntent(lastIntent.getIntent().concat(".".concat(splitIntent[splitIntent.length - 1]))),
                                null);
                } catch (ArrayIndexOutOfBoundsException e) {
//                logger.warn("no memory found");
                }
            }
            if (dto == null) dto = this.makeDto(request.getHost(), request.getSession(), request.getIntent(), null);

            // intentRel 조회하여 ConditionAnswer 붙여서 answer로 출력될 수 있도록 함.
            // intentRel 조회
            List<IntentRelDto> intentRel = dao.getIntentRel(
                    request.getSrcIntent(),
                    request.getIntent().getIntent(),
                    request.getHost(),
                    request.getLang(),
                    request.getBertIntent()
            );

            // intentRel 조회 결과를 이용하여 conditionAnswer text 조회
            String conditionAnswer = intentRel.isEmpty() ? null : dao.getConditionAnswer(
                    Integer.toString(intentRel.get(0).getConditionAnswer()), request.getHost(), request.getLang());
            dto.setLang(Integer.parseInt(request.getLang()));
            List<DbResponseDto> dbResponses = dao.getResponse(dto);
            System.out.println("maker dbResponses =====" + dbResponses);

            if (dbResponses.isEmpty()) {
                dbResponses.add(new DbResponseDto(null, "", "", "", "", "", "", "", "", "","","",""));
            } else {
                if (dbResponses.get(0).getAnswerUrl() != null && !dbResponses.get(0).getAnswerUrl().equals("")) {
                    long customServerStartTime = System.currentTimeMillis();
                    sdsMeta.setRestApiDto(callCustomServer(request.getUtter().getUtter(), request.getIntent().getIntent(),
                            entitySet, dbResponses.get(0).getAnswerUrl()));
                    long customServerEndTime = System.currentTimeMillis();
                    customServerTime = (int) (customServerEndTime - customServerStartTime);
                    customServerEngine = sdsMeta.getRestApiDto().getEngine();
                }
            }

            // intentRel 조회를 이용해 Answer split
            try {
                String destAnsScope = intentRel.get(0).getDestAnswerScope();
                String dbrAnswer = dbResponses.get(0).getAnswer();
                String afterAnswer = SdsString.splitIdx(dbrAnswer, destAnsScope);
                dbResponses.get(0).setAnswer(afterAnswer);
            } catch (Exception e) {
                System.out.println("dbResponse Answer Error - " + e.toString());
            }
            // conditionAnswer가 null이 아닌 경우 기존의 answer 앞에 붙여준다.
            if (conditionAnswer != null) {
                dbResponses.get(0).setAnswer(conditionAnswer + " " + dbResponses.get(0).getAnswer());
            }

            dbResponses.get(0).setAnswer(
                    fillEntities(request.getHost(), request.getLang(), entitySet, dbResponses.get(0).getAnswer()));
            setMemory.setEntitySet(entitySet);

            SdsActionResponse response = new SdsActionResponse();
            response.setAnswer(new SdsAnswer(""));

            int cnt = 0;
            for (DbResponseDto dbResponse : dbResponses) {
                if (dbResponse.getHi() != null && !"".equals(dbResponse.getHi())) {
                    if (response.getAnswer().getAnswer().length() == 0) {
                        response.setAnswer(new SdsAnswer(dbResponse.getHi()));
                        response.setFarewell(new SdsAnswer(dbResponse.getAnswer()));
                    }
                } else if ((!"".equals(dbResponse.getAnswer()) || !"".equals(dbResponse.getDisplay())) && cnt == 0) {
                    System.out.println("maker for ======" + dbResponse);
                    response.setAnswer(new SdsAnswer(dbResponse.getAnswer()));
                    response.setDisplay(dbResponse.getDisplay());
                    response.setResponseOrder(dbResponse.getResponseOrder());
                    response.setH_task(dbResponse.getH_task());
                    response.setH_item(dbResponse.getH_item());
                    response.setH_param(dbResponse.getH_param());
                    cnt++;
                } else if ("".equals(dbResponse.getAnswer()) && dbResponse.getHi() != null) {
                    response.setExpectedIntent(
                            new SdsIntent(
                                    dbResponse.getNext(),
                                    dbResponse.getName(),
                                    dbResponse.getType(),
                                    dbResponse.getUrl(),
                                    dbResponse.getDs(),
                                    dbResponse.getH_task(),
                                    dbResponse.getH_item(),
                                    dbResponse.getH_param()
                            )
                    );
                }
            }
            response.setMeta(sdsMeta);
            response.setSetMemory(setMemory);
            if (customServerTime != 0) {
                response.setCustomServerTime(Integer.toString(customServerTime));
                response.setCustomServerEngine(customServerEngine);
                response.setCustomServerAnswer(sdsMeta.getRestApiDto().getCustomServerMapList());
            }

            return this.postProcessAnswer(entityMap, response);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private boolean memIntentCheck(List<SdsMemory> memories){
        try {
            SdsMemory lastMem = memories.get(memories.size() - 1);
            return lastMem.getIntent().isEntityFlag();
        } catch (ArrayIndexOutOfBoundsException e){
            return false;
        }
    }

    private SdsActionResponse checkEntityAnswer(SdsMemory setMemory, List<Map> reqEntities, Map entitySet, String host, String lang) {
        // 비어있는 entity의 answer 조회
        if (entitySet.containsValue("") && reqEntities != null) {
            String entityName = "";
            for (Map reqEntity : reqEntities) {
                if (entitySet.get(reqEntity.get("Name")) == "") {
                    entityName = reqEntity.get("Name").toString();
                    break;
                }
            }

            if (entityName.equals("")) {
                // intent에서 필요로 하는 값이 아닌데 ""로 들어있는 entity는 잘못 들어온 entity라고 판단하여 entitySet에서 제거
                entitySet.values().removeIf(value -> value == "");
                return null;
            }

            String answer = dao.getRequestAnswer(host, lang, entityName);
            SdsActionResponse response = new SdsActionResponse();
            response.setAnswer(new SdsAnswer(answer));
            response.setSetMemory(setMemory);
            System.out.println("entityAnswer : " + response.toString());
            return response;
        } else { // 비어있는 entity가 없으면 intent에서 필요로 하는 entity들 제거
//            for (Map reqEntity : reqEntities) {
//                entitySet.remove(reqEntity.get("Name"));
//            }
            // reqEntities가 null인 경우 value가 ""인 것은 잘못 들어온 것으로 판단하여 제거
            entitySet.values().removeIf(value -> value == "");
            return null;
        }
    }

    private SdsActionResponse postProcessAnswer(Map<String, String> entityMap, SdsActionResponse response){
        try {
            String rawAnswer = response.getAnswer().getAnswer();
            Pattern entityRepPattern = Pattern.compile("\\{(.*?)\\}");
            Matcher matcher = entityRepPattern.matcher(rawAnswer);

            List<String> replaceSub = new ArrayList<>();
            while(matcher.find()){
                replaceSub.add(matcher.group());
            }

            if(replaceSub.isEmpty()) return response;

            for(String candidate: replaceSub){
                String canCut = candidate.substring(candidate.indexOf('{')+1, candidate.indexOf('}'));
                if(entityMap.containsKey(canCut.toUpperCase()))
                    rawAnswer = rawAnswer.replace(candidate, entityMap.get(canCut.toUpperCase()));
            }

            response.setAnswer(new SdsAnswer(rawAnswer));

            return response;
        } catch (NullPointerException e){
            return response;
        }
    }

    private MakerDatabaseDto makeDto(String host, String session, SdsIntent intent, Map<String, String> entityMap){
        String entityNameCs = "";
        String entityValueCs = "";
        if(entityMap != null) {
            for (Map.Entry<String, String> entry : entityMap.entrySet()) {
                entityNameCs = entityNameCs.concat(entry.getKey().concat(","));
                entityValueCs = entityValueCs.concat(entry.getValue().concat(","));
            }
        }

        try {
            return new MakerDatabaseDto(host, session, intent.getIntent(), entityNameCs, entityValueCs);
        } catch (NullPointerException e){
            return new MakerDatabaseDto("1", session, intent.getIntent(), entityNameCs, entityValueCs);
        }
    }

    private Map<String, String> setReqEntities(List<String> reqEntities, List<SdsEntity> entityList){
        Map<String, String> entityMap = new HashMap<>();
        // Search inverse for most recent entities
        try {
            for(String reqEntityName: reqEntities) {
                for (int i = entityList.size() - 1; i > -1; i--) {
                    if(entityList.get(i).getEntityName().equals(reqEntityName)) {
                        entityMap.put(reqEntityName, entityList.get(i).getEntityValue());
                        break;
                    }
                }
            }

            return entityMap;
        } catch (NullPointerException e){
            logger.info("No entities from data");
            return entityMap;
        }
    }

    private Map<String, String> recallEntities(List<String> reqEntities, Map<String, String> entityMap, List<SdsMemory> memories){
        // refer from memory if entity not enough
        if(reqEntities.size() != entityMap.size()){
            for(SdsMemory memory: memories){
                for(SdsEntity memoryEntity: memory.getEntities().getEntities()){
                    if(reqEntities.contains(memoryEntity.getEntityName())
                            && !entityMap.containsKey(memoryEntity.getEntityName()))
                        entityMap.put(memoryEntity.getEntityName(), memoryEntity.getEntityValue());
                }
            }
        }

        return entityMap;
    }

    private SdsEntityList setMemoryEntities(Map<String, String> entityMap){
        SdsEntityList retEntities = new SdsEntityList();
        try {
            for(Map.Entry<String, String> entry: entityMap.entrySet()){
                retEntities.setEntity(new SdsEntity(entry.getKey(), entry.getValue()));
            }
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }

        return retEntities;
    }

    private String fillEntities(String host, String lang, Map entitySet, String answer) {
        // answer에서 <entityName>의 형태인것들 뽑아내기 ex) {cust_name}
        int entityIndex = answer.indexOf('{');
        int lastIndex = 0;
        List<String> entities = new ArrayList<>();
        while (entityIndex != -1) {
            lastIndex = answer.indexOf('}', entityIndex + 1);
            if (lastIndex == -1) {
              break;
            }
            entities.add(answer.substring(entityIndex + 1, lastIndex));
            entityIndex = answer.indexOf('{', lastIndex + 1);
        }
        System.out.println("entity list from answer : " + entities.toString());
        System.out.println("entity info from memory : " + entitySet.toString());

        // DB의 entity정보 조회
        List<Map> dbEntityList = dao.getEntityList(host, lang);

        // answer의 entity 변수가 메모리에 있으면 메모리의 value로, 없으면 db의 default data로 replace하여 return
        for (String entity : entities) {
            if (entitySet.containsKey(entity)) { // 메모리에 있는 경우
                answer = answer.replaceAll("\\{".concat(entity).concat("}"), entitySet.get(entity).toString());
            } else {
                for (Map dbEntity : dbEntityList) { // 메모리에 없는 경우 db 조회 결과를 돌며 default value로 replace
                    if (dbEntity.get("Name").equals(entity) && dbEntity.containsKey("DefaultValue")) {
                        entitySet.put(entity, dbEntity.get("DefaultValue").toString());
                        answer = answer.replaceAll("\\{".concat(entity).concat("}"), dbEntity.get("DefaultValue").toString());
                    }
                }
            }
        }

        return answer;
    }

    private RestApiDto callCustomServer(String utter, String intent, Map entitySet, String answerUrl){
        RestApiDto restApiDto = new RestApiDto();
        List<SdsSpecCustomServerMap> sdsSpecCustomServerMapList = new ArrayList<>();
        if (answerUrl != null && !answerUrl.equals("")) {
            try {
                RestApiClient restApiClient = new RestApiClient();
                restApiDto.setUtter(utter);
                restApiDto.setIntent(intent);
                restApiDto.setEntities(convertEntitySetToList(entitySet));
                restApiDto = restApiClient.sendHttp(restApiDto, answerUrl);
                SdsEntityList apiEntities = new SdsEntityList(restApiDto.getEntities());
                apiEntities.getEntities().removeIf(entity -> entity.getEntityValue().equals(""));
                for (SdsEntity apiEntity : apiEntities.getEntities()) {
                    sdsSpecCustomServerMapList.add(new SdsSpecCustomServerMap(apiEntity.getEntityName(), apiEntity.getEntityValue()));
                    entitySet.put(apiEntity.getEntityName(), apiEntity.getEntityValue());
                }
            } catch (Exception e){
                System.out.println("call rest api error : " + e.toString());
            }
        }
        restApiDto.setCustomServerMapList(sdsSpecCustomServerMapList);
        return restApiDto;
    }

    private List<SdsEntity> convertEntitySetToList(Map entitySet){
        List<SdsEntity> entities = new ArrayList<>();
        try {
            Iterator<String> keys = entitySet.keySet().iterator();
            while (keys.hasNext()){
                String key = keys.next();
                SdsEntity entity = new SdsEntity(key, entitySet.get(key).toString());
                entities.add(entity);
            }
        } catch (Exception e){
            System.out.println("entitySet to List<SdsEntity> convert error");
        }

        return entities;
    }
}
