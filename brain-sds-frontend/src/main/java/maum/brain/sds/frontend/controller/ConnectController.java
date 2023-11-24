package maum.brain.sds.frontend.controller;

import maum.brain.sds.data.dto.adapter.SdsAdapterStringRequest;
import maum.brain.sds.frontend.model.HostInfoParams;
import maum.brain.sds.frontend.service.ConnectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.UUID;

@Controller
@RequestMapping("/chat")
public class ConnectController {

    private static final Logger logger = LoggerFactory.getLogger(ConnectController.class);

    @Autowired
    ConnectService service;

    @RequestMapping(
            value = "/hostInfo",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ResponseBody
    public void hostInfo(@RequestBody HostInfoParams params) {
        logger.info(params.toString());
    }

    @RequestMapping(
            value = "/request",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ResponseBody
    public ResponseEntity request(@RequestBody SdsAdapterStringRequest params, HttpSession session) {
        logger.info("host : " + params.getHost() + ", lang : " + params.getLang());
        logger.info("ChatParams : " + params);

        if(session.isNew()){
            String sessionStr = UUID.randomUUID().toString().replaceAll("-", "");
            session.setAttribute("session", sessionStr);
        }

        params.setSession((String) session.getAttribute("session"));

        return new ResponseEntity(service.doChat(params), HttpStatus.OK);
    }


}
