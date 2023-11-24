package maum.brain.sds.adapter.controller;

import maum.brain.sds.adapter.service.SdsAdapterService;
import maum.brain.sds.data.dto.SdsResponse;
import maum.brain.sds.data.dto.adapter.SdsAdapterCoupledRequest;
import maum.brain.sds.data.dto.adapter.SdsAdapterStringRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/adapter")
public class SdsAdapterController {

    @Autowired
    private SdsAdapterService service;

    @RequestMapping(
            value = "/simple",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    ) public @ResponseBody SdsResponse plainAdapter(@RequestBody SdsAdapterStringRequest request){
        return service.fromStringRequest(request);
    }

    @RequestMapping(
            value = "/coupled",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    ) public @ResponseBody SdsResponse coupledAdapter(@RequestBody SdsAdapterCoupledRequest request){
        return service.fromCoupledRequest(request);
    }
}
