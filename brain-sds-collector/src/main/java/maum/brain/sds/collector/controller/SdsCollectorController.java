package maum.brain.sds.collector.controller;

import maum.brain.sds.collector.data.async.AsyncIntentPar;
import maum.brain.sds.collector.data.async.AsyncUtterPar;
import maum.brain.sds.collector.data.async.CollectIntentReturn;
import maum.brain.sds.collector.data.async.CollectUtterReturn;
import maum.brain.sds.collector.service.SdsCollectorService;
import maum.brain.sds.data.dto.cache.SdsAddCacheRequest;
import maum.brain.sds.data.dto.collect.SdsIntentCollectRequest;
import maum.brain.sds.data.dto.SdsResponse;
import maum.brain.sds.data.dto.collect.SdsUtterCollectRequest;
import maum.brain.sds.data.dto.general.SdsActionResponse;
import maum.brain.sds.util.logger.SdsLogger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/collect")
public class SdsCollectorController {
    @Autowired
    private SdsCollectorService service;

    @RequestMapping(
        value = "/run/utter",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    ) public @ResponseBody SdsResponse run(@RequestBody SdsUtterCollectRequest request) throws JSONException {
        boolean nowLog = true;
        try{
            nowLog = (!request.getSession().toLowerCase().equals("test"));
        }catch (Exception e){
            nowLog = true;
            System.out.println("nowLog Error [/run/utter] : " + request.toString());
        }
        CollectUtterReturn collectUtterReturn = service.collect(request, false);
        SdsResponse sdsResponse = collectUtterReturn.getSdsResponse();
        AsyncUtterPar asyncUtterPar = collectUtterReturn.getAsyncUtterPar();
        SdsAddCacheRequest sdsAddCacheRequest = collectUtterReturn.getSdsAddCacheRequest();
        if(nowLog){
            try{
                service.asyncLogAtUtter(asyncUtterPar);
            }catch (Exception e){
                // Thread Error
            }
        }
        if(collectUtterReturn.isNowCacheAdd()){
            try{
                service.asyncCache(sdsAddCacheRequest);
            }catch (Exception e){
                // Thread Error
            }
        }
        return sdsResponse;
    }

    @RequestMapping(
        value = "/run/intent",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    ) public @ResponseBody SdsResponse run(@RequestBody SdsIntentCollectRequest request) throws JSONException {
        boolean nowLog = true;
        try{
            nowLog = (!request.getSession().toLowerCase().equals("test"));
        }catch (Exception e){
            nowLog = true;
            System.out.println("nowLog Error [/run/intent] : " + request.toString());
        }
        CollectIntentReturn collectIntentReturn = service.collect(request, false);
        SdsResponse sdsResponse = collectIntentReturn.getSdsResponse();
        AsyncIntentPar asyncIntentPar = collectIntentReturn.getAsyncIntentPar();
        SdsAddCacheRequest sdsAddCacheRequest = collectIntentReturn.getSdsAddCacheRequest();
        JSONObject nowCheckJsonDebugData = new JSONObject(asyncIntentPar.getJsonDebugData());
        if(!nowCheckJsonDebugData.has("prevIntent") && asyncIntentPar.getSdsUtter().getUtter().equals("처음으로")){
            //맨 처음 챗봇 실행의 경우
            nowLog = false;
        }
        if(nowLog){
            if(!asyncIntentPar.getSdsUtter().getUtter().equals("선톡") &&
                !asyncIntentPar.getSdsUtter().getUtter().equals("챗봇공지사항") ){
                try{
                    service.asyncLogAtIntent(asyncIntentPar);
                }catch (Exception e){
                    // Thread Error
                }
            }
        }
        if(collectIntentReturn.isNowCacheAdd()){
            try{
                service.asyncCache(sdsAddCacheRequest);
            }catch (Exception e){
                // Thread Error
            }
        }
        return sdsResponse;
    }

    @RequestMapping(
        value = "/run/utter/detail",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    ) public @ResponseBody SdsResponse runDetail(@RequestBody SdsUtterCollectRequest request) throws JSONException {
        boolean nowLog = true;
        try{
            nowLog = (!request.getSession().toLowerCase().equals("test"));
        }catch (Exception e){
            nowLog = true;
            System.out.println("nowLog Error [/run/utter] : " + request.toString());
        }
        CollectUtterReturn collectUtterReturn = service.collect(request, true);
        SdsResponse sdsResponse = collectUtterReturn.getSdsResponse();
        AsyncUtterPar asyncUtterPar = collectUtterReturn.getAsyncUtterPar();
        SdsAddCacheRequest sdsAddCacheRequest = collectUtterReturn.getSdsAddCacheRequest();
        if(nowLog){
            try{
                service.asyncLogAtUtter(asyncUtterPar);
            }catch (Exception e){
                // Thread Error
            }
        }
        return sdsResponse;
    }

    @RequestMapping(
        value = "/run/intent/detail",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    ) public @ResponseBody SdsResponse runDetail(@RequestBody SdsIntentCollectRequest request) throws JSONException {
        boolean nowLog = true;
        try{
            nowLog = (!request.getSession().toLowerCase().equals("test"));
        }catch (Exception e){
            nowLog = true;
            System.out.println("nowLog Error [/run/intent] : " + request.toString());
        }
        CollectIntentReturn collectIntentReturn = service.collect(request, true);
        SdsResponse sdsResponse = collectIntentReturn.getSdsResponse();
        AsyncIntentPar asyncIntentPar = collectIntentReturn.getAsyncIntentPar();
        SdsAddCacheRequest sdsAddCacheRequest = collectIntentReturn.getSdsAddCacheRequest();
        JSONObject nowCheckJsonDebugData = new JSONObject(asyncIntentPar.getJsonDebugData());
        if(!nowCheckJsonDebugData.has("prevIntent") && asyncIntentPar.getSdsUtter().getUtter().equals("처음으로")){
            //맨 처음 챗봇 실행의 경우
            nowLog = false;
        }
        if(nowLog){
            if(!asyncIntentPar.getSdsUtter().getUtter().equals("선톡") &&
                !asyncIntentPar.getSdsUtter().getUtter().equals("챗봇공지사항") ){
                try{
                    service.asyncLogAtIntent(asyncIntentPar);
                }catch (Exception e){
                    // Thread Error
                }
            }
        }
        return sdsResponse;
    }
}
