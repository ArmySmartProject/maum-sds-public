package maum.brain.sds.collector.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import maum.brain.bert.intent.Itc;
import maum.brain.bert.intent.Itc.Intent;
import maum.brain.sds.collector.client.*;
import maum.brain.sds.collector.component.SdsCollectorDao;
import maum.brain.sds.collector.data.SdsBackendDto;
import maum.brain.sds.collector.data.SdsIntentData;
import maum.brain.sds.collector.data.async.AsyncIntentPar;
import maum.brain.sds.collector.data.async.AsyncUtterPar;
import maum.brain.sds.collector.data.async.CollectIntentReturn;
import maum.brain.sds.collector.data.async.CollectUtterReturn;
import maum.brain.sds.collector.data.memory.SdsMemoryDataConverter;
import maum.brain.sds.collector.data.memory.SdsMemoryDto;
import maum.brain.sds.data.dto.SdsResponse;
import maum.brain.sds.data.dto.cache.SdsAddCacheRequest;
import maum.brain.sds.data.dto.cache.SdsGetCacheResponse;
import maum.brain.sds.data.dto.collect.SdsIntentCollectRequest;
import maum.brain.sds.data.dto.collect.SdsUtterCollectRequest;
import maum.brain.sds.data.dto.db.SdsReqEntityDto;
import maum.brain.sds.data.dto.general.SdsActionResponse;
import maum.brain.sds.data.dto.maker.SdsMakerRequest;
import maum.brain.sds.data.vo.*;
import maum.brain.sds.data.vo.engines.SdsEngines;
import maum.brain.sds.data.vo.engines.SdsModel;
import maum.brain.sds.data.vo.engines.spec.*;
import maum.brain.sds.data.vo.relations.SdsRelation;
import maum.brain.sds.memory.Memory;
import maum.brain.sds.util.data.SdsInquiryData;
import maum.brain.sds.util.data.SdsOrderData;
import maum.brain.sds.util.handler.SdsConciergeClient;
import maum.brain.sds.util.logger.SdsLogger;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Service
public class SdsCollectorService {

    private static final SdsLogger logger = new SdsLogger();
    private static final Logger commonLogger = LoggerFactory.getLogger(SdsCollectorService.class);

    private SdsConciergeClient conciergeClient = new SdsConciergeClient();

    @Autowired
    private BertItfClient itfClient;

    @Autowired
    private BertNerClient nerClient;

    @Autowired
    private SdsMakerClient makerClient;

    @Autowired
    private SdsCacheClient cacheClient;

    @Autowired
    private SdsMemoryClient memoryClient;

    @Autowired
    private NQAClient nqaClient;

    @Autowired
    private SdsCollectorDao dao;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${brain.sds.port.offset}")
    private int portOffset;

    @PostConstruct
    private void postConstruct(){
        makerClient.setPortOffset(portOffset);
        memoryClient.setPortOffset(portOffset);
        nqaClient.setDestinationInfo();
        logger.setPortOffset(portOffset);
    }

    // Collector -> run/utter
    public CollectUtterReturn collect(SdsUtterCollectRequest request, boolean detail) throws JSONException {
        List<Object> outputList = dao.replaceSdsUtter(request, false);
        SdsUtter utter = (SdsUtter) outputList.get(0);
        String newStr = utter.getUtter();
        newStr = newStr.replace("\n","").
            replace("<br>","").trim();
        utter.setUtter(newStr);
        String srcIntent = "";
        String bertIntent = "";
        SdsIntent intent = new SdsIntent();
        double prob = 0.0;
        SdsMeta sdsMeta = null;
        String logAnswer = "";
        String strBertIntent = "";
        boolean nowGotCache = false;
        SdsEngines sdsEngines = new SdsEngines();
        SdsRelation sdsRelation = new SdsRelation();

        // debug 용 json 생성 (IntentLog 테이블에 debugJson으로 저장)
        JSONObject jsonDebugData = new JSONObject();
        try{
            jsonDebugData.put("type","utter");
            jsonDebugData.put("input", utter.getUtter());
            jsonDebugData.put("replace", outputList.get(1));
        }catch(Exception e){}

        // 메모리 정보 가져오기
        this.initMemoryUser(request.getHost(), request.getSession());
        List<SdsMemory> memoryList = this.getAllMemories(request.getHost(), request.getSession());

        // 현 session의 첫 대화
        if(memoryList.isEmpty()){
            SdsIntentData sdsIntentData = new SdsIntentData();
            try{
                sdsIntentData = this.getIntent(utter.getUtter(), request.getHost(), request.getLang(), memoryList);
                sdsEngines = sdsIntentData.getSdsEngines();
                sdsRelation = sdsIntentData.getSdsRelation();
                strBertIntent = sdsIntentData.getStrBertIntent();
                jsonDebugData.put("engine", sdsIntentData.getEngine());
                jsonDebugData.put("model", sdsIntentData.getModel());
                jsonDebugData.put("taskRel", sdsIntentData.getTaskRel());
                jsonDebugData.put("sdsLog",sdsIntentData.getLogString());
                sdsMeta = sdsIntentData.getMeta();
                bertIntent = sdsIntentData.getBertIntent()!=null ? sdsIntentData.getBertIntent() : bertIntent;
                Intent preIntent = sdsIntentData.getIntent();
                prob = preIntent.getProb();
                intent = new SdsIntent(
                    preIntent.getIntent()
                );
            }catch (Exception e){
                try{
                    jsonDebugData.put("ERROR", sdsIntentData.getError());
                }catch (Exception e2){}
            }
        }
        // 현 session의 이전 대화가 있을 때
        else {
            SdsMemory memoryLast = memoryList.get(memoryList.size() - 1);
            try {
                // 이전 task
                srcIntent = memoryLast.getIntent().getIntent();
                jsonDebugData.put("prevIntent", srcIntent);
            } catch (Exception e){
                jsonDebugData.put("ERROR","Memory intent Error");
            }

            try{
                SdsIntentData intentData = this.getIntent(utter.getUtter(), request.getHost(), request.getLang(), memoryList);
                sdsEngines = intentData.getSdsEngines();
                sdsRelation = intentData.getSdsRelation();
                strBertIntent = intentData.getStrBertIntent();
                sdsMeta = intentData.getMeta();
                bertIntent = intentData.getBertIntent()!=null ? intentData.getBertIntent() : bertIntent;
                prob = intentData.getIntent().getProb();
                intent = new SdsIntent(intentData.getIntent().getIntent());
                try {
                    strBertIntent = intentData.getStrBertIntent();
                    jsonDebugData.put("bertIntent", bertIntent);
                    jsonDebugData.put("engine", intentData.getEngine());
                    jsonDebugData.put("model", intentData.getModel());
                    jsonDebugData.put("taskRel", intentData.getTaskRel());
                    jsonDebugData.put("sdsLog",intentData.getLogString());
                } catch(Exception e){}

            }catch (Exception e){
                System.out.println("intentDataError : ");
                e.printStackTrace();
                try{
                    jsonDebugData.put("ERROR","intentData Error");
                }catch (Exception e2){}
            }
        }

        /* ------- ENTITY 관련 code START ------- */

        SdsEntityList entities = request.getEntities() == null ? new SdsEntityList() : new SdsEntityList(request.getEntities());

        Boolean entityFlag = dao.hasRequiredEntities(intent.getIntent(), request.getLang(), request.getHost());
        intent.setEntityFlag(entityFlag);

        // bert-NER 통해서 entity값 받아오기
        try {
            SdsEntityList bertEntities = this.getEntity(utter, request.getHost());
            if (bertEntities.getEntities() != null) {
                for (SdsEntity bertEntity : bertEntities.getEntities()) {
                    if (bertEntity.getEntityValue() != null && !bertEntities.getEntities().equals("")) {
                        entities.setEntity(bertEntity);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("[BERT-NER] failed get bert entities!");
            System.out.println(e);
        }

        /* ------- ENTITY 관련 code END ------- */

        SdsActionResponse cacheResponse = new SdsActionResponse();
        if(!detail){
            /*----------       캐싱 서버      ---------*/
            SdsAddCacheRequest sdsGetCacheRequest = new SdsAddCacheRequest(
                request.getHost(),
                srcIntent,
                strBertIntent,
                "",
                true,
                request.getLang()
            );
            try{
                if(sdsGetCacheRequest.getPrevIntent().trim().length()>0 &&
                    sdsGetCacheRequest.getBertIntent().trim().length()>0){
                    SdsGetCacheResponse sdsGetCacheResponse = cacheClient.sendHttpGetCache(sdsGetCacheRequest);
                    if(sdsGetCacheResponse.getCacheNo()!=-1){
                        cacheResponse = objectMapper.readValue(sdsGetCacheResponse.getAnswer(), SdsActionResponse.class);
                        nowGotCache = true;
                    }
                }
            }catch (Exception e){
                nowGotCache = false;
            }
            /*----------     캐싱 서버 끝     ---------*/
        }


        SdsResponse makerResponse = null;
        if(nowGotCache){
            makerResponse = cacheResponse;
        }else{
            makerResponse = makerClient.getDecision(
                new SdsMakerRequest(
                    request.getHost(),
                    request.getSession(),
                    srcIntent,
                    intent,
                    utter,
                    entities.getEntities(),
                    memoryList,
                    request.getLang(),
                    bertIntent,
                    sdsMeta
                )
            );
        }


        // Answer가 없을 때에 Fallback 으로 넘겨주기
        try{
            SdsActionResponse tmpRes = ((SdsActionResponse) makerResponse);
            // bert 에서 없는 의도로 맵핑 했을 경우도 Fallback 으로 넘겨주기 ( makerResponse : answer=SdsAnswer{answer='null'} )
            if((tmpRes.getAnswer()==null || tmpRes.getAnswer().getAnswer()==null) &&
                    (tmpRes.getSetMemory().getIntent().getIntent() == null || tmpRes.getMeta() == null)
            ){
                throw new Exception();
            }
        }catch (Exception answerE){
            try{
                long fallbackStartTime = System.currentTimeMillis();
                SdsIntentData intentData = this.getFallback(request.getHost(), request.getLang(), srcIntent);
                sdsMeta = intentData.getMeta();
                bertIntent = intentData.getBertIntent()!=null ? intentData.getBertIntent() : bertIntent;
                prob = intentData.getIntent().getProb();
                long fallbackEndTime = System.currentTimeMillis();
                SdsModel sdsModel = new SdsModel("fallback",(int)(fallbackEndTime-fallbackStartTime),false);
                sdsModel.setSpecific(
                    new SdsSpecFallback(
                        intentData.getIntent().getIntent()
                    )
                );
                sdsEngines.addEngine(sdsModel);
                intent = new SdsIntent(intentData.getIntent().getIntent());
                try {
                    jsonDebugData.put("bertIntent", bertIntent);
                    jsonDebugData.put("engine", intentData.getEngine());
                    jsonDebugData.put("model", intentData.getModel());
                    jsonDebugData.put("taskRel", intentData.getTaskRel());
                } catch(Exception e){}
            }catch (Exception e){ e.printStackTrace(); }
            makerResponse = makerClient.getDecision(
                new SdsMakerRequest(
                    request.getHost(),
                    request.getSession(),
                    srcIntent,
                    intent,
                    utter,
                    entities.getEntities(),
                    memoryList,
                    request.getLang(),
                    bertIntent,
                    sdsMeta
                )
            );
        }

        try{
            logAnswer = ((SdsActionResponse) makerResponse).getAnswer().getAnswer();
        }catch (Exception e){
            // 답변이 오지 않고 콜백이 없을 경우
        }

        jsonDebugData.put("bertIntent",bertIntent);
        if (sdsMeta != null) {
            jsonDebugData.put("metaData", sdsMeta.getIntentRel().getMetaData());
        } else {
            jsonDebugData.put("metaData", "");
        }

        boolean nowLoggerAnswer = false;
        boolean nowLoggerError  = false;
        SdsUtter nowAnswerLogSdsUtter = null;
        SdsAnswer nowAnswerLogSdsAnswer = null;
        SdsResponse nowErrorLogSdsResponse = null;

        if(makerResponse.getClass().equals(SdsActionResponse.class)){
            try{
                nowLoggerAnswer = true;
                nowAnswerLogSdsUtter = request.getData();
                nowAnswerLogSdsAnswer = ((SdsActionResponse) makerResponse).getAnswer();
            }catch (Exception e){
                nowLoggerAnswer = false;
                jsonDebugData.put("ERROR","logger Answer Error");
            }
            try{
                SdsMemory sdsMemory = ((SdsActionResponse) makerResponse).getSetMemory();
                sdsMemory.setSession(request.getSession());
                this.setMemory(
                    sdsMemory, request.getHost(), request.getSession());
            }catch (Exception e){
                jsonDebugData.put("ERROR","Maker memory Error");
            }
        } else {
            nowLoggerError = true;
            nowErrorLogSdsResponse = makerResponse;
        }
        try{
            if(jsonDebugData.get("ERROR").toString().length()>0){
                System.out.println("ERROR DEBUG : " + jsonDebugData.get("ERROR"));
            }
        }catch (Exception e){

        }
        try{
            jsonDebugData.put("prob",prob);
        }catch (Exception e){

        }

        try{
            ((SdsActionResponse) makerResponse).setJsonDebug(jsonDebugData.toString());
        }catch (Exception e){

        }
        AsyncUtterPar asyncUtterPar = new AsyncUtterPar(
            request, intent, prob, new JSONObject(jsonDebugData.toString()), logAnswer,entities, nowLoggerAnswer,
            nowAnswerLogSdsUtter, nowAnswerLogSdsAnswer,
            nowLoggerError, nowErrorLogSdsResponse
        );
        SdsAddCacheRequest sdsAddCacheRequest = new SdsAddCacheRequest();
        boolean nowAddCache = false;
        try{//sdsAddCacheRequest 작성부분
            sdsAddCacheRequest.setHost(request.getHost());
            sdsAddCacheRequest.setPrevIntent(srcIntent);
            sdsAddCacheRequest.setBertIntent(strBertIntent);
            sdsAddCacheRequest.setAnswer(objectMapper.writeValueAsString(makerResponse));
            sdsAddCacheRequest.setNowUtter(true);
            sdsAddCacheRequest.setLang(request.getLang());
            nowAddCache = true;
        }catch (Exception e){
            nowAddCache = false;
        }
        try{ // Custom-server를 사용하였을 경우 캐시 프로세스에서 제외
            if((((SdsActionResponse) makerResponse).getMeta().getRestApiDto()) != null) {
                nowAddCache = false;
            }
        }catch (NullPointerException e){
            nowAddCache = true;
        }catch (Exception e){
            nowAddCache = false;
        }
        if(nowGotCache){
            nowAddCache = false;
        }

        if(detail){
            if(makerResponse.getClass().equals(SdsActionResponse.class)){
                ((SdsActionResponse) makerResponse).setEngines(sdsEngines);
            }
            if(makerResponse.getClass().equals(SdsActionResponse.class)){
                ((SdsActionResponse) makerResponse).setRelation(sdsRelation);
            }
            try{
                if(((SdsActionResponse) makerResponse).getCustomServerEngine().length()>0){
                    SdsModel sdsModel = new SdsModel("Custom Server",Integer.parseInt(((SdsActionResponse) makerResponse).getCustomServerTime()),false);
                    sdsModel.setSpecific(
                        new SdsSpecCustomServer(
                            ((SdsActionResponse) makerResponse).getCustomServerEngine(),
                            ((SdsActionResponse) makerResponse).getCustomServerAnswer()
                        )
                    );
                    sdsEngines.addEngine(sdsModel);
                }
            }catch (Exception e){

            }
            // SdsActionResponse : custom server time value 삭제 (constructor)
            if(makerResponse.getClass().equals(SdsActionResponse.class)){
                SdsActionResponse refinedMakerResponse = new SdsActionResponse((SdsActionResponse)makerResponse);
                makerResponse = refinedMakerResponse;
            }
        }else{
            try{
                ((SdsActionResponse) makerResponse).setEngines(null);
            }catch (Exception e){

            }
        }
        if(detail) nowAddCache = false;
        CollectUtterReturn collectUtterReturn = new CollectUtterReturn(makerResponse, asyncUtterPar,sdsAddCacheRequest, nowAddCache);
        return collectUtterReturn;
    }

    // Collector -> run/intent
    public CollectIntentReturn collect(SdsIntentCollectRequest request,boolean detail) throws JSONException {
        long startIntentTime = System.currentTimeMillis();
        SdsIntent intent = request.getData();
        SdsEngines sdsEngines = new SdsEngines();
        SdsResponse makerResponse = new SdsResponse() {
            @Override
            public String toString() {
                return super.toString();
            }
        };
        SdsMeta sdsMeta = new SdsMeta(); //custom server 통신을 하기 위해 sdsMeta 생성
        JSONObject jsonDebugData = new JSONObject();

        boolean nowLoggerError  = false;
        SdsResponse nowErrorLogSdsResponse = null;
        AsyncIntentPar asyncIntentPar = null;

        String logAnswer = "";
        boolean nowGotCache = false;
        boolean nowEtc = false; // 선톡, 챗봇공지사항, 처음으로 Intent일때 true

        try{
            jsonDebugData.put("type","Intent");
            jsonDebugData.put("input","");
        }catch(Exception e){}

        if (request.getData().getIntent().contains("pickupTime")) {
            try{
                this.sendToOrder(request);
            }catch (Exception e){
                jsonDebugData.put("ERROR","sendToOrder API Error");
            }

        } else if (request.getData().getIntent().contains("inquiryMsg")) {
            try{
                this.sendToEmail(request);
            }catch (Exception e){
                jsonDebugData.put("ERROR","sendToEmail API Error");
            }
        } else {
            intent.setEntityFlag(dao.hasRequiredEntities(intent.getIntent(), request.getLang(), request.getHost()));
            Memory.MemMessage memMessage = memoryClient.initUser(request.getHost(), request.getSession());
            try{
                if(memMessage.getStatus() != 200){
                    logger.warn("Memory Engine Failure");
                }
            }catch (Exception e){
                // First Memory
            }

            SdsEntityList entities = request.getEntities() == null ? new SdsEntityList() : new SdsEntityList(request.getEntities());

            /* ------- ENTITY 관련 code START ------- */
            Boolean entityFlag = dao.hasRequiredEntities(intent.getIntent(), request.getLang(), request.getHost());
            intent.setEntityFlag(entityFlag);

            // entities 테이블에서 default entity값 받아오기
            try {
                if (entityFlag) {
                    int intentNum = dao.getIntentNum(new SdsReqEntityDto(intent.getIntent(), request.getLang(), request.getHost()));
                    Map <String, Object> param = new HashMap<>();
                    param.put("intentNo", intentNum);
                    param.put("lang", Integer.parseInt(request.getLang()));
                    param.put("host", Integer.parseInt(request.getHost()));
                    List<SdsEntity> getE = dao.getEntitiesDefault(param);
                    entities.setEntities(getE);
                }

            } catch (Exception e) {
                System.out.println("[BERT-NER] failed get bert entities!");
                System.out.println(e);
            }
            /* ------- ENTITY 관련 code END ------- */

            List<SdsMemory> memoryList = this.getAllMemories(request.getHost(), request.getSession());

            String srcIntent = "";
            if (memoryList.size() > 1) {
                SdsMemory memoryLast = memoryList.get(memoryList.size() - 1);
                srcIntent = memoryLast.getIntent().getIntent();
                try{
                    jsonDebugData.put("prevIntent",srcIntent);
                }catch(Exception e){}
            }

            SdsActionResponse cacheResponse = new SdsActionResponse();
            if(intent.getIntent().equals("처음으로")||intent.getIntent().equals("챗봇공지사항")||intent.getIntent().equals("선톡")){
                nowGotCache = false;
                nowEtc = true;
            }else{
                if(!detail){
                    /*----------       캐싱 서버      ---------*/
                    SdsAddCacheRequest sdsGetCacheRequest = new SdsAddCacheRequest(
                        request.getHost(),
                        "-1",
                        intent.getIntent(),
                        "",
                        false,
                        request.getLang()
                    );
                    try{
                        if(sdsGetCacheRequest.getPrevIntent().trim().length()>0 &&
                            sdsGetCacheRequest.getBertIntent().trim().length()>0){
                            SdsGetCacheResponse sdsGetCacheResponse = cacheClient.sendHttpGetCache(sdsGetCacheRequest);
                            if(sdsGetCacheResponse.getCacheNo()!=-1){
                                cacheResponse = objectMapper.readValue(sdsGetCacheResponse.getAnswer(), SdsActionResponse.class);
                                nowGotCache = true;
                            }
                        }
                    }catch (Exception e){
                        nowGotCache = false;
                    }
                    /*----------     캐싱 서버 끝     ---------*/
                }
            }


            if(nowGotCache){
                makerResponse = cacheResponse;
            }else{
                makerResponse = this.makerClient.getDecision(
                    new SdsMakerRequest(
                        request.getHost(),
                        request.getSession(),
                        srcIntent,
                        request.getData(),
                        new SdsUtter(intent.getIntent().replace('.', ' ')),
                        entities.getEntities(),
                        memoryList,
                        request.getLang(),
                        "", //bertIntent
                        sdsMeta //custom server 통신을 하기 위해 넘김
                    )
                );
            }


            if(makerResponse.getClass().equals(SdsActionResponse.class)){
                SdsMemory sdsMemory = ((SdsActionResponse) makerResponse).getSetMemory();
                sdsMemory.setSession(request.getSession());
                this.setMemory(sdsMemory, request.getHost(), request.getSession());
            }
            else {
                nowLoggerError = true;
                nowErrorLogSdsResponse = makerResponse;
            }
            try{
                jsonDebugData.put("prob",1.0);
            }catch (Exception e){

            }
        }

        try{
            ((SdsActionResponse) makerResponse).setJsonDebug(jsonDebugData.toString());
        }catch (Exception e){

        }
        try{
            logAnswer = ((SdsActionResponse) makerResponse).getAnswer().getAnswer();
        }catch (Exception e){
            // 답변이 오지 않고 콜백이 없을 경우
        }
        try{
            asyncIntentPar = new AsyncIntentPar(request.getSession(), new SdsUtter(request.getData().getIntent())
                , intent, Integer.parseInt(request.getHost()), request.getLang(), 1.0, request.getJsonData(),
                jsonDebugData.toString(),  nowLoggerError, nowErrorLogSdsResponse, logAnswer);
        }catch (Exception e){

        }
        SdsAddCacheRequest sdsAddCacheRequest = new SdsAddCacheRequest();
        boolean nowAddCache = false;
        try{ //sdsAddCacheRequest 작성부분
            sdsAddCacheRequest.setHost(request.getHost());
            sdsAddCacheRequest.setPrevIntent("-1");
            sdsAddCacheRequest.setBertIntent(intent.getIntent());
            sdsAddCacheRequest.setAnswer(objectMapper.writeValueAsString(makerResponse));
            sdsAddCacheRequest.setNowUtter(false);
            sdsAddCacheRequest.setLang(request.getLang());
            nowAddCache = true;
        }catch (Exception e){
            nowAddCache = false;
        }

        try{ // Custom-server를 사용하였을 경우 캐시 프로세스에서 제외
            if((((SdsActionResponse) makerResponse).getMeta().getRestApiDto()) != null) {
                nowAddCache = false;
            }
        }catch (NullPointerException e){
            nowAddCache = true;
        }catch (Exception e){
            nowAddCache = false;
        }

        if(nowGotCache){
            nowAddCache = false;
        }
        if(nowEtc){
            nowAddCache = false;
        }
        if(detail){
            if(makerResponse.getClass().equals(SdsActionResponse.class)){
                long endIntentTime = System.currentTimeMillis();
                sdsEngines.setIntent(intent.getIntent());
                sdsEngines.setModel("Intent");
                SdsModel sdsModel = new SdsModel();
                sdsModel.setModel("Intent");
                sdsModel.setTime((int)(endIntentTime - startIntentTime));
                sdsModel.setPass(false);
                sdsEngines.addEngine(
                    sdsModel
                );
                ((SdsActionResponse) makerResponse).setEngines(sdsEngines);
            }

            try{
                if(((SdsActionResponse) makerResponse).getCustomServerEngine().length()>0){
                    SdsModel sdsModel = new SdsModel("Custom Server",Integer.parseInt(((SdsActionResponse) makerResponse).getCustomServerTime()),false);
                    sdsModel.setSpecific(
                      new SdsSpecCustomServer(
                        ((SdsActionResponse) makerResponse).getCustomServerEngine(),
                        ((SdsActionResponse) makerResponse).getCustomServerAnswer()
                      )
                    );
                    sdsEngines.addEngine(sdsModel);
                }
            }catch (Exception e){

            }
            // SdsActionResponse : custom server time value 삭제 (constructor)
            if(makerResponse.getClass().equals(SdsActionResponse.class)){
                SdsActionResponse refinedMakerResponse = new SdsActionResponse((SdsActionResponse)makerResponse);
                makerResponse = refinedMakerResponse;
            }
        }else{
            try{
                ((SdsActionResponse) makerResponse).setEngines(null);
            }catch (Exception e){

            }
        }
        if(detail) nowAddCache = false;
        CollectIntentReturn collectIntentReturn = new CollectIntentReturn(makerResponse, asyncIntentPar, sdsAddCacheRequest,nowAddCache);
        return collectIntentReturn;
    }


    private void sendToEmail(SdsIntentCollectRequest request) {

        Gson gson = new Gson();
        SdsInquiryData sdsConciergeRequest = gson.fromJson(request.getData().getIntent(), SdsInquiryData.class);
        String sdsIntent;

        if (sdsConciergeRequest.getInquiryMsg().isEmpty()) {
            sdsIntent = "예약하기";
        } else {
            sdsIntent = "문의하기";
        }

        sdsConciergeRequest.setHost(request.getHost());
        sdsConciergeRequest.setIntent(sdsIntent);

        // 메일 발송 결과도 DB에 적재
        Boolean sendRes = conciergeClient.sendToEmail(gson.toJson(sdsConciergeRequest));
        sdsConciergeRequest.setSendResult(sendRes);

        logger.intent(request.getSession(), new SdsUtter(request.getData().getIntent())
            , new SdsIntent(sdsIntent), Integer.parseInt(request.getHost()), request.getLang(), 1.0, gson.toJson(sdsConciergeRequest), "");
    }

    private void sendToOrder(SdsIntentCollectRequest request) {

        Gson gson = new Gson();
        SdsOrderData sdsOrderRequest = gson.fromJson(request.getData().getIntent(), SdsOrderData.class);

        sdsOrderRequest.setHost(request.getHost());
        sdsOrderRequest.setIntent("주문하기");

        // 메일 발송 결과도 DB에 적재
        Boolean sendRes = conciergeClient.sendToOrder(gson.toJson(sdsOrderRequest));
        sdsOrderRequest.setSendResult(sendRes);

        logger.intent(request.getSession(), new SdsUtter(request.getData().getIntent())
            , new SdsIntent("주문하기"), Integer.parseInt(request.getHost()), request.getLang(), 1.0, gson.toJson(sdsOrderRequest), "");
    }

    private SdsIntentData getIntent(String utter, String host, String lang, List<SdsMemory> memoryList){
        String engine   = "";
        String model    = "";
        String error    = "";
        String logString= "";
        boolean nowBackendInfo = true;
        long startTime = System.currentTimeMillis();
        SdsEngines retEngines = new SdsEngines();

        SdsBackendDto destinationInfo = dao.getBackendDestination(host, "ITF");

        // 엔진에서 분류한 인텐트명. by regex, nqa, bert
        Intent engnIntent = null;

        String srcIntent = "empty";
        int srcIntentNum;
        if(memoryList.isEmpty()){
            srcIntentNum = 0;
        } else {
            SdsMemory memoryLast = memoryList.get(memoryList.size() - 1);
            srcIntent = memoryLast.getIntent().getIntent();
            srcIntentNum = dao.getIntentNum(new SdsReqEntityDto(srcIntent, lang, host));
        }
        // 정규식 체크
        long regexStartTime = System.currentTimeMillis();
        Map<String, Object> findReg = this.findAllRegex(host, utter, lang, srcIntentNum);
        logString += findReg.get("logString");
        if ((Boolean)findReg.get("isFind")) {
            String intentName = findReg.get("intent").toString();
            engnIntent = Intent.newBuilder().setIntent(intentName).setProb(1).build();
            try {
                engine  = "RegEx";
                model   = findReg.get("Regex").toString();
            } catch (Exception e) {
                logger.warn(e.toString());
            }
            long regexEndTime = System.currentTimeMillis();
            SdsModel sdsModel = new SdsModel("Regex",(int)(regexEndTime-regexStartTime),false);
            sdsModel.setSpecific(
                new SdsSpecRegex(
                    true,
                    findReg.get("Regex").toString(),
                    findReg.get("intent").toString()
                )
            );
            retEngines.setModel("Regex");
            retEngines.setIntent(findReg.get("intent").toString());
            retEngines.addEngine(sdsModel);
        }else{
            long regexEndTime = System.currentTimeMillis();
            SdsModel sdsModel = new SdsModel("Regex",(int)(regexEndTime-regexStartTime),true);
            sdsModel.setSpecific(
                new SdsSpecRegex(
                    false,
                    "",
                    ""
                )
            );
            retEngines.addEngine(sdsModel);
        }

        // NQA
        long nqaStartTime = System.currentTimeMillis();
        if (engnIntent == null && nqaClient.getNqaHost() != null && nqaClient.getNqaHost().length()>2) {
            List<Object> nqaList = nqaClient.getIntent(host, utter, srcIntent);
            Intent nqaIntent = (Intent) nqaList.get(0);
            logString += "\n" + nqaList.get(1);
            if (!nqaIntent.getIntent().isEmpty()) {
                engnIntent = nqaIntent;
                engine = "NQA";
                model = "";
                long nqaEndTime = System.currentTimeMillis();
                SdsModel sdsModel = new SdsModel("NQA", (int)(nqaEndTime-nqaStartTime),false);
                sdsModel.setSpecific(
                    new SdsSpecNqa(
                        ((Intent) nqaList.get(0)).getIntent(), (float) nqaList.get(2)
                    )
                );
                retEngines.setModel("NQA");
                retEngines.setIntent(((Intent) nqaList.get(0)).getIntent());
                retEngines.addEngine(sdsModel);
            }else{
                try{
                    long nqaEndTime = System.currentTimeMillis();
                    SdsModel sdsModel = new SdsModel("NQA", (int)(nqaEndTime-nqaStartTime),true);
                    sdsModel.setSpecific(
                        new SdsSpecNqa(
                            "", (float) nqaList.get(2)
                        )
                    );
                    retEngines.addEngine(sdsModel);
                }catch (Exception e){
                    long nqaEndTime = System.currentTimeMillis();
                    SdsModel sdsModel = new SdsModel("NQA", (int)(nqaEndTime-nqaStartTime),true);
                    sdsModel.setSpecific(
                        new SdsSpecNqa(
                            "", 0f
                        )
                    );
                    retEngines.addEngine(sdsModel);
                }

            }
        }

        // BERT
        long bertStartTime = System.currentTimeMillis();
        SdsIntentData result;
        Map<String, Object> param = new HashMap<>();
        if(nowBackendInfo){
            if (engnIntent == null) {
                try{
                    param = itfClient.getIntent(destinationInfo, utter);
                    engnIntent = (Intent) param.get("intent");
                    engine = "BERT";
                    model = "";
                    logString += "\n" + param.get("logString");
                    long bertEndTime = System.currentTimeMillis();
                    SdsModel sdsModel = new SdsModel("BERT", (int)(bertEndTime-bertStartTime), false);
                    sdsModel.setSpecific(
                        new SdsSpecBert(
                            ((Intent) param.get("intent")).getIntent(),
                            (float) param.get("prob")
                        )
                    );
                    retEngines.setModel("BERT");
                    retEngines.setIntent(((Intent) param.get("intent")).getIntent());
                    retEngines.addEngine(sdsModel);
                }catch (Exception e){
                    long bertEndTime = System.currentTimeMillis();
                    SdsModel sdsModel = new SdsModel("BERT", (int)(bertEndTime-bertStartTime), true);
                    sdsModel.setSpecific(
                        new SdsSpecBert(
                            "bert error",
                            0f
                        )
                    );
                    retEngines.addEngine(sdsModel);
                }
            }
        }

        // FROM engnIntent (bertIntent) GET Destination TASK (destIntent)
        param.put("host", host);
        param.put("lang", lang);
        try{
            param.put("intent", engnIntent.getIntent());
        }catch (Exception e){
            param.put("intent", "");
        }
        try{
            param.put("bertItfId", destinationInfo.getId());
        }catch (Exception e){
            param.put("bertItfId", "");
        }

        List<Object> destList = dao.getDest(utter,lang,host,srcIntent,srcIntentNum,param,engnIntent,itfClient);
        result = (SdsIntentData) destList.get(0);
        // fallback intent check
        if(result.getLogString() == "BERT-fallback") engine = "fallback";
        long endTime = System.currentTimeMillis();
        commonLogger.info("[getIntent 시간 측정 / 임시로그] host: " + host + ", getIntent() :" + (endTime - startTime) + " ms");
        logString += "\n[getIntent 시간 측정 / 임시로그] host: " + host + ", getIntent() :" + (endTime - startTime) + " ms";

        if((int)destList.get(3)==1){
            try{
                SdsModel sdsModel = new SdsModel("biCheck", 0, false);
                sdsModel.setSpecific(
                    new SdsSpecBiCheck(
                        engnIntent.getIntent(),
                        "bertIntent"
                    )
                );
                retEngines.setModel("biCheck");
                retEngines.setIntent(engnIntent.getIntent());
                retEngines.addEngine(sdsModel);
            }catch (Exception e){}
        }
        if((int)destList.get(3)==2){
            try{
                SdsModel sdsModel = new SdsModel("biCheck", 0, false);
                sdsModel.setSpecific(
                    new SdsSpecBiCheck(
                        engnIntent.getIntent(),
                        "utter"
                    )
                );
                retEngines.setModel("biCheck");
                retEngines.setIntent(engnIntent.getIntent());
                retEngines.addEngine(sdsModel);
            }catch (Exception e){}
        }

        result.addEngineModel(engine,model);
        result.addError(error);
        result.setTaskRel((String) destList.get(1));
        result.setLogString(logString);
        result.setStrBertIntent((String) destList.get(2));
        result.setSdsEngines(retEngines);
        result.setSdsRelation((SdsRelation) destList.get(4));
        return result;

    }

    private SdsIntentData getFallback(String host, String lang, String srcIntent){
        String engine   = "fallback";
        String model    = "fallback";
        String error    = "fallback";
        List<Object> destList = dao.getFallback(lang,host, srcIntent);
        SdsIntentData result;
        result = (SdsIntentData) destList.get(0);
        result.addEngineModel(engine,model);
        result.addError(error);
        result.setTaskRel("fallback");
        return result;

    }

    private SdsEntityList getEntity(SdsUtter utter, String host){
        return nerClient.getEntities(utter.getUtter(), host);
    }

    private void initMemoryUser(String host, String session){
        try{
            Memory.MemMessage memMessage = memoryClient.initUser(host, session);
            if(memMessage.getStatus() != 200){
                logger.warn("Memory Engine Failure");
            }
        } catch (Exception e){
            logger.warn("Memory Engine Failure");
        }
    }

    private List<SdsMemory> getAllMemories(String host, String session){
        List<SdsMemory> memoryList = new ArrayList<>();
        try {
            Iterator<Memory.MemTurn> memories = memoryClient.getAllMemory(host, session);
            while (memories.hasNext()){
                Memory.MemTurn memory = memories.next();
                if(!(memory.getUtter().equals("선톡")) && !(memory.getUtter().equals("공지사항")) && !(memory.getUtter().equals("챗봇공지사항"))) memoryList.add(SdsMemoryDataConverter.convert(memory));
            }
        } catch (Exception e){
            logger.warn("Memory Engine Failure");
        }
        return memoryList;
    }

    private void setMemory(SdsMemory aftSetMem, String host, String session){
        if (aftSetMem.getEntitySet() == null) {
            aftSetMem.setEntitySet(new HashMap());
        }
        if(aftSetMem != null)
            memoryClient.addMemory(new SdsMemoryDto(aftSetMem));
        else
            memoryClient.clearMemory(host, session);

    }

    private Map<String, Object> findAllRegex(String host, String utter, String lang, int taskNum) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> regexList = dao.getRegexList(host, lang, 0);
        String regexLogString = "";

        result.put("isFind", false);
        String matchingRegex = null;

        for (Map<String, Object> regex : regexList) {
            try {
                Pattern pattern = Pattern.compile(regex.get("Regex").toString());
                Matcher matcher = pattern.matcher(utter);

                if (matcher.find()) {
                    matchingRegex = regex.get("Regex").toString();
                    result.put("task", regex.get("Task"));
                    result.put("isFind", true);
                    result.put("intent", regex.get("Intent").toString());
                    result.put("Regex",matchingRegex);
                    break;
                }
            } catch (PatternSyntaxException e) {
                commonLogger.info("[RegEx] regex syntax error about: {}", regex.get("Regex"));
                regexLogString = "[RegEx] regex syntax error about: " + regex.get("Regex");
            }
        }

        if(matchingRegex == null && taskNum != 0) {
            regexList = dao.getRegexList(host, lang, taskNum);
            for (Map<String, Object> regex : regexList) {
                try {
                    boolean isMatched = false;
                    String[] taskList = regex.get("Task").toString().split(",");
                    for (int i = 0; i < taskList.length; i++) { // 다음 테스크의 id 일치 여부 확인.
                        if (taskList[i].equals(taskNum + "")) {
                            isMatched = true;
                            break;
                        }
                    }
                    Pattern pattern = Pattern.compile(regex.get("Regex").toString());
                    Matcher matcher = pattern.matcher(utter);

                    if (matcher.find() && isMatched == true) {
                        matchingRegex = regex.get("Regex").toString();
                        result.put("task", regex.get("Task"));
                        result.put("isFind", true);
                        result.put("intent", regex.get("Intent").toString());
                        result.put("Regex", matchingRegex);
                        break;
                    }
                    isMatched = false;
                } catch (PatternSyntaxException e) {
                    commonLogger.info("[RegEx] regex syntax error about: {}", regex.get("Regex"));
                    regexLogString = "[RegEx] regex syntax error about: " + regex.get("Regex");
                }
            }
        }

        if (matchingRegex == null) {
            commonLogger.info("[RegEx] No Match, Input: {}", utter);
            regexLogString = "[RegEx] No Match, Input: " + utter;
        } else {
            commonLogger.info("[RegEx] Matched, Input: {}, Intent: {}, Task: {}, RegEx: {}",
                utter, result.get("intent"), result.get("task"), matchingRegex);
            regexLogString = "[RegEx] Matched, Input: "+utter
                +", Intent: "+result.get("intent")
                +", Task: "+result.get("task")
                +", RegEx: "+matchingRegex;
        }
        result.put("logString", regexLogString);

        return result;
    }

    @Async("threadPoolTaskExecutor")
    public void asyncLogAtIntent(AsyncIntentPar asyncIntentPar){
        String session = asyncIntentPar.getSession();
        SdsUtter sdsUtter = asyncIntentPar.getSdsUtter();
        SdsIntent sdsIntent = asyncIntentPar.getSdsIntent();
        int host = asyncIntentPar.getHost();
        String lang = asyncIntentPar.getLang();
        double prob = asyncIntentPar.getProb();
        String logAnswer = asyncIntentPar.getAnswer();
        String jsonData = asyncIntentPar.getJsonData();
        String jsonDebugData = asyncIntentPar.getJsonDebugData();

        boolean nowErrorLog = asyncIntentPar.isNowErrorLog();
        SdsResponse nowErrorSdsResponse = asyncIntentPar.getNowErrorSdsResponse();


        System.out.println("asyncLogAtIntent start by request : " + asyncIntentPar.getSession());
        SdsLogger asyncLogger = new SdsLogger();
        if(nowErrorLog){
            asyncLogger.error(nowErrorSdsResponse);
        }
        String nowSessionLogPK = asyncLogger.sessionCountAdd(host,lang,session, jsonData);
        asyncLogger.intent(nowSessionLogPK, session,sdsUtter,sdsIntent,host,lang,prob,jsonData,jsonDebugData, logAnswer);
        System.out.println("asyncLogAtIntent Ended : " + asyncIntentPar.getSession());
    }

    @Async("threadPoolTaskExecutor")
    public void asyncLogAtUtter(AsyncUtterPar asyncUtterPar){
        SdsUtterCollectRequest request = null;
        SdsIntent intent = null;
        double prob = 1.0;
        JSONObject jsonDebugData = null;
        String logAnswer = null;
        SdsEntityList entities = null;

        try{
            if(asyncUtterPar.getRequest()!=null) request = asyncUtterPar.getRequest();
            if(asyncUtterPar.getIntent()!=null) intent = asyncUtterPar.getIntent();
            if(asyncUtterPar.getJsonDebugData()!=null) jsonDebugData = new JSONObject(asyncUtterPar.getJsonDebugData().toString());
            if(asyncUtterPar.getLogAnswer()!=null) logAnswer = asyncUtterPar.getLogAnswer();
            if(asyncUtterPar.getEntities()!=null) entities = asyncUtterPar.getEntities();
            prob = asyncUtterPar.getProb();
        }catch (Exception e){
            System.out.println("asyncLogAtUtter Error(var) : " + e.toString());
        }

        System.out.println("asyncLogAtUtter start by request : " + request.toString());
        SdsLogger asyncLogger = new SdsLogger();

        try{
            if(asyncUtterPar.isNowAnswerLog()){
                SdsUtter nowAnswerLogSdsUtter = asyncUtterPar.getNowAnswerLogSdsUtter();
                SdsAnswer nowAnswerLogSdsAnswer = asyncUtterPar.getNowAnswerLogSdsAnswer();
                asyncLogger.answer(nowAnswerLogSdsUtter, nowAnswerLogSdsAnswer);
            }
            if(asyncUtterPar.isNowErrorLog()){
                SdsResponse nowErrorLogSdsResponse = asyncUtterPar.getNowErrorLogSdsResponse();
                asyncLogger.error(nowErrorLogSdsResponse);
            }
        }catch (Exception e){
            System.out.println("asyncLogAtUtter Error : "  + e.toString());
        }
        String nowSessionLogPK = asyncLogger.sessionCountAdd(Integer.parseInt(request.getHost()), request.getLang(), request.getSession(), request.getJsonData());
        asyncLogger.intent(nowSessionLogPK, request.getSession(), request.getData(), intent, Integer.parseInt(request.getHost()), request.getLang(), prob, request.getJsonData(), jsonDebugData.toString(), logAnswer);
        asyncLogger.entity(request.getSession(), request.getData(), entities, Integer.parseInt(request.getHost()), request.getLang());
        System.out.println("asyncLogAtUtter Ended : " + request.toString());
    }


    @Async("threadPoolTaskExecutor")
    public void asyncCache(SdsAddCacheRequest sdsAddCacheRequest){
        System.out.println("[CACHE] asyncCacheStart : " + sdsAddCacheRequest.toString());
        cacheClient.sendHttpAddCache(sdsAddCacheRequest);
        System.out.println("[CACHE] asyncCacheEnd");
    }
}
