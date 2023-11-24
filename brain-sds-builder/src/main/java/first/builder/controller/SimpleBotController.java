package first.builder.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import first.builder.client.CallManagerClient;
import first.builder.client.VoiceBotBLClient;
import first.builder.service.BuilderService;
import first.builder.service.SimpleBotService;
import first.builder.vo.SimpleBotVO;
import first.common.util.PropInfo;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HTTP;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SimpleBotController {

  Logger logger = LoggerFactory.getLogger(this.getClass());

  @Resource(name = "simpleBotService")
  private SimpleBotService simpleBotService;
  
  @Resource(name = "builderService")
  private BuilderService builderService;

  @Resource(name = "callManagerClient")
  private CallManagerClient callManagerClient;

  @Resource(name = "voiceBotBLClient")
  private VoiceBotBLClient voiceBotBLClient;

  @ResponseBody
  @RequestMapping(value = "/simpleBot")
  public ModelAndView simpleBot(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    ModelAndView view = new ModelAndView("/simpleBot");
    view.addObject("env", PropInfo.env.toLowerCase());
    view.addObject("m2uUrl", PropInfo.m2uUrl);
    return view;
  }

	@ResponseBody
	@RequestMapping(value = "/voicebotBuilder")
	public ModelAndView voicebotBuilder(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ModelAndView view = new ModelAndView("/voicebotBuilder");
		view.addObject("env", PropInfo.env.toLowerCase());
		view.addObject("m2uUrl", PropInfo.m2uUrl);
		return view;
	}

  @ResponseBody
  @RequestMapping(
      value = "/simpleBot/addScenario",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE
  )
  public int addScenario(@RequestBody Map request) throws Exception {

    int lang = (Integer) request.get("lang");
    String userId = (String) request.get("userId");
    String companyId = (String) request.get("companyId");
    String name = (String) request.get("name");

    int simplebotId = -1;

    if (companyId.isEmpty()) {
      simplebotId = simpleBotService.createSimpleBot(userId, "", name, lang);
    } else {
      simplebotId = simpleBotService.createSimpleBot(userId, companyId, name, lang);
    }

    return simplebotId;
  }


  @ResponseBody
  @RequestMapping(
      value = "/simpleBot/apply",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE
  )
  public ResponseEntity applySimpleBot(@RequestBody Map request) throws Exception {

    int simplebotId = (Integer) request.get("simplebotId");
    String userId = (String) request.get("userId");
    String companyId = (String) request.get("companyId");
    String scenarioJson = (String)request.get("scenarioJson");
    ResponseEntity response =
        new ResponseEntity(
            simpleBotService.applyScenario(simplebotId, userId, companyId, scenarioJson),
            HttpStatus.OK);
    return response;

  }

	@ResponseBody
	@RequestMapping(
			value = "/simpleBot/applyV2",
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE
	)
	public ResponseEntity applyVoiceBot(@RequestBody Map request) throws Exception {

		int simplebotId = (Integer) request.get("simplebotId");
		String userId = (String) request.get("userId");
		String companyId = (String) request.get("companyId");
		String scenarioJson = (String)request.get("scenarioJson");
		String isExcelUpload = (String)request.get("isExcelUpload");
		String campaignInfoObj = (String)request.get("campaignInfoObj") != null ? (String)request.get("campaignInfoObj")  : "{'updateCheck':'','oldTaskName':'','newTaskName':'','successYn':''}";

		ResponseEntity response =
				new ResponseEntity(
						simpleBotService.applyScenarioV2(simplebotId, userId, companyId, scenarioJson, isExcelUpload, campaignInfoObj),
						HttpStatus.OK);
		return response;
	}

  @ResponseBody
  @RequestMapping(value = "/simpleBot/upload",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ModelAndView uploadExcel(@RequestParam int simplebotId, HttpServletRequest request) throws Exception {
    MultipartFile excelFile = ((MultipartHttpServletRequest) request).getFile("excel_file");
    logger.info("엑셀 파일 업로드 컨트롤러");
    if (excelFile == null || excelFile.isEmpty()) {
      throw new RuntimeException("엑셀파일을 선택 해 주세요.");
    }

    File destFile = new File(
        request.getSession().getServletContext().getInitParameter("excelPath") + excelFile
            .getOriginalFilename());
    try {
      excelFile.transferTo(destFile);
    } catch (IllegalStateException | IOException e) {
      logger.info("excelFile.tranferTo ERR" + e.getMessage());
      throw new RuntimeException(e.getMessage(), e);
    }

    String scenarioJson = simpleBotService.uploadScenario(simplebotId, destFile);

    if (destFile.delete()) {
      logger.info("Fail to delete Excel File.");
    }

    ModelAndView view = new ModelAndView("jsonView");
    view.addObject("status", "OK");
    view.addObject("scenarioJson", scenarioJson);
    return view;
  }

  @ResponseBody
  @RequestMapping(value = "/simpleBot/uploadScenarioV2",
  method = RequestMethod.POST,
  produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ModelAndView uploadExcelV2(@RequestParam int simplebotId, HttpServletRequest request) throws Exception {
	  MultipartFile excelFile = ((MultipartHttpServletRequest) request).getFile("excel_file_v2");
	  logger.info("엑셀 파일 업로드 컨트롤러");
	  if (excelFile == null || excelFile.isEmpty()) {
		  throw new RuntimeException("엑셀파일을 선택 해 주세요.");
	  }

	  File destFile = new File(
			  request.getSession().getServletContext().getInitParameter("excelPath") + excelFile
			  .getOriginalFilename());
	  try {
		  excelFile.transferTo(destFile);
	  } catch (IllegalStateException | IOException e) {
		  logger.info("excelFile.tranferTo ERR" + e.getMessage());
		  throw new RuntimeException(e.getMessage(), e);
	  }

		Map<String, Object> scenarioJson = simpleBotService.uploadScenarioV2(simplebotId, destFile);

	  ModelAndView view = new ModelAndView("jsonView");
	  view.addObject("status", "OK");
	  if (scenarioJson.containsKey("scenarioJson")){
			view.addObject("scenarioJson", scenarioJson.get("scenarioJson").toString());

		} else if (scenarioJson.containsKey("checkTask")) {
			view.addObject("checkTask", scenarioJson.get("checkTask").toString());
			destFile.delete();
		} else if (scenarioJson.containsKey("checkRegexIntent")){
		  view.addObject("checkRegexIntent", scenarioJson.get("checkRegexIntent").toString());
		  destFile.delete();
	  }

		if (destFile.delete()) {
			logger.info("Fail to delete Excel File.");
		}

	  return view;
  }

  @RequestMapping(value = "/simpleBot/sampleDownload")
  public @ResponseBody
  Map<String, Object> getItemCarouselSampleDown(HttpServletRequest request,
      HttpServletResponse response) throws Exception {

    try {
      String sampleFilePath =
          request.getSession().getServletContext().getInitParameter("excelPath")
              + "sample/YN봇 대화엑셀 샘플.xlsx";

      downloadFile(request, response, sampleFilePath);
    } catch (Exception e) {
      logger.error("sampleDownload Exception:", e);

    }

    return null;
  }

	@RequestMapping(value = "/voiceBot/sampleDownload")
	public @ResponseBody
	Map<String, Object> getItemCarouselSampleDownV2(HttpServletRequest request,
													HttpServletResponse response) throws Exception {

		try {
			String sampleFilePath =
					request.getSession().getServletContext().getInitParameter("excelPath")
							+ "sample/음성봇_시나리오_샘플.xlsx";

			downloadFile(request, response, sampleFilePath);
		} catch (Exception e) {
			logger.error("sampleDownload Exception:", e);

		}

		return null;
	}

  private void downloadFile(HttpServletRequest request, HttpServletResponse response,
      String filePath) throws Exception {

    File file = null;
    FileInputStream fileInputStream = null;

    try {
      file = new File(filePath);
      // 스트림 열기
      fileInputStream = new FileInputStream(filePath);
      String userAgent = request.getHeader("USER-AGENT");
      // 컨텐츠 타입 //application/octet-stream
      String downloadContentType = "application/x-msdownload; charset=UTF-8";
      response.setContentType(downloadContentType);
      response.setContentLength((int) file.length());
      response.setHeader("Content-Transfer-Encoding", "binary;");
      response.setHeader("Pragma", "no-cache;");
      response.setHeader("Expires", "-1;");
      // response.setHeader("Content-Disposition","attachment; filename=\"" +
      // attf.getAttfNm() +"\"");
      if (userAgent.indexOf("MSIE") >= 0) {
        int i = userAgent.indexOf('M', 2);
        String IEV = userAgent.substring(i + 5, i + 8);
        logger.debug("# IEV:" + IEV);
        if (IEV.equalsIgnoreCase("5.5")) {
          response.setHeader("Content-Disposition",
              "filename=" + URLEncoder.encode(file.getName(), "UTF-8"));
        } else {
          logger.debug("download file start!");
          response.setHeader("Content-Disposition",
              "attachment; filename=" + URLEncoder.encode(file.getName(), "UTF-8"));
        }
      } else {
        response.setHeader("Content-Disposition",
            "attachment; filename=" + URLEncoder.encode(file.getName(), "UTF-8")
                .replaceAll("\\+", "\\ "));
      }
      FileCopyUtils.copy(fileInputStream, response.getOutputStream());
    } catch (UnsupportedEncodingException ue) {
      logger.error("UnsupportedEncodingException " + ue.getMessage());
      throw new UnsupportedEncodingException("### UnsupportedEncodingException ");
    } catch (FileNotFoundException fne) {
      logger.error("FileNotFoundException " + fne.getMessage());
      throw new FileNotFoundException("### FileNotFoundException");
    } catch (IOException ie) {
      logger.error("IOException " + ie.getMessage());
      throw new IOException("### IOException ", ie);
    } catch (Exception e) {
      logger.error("Exception " + e.getMessage());
      throw new Exception(e);
    } finally {
      if (fileInputStream != null) {
        try {
          fileInputStream.close();
        } catch (IOException ie) {
          logger.error("Exception" + ie.getMessage());
          throw new IOException(ie);
        }
      }
    }
  }

  @RequestMapping(value = "/simpleBot/scenarioDownload")
  public @ResponseBody
  Map<String, Object> getScenarioDownloadFile(HttpServletRequest request,
		  HttpServletResponse response,@RequestParam("scenarioJson") String scenarioJson,@RequestParam("scenarioHost") String host) throws Exception {

	  Map<String, Object> map = null;
	  // json파서
	  JsonParser jp = new JsonParser();
	  JsonObject jsonObj = (JsonObject) jp.parse(scenarioJson);
	  JsonArray jArray = new JsonArray();

	  // json -> map
	  map = new ObjectMapper().readValue(jsonObj.toString(), Map.class);

	  try {
		  String scenarioFilePath =
				  request.getSession().getServletContext().getInitParameter("excelPath")
				  + "/download/sample/음성봇빌더V2_시나리오.xlsx";

		  scenarioDownloadFile(request, response, scenarioFilePath, map, host);
	  } catch (Exception e) {
		  logger.error("sampleDownload Exception:", e);

	  }

	  return null;
  }

  private void scenarioDownloadFile(HttpServletRequest request, HttpServletResponse response,String filePath, Map<String,Object> map, String host) throws Exception {

	    InputStream is = new FileInputStream(filePath);
		XSSFWorkbook workbook = new XSSFWorkbook(is);
		try {
			String fileName = new String("음성봇빌더_시나리오".getBytes("UTF-8"), "ISO-8859-1");
			
			CellStyle style = workbook.createCellStyle();

			style.setAlignment(HorizontalAlignment.CENTER);
			style.setVerticalAlignment(VerticalAlignment.CENTER);
			style.setBorderBottom(BorderStyle.THIN);
			style.setBorderTop(BorderStyle.THIN);
			style.setBorderRight(BorderStyle.THIN);
			style.setBorderLeft(BorderStyle.THIN);
			style.setWrapText(true);

			XSSFSheet sheet = workbook.getSheetAt(0);
			XSSFSheet sheet2 = workbook.getSheetAt(1);
			
			Row row = null;
			Cell cell = null;
			int rowNo = 2;
			int mergeFirstRow = 2;
			int mergeLastRow = 1;
			int taskNum = 1;
			int globalTaskNum = 1;
			List<Map<String,Object>> nodeListMap = (List<Map<String, Object>>) map.get("nodes");
			int intentListSize = 0;
			for (int i = 0; i < nodeListMap.size(); i++) {
			    if(!nodeListMap.get(i).get("type").toString().equals("global")) {
					if(!nodeListMap.get(i).get("label").toString().equals("종료")) {
						List<Map<String,Object>> attrListMap = (List<Map<String, Object>>) nodeListMap.get(i).get("attr");
						if(attrListMap.size() > 0) {
							List<Map<String,Object>> intentListMap = (List<Map<String, Object>>) attrListMap.get(0).get("intentList");
							intentListSize = intentListMap.size();
						}
						if(intentListSize > 1) {
							for (int j = mergeFirstRow; j <= mergeLastRow + intentListSize; j++) {
								row = sheet.createRow(j);
								for (int k = 0; k < 15; k++){
									cell = row.createCell(k);
									cell.setCellStyle(style);
								}
								row.setHeight((short)800);
							}

							sheet.addMergedRegion(new CellRangeAddress(mergeFirstRow, mergeLastRow + intentListSize, 0, 0));
							row = sheet.getRow(mergeFirstRow);
							cell = row.createCell(0);
							cell.setCellStyle(style);
							cell.setCellValue(taskNum);

							sheet.addMergedRegion(new CellRangeAddress(mergeFirstRow, mergeLastRow + intentListSize,1,1));
							row = sheet.getRow(mergeFirstRow);
							cell = row.createCell(1);
							cell.setCellStyle(style);
							cell.setCellValue(nodeListMap.get(i).get("taskGroup").toString());

							sheet.addMergedRegion(new CellRangeAddress(mergeFirstRow, mergeLastRow + intentListSize,2,2));
							row = sheet.getRow(mergeFirstRow);
							cell = row.createCell(2);
							cell.setCellStyle(style);
							cell.setCellValue(nodeListMap.get(i).get("label").toString());

							if(attrListMap.size() > 0) {
								sheet.addMergedRegion(new CellRangeAddress(mergeFirstRow, mergeLastRow + intentListSize,3,3));
								row = sheet.getRow(mergeFirstRow);
								cell = row.createCell(3);
								cell.setCellStyle(style);
								cell.setCellValue(attrListMap.get(0).get("utter").toString());
							}
							List<Map<String,Object>> intentListMap = (List<Map<String, Object>>) attrListMap.get(0).get("intentList");
							for (int j = 0; j < intentListMap.size(); j++) {
								row = sheet.getRow(mergeFirstRow+j);
								cell = row.createCell(4);
								cell.setCellStyle(style);
								cell.setCellValue(intentListMap.get(j).get("intent").toString());
								row = sheet.getRow(mergeFirstRow+j);
								cell = row.createCell(5);
								cell.setCellStyle(style);
								cell.setCellValue(intentListMap.get(j).get("info").toString());
								row = sheet.getRow(mergeFirstRow+j);
								cell = row.createCell(6);
								cell.setCellStyle(style);
								cell.setCellValue(intentListMap.get(j).get("answer").toString());
								row = sheet.getRow(mergeFirstRow+j);
								cell = row.createCell(7);
								cell.setCellStyle(style);
								cell.setCellValue(intentListMap.get(j).get("nextTask").toString());
							}
							sheet.addMergedRegion(new CellRangeAddress(mergeFirstRow, mergeLastRow + intentListSize,8,8));
							row = sheet.getRow(mergeFirstRow);
							cell = row.createCell(8);
							String[] inputTypes = attrListMap.get(0).get("inputType").toString().split(",");
							String inputUtter = "";
							String inputDial = "";

							int maxTurnCnt = Integer.parseInt(attrListMap.get(0).get("maxTurn").toString());
							if (maxTurnCnt > -1) {
								for (int j = 0; j < maxTurnCnt + 1; j++) {
									if (inputTypes[j].equals("0") || inputTypes[j].equals("2")) {
										inputUtter = inputUtter.length() > 0 ? inputUtter.concat(",").concat(Integer.toString(j)) : inputUtter.concat(Integer.toString(j));
									}
									if (inputTypes[j].equals("1") || inputTypes[j].equals("2")) {
										inputDial = inputDial.length() > 0 ? inputDial.concat(",").concat(Integer.toString(j)) : inputDial.concat(Integer.toString(j));
									}
								}
							}
							cell.setCellValue(inputUtter);

							sheet.addMergedRegion(new CellRangeAddress(mergeFirstRow, mergeLastRow + intentListSize,9,9));
							row = sheet.getRow(mergeFirstRow);
							cell = row.createCell(9);
							cell.setCellStyle(style);
							cell.setCellValue(inputDial);
							sheet.addMergedRegion(new CellRangeAddress(mergeFirstRow, mergeLastRow + intentListSize,10,10));
							row = sheet.getRow(mergeFirstRow);
							cell = row.createCell(10);
							cell.setCellStyle(style);
							if(!attrListMap.get(0).get("maxTurn").toString().equals("-1")) {
								cell.setCellValue(attrListMap.get(0).get("maxTurn").toString());
							}
							sheet.addMergedRegion(new CellRangeAddress(mergeFirstRow, mergeLastRow + intentListSize,11,11));
							row = sheet.getRow(mergeFirstRow);
							cell = row.createCell(11);
							cell.setCellStyle(style);
							cell.setCellValue(attrListMap.get(0).get("taskOverMax").toString());
							sheet.addMergedRegion(new CellRangeAddress(mergeFirstRow, mergeLastRow + intentListSize,12,12));
							row = sheet.getRow(mergeFirstRow);
							cell = row.createCell(12);
							cell.setCellStyle(style);
							if(attrListMap.get(0).get("acceptSttStcIdx") != null) {
								String[] acceptSttStcIdxList = attrListMap.get(0).get("acceptSttStcIdx").toString().replaceAll("\\p{Z}", "").split(",");
								String acceptSttStcIdx = "";
								if (acceptSttStcIdxList.length > 0 && !"".equals(acceptSttStcIdxList[0])) {
								  for (int j = 0; j < acceptSttStcIdxList.length; j++) {
									  if (j != acceptSttStcIdxList.length - 1) {
										  acceptSttStcIdx += Integer.parseInt(acceptSttStcIdxList[j]) + 1 + ",";
									  } else {
										  acceptSttStcIdx += Integer.parseInt(acceptSttStcIdxList[j]) + 1;
									  }
								   }
								  cell.setCellValue(acceptSttStcIdx);
								}
							}
							sheet.addMergedRegion(new CellRangeAddress(mergeFirstRow, mergeLastRow + intentListSize,13,13));
							row = sheet.getRow(mergeFirstRow);
							cell = row.createCell(13);
							cell.setCellStyle(style);
							if(attrListMap.get(0).get("repeatAnswerStcIdx") != null) {
								String[] repeatAnswerStcIdxList = attrListMap.get(0).get("repeatAnswerStcIdx").toString().replaceAll("\\p{Z}", "").split(",");
								String repeatAnswerStcIdx = "";
								if (repeatAnswerStcIdxList.length > 0 && !"".equals(repeatAnswerStcIdxList[0])) {
								    for (int j = 0; j < repeatAnswerStcIdxList.length; j++) {
									    if (j != repeatAnswerStcIdxList.length - 1) {
										    repeatAnswerStcIdx += Integer.parseInt(repeatAnswerStcIdxList[j]) + 1 + ",";
										} else {
										    repeatAnswerStcIdx += Integer.parseInt(repeatAnswerStcIdxList[j]) + 1;
										}
									}
								    cell.setCellValue(repeatAnswerStcIdx);
								}
							}

							sheet.addMergedRegion(new CellRangeAddress(mergeFirstRow, mergeLastRow + intentListSize,14,14));
							row = sheet.getRow(mergeFirstRow);
							cell = row.createCell(14);
							cell.setCellStyle(style);
							if(nodeListMap.get(i).containsKey("successYn")){
								cell.setCellValue(nodeListMap.get(i).get("successYn").toString() != null ? nodeListMap.get(i).get("successYn").toString() : "");
							}else{
								cell.setCellValue("");
							}

							mergeLastRow = mergeLastRow + intentListSize;
							mergeFirstRow = mergeLastRow + 1;
						}else {
							row = sheet.createRow(mergeFirstRow);
							cell = row.createCell(0);
							cell.setCellStyle(style);
							cell.setCellValue(taskNum);

							cell = row.createCell(1);
							cell.setCellStyle(style);
							cell.setCellValue(nodeListMap.get(i).get("taskGroup").toString());

							cell = row.createCell(2);
							cell.setCellStyle(style);
							cell.setCellValue(nodeListMap.get(i).get("label").toString());

							if(attrListMap.size() > 0) {
								row.setHeight((short)1000);
								cell = row.createCell(3);
								cell.setCellStyle(style);
								cell.setCellValue(attrListMap.get(0).get("utter").toString());
							}
							List<Map<String,Object>> intentListMap = (List<Map<String, Object>>) attrListMap.get(0).get("intentList");

							for (int j = 0; j < intentListMap.size(); j++) {
								cell = row.createCell(4);
								cell.setCellStyle(style);
								cell.setCellValue(intentListMap.get(j).get("intent").toString());
								row = sheet.getRow(mergeFirstRow+j);
								cell = row.createCell(5);
								cell.setCellStyle(style);
								cell.setCellValue(intentListMap.get(j).get("info").toString());
								row = sheet.getRow(mergeFirstRow+j);
								cell = row.createCell(6);
								cell.setCellStyle(style);
								cell.setCellValue(intentListMap.get(j).get("answer").toString());
								row = sheet.getRow(mergeFirstRow+j);
								cell = row.createCell(7);
								cell.setCellStyle(style);
								cell.setCellValue(intentListMap.get(j).get("nextTask").toString());
							}

							String[] inputTypes = attrListMap.get(0).get("inputType").toString().split(",");
							String inputUtter = "";
							String inputDial = "";

							int maxTurnCnt = Integer.parseInt(attrListMap.get(0).get("maxTurn").toString());
							if (maxTurnCnt > -1) {
								for (int j = 0; j < maxTurnCnt + 1; j++) {
									if (inputTypes[j].equals("0") || inputTypes[j].equals("2")) {
										inputUtter = inputUtter.length() > 0 ? inputUtter.concat(",").concat(Integer.toString(j)) : inputUtter.concat(Integer.toString(j));
									}
									if (inputTypes[j].equals("1") || inputTypes[j].equals("2")) {
										inputDial = inputDial.length() > 0 ? inputDial.concat(",").concat(Integer.toString(j)) : inputDial.concat(Integer.toString(j));
									}
								}
							}

							cell = row.createCell(8);
							cell.setCellStyle(style);
							cell.setCellValue(inputUtter);

							cell = row.createCell(9);
							cell.setCellStyle(style);
							cell.setCellValue(inputDial);

							cell = row.createCell(10);
							cell.setCellStyle(style);
							if(!attrListMap.get(0).get("maxTurn").toString().equals("-1")) {
								cell.setCellValue(attrListMap.get(0).get("maxTurn").toString());
							}

							cell = row.createCell(11);
							cell.setCellStyle(style);
							cell.setCellValue(attrListMap.get(0).get("taskOverMax").toString());

							cell = row.createCell(12);
							cell.setCellStyle(style);
							if(attrListMap.get(0).get("acceptSttStcIdx") != null) {
								String[] acceptSttStcIdxList = attrListMap.get(0).get("acceptSttStcIdx").toString().replaceAll("\\p{Z}", "").split(",");
								String acceptSttStcIdx = "";
								if (acceptSttStcIdxList.length > 0 && !"".equals(acceptSttStcIdxList[0])) {
								  for (int j = 0; j < acceptSttStcIdxList.length; j++) {
									  if (j != acceptSttStcIdxList.length - 1) {
										  acceptSttStcIdx += Integer.parseInt(acceptSttStcIdxList[j]) + 1 + ",";
									  } else {
										  acceptSttStcIdx += Integer.parseInt(acceptSttStcIdxList[j]) + 1;
									  }
								   }
								  cell.setCellValue(acceptSttStcIdx);
								}
							}

							cell = row.createCell(13);
							cell.setCellStyle(style);
							if(attrListMap.get(0).get("repeatAnswerStcIdx") != null) {
								String[] repeatAnswerStcIdxList = attrListMap.get(0).get("repeatAnswerStcIdx").toString().replaceAll("\\p{Z}", "").split(",");
								String repeatAnswerStcIdx = "";
								if (repeatAnswerStcIdxList.length > 0 && !"".equals(repeatAnswerStcIdxList[0])) {
								    for (int j = 0; j < repeatAnswerStcIdxList.length; j++) {
									    if (j != repeatAnswerStcIdxList.length - 1) {
										    repeatAnswerStcIdx += Integer.parseInt(repeatAnswerStcIdxList[j]) + 1 + ",";
										} else {
										    repeatAnswerStcIdx += Integer.parseInt(repeatAnswerStcIdxList[j]) + 1;
										}
									}
								    cell.setCellValue(repeatAnswerStcIdx);
								}
							}

							cell = row.createCell(14);
							cell.setCellStyle(style);
							if(nodeListMap.get(i).containsKey("successYn")){
								cell.setCellValue(nodeListMap.get(i).get("successYn").toString() != null ? nodeListMap.get(i).get("successYn").toString() : "");
							}else{
								cell.setCellValue("");
							}

							mergeFirstRow = mergeFirstRow + 1;
							mergeLastRow = mergeLastRow + 1;
						}
						taskNum++;
					}
			    }

			    // 다운로드 제일 마지막 행에 종료 정보 넣기
			    if(i == nodeListMap.size() -1) {
					row = sheet.createRow(mergeFirstRow);
					cell = row.createCell(0);
					cell.setCellStyle(style);
					cell.setCellValue(taskNum);
					cell = row.createCell(1);
					cell.setCellStyle(style);
					cell.setCellValue("통화종료");
					cell = row.createCell(2);
					cell.setCellStyle(style);
					cell.setCellValue("종료");
					cell = row.createCell(3);
					cell.setCellStyle(style);
					cell.setCellValue("");
					cell = row.createCell(4);
					cell.setCellStyle(style);
					cell.setCellValue("");
					cell = row.createCell(5);
					cell.setCellStyle(style);
					cell.setCellValue("");
					cell = row.createCell(6);
					cell.setCellStyle(style);
					cell.setCellValue("");
					cell = row.createCell(7);
					cell.setCellStyle(style);
					cell.setCellValue("");
					cell = row.createCell(8);
					cell.setCellStyle(style);
					cell.setCellValue("");
					cell = row.createCell(9);
					cell.setCellStyle(style);
					cell.setCellValue("");
					cell = row.createCell(10);
					cell.setCellStyle(style);
					cell.setCellValue("");
					cell = row.createCell(11);
					cell.setCellStyle(style);
					cell.setCellValue("");
					cell = row.createCell(12);
					cell.setCellStyle(style);
					cell.setCellValue("");
					cell = row.createCell(13);
					cell.setCellStyle(style);
					cell.setCellValue("");
					cell = row.createCell(14);
					cell.setCellStyle(style);
					cell.setCellValue("");
				}
			}
			
			int rowNoSheet2 = 1;
			
			List<Map<String,Object>> regexListMap = builderService.getRegexInfoList(host);
			for (int i = 0; i < regexListMap.size(); i++) {
				row = sheet2.createRow(rowNoSheet2++);
				cell = row.createCell(0);
				cell.setCellValue(regexListMap.get(i).get("Intent").toString());
				cell = row.createCell(1);
				cell.setCellValue(regexListMap.get(i).get("Regex").toString());
			}
			
			response.setContentType("ms-vnd/excel");
			response.setHeader("Content-Disposition", "attachment;filename="+fileName+".xlsx");

			workbook.write(response.getOutputStream());
			workbook.close();
			response.getOutputStream().flush();
			response.getOutputStream().close();
		} catch (Exception e) {
			e.printStackTrace();
			response.setHeader("Set-Cookie", "fileDownload=false; path=/");
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			response.setHeader("Content-Type", "text/html; charset=utf-8");

			OutputStream out = null;
			try {
				out = response.getOutputStream();
				byte[] data = "fail..".getBytes();
				out.write(data, 0, data.length);
			} catch (Exception ignore) {
				ignore.printStackTrace();
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (Exception ignore) {
					}
				}
			}

		}
  }




  @ResponseBody
  @RequestMapping(
      value = "/simpleBot/getTestCustData",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE
  )
  public String getTestCustData(@RequestBody Map request) throws Exception {
    String testCustData = "";
    try {
      int simplebotId = (Integer) request.get("simplebotId");
      testCustData = simpleBotService.getTestCustData(simplebotId);

    } catch (Exception e) {
      logger.error("[getTestCustData] Exception:", e);
//
    }

    return testCustData;
  }

  @ResponseBody
  @RequestMapping(
      value = "/simpleBot/saveTestCustData",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE
  )
  public void saveTestCustData(@RequestBody Map request) throws Exception {
    try {
      int simplebotId = (Integer) request.get("simplebotId");
      String customInfoJson = (String) request.get("custData");
      simpleBotService.saveTestCustData(simplebotId, customInfoJson);

    } catch (Exception e) {
      logger.error("[saveTestCustData] Exception:", e);
    }
  }

  @ResponseBody
  @RequestMapping(
      value = "/simpleBot/getContractNo",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE
  )
  public long getContractNo(@RequestBody Map request) throws Exception {
    long contractNo = 0;
    try {
      int simplebotId = Integer.parseInt(request.get("simplebotId").toString());
      String telNo = (String) request.get("telNo");

      contractNo = simpleBotService.getContractNo(simplebotId, telNo);

    } catch (Exception e) {
      logger.error("[getContractNo] Exception:", e);
    }

    return contractNo;
  }

  @ResponseBody
  @RequestMapping(
      value = "/simpleBot/callStart",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE
  )
  public void callStart(@RequestBody Map request) throws Exception {
    try {
      callManagerClient.callStart(request);
    } catch (Exception e) {
      logger.error("[callStart] Exception:", e);
    }
  }

  @ResponseBody
  @RequestMapping(
      value = "/simpleBot/getTesterInfo",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE
  )
  public String getTesterInfo(@RequestBody Map request) throws Exception {
    try {
      return voiceBotBLClient.findTeseterInfo(request);
    } catch (Exception e) {
      logger.error("[getTesterInfo] Exception:", e);
      return "ERROR";
    }
  }

  @ResponseBody
  @RequestMapping(
      value = "/simpleBot/saveTesterInfo",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE
  )
  public String saveTesterInfo(@RequestBody Map request) throws Exception {
    try {
      return voiceBotBLClient.saveTesterInfo(request);
    } catch (Exception e) {
      logger.error("[saveTesterInfo] Exception:", e);
      return "ERROR";
    }
  }

  @ResponseBody
  @RequestMapping(
      value = "/simpleBot/callList",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE
  )
  public int callList(@RequestBody Map request) throws Exception {
	  int waitingCount;
	  try {
//	    return voiceBotBLClient.callList(request);
	    int contractNo = Integer.parseInt(request.get("contract_no").toString());
		  waitingCount = simpleBotService.getWaitingCustomer(contractNo);
	    return waitingCount;
	  } catch (Exception e) {
	    logger.error("[callList] Exception:", e);
	    return 0;
	  }
  }

  @ResponseBody
  @RequestMapping(
      value = "/simpleBot/getSimpleBotList",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE
  )
  public List<Map> getSimpleBotList(@RequestBody Map request) throws Exception {
    List<Map> simpleBotList;
    try {
      String userId = (String) request.get("userId");
      String companyId = (String) request.get("companyId");
      String keyword = (String) request.get("keyword");

      if (companyId.isEmpty()) { // 심플봇
        simpleBotList = simpleBotService.getSimpleBotListFromUserId(userId, keyword);
      } else { // 음성봇 빌더
        simpleBotList = simpleBotService.getSimpleBotListFromCompanyId(companyId, keyword);
      }

      return simpleBotList;
    } catch (Exception e) {
      logger.error("[getSimpleBotList] Exception:", e);
      return null;
    }
  }

  @ResponseBody
  @RequestMapping(
      value = "/simpleBot/getSimplebotById",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE
  )
  public String getSimplebotById(@RequestBody Map request) throws Exception {
    SimpleBotVO simpleBotVO;
    try {
      int simplebotId = (Integer) request.get("simplebotId");

      simpleBotVO = simpleBotService.getSimplebotById(simplebotId);

    } catch (Exception e) {
      logger.error("[getSimplebotById] Exception:", e);
      return "";
    }

    Gson gson = new Gson();
    String simplebotJson = gson.toJson(simpleBotVO);
    return simplebotJson;
  }

  @ResponseBody
  @RequestMapping(
      value = "/simpleBot/deleteScenario",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE
  )
  public void deleteScenario(@RequestBody Map request) throws Exception {
    try {
      int host = (Integer) request.get("host");
      int simplebotId = (Integer) request.get("simplebotId");

      simpleBotService.deleteScenario(host, simplebotId);

    } catch (Exception e) {
      logger.error("[deleteScenario] Exception:", e);
    }
  }

  @ResponseBody
  @RequestMapping(
          value = "/ttsGet",
          method = RequestMethod.GET
  )
  public byte[] ttsGet(HttpServletRequest request, HttpServletResponse responsed)
          throws Exception {

	  String nowTTSString = "";
	  if (PropInfo.siteCustom.equals("mindslab")) {
		  nowTTSString = new String(((String) request.getParameter("nowText"))
				  .getBytes("8859_1"), "UTF-8");
	  } else {
		  nowTTSString = new String(((String) request.getParameter("nowText"))
				  .getBytes("UTF-8"), "UTF-8");
	  }

    String nowPost = PropInfo.SimpleBotTTSIP + ":" + PropInfo.SimpleBotTTSPort;
    String nowFilename = UUID.randomUUID().toString().replace("-","");
    System.out.println("TTSGET : " + nowPost + " :: " + nowFilename + " :: " + nowTTSString);
    HttpResponse response = null;
    try {
      HttpClient client = HttpClientBuilder.create().build(); // HttpClient 생성
      HttpPost postRequest = new HttpPost(nowPost + "/tts-rest/tts/block/speak"); //POST 메소드 URL 새성
      postRequest.setHeader("Accept", "*/*");
      postRequest.setHeader("Connection", "keep-alive");
      postRequest.setHeader("Content-Type", "application/json");

      StringEntity stringEntity = new StringEntity("" +
              "{\n" +
              "    \"name\": \""+"test"+"\",\n" +
              "    \"text\": \""+ nowTTSString +"\",\n" +
              "    \"audioEncoding\": \""+"WAV"+"\"" +
              "}", HTTP.UTF_8);

      postRequest.setEntity(stringEntity);
      response = client.execute(postRequest);
    } catch (Exception e){
      System.out.println("ERROR in simpleBotController Line 456");
      System.err.println(e.toString());
    }
    InputStream inputStream = response.getEntity().getContent();

    return IOUtils.toByteArray(inputStream);
  }
}
