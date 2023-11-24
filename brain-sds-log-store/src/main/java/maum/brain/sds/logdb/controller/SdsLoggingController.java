package maum.brain.sds.logdb.controller;

import maum.brain.sds.data.dto.log.SdsLogRequest;
import maum.brain.sds.data.dto.log.SdsLogResponse;
import maum.brain.sds.logdb.service.SdsDbLogService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/log")
public class SdsLoggingController {

    private SdsDbLogService service;

    @RequestMapping(
            value="/addIntent",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public @ResponseBody SdsLogResponse logIntent(@RequestBody SdsLogRequest request){

        service.logAnything(request);

        return new SdsLogResponse();
    }



}
