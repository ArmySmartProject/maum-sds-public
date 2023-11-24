package maum.brain.cache.controller;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import maum.brain.cache.service.SdsCacheService;
import maum.brain.sds.data.dto.SdsResponse;
import maum.brain.sds.data.dto.cache.SdsAddCacheRequest;
import maum.brain.sds.data.dto.cache.SdsGetCacheResponse;
import maum.brain.sds.data.dto.general.SdsErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cache")
public class SdsCacheController {
  @Autowired
  private SdsCacheService service;

  @RequestMapping(
      value = "/addCache",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE
  ) public @ResponseBody
  SdsResponse addCache(@RequestBody SdsAddCacheRequest sdsAddCacheRequest){
    try{
      System.out.println("[POST ] SdsCacheController :: addCache :: " + sdsAddCacheRequest);
      service.addCache(sdsAddCacheRequest);
    }catch (Exception e){
      System.out.println("[ERROR] SdsCacheController :: addCache :: " + e);
      return new SdsErrorResponse(500,"");
    }
    return new SdsErrorResponse(200,"");
  }

  @CrossOrigin(origins="*")
  @RequestMapping(
      value = "/delCache",
      method = RequestMethod.POST
  ) public @ResponseBody
  Integer delCache(@RequestBody Integer inputDelHost, HttpServletRequest request,
      HttpServletResponse response){
    Integer nowDelHost = inputDelHost;
    try{
      service.delCache(nowDelHost);
      System.out.println("[POST ] SdsCacheController :: delCache :: " + nowDelHost);
    }catch (Exception e){
      System.out.println("[ERROR] SdsCacheController :: delCache :: " + e);
      return nowDelHost;
    }
    return nowDelHost;
  }


  @RequestMapping(
      value = "/getCache",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE
  ) public @ResponseBody
  SdsGetCacheResponse getCache(@RequestBody SdsAddCacheRequest sdsAddCacheRequest){
    SdsGetCacheResponse sdsGetCacheResponse = service.getCache(sdsAddCacheRequest);
    return sdsGetCacheResponse;
  }
}
