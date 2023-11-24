package maum.brain.sds.maker.controller;

import maum.brain.sds.data.dto.general.SdsActionResponse;
import maum.brain.sds.data.dto.maker.SdsMakerRequest;
import maum.brain.sds.maker.service.SdsMakerService;
import maum.brain.sds.util.logger.SdsLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/maker")
public class SdsMakerController {
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    private SdsMakerService service;

    @RequestMapping(
            value = "/",
            method = RequestMethod.GET
    ) public @ResponseBody String root(){
        return "Decision Maker Root";
    }

    @RequestMapping(
            value = "/search",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    ) public @ResponseBody
    SdsActionResponse search(@RequestBody SdsMakerRequest request){
        return service.getResponse(request);
    }

    @RequestMapping(
            value = "/setDatabase",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    ) public void setDatabase(
            @RequestParam("file") MultipartFile csvFile
    ) {
        throw new UnsupportedOperationException();
//        try {
//            byte[] bytes = csvFile.getBytes();
//            Path path = Paths.get(uploadDir + csvFile.getOriginalFilename());
//            Files.write(path, bytes);
//            service.setDatabase(/*uploadDir + csvFile.getOriginalFilename()*/);
//            Files.delete(path);
//        } catch (IOException e){
//            e.printStackTrace();
//        }
    }
}
