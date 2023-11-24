package maum.brain.sds.adapter.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import maum.brain.sds.adapter.service.SdsKakaoAdapterService;
import maum.brain.sds.data.dto.SdsResponse;
import maum.brain.sds.data.dto.adapter.SdsKakaoAdapterRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/adapter/kakao")
public class SdsKakaoAdapterController {

    private Logger logger = LoggerFactory.getLogger(SdsKakaoAdapterController.class);

    @Autowired
    private SdsKakaoAdapterService service;

    @RequestMapping(
            value = "/simple/{host}",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public @ResponseBody
    SdsResponse plainKakaoAdapter(@PathVariable(value = "host") String host,
                                  @RequestBody SdsKakaoAdapterRequest request) {

        logger.info("[[request from KAKAO]] " + writeValueAsString(request));
        SdsResponse response = service.adapt(host, request);
        logger.info("[[response to KAKAO]] " + writeValueAsString(response));
        return response;
    }

    private String writeValueAsString(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        String value = "";
        try {
            value = mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        if (value.isEmpty()) {
            value = obj.toString();
        }

        return value;
    }
}
