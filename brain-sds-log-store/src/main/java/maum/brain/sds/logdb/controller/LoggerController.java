package maum.brain.sds.logdb.controller;

import maum.brain.sds.logdb.data.LogMessageRequest;
import maum.brain.sds.logdb.service.LoggerService;
import maum.brain.sds.util.logger.LogAllRequest;
import maum.brain.sds.util.logger.LogAnswerRequest;
import maum.brain.sds.util.logger.LogEntityRequest;
import maum.brain.sds.util.logger.LogIntentRequest;
import maum.brain.sds.util.logger.LogSessionCounselorRequest;
import maum.brain.sds.util.logger.LogSessionCountRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/logger")
public class LoggerController {
    private static final Logger logger = LoggerFactory.getLogger(LoggerController.class);

    @Autowired
    private LoggerService service;

    @RequestMapping(
        value = "/addIntent",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public void logIntent(@RequestBody LogIntentRequest request, HttpServletResponse response){
        System.out.println(request);
        int status = service.logIntent(request);
        if(status != 0) response.setStatus(500);
    }

    @RequestMapping(
        value = "/addEntity",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public void logEntity(@RequestBody LogEntityRequest request, HttpServletResponse response){
        System.out.println(request);
        int status = service.logEntities(request);
        if(status != 0) response.setStatus(500);
    }

    @RequestMapping(
        value = "/addAnswer",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public void logAnswer(@RequestBody LogAnswerRequest request, HttpServletResponse response){
        System.out.println(request);
        int status = service.logAnswer(request);
        if(status != 0) response.setStatus(500);
    }

    @RequestMapping(
        value = "/addAll",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public void logAll(@RequestBody LogAllRequest request, HttpServletResponse response){
        System.out.println(request);
        int status = service.logAll(request);
        if(status != 0) response.setStatus(500);
    }

    @RequestMapping(
        value = "/log",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public void logger(@RequestBody LogMessageRequest request){
        System.out.println(request);
        service.logMessage(request);
    }

    @RequestMapping(
        value = "/logStats",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public void logStats(HttpServletResponse response){

    }

    @RequestMapping(
        value = "/sessionCount",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public int sessionCount(@RequestBody LogSessionCountRequest request, HttpServletResponse response){
        System.out.println(request);
        int nowSession = service.sessionCount(request);
        return nowSession;
    }

    @RequestMapping(
        value = "/sessionCounselor",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public void sessionCounselor(@RequestBody LogSessionCounselorRequest request){
        System.out.println(request);
        service.updateCounselor(request);
    }
}
