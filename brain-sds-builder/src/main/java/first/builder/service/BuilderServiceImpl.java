package first.builder.service;


import com.google.common.collect.Lists;
import first.builder.dao.BuilderDAO;
import first.builder.vo.AnswerVO;
import first.builder.vo.IntentVO;
import first.builder.vo.LogVO;
import first.common.util.Criteria;
import first.common.util.ExcelRead;
import first.common.util.NqaUploadStatusList;
import first.common.util.ReadOption;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@Service("builderService")
public class BuilderServiceImpl implements BuilderService {

  @Resource(name="sqlSession")
  private SqlSessionFactory sqlSessionFactory;

  Logger logger = LoggerFactory.getLogger(this.getClass());

  @Resource(name = "builderDAO")
  private BuilderDAO builderDAO;

  @Override
  public String chatExcelUpload(File destFile, Map<String, Object> param) throws Exception {
    String errorMsg = "";
    try {
      Map<String, Object> resultMap;
      int sheet = 0;
      int hostNum;
      int backendInfoNum;
      int columnSize = 0;
      resultMap = builderDAO.accountCheck(param);
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
      Calendar calendar = Calendar.getInstance();
      String strToday = simpleDateFormat.format(calendar.getTime());
      Map<String, Object> dateMap = new HashMap<>();
      dateMap.put("date", strToday.substring(2));
      param = builderDAO.getHostIdBackEndId(param);
      hostNum = (int) param.get("no");
      backendInfoNum = (int) param.get("id");
      Map<String, Object> mapData = new HashMap<>();
      mapData.put("host", hostNum);
      mapData.put("bertItfId", backendInfoNum);
      mapData.put("date", strToday.substring(2));
      builderDAO.updateRegexIntent();
      builderDAO.bakDelIntentRel(mapData);
      builderDAO.bakDelBertIntent(mapData);
      String []columns = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
          "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X"};
      ReadOption excelReadOption = new ReadOption();
      excelReadOption.setFilePath(destFile.getAbsolutePath());
      excelReadOption.setOutputColumns(columns);
      excelReadOption.setStartRow(1);
      String[] colCheckArr = { "태스크(영)", "태스크(일)", "태스크(중)","다음태스크(영)", "다음태스크(일)", "다음태스크(중)"};
      List<Map<String, String>> excelContent = ExcelRead.read(excelReadOption);
      for (int i=0; i<columns.length; i++) {
        if (excelContent.get(0).get(columns[i]) != null && !"".equals(excelContent.get(0).get(columns[i]))) {
          if(Arrays.asList(colCheckArr).contains(excelContent.get(0).get(columns[i]).trim())){
            throw new Exception("excelColErr");
          }

          columnSize++;
        }
      }

      if (!(columnSize == 17 || columnSize == 20)) {
        throw new Exception("excelform");
      } else {
        excelContent.remove(0);
      }
      boolean kr = false;
      boolean en = false;
      boolean jp = false;
      boolean ch = false;
      Map<String, Object> commonMap = new HashMap<>();
      commonMap.put("account", resultMap.get("No"));
      int answerKr = 1;
      int answerEn = 1;
      int answerJp = 1;
      int answerCh = 1;
      int intentKr = 0;
      int intentEn = 0;
      int intentJp = 0;
      int intentCh = 0;
      List<AnswerVO> answerList = new ArrayList<>();
      List<IntentVO> intentList = new ArrayList<>();
      List<List<AnswerVO>> afterAnsList;
      List<List<IntentVO>> afterIntList;
      List<List<Map<String, Object>>> afterMapList;
      List<Map<String, Object>> mapList = new ArrayList<>();
      List<Map<String, Object>> fallbackMapList = new ArrayList<>();

      List<List<Map<String, Object>>> afterEntityMapList;
      List<Map<String, Object>> entityMapList = new ArrayList<>();
      String[] entityTaskCol = {"A", "D", "C", "B"};
      String[] entityNameCol = {"E", "H", "G", "F"};
      String[] entityValueCol = {"I", "J", "K", "L"};

      for (Map<String, String> article : excelContent) {
        IntentVO intent = new IntentVO();
        AnswerVO answer = new AnswerVO();
        int srcIntent;
        String[] intentCol = {"A", "D", "C", "B"};
        String[] intetRelSrcCol = {"A", "J", "G", "D"};
        String[] intetRelDestCol = {"B", "K", "H", "E"};
        String[] intetRelBertCol = {"C", "L", "I", "F"};
        int[] langCol = {1, 2, 3, 4};

        if (article.get("A").equals("Sheet_Change")) {
          if (sheet == 0) {

            answerList = answerList.stream().collect(Collectors.groupingBy(p-> p.getAnswer() + "_" + p.getLanguage()))
                    .entrySet().stream().map(t -> t.getValue().get(0)).collect(Collectors.toList());

            if (answerList.size() > 249) {
              afterAnsList = Lists.partition(answerList,
                  (int) (answerList.size() / (answerList.size() / 250 + 0.99)));
              for (int i = 0; i < afterAnsList.size(); i++) {
                builderDAO.insertAnswer(afterAnsList.get(i));
              }
            } else {
              builderDAO.insertAnswer(answerList);
            }

            if (intentList.size() > 174) {
              afterIntList = Lists.partition(intentList,
                  (int) (intentList.size() / (intentList.size() / 175 + 0.99)));
              for (int i = 0; i < afterIntList.size(); i++) {
                builderDAO.insertIntent(afterIntList.get(i));
              }
            } else {
              builderDAO.insertIntent(intentList);
            }

            if (kr == true) {
              commonMap.put("language", 1);
              builderDAO.updateScenario(commonMap);
              updateIntent(commonMap);
            }
            if (en == true) {
              commonMap.put("language", 2);
              builderDAO.updateScenario(commonMap);
              updateIntent(commonMap);
            }
            if (jp == true) {
              commonMap.put("language", 3);
              builderDAO.updateScenario(commonMap);
              updateIntent(commonMap);
            }
            if (ch == true) {
              commonMap.put("language", 4);
              builderDAO.updateScenario(commonMap);
              updateIntent(commonMap);
            }
          }
          if (sheet == 1) {

            mapList = mapList.stream().collect(Collectors.groupingBy(p-> p.get("intent") + "_" + p.get("lang")))
                    .entrySet().stream().map(t -> t.getValue().get(0)).collect(Collectors.toList());

            if (mapList.size() > 349) {
              afterMapList = Lists.partition(mapList,
                  (int) (mapList.size() / (mapList.size() / 350 + 0.99)));
              for (int i = 0; i < afterMapList.size(); i++) {
                builderDAO.insertBertIntent(afterMapList.get(i));
              }
            } else if (mapList.size() > 0) {
              builderDAO.insertBertIntent(mapList);
            }
          } else if (sheet == 2) {
            if (mapList.size() > 299) {
              afterMapList = Lists.partition(mapList,
                  (int) (mapList.size() / (mapList.size() / 300 + 0.99)));
              for (int i = 0; i < afterMapList.size(); i++) {
                builderDAO.insertIntentRel(afterMapList.get(i));
              }
            } else if (mapList.size() > 0){
              builderDAO.insertIntentRel(mapList);
            }

            // 엑셀 업로드시 task 별 bert intent 데이터 추가
            List<Map<String, Object>> srcList = builderDAO.selectSrcList((int) mapData.get("bertItfId"));
            builderDAO.updateIntentTask(srcList);
          }
          sheet++;
          mapList = new ArrayList<>();
        }

        if (sheet == 0) {
          if (article.get("M") != null && article.get("G") != null  && !"".equals(article.get("G"))) {
            answer.setAccount(resultMap.get("No") + "");
            answer.setLanguage("1");
            answer.setAnswer(article.get("M"));
            if (answerKr == 1) {
              builderDAO.deleteAnswer(answer);
            }
            answer.setNum(answerKr);
            if (article.get("Q") != null && !"".equals(article.get("Q"))) {
              answer.setServerUrl(article.get("Q"));
            }
            answerList.add(answer);
            answerKr++;
          }

          if (article.get("P") != null && article.get("G") != null  && !"".equals(article.get("G"))) {
            answer = new AnswerVO();
            answer.setAccount(resultMap.get("No") + "");
            answer.setLanguage("2");
            answer.setAnswer(article.get("P"));
            if (answerEn == 1) {
              builderDAO.deleteAnswer(answer);
            }
            answer.setNum(answerEn);
            if (article.get("Q") != null && !"".equals(article.get("Q"))) {
              answer.setServerUrl(article.get("Q"));
            }
            answerList.add(answer);
            answerEn++;
          }

          if (article.get("O") != null && article.get("G") != null  && !"".equals(article.get("G"))) {
            answer = new AnswerVO();
            answer.setAccount(resultMap.get("No") + "");
            answer.setLanguage("3");
            answer.setAnswer(article.get("O"));
            if (answerJp == 1) {
              builderDAO.deleteAnswer(answer);
            }
            answer.setNum(answerJp);
            if (article.get("Q") != null && !"".equals(article.get("Q"))) {
              answer.setServerUrl(article.get("Q"));
            }
            answerList.add(answer);
            answerJp++;
          }

          if (article.get("N") != null && article.get("G") != null  && !"".equals(article.get("G"))) {
            answer = new AnswerVO();
            answer.setAccount(resultMap.get("No") + "");
            answer.setLanguage("4");
            answer.setAnswer(article.get("N"));
            if (answerCh == 1) {
              builderDAO.deleteAnswer(answer);
            }
            answer.setNum(answerCh);
            if (article.get("Q") != null && !"".equals(article.get("Q"))) {
              answer.setServerUrl(article.get("Q"));
            }
            answerList.add(answer);
            answerCh++;
          }

          intent.setEntity("N");
          intent.setImage(article.get("B"));
          intent.setNum(article.get("A"));
          intent.setAccount(resultMap.get("No") + "");

          if (article.get("H") != null && !"".equals(article.get("H"))) {
            intent.setLanguage(1);
            intent.setIntent(article.get("G"));
            intent.setDisplay(article.get("H"));
            intent.setAnswer(article.get("M"));
            intent.setDescription(article.get("C"));
            intent.setNextIntent(article.get("L").toUpperCase());
            if (intentKr == 0) {
              builderDAO.deleteIntent(intent);
            }
            intentList.add(intent);
            intentKr++;
            kr = true;
          }

          if (article.get("K") != null && !"".equals(article.get("K"))) {
            intent = new IntentVO();
            intent.setEntity("N");
            intent.setImage(article.get("B"));
            intent.setNum(article.get("A"));
            intent.setAccount(resultMap.get("No") + "");
            intent.setLanguage(2);
            intent.setIntent(article.get("G"));
            intent.setDisplay(article.get("K"));
            intent.setAnswer(article.get("P"));
            intent.setDescription(article.get("F"));
            intent.setNextIntent(article.get("L").toUpperCase());
            if (intentEn == 0) {
              builderDAO.deleteIntent(intent);
            }
            intentList.add(intent);
            intentEn++;
            en = true;
          }

          if (article.get("J") != null && !"".equals(article.get("J"))) {
            intent = new IntentVO();
            intent.setEntity("N");
            intent.setImage(article.get("B"));
            intent.setNum(article.get("A"));
            intent.setAccount(resultMap.get("No") + "");
            intent.setLanguage(3);
            intent.setIntent(article.get("G"));
            intent.setDisplay(article.get("J"));
            intent.setAnswer(article.get("O"));
            intent.setDescription(article.get("E"));
            intent.setNextIntent(article.get("L").toUpperCase());
            if (intentJp == 0) {
              builderDAO.deleteIntent(intent);
            }
            intentList.add(intent);
            intentJp++;
            jp = true;
          }

          if (article.get("I") != null && !"".equals(article.get("I"))) {
            intent = new IntentVO();
            intent.setEntity("N");
            intent.setImage(article.get("B"));
            intent.setNum(article.get("A"));
            intent.setAccount(resultMap.get("No") + "");
            intent.setLanguage(4);
            intent.setIntent(article.get("G"));
            intent.setDisplay(article.get("I"));
            intent.setAnswer(article.get("N"));
            intent.setDescription(article.get("D"));
            intent.setNextIntent(article.get("L").toUpperCase());
            if (intentCh == 0) {
              builderDAO.deleteIntent(intent);
            }
            intentList.add(intent);
            intentCh++;
            ch = true;
          }
        } else if (sheet == 1) {
          for (int i = 0; i < 4; i++) {
            if (article.get(intentCol[i]) != null && !"".equals(article.get(intentCol[i]))
                && !"Sheet_Change".equals(article.get(intentCol[i])) && !(article.get(intentCol[i]).contains("Intent"))) {
              mapData = new HashMap<>();
              mapData.put("bertItfId", backendInfoNum);
              mapData.put("lang", langCol[i]);
              mapData.put("intent", article.get(intentCol[i]));
              mapList.add(mapData);
            }
          }
        } else if (sheet == 2) {
          for (int i = 0; i < 4; i++) {
            if (article.get(intetRelSrcCol[i]) != null && !"".equals(article.get(intetRelSrcCol[i]))
                && article.get(intetRelDestCol[i]) != null && !""
                .equals(article.get(intetRelDestCol[i]))
                && article.get(intetRelBertCol[i]) != null && !""
                .equals(article.get(intetRelBertCol[i])) && !(article.get(intentCol[i]).contains("Task"))
                && !(article.get(intentCol[i]).contains("Intent"))) {
              mapData = new HashMap<>();
              mapData.put("bertItfId", backendInfoNum);
              mapData.put("host", hostNum);
              mapData.put("lang", langCol[i]);

              String srcIntentNm = article.get(intetRelSrcCol[i]);
              String bertIntentNm = article.get(intetRelBertCol[i]);
              String destIntentNm = article.get(intetRelDestCol[i]);

              mapData.put("intent", srcIntentNm);
              if (srcIntentNm.equals("*")) {
                srcIntent = 0;
              } else {
                try{ // Src_Task 확인
                  srcIntent = builderDAO.getIntentNum(mapData);
                }catch (NullPointerException e){
                  throw new Exception("NotFoundSrcIntent :" + mapData.get("intent"));
                }
              }

              mapData.replace("intent", bertIntentNm);
              try { // Bert_Intent 확인
                builderDAO.getBertIntentNum(mapData);
              } catch (NullPointerException e) {
                throw new Exception("NotFoundBertIntent :" + mapData.get("intent"));
              }

              mapData.replace("intent", destIntentNm);
              try { // Dest_Task 확인
                builderDAO.getIntentNum(mapData);
              } catch (NullPointerException e) {
                throw new Exception("NotFoundDestIntent :" + mapData.get("intent"));
              }

              mapData.put("bertIntent", bertIntentNm);
              mapData.put("srcIntent", srcIntent);
              mapList.add(mapData);
            }
          }
        } else if (sheet == 3){
          for (int i = 0; i < 4; i++) {
            if (!article.get("A").equals("Sheet_Change") && article.get(intentCol[i]) != null && !""
                .equals(article.get(intentCol[i])) && !(article.get(intentCol[i]).contains("Default"))) {
              mapData = new HashMap<>();
              mapData.put("host", hostNum);
              mapData.put("lang", langCol[i]);
              mapData.put("intent", article.get(intentCol[i]));
              fallbackMapList.add(mapData);
            }
          }
        } else if (sheet == 4 && !"Sheet_Change".equals(article.get("A"))) { // Entity Upload.
          for(int i = 0; i < 4; i++){ // Language
            if(article.get(entityTaskCol[i]) != null && !"".equals(article.get(entityTaskCol[i])) &&
                    article.get(entityNameCol[i]) != null && !"".equals(article.get(entityNameCol[i])) &&
                    !article.get(entityTaskCol[i]).contains("Entity_Task")){
              Map<String, Object> tmpMap = new HashMap<>();
              tmpMap.put("host", hostNum);
              tmpMap.put("lang", langCol[i]);
              tmpMap.put("intent", article.get(entityTaskCol[i]));
              tmpMap.put("entityTaskNo", builderDAO.getIntentNum(tmpMap));
              tmpMap.put("entityName", article.get(entityNameCol[i]));
              tmpMap.put("entityValue", article.get(entityValueCol[i]) != "" ? article.get(entityValueCol[i]) : "*");
              entityMapList.add(tmpMap);
            }
          }
        }
      }
      if (fallbackMapList.size() > 0) {
        builderDAO.delInsertFallback(fallbackMapList);
      }
      mapData.put("date", strToday.substring(2));
      if("".equals(mapData.get("bertItfId")) || mapData.get("bertItfId") == null){
        mapData.put("bertItfId", backendInfoNum);
      }
      builderDAO.updateRegexBertNo(mapData);

      // entity delete, insert & intent(entity) update
      builderDAO.deleteEntities(hostNum);
      if(entityMapList.size()>0){
        builderDAO.insertEntitiesList(entityMapList);
        builderDAO.updateEntityOfIntent(hostNum);
      }

    } catch (Exception e) {
      logger.error("ChatbotExcelUpload Exception:", e);
      String b = e.getMessage();
      if ("excelform".equals(b)) {
        errorMsg = "엑셀 형식이 잘못 되었습니다. 엑셀 컬럼을 다시 확인해주세요.";
      } else if ("excelColErr".equals(b)) {
        errorMsg = "엑셀 컬럼에 태스크 : 영어,일본어,중국어 / 다음태스크 : 영어,일본어,중국어 를 제거해주세요.";
      } else if (b != null && b.contains("For input string")) {
        errorMsg = "데이터 형식이 잘못되었습니다. 잘못된 엑셀 데이터 : " + b;
      } else if (b != null && b.contains("NotFoundSrcIntent")) {
        errorMsg = "Relation 시트의 Src_Task 데이터를 확인해주세요.<br> 확인이 필요한 데이터 : \"" + b.split(":")[1] + "\"";
      } else if (b != null && b.contains("NotFoundDestIntent")) {
        errorMsg = "Relation 시트의 Dest_Task 데이터를 확인해주세요.<br> 확인이 필요한 데이터 : \"" + b.split(":")[1] + "\"";
      } else if (b != null && b.contains("NotFoundBertIntent")) {
        errorMsg = "Relation 시트의 Bert_Intent 데이터를 확인해주세요.<br> 확인이 필요한 데이터 : \"" + b.split(":")[1] + "\"";
      } else {
        errorMsg = "데이터 정합성 오류입니다. 다음테스크 데이터 및 Intent,BertIntent,Relation 데이터들이 정확하게 들어갔는지 확인해주세요.";
      }
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return errorMsg;
    }
    return "모든 데이터가 업로드 되었습니다.";
  }
  @Override
  public String chatExcelUploadHTask(File destFile, Map<String, Object> param) throws Exception {
    String errorMsg = "";
    try {
      Map<String, Object> resultMap;
      int sheet = 0;
      int hostNum;
      int backendInfoNum;
      int columnSize = 0;
      resultMap = builderDAO.accountCheck(param);
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
      Calendar calendar = Calendar.getInstance();
      String strToday = simpleDateFormat.format(calendar.getTime());
      Map<String, Object> dateMap = new HashMap<>();
      dateMap.put("date", strToday.substring(2));
      param = builderDAO.getHostIdBackEndId(param);
      hostNum = (int) param.get("no");
      backendInfoNum = (int) param.get("id");
      Map<String, Object> mapData = new HashMap<>();
      mapData.put("host", hostNum);
      mapData.put("bertItfId", backendInfoNum);
      mapData.put("date", strToday.substring(2));
      builderDAO.updateRegexIntent();
      builderDAO.bakDelIntentRel(mapData);
      builderDAO.bakDelBertIntent(mapData);
      String []columns = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
          "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
      ReadOption excelReadOption = new ReadOption();
      excelReadOption.setFilePath(destFile.getAbsolutePath());
      excelReadOption.setOutputColumns(columns);
      excelReadOption.setStartRow(1);

      String[] colCheckArr = { "태스크(영)", "태스크(일)", "태스크(중)","다음태스크(영)", "다음태스크(일)", "다음태스크(중)"};
      List<Map<String, String>> excelContent = ExcelRead.read(excelReadOption);
      for (int i=0; i<columns.length; i++) {
        if (excelContent.get(0).get(columns[i]) != null && !"".equals(excelContent.get(0).get(columns[i]))) {
          if(Arrays.asList(colCheckArr).contains(excelContent.get(0).get(columns[i]).trim())){
            throw new Exception("excelColErr");
          }

          columnSize++;
        }
      }

      if (!(columnSize == 16 || columnSize == 17 || columnSize == 20)) {
        throw new Exception("excelform");
      } else {
        excelContent.remove(0);
      }
      boolean kr = false;
      boolean en = false;
      boolean jp = false;
      boolean ch = false;
      Map<String, Object> commonMap = new HashMap<>();
      commonMap.put("account", resultMap.get("No"));
      int answerKr = 1;
      int answerEn = 1;
      int answerJp = 1;
      int answerCh = 1;
      int intentKr = 0;
      int intentEn = 0;
      int intentJp = 0;
      int intentCh = 0;
      List<AnswerVO> answerList = new ArrayList<>();
      List<IntentVO> intentList = new ArrayList<>();
      List<List<AnswerVO>> afterAnsList;
      List<List<IntentVO>> afterIntList;
      List<List<Map<String, Object>>> afterMapList;
      List<Map<String, Object>> mapList = new ArrayList<>();
      List<Map<String, Object>> fallbackMapList = new ArrayList<>();

      List<List<Map<String, Object>>> afterEntityMapList;
      List<Map<String, Object>> entityMapList = new ArrayList<>();
      String[] entityTaskCol = {"A", "D", "C", "B"};
      String[] entityNameCol = {"E", "H", "G", "F"};
      String[] entityValueCol = {"I", "J", "K", "L"};

      for (Map<String, String> article : excelContent) {
        IntentVO intent = new IntentVO();
        AnswerVO answer = new AnswerVO();
        int srcIntent;
        String[] intentCol = {"A", "D", "C", "B"};
        String[] intetRelSrcCol = {"A", "J", "G", "D"};
        String[] intetRelDestCol = {"B", "K", "H", "E"};
        String[] intetRelBertCol = {"C", "L", "I", "F"};
        int[] langCol = {1, 2, 3, 4};

        if (article.get("A").equals("Sheet_Change")) {
          if (sheet == 0) {

            answerList = answerList.stream().collect(Collectors.groupingBy(p-> p.getAnswer() + "_" + p.getLanguage()))
                    .entrySet().stream().map(t -> t.getValue().get(0)).collect(Collectors.toList());

            if (answerList.size() > 249) {
              afterAnsList = Lists.partition(answerList,
                  (int) (answerList.size() / (answerList.size() / 250 + 0.99)));
              for (int i = 0; i < afterAnsList.size(); i++) {
                builderDAO.insertAnswer(afterAnsList.get(i));
              }
            } else {
              builderDAO.insertAnswer(answerList);
            }

            if (intentList.size() > 174) {
              afterIntList = Lists.partition(intentList,
                  (int) (intentList.size() / (intentList.size() / 175 + 0.99)));
              for (int i = 0; i < afterIntList.size(); i++) {
                builderDAO.insertIntentHTask(afterIntList.get(i));
              }
            } else {
              builderDAO.insertIntentHTask(intentList);
            }

            if (kr == true) {
              commonMap.put("language", 1);
              builderDAO.updateScenario(commonMap);
              updateIntent(commonMap);
            }
            if (en == true) {
              commonMap.put("language", 2);
              builderDAO.updateScenario(commonMap);
              updateIntent(commonMap);
            }
            if (jp == true) {
              commonMap.put("language", 3);
              builderDAO.updateScenario(commonMap);
              updateIntent(commonMap);
            }
            if (ch == true) {
              commonMap.put("language", 4);
              builderDAO.updateScenario(commonMap);
              updateIntent(commonMap);
            }
          }
          if (sheet == 1) {

            mapList = mapList.stream().collect(Collectors.groupingBy(p-> p.get("intent") + "_" + p.get("lang")))
                    .entrySet().stream().map(t -> t.getValue().get(0)).collect(Collectors.toList());

            if (mapList.size() > 349) {
              afterMapList = Lists.partition(mapList,
                  (int) (mapList.size() / (mapList.size() / 350 + 0.99)));
              for (int i = 0; i < afterMapList.size(); i++) {
                builderDAO.insertBertIntent(afterMapList.get(i));
              }
            } else if (mapList.size() > 0) {
              builderDAO.insertBertIntent(mapList);
            }
          } else if (sheet == 2) {
            if (mapList.size() > 299) {
              afterMapList = Lists.partition(mapList,
                  (int) (mapList.size() / (mapList.size() / 300 + 0.99)));
              for (int i = 0; i < afterMapList.size(); i++) {
                builderDAO.insertIntentRel(afterMapList.get(i));
              }
            } else if (mapList.size() > 0){
              builderDAO.insertIntentRel(mapList);
            }

            // 엑셀 업로드시 task 별 bert intent 데이터 추가
            List<Map<String, Object>> srcList = builderDAO.selectSrcList((int) mapData.get("bertItfId"));
            builderDAO.updateIntentTask(srcList);
          }
          sheet++;
          mapList = new ArrayList<>();
        }

        if (sheet == 0) {
          if (article.get("M") != null && article.get("G") != null  && !"".equals(article.get("G"))) {
            answer.setAccount(resultMap.get("No") + "");
            answer.setLanguage("1");
            answer.setAnswer(article.get("M"));
            if (answerKr == 1) {
              builderDAO.deleteAnswer(answer);
            }
            answer.setNum(answerKr);
            if (article.get("Q") != null && !"".equals(article.get("Q"))) {
              answer.setServerUrl(article.get("Q"));
            }
            answerList.add(answer);
            answerKr++;
          }

          if (article.get("P") != null && article.get("G") != null  && !"".equals(article.get("G"))) {
            answer = new AnswerVO();
            answer.setAccount(resultMap.get("No") + "");
            answer.setLanguage("2");
            answer.setAnswer(article.get("P"));
            if (answerEn == 1) {
              builderDAO.deleteAnswer(answer);
            }
            answer.setNum(answerEn);
            if (article.get("Q") != null && !"".equals(article.get("Q"))) {
              answer.setServerUrl(article.get("Q"));
            }
            answerList.add(answer);
            answerEn++;
          }

          if (article.get("O") != null && article.get("G") != null  && !"".equals(article.get("G"))) {
            answer = new AnswerVO();
            answer.setAccount(resultMap.get("No") + "");
            answer.setLanguage("3");
            answer.setAnswer(article.get("O"));
            if (answerJp == 1) {
              builderDAO.deleteAnswer(answer);
            }
            answer.setNum(answerJp);
            if (article.get("Q") != null && !"".equals(article.get("Q"))) {
              answer.setServerUrl(article.get("Q"));
            }
            answerList.add(answer);
            answerJp++;
          }

          if (article.get("N") != null && article.get("G") != null  && !"".equals(article.get("G"))) {
            answer = new AnswerVO();
            answer.setAccount(resultMap.get("No") + "");
            answer.setLanguage("4");
            answer.setAnswer(article.get("N"));
            if (answerCh == 1) {
              builderDAO.deleteAnswer(answer);
            }
            answer.setNum(answerCh);
            if (article.get("Q") != null && !"".equals(article.get("Q"))) {
              answer.setServerUrl(article.get("Q"));
            }
            answerList.add(answer);
            answerCh++;
          }

          intent.setEntity("N");
          intent.setImage(article.get("B"));
          intent.setNum(article.get("A"));
          intent.setAccount(resultMap.get("No") + "");

          if (article.get("H") != null && !"".equals(article.get("H"))) {
            intent.setLanguage(1);
            intent.setIntent(article.get("G"));
            intent.setDisplay(article.get("H"));
            intent.setAnswer(article.get("M"));
            intent.setDescription(article.get("C"));
            intent.setNextIntent(article.get("L").toUpperCase());

            //h 컬럼
            intent.sethTask(article.get("R") != null && !"".equals(article.get("R")) ? article.get("R") : "");
            if(article.get("S") != null && !"".equals(article.get("S"))) intent.sethItem(article.get("S"));
            if(article.get("T") != null && !"".equals(article.get("T"))) intent.sethParam(article.get("T"));

            if (intentKr == 0) {
              builderDAO.deleteIntent(intent);
            }
            intentList.add(intent);
            intentKr++;
            kr = true;
          }

          if (article.get("K") != null && !"".equals(article.get("K"))) {
            intent = new IntentVO();
            intent.setEntity("N");
            intent.setImage(article.get("B"));
            intent.setNum(article.get("A"));
            intent.setAccount(resultMap.get("No") + "");
            intent.setLanguage(2);
            intent.setIntent(article.get("G"));
            intent.setDisplay(article.get("K"));
            intent.setAnswer(article.get("P"));
            intent.setDescription(article.get("F"));
            intent.setNextIntent(article.get("L").toUpperCase());

            //h 컬럼
            intent.sethTask(article.get("R") != null && !"".equals(article.get("R")) ? article.get("R") : "");
            if(article.get("S") != null && !"".equals(article.get("S"))) intent.sethItem(article.get("S"));
            if(article.get("T") != null && !"".equals(article.get("T"))) intent.sethParam(article.get("T"));

            if (intentEn == 0) {
              builderDAO.deleteIntent(intent);
            }
            intentList.add(intent);
            intentEn++;
            en = true;
          }

          if (article.get("J") != null && !"".equals(article.get("J"))) {
            intent = new IntentVO();
            intent.setEntity("N");
            intent.setImage(article.get("B"));
            intent.setNum(article.get("A"));
            intent.setAccount(resultMap.get("No") + "");
            intent.setLanguage(3);
            intent.setIntent(article.get("G"));
            intent.setDisplay(article.get("J"));
            intent.setAnswer(article.get("O"));
            intent.setDescription(article.get("E"));
            intent.setNextIntent(article.get("L").toUpperCase());

            //h 컬럼
            intent.sethTask(article.get("R") != null && !"".equals(article.get("R")) ? article.get("R") : "");
            if(article.get("S") != null && !"".equals(article.get("S"))) intent.sethItem(article.get("S"));
            if(article.get("T") != null && !"".equals(article.get("T"))) intent.sethParam(article.get("T"));

            if (intentJp == 0) {
              builderDAO.deleteIntent(intent);
            }
            intentList.add(intent);
            intentJp++;
            jp = true;
          }

          if (article.get("I") != null && !"".equals(article.get("I"))) {
            intent = new IntentVO();
            intent.setEntity("N");
            intent.setImage(article.get("B"));
            intent.setNum(article.get("A"));
            intent.setAccount(resultMap.get("No") + "");
            intent.setLanguage(4);
            intent.setIntent(article.get("G"));
            intent.setDisplay(article.get("I"));
            intent.setAnswer(article.get("N"));
            intent.setDescription(article.get("D"));
            intent.setNextIntent(article.get("L").toUpperCase());

            //h 컬럼
            intent.sethTask(article.get("R") != null && !"".equals(article.get("R")) ? article.get("R") : "");
            if(article.get("S") != null && !"".equals(article.get("S"))) intent.sethItem(article.get("S"));
            if(article.get("T") != null && !"".equals(article.get("T"))) intent.sethParam(article.get("T"));

            if (intentCh == 0) {
              builderDAO.deleteIntent(intent);
            }
            intentList.add(intent);
            intentCh++;
            ch = true;
          }
        } else if (sheet == 1) {
          for (int i = 0; i < 4; i++) {
            if (article.get(intentCol[i]) != null && !"".equals(article.get(intentCol[i]))
                && !"Sheet_Change".equals(article.get(intentCol[i])) && !(article.get(intentCol[i]).contains("Intent"))) {
              mapData = new HashMap<>();
              mapData.put("bertItfId", backendInfoNum);
              mapData.put("lang", langCol[i]);
              mapData.put("intent", article.get(intentCol[i]));
              mapList.add(mapData);
            }
          }
        } else if (sheet == 2) {
          for (int i = 0; i < 4; i++) {
            if (article.get(intetRelSrcCol[i]) != null && !"".equals(article.get(intetRelSrcCol[i]))
                && article.get(intetRelDestCol[i]) != null && !""
                .equals(article.get(intetRelDestCol[i]))
                && article.get(intetRelBertCol[i]) != null && !""
                .equals(article.get(intetRelBertCol[i])) && !(article.get(intentCol[i]).contains("Task"))
                && !(article.get(intentCol[i]).contains("Intent"))) {
              mapData = new HashMap<>();
              mapData.put("bertItfId", backendInfoNum);
              mapData.put("host", hostNum);
              mapData.put("lang", langCol[i]);

              String srcIntentNm = article.get(intetRelSrcCol[i]);
              String bertIntentNm = article.get(intetRelBertCol[i]);
              String destIntentNm = article.get(intetRelDestCol[i]);

              mapData.put("intent", srcIntentNm);
              if (srcIntentNm.equals("*")) {
                srcIntent = 0;
              } else {
                try{ // Src_Task 확인
                  srcIntent = builderDAO.getIntentNum(mapData);
                }catch (NullPointerException e){
                  throw new Exception("NotFoundSrcIntent :" + mapData.get("intent"));
                }
              }

              mapData.replace("intent", bertIntentNm);
              try { // Bert_Intent 확인
                builderDAO.getBertIntentNum(mapData);
              } catch (NullPointerException e) {
                throw new Exception("NotFoundBertIntent :" + mapData.get("intent"));
              }

              mapData.replace("intent", destIntentNm);
              try { // Dest_Task 확인
                builderDAO.getIntentNum(mapData);
              } catch (NullPointerException e) {
                throw new Exception("NotFoundDestIntent :" + mapData.get("intent"));
              }

              mapData.put("bertIntent", bertIntentNm);
              mapData.put("srcIntent", srcIntent);
              mapList.add(mapData);
            }
          }
        } else if (sheet == 3){
          for (int i = 0; i < 4; i++) {
            if (!article.get("A").equals("Sheet_Change") && article.get(intentCol[i]) != null && !""
                .equals(article.get(intentCol[i])) && !(article.get(intentCol[i]).contains("Default"))) {
              mapData = new HashMap<>();
              mapData.put("host", hostNum);
              mapData.put("lang", langCol[i]);
              mapData.put("intent", article.get(intentCol[i]));
              fallbackMapList.add(mapData);
            }
          }
        } else if (sheet == 4 && !"Sheet_Change".equals(article.get("A"))) { // Entity Upload.
          for(int i = 0; i < 4; i++){ // Language
            if(article.get(entityTaskCol[i]) != null && !"".equals(article.get(entityTaskCol[i])) &&
                    article.get(entityNameCol[i]) != null && !"".equals(article.get(entityNameCol[i])) &&
                    !article.get(entityTaskCol[i]).contains("Entity_Task")){
              Map<String, Object> tmpMap = new HashMap<>();
              tmpMap.put("host", hostNum);
              tmpMap.put("lang", langCol[i]);
              tmpMap.put("intent", article.get(entityTaskCol[i]));
              tmpMap.put("entityTaskNo", builderDAO.getIntentNum(tmpMap));
              tmpMap.put("entityName", article.get(entityNameCol[i]));
              tmpMap.put("entityValue", article.get(entityValueCol[i]) != "" ? article.get(entityValueCol[i]) : "*");
              entityMapList.add(tmpMap);
            }
          }
        }
      }
      if (fallbackMapList.size() > 0) {
        builderDAO.delInsertFallback(fallbackMapList);
      }
      mapData.put("date", strToday.substring(2));
      if("".equals(mapData.get("bertItfId")) || mapData.get("bertItfId") == null){
        mapData.put("bertItfId", backendInfoNum);
      }
      builderDAO.updateRegexBertNo(mapData);

      // entity delete, insert & intent(entity) update
      builderDAO.deleteEntities(hostNum);
      if(entityMapList.size()>0){
        builderDAO.insertEntitiesList(entityMapList);
        builderDAO.updateEntityOfIntent(hostNum);
      }

    } catch (Exception e) {
      logger.error("ChatbotExcelUpload Exception:", e);
      String b = e.getMessage();
      if ("excelform".equals(b)) {
        errorMsg = "엑셀 형식이 잘못 되었습니다. 엑셀 컬럼을 다시 확인해주세요.";
      } else if ("excelColErr".equals(b)) {
        errorMsg = "엑셀 컬럼에 태스크 : 영어,일본어,중국어 / 다음태스크 : 영어,일본어,중국어 를 제거해주세요.";
      } else if (b != null && b.contains("For input string")) {
        errorMsg = "데이터 형식이 잘못되었습니다. 잘못된 엑셀 데이터 : " + b;
      } else if (b != null && b.contains("NotFoundSrcIntent")) {
        errorMsg = "Relation 시트의 Src_Task 데이터를 확인해주세요.<br> 확인이 필요한 데이터 : \"" + b.split(":")[1] + "\"";
      } else if (b != null && b.contains("NotFoundDestIntent")) {
        errorMsg = "Relation 시트의 Dest_Task 데이터를 확인해주세요.<br> 확인이 필요한 데이터 : \"" + b.split(":")[1] + "\"";
      } else if (b != null && b.contains("NotFoundBertIntent")) {
        errorMsg = "Relation 시트의 Bert_Intent 데이터를 확인해주세요.<br> 확인이 필요한 데이터 : \"" + b.split(":")[1] + "\"";
      } else {
        errorMsg = "데이터 정합성 오류입니다. 다음테스크 데이터 및 Intent,BertIntent,Relation 데이터들이 정확하게 들어갔는지 확인해주세요.";
      }
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return errorMsg;
    }
    return "모든 데이터가 업로드 되었습니다.";
  }

  @Transactional
  @Override
  public void excelUpload(File destFile, Map<String, Object> map) throws Exception {
    int columnSize = 0;
    Map<String, Object> resultMap = new HashMap<>();
    resultMap = builderDAO.accountCheck(map);
    if (resultMap == null || resultMap.isEmpty()) {
      builderDAO.insertAccount(map);
      resultMap = builderDAO.accountCheck(map);
    }

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
    Calendar calendar = Calendar.getInstance();
    String strToday = simpleDateFormat.format(calendar.getTime());
    Map<String, Object> dateMap = new HashMap<>();
    dateMap.put("date", strToday.substring(2));

    ReadOption excelReadOption = new ReadOption();
    excelReadOption.setFilePath(destFile.getAbsolutePath());
    String []columns = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
        "O", "P", "Q", "R", "S", "T", "U", "V", "W"};
    excelReadOption
        .setOutputColumns(columns);
    excelReadOption.setStartRow(1);

    List<Map<String, String>> excelContent = ExcelRead.read(excelReadOption);
    for (int i=0; i<columns.length; i++) {
      if (excelContent.get(0).get(columns[i]) != null && !"".equals(excelContent.get(0).get(columns[i]))) {
        columnSize++;
      }
    }

    if (columnSize != 22) {
      throw new Exception();
    } else {
      excelContent.remove(0);
    }
    boolean kr = false;
    boolean en = false;
    boolean jp = false;
    boolean ch = false;
    Map<String, Object> commonMap = new HashMap<>();
    commonMap.put("account", resultMap.get("No"));
    int krAnswerNum = 0;
    int enAnswerNum = 0;
    int jpAnswerNum = 0;
    int chAnswerNum = 0;
    int answerKr = 1;
    int answerEn = 1;
    int answerJp = 1;
    int answerCh = 1;
    int intentKr = 0;
    int intentEn = 0;
    int intentJp = 0;
    int intentCh = 0;
    List<AnswerVO> answerList = new ArrayList<>();
    List<IntentVO> intentList = new ArrayList<>();
    List<List<AnswerVO>> afterAnsList = new ArrayList<>();
    List<List<IntentVO>> afterIntList = new ArrayList<>();

    for (Map<String, String> article : excelContent) {
      IntentVO intent = new IntentVO();
      AnswerVO answer = new AnswerVO();

      if (article.get("S") != null && !"".equals(article.get("S"))) {
        answer.setAccount(resultMap.get("No") + "");
        answer.setLanguage("1");
        answer.setAnswer(article.get("S"));
        if (answerKr == 1) {
          builderDAO.deleteAnswer(answer);
        }
        answer.setNum(answerKr);
        answerList.add(answer);
        answerKr++;
      }

      if (article.get("V") != null && !"".equals(article.get("V"))) {
        answer = new AnswerVO();
        answer.setAccount(resultMap.get("No") + "");
        answer.setLanguage("2");
        answer.setAnswer(article.get("V"));
        if (answerEn == 1) {
          builderDAO.deleteAnswer(answer);
        }
        answer.setNum(answerEn);
        answerList.add(answer);
        answerEn++;
      }

      if (article.get("U") != null && !"".equals(article.get("U"))) {
        answer = new AnswerVO();
        answer.setAccount(resultMap.get("No") + "");
        answer.setLanguage("3");
        answer.setAnswer(article.get("U"));
        if (answerJp == 1) {
          builderDAO.deleteAnswer(answer);
        }
        answer.setNum(answerJp);
        answerList.add(answer);
        answerJp++;
      }

      if (article.get("T") != null && !"".equals(article.get("T"))) {
        answer = new AnswerVO();
        answer.setAccount(resultMap.get("No") + "");
        answer.setLanguage("4");
        answer.setAnswer(article.get("T"));
        if (answerCh == 1) {
          builderDAO.deleteAnswer(answer);
        }
        answer.setNum(answerCh);
        answerList.add(answer);
        answerCh++;
      }

      intent.setEntity("N");
      intent.setImage(article.get("B"));
      intent.setNum(article.get("A"));
      intent.setAccount(resultMap.get("No") + "");

      if (article.get("K") != null && !"".equals(article.get("K"))) {
        intent.setLanguage(1);
        intent.setIntent(article.get("G"));
        intent.setDisplay(article.get("K"));
        intent.setAnswer(article.get("S"));
        intent.setDescription(article.get("C"));
        intent.setNextIntent(article.get("O"));
        if (intentKr == 0) {
          builderDAO.deleteIntent(intent);
        }
        intentList.add(intent);
        intentKr++;
        kr = true;
      }

      if (article.get("N") != null && !"".equals(article.get("N"))) {
        intent = new IntentVO();
        intent.setEntity("N");
        intent.setImage(article.get("B"));
        intent.setNum(article.get("A"));
        intent.setAccount(resultMap.get("No") + "");
        intent.setLanguage(2);
        intent.setIntent(article.get("G"));
        intent.setDisplay(article.get("N"));
        intent.setAnswer(article.get("V"));
        intent.setDescription(article.get("F"));
        intent.setNextIntent(article.get("R"));
        if (intentEn == 0) {
          builderDAO.deleteIntent(intent);
        }
        intentList.add(intent);
        intentEn++;
        en = true;
      }

      if (article.get("M") != null && !"".equals(article.get("M"))) {
        intent = new IntentVO();
        intent.setEntity("N");
        intent.setImage(article.get("B"));
        intent.setNum(article.get("A"));
        intent.setAccount(resultMap.get("No") + "");
        intent.setLanguage(3);
        intent.setIntent(article.get("G"));
        intent.setDisplay(article.get("M"));
        intent.setAnswer(article.get("U"));
        intent.setDescription(article.get("E"));
        intent.setNextIntent(article.get("Q"));
        if (intentJp == 0) {
          builderDAO.deleteIntent(intent);
        }
        intentList.add(intent);
        intentJp++;
        jp = true;
      }

      if (article.get("L") != null && !"".equals(article.get("L"))) {
        intent = new IntentVO();
        intent.setEntity("N");
        intent.setImage(article.get("B"));
        intent.setNum(article.get("A"));
        intent.setAccount(resultMap.get("No") + "");
        intent.setLanguage(4);
        intent.setIntent(article.get("G"));
        intent.setDisplay(article.get("L"));
        intent.setAnswer(article.get("T"));
        intent.setDescription(article.get("D"));
        intent.setNextIntent(article.get("P"));
        if (intentCh == 0) {
          builderDAO.deleteIntent(intent);
        }
        intentList.add(intent);
        intentCh++;
        ch = true;
      }
    }

    answerList = answerList.stream().collect(Collectors.groupingBy(p-> p.getAnswer() + "_" + p.getLanguage()))
            .entrySet().stream().map(t -> t.getValue().get(0)).collect(Collectors.toList());

    if (answerList.size() > 299) {
      afterAnsList = Lists.partition(answerList,
          (int) (answerList.size() / (answerList.size()/300+0.99)));
      for (int i=0; i<afterAnsList.size(); i++) {
        builderDAO.insertAnswer(afterAnsList.get(i));
      }
    } else {
      builderDAO.insertAnswer(answerList);
    }

    if (intentList.size() > 174) {
      afterIntList = Lists.partition(intentList,
          (int) (intentList.size() / (intentList.size()/175+0.99)));
      for (int i=0; i<afterIntList.size(); i++) {
        builderDAO.insertIntent(afterIntList.get(i));
      }
    } else {
      builderDAO.insertIntent(intentList);
    }

    if (kr == true) {
      commonMap.put("language", 1);
      builderDAO.updateScenario(commonMap);
      updateIntent(commonMap);
    }
    if (en == true) {
      commonMap.put("language", 2);
      builderDAO.updateScenario(commonMap);
      updateIntent(commonMap);
    }
    if (jp == true) {
      commonMap.put("language", 3);
      builderDAO.updateScenario(commonMap);
      updateIntent(commonMap);
    }
    if (ch == true) {
      commonMap.put("language", 4);
      builderDAO.updateScenario(commonMap);
      updateIntent(commonMap);
    }

  }

  @Transactional
  @Override
  public void bertExcelUpload(File destFile, Map<String, Object> param) throws Exception {
    int sheet = 0;
    int hostNum;
    int backendInfoNum;
    Map<String, Object> mapData = new HashMap<>();
    List<Map<String, Object>> mapList = new ArrayList<>();
    List<List<Map<String, Object>>> afterMapList = new ArrayList<>();

    try {

      param = builderDAO.getHostIdBackEndId(param);
      hostNum = (int) param.get("no");
      backendInfoNum = (int) param.get("id");
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
      Calendar calendar = Calendar.getInstance();
      String strToday = simpleDateFormat.format(calendar.getTime());
      mapData.put("host", hostNum);
      mapData.put("bertItfId", backendInfoNum);
      mapData.put("date", strToday.substring(2));
      builderDAO.bakDelIntentRel(mapData);
      builderDAO.bakDelBertIntent(mapData);

      ReadOption excelReadOption = new ReadOption();
      excelReadOption.setFilePath(destFile.getAbsolutePath());
      excelReadOption
          .setOutputColumns("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L");
      excelReadOption.setStartRow(2);

      List<Map<String, String>> excelContent = ExcelRead.read(excelReadOption);

      for (Map<String, String> article : excelContent) {
        int srcIntent;
        String[] intentCol = {"A", "D", "C", "B"};
        String[] intetRelSrcCol = {"A", "J", "G", "D"};
        String[] intetRelDestCol = {"B", "K", "H", "E"};
        String[] intetRelBertCol = {"C", "L", "I", "F"};
        int[] langCol = {1, 2, 3, 4};

        if (article.get("A").equals("Sheet_Change")) {
          if (sheet == 0) {
            if (mapList.size() > 349) {
              mapList = mapList.stream().collect(Collectors.groupingBy(p-> p.get("intent") + "_" + p.get("lang")))
                      .entrySet().stream().map(t -> t.getValue().get(0)).collect(Collectors.toList());

              afterMapList = Lists.partition(mapList,
                  (int) (mapList.size() / (mapList.size()/350+0.99)));
              for (int i=0; i<afterMapList.size(); i++) {
                builderDAO.insertBertIntent(afterMapList.get(i));
              }
            } else {
              builderDAO.insertBertIntent(mapList);
            }
          } else if (sheet == 1) {
            if (mapList.size() > 299) {
              afterMapList = Lists.partition(mapList,
                  (int) (mapList.size() / (mapList.size()/300+0.99)));
              for (int i=0; i<afterMapList.size(); i++) {
                builderDAO.insertIntentRel(afterMapList.get(i));
              }
            } else {
              builderDAO.insertIntentRel(mapList);
            }
          }
          sheet++;
          mapList = new ArrayList<>();
        }

        if (sheet == 0) {
          for (int i = 0; i < 4; i++) {
            if (article.get(intentCol[i]) != null && !"".equals(article.get(intentCol[i]))) {
              mapData = new HashMap<>();
              mapData.put("bertItfId", backendInfoNum);
              mapData.put("lang", langCol[i]);
              mapData.put("intent", article.get(intentCol[i]));
              mapList.add(mapData);
            }
          }
        } else if (sheet == 1) {
          for (int i = 0; i < 4; i++) {
            if (article.get(intetRelSrcCol[i]) != null && !"".equals(article.get(intetRelSrcCol[i]))
                && article.get(intetRelDestCol[i]) != null && !""
                .equals(article.get(intetRelDestCol[i]))
                && article.get(intetRelBertCol[i]) != null && !""
                .equals(article.get(intetRelBertCol[i]))) {
              mapData = new HashMap<>();
              mapData.put("bertItfId", backendInfoNum);
              mapData.put("host", hostNum);
              mapData.put("lang", langCol[i]);
              mapData.put("intent", article.get(intetRelSrcCol[i]));
              if (article.get(intetRelSrcCol[i]).equals("*")) {
                srcIntent = 0;
              } else {
                srcIntent = builderDAO.getIntentNum(mapData);
              }
              mapData.put("intent", article.get(intetRelDestCol[i]));
              mapData.put("bertIntent", article.get(intetRelBertCol[i]));
              mapData.put("srcIntent", srcIntent);
              mapList.add(mapData);
            }
          }
        } else {
          for (int i = 0; i < 4; i++) {
            if (!article.get("A").equals("Sheet_Change") && article.get(intentCol[i]) != null && !""
                .equals(article.get(intentCol[i]))) {
              mapData = new HashMap<>();
              mapData.put("host", hostNum);
              mapData.put("lang", langCol[i]);
              mapData.put("intent", article.get(intentCol[i]));
              mapList.add(mapData);
            }
          }
        }
      }
      builderDAO.delInsertFallback(mapList);
      mapData.put("date", strToday.substring(2));
      builderDAO.updateRegexBertNo(mapData);
    } catch (Exception e) {
      logger.error("bertExcelUpload Exception:", e);
      throw e;
    }

  }

  @Transactional
  @Override
  public void insertBackendInfo(Map<String, Object> map) throws Exception {
    builderDAO.insertBackendInfo(map);
  }

  @Override
  public List<LogVO> selectChatLogList(Criteria cri) throws Exception {
    return builderDAO.selectChatLogList(cri);
  }

  @Override
  public void selectChatLogListExcel(HttpServletResponse response) throws Exception {
    SqlSession sqlSession = sqlSessionFactory.openSession();

    // 메모리에 100개의 행을 유지합니다. 행의 수가 넘으면 디스크에 적습니다.
    SXSSFWorkbook wb = new SXSSFWorkbook(100);
    Sheet sheet = wb.createSheet();

    try {
      sqlSession.select("selectChatLogListExcel", new ResultHandler<LogVO>() {
        int cnt = 0;
        int flowNo = 1;
        String session = "";

        @Override
        public void handleResult(ResultContext<? extends LogVO> context) {
          LogVO vo = context.getResultObject();
          Row row = sheet.createRow(context.getResultCount() - 1);
          Cell cell = null;
          if (cnt == 0) {
            cell = row.createCell(0);
            cell.setCellValue("No");
            cell = row.createCell(1);
            cell.setCellValue("Chatbot");
            cell = row.createCell(2);
            cell.setCellValue("Session");
            cell = row.createCell(3);
            cell.setCellValue("Flow_No");
            cell = row.createCell(4);
            cell.setCellValue("Utter");
            cell = row.createCell(5);
            cell.setCellValue("Prev");
            cell = row.createCell(6);
            cell.setCellValue("ButtonYN");
            cell = row.createCell(7);
            cell.setCellValue("Intent");
            cell = row.createCell(8);
            cell.setCellValue("Answer");
            cell = row.createCell(9);
            cell.setCellValue("Confidence");
            cell = row.createCell(10);
            cell.setCellValue("Datetime");
          } else {
            cell = row.createCell(0);
            cell.setCellValue(vo.getId());
            cell = row.createCell(1);
            cell.setCellValue(vo.getName());
            cell = row.createCell(2);
            cell.setCellValue(vo.getSession());
            cell = row.createCell(3);
            if (session.equals(vo.getSession())) {
              cell.setCellValue(++flowNo);
            } else {
              flowNo = 1;
              cell.setCellValue(flowNo);
            }
            session = vo.getSession();
            cell = row.createCell(4);
            cell.setCellValue(vo.getUtter());
            cell = row.createCell(5);
            cell.setCellValue(vo.getPrevIntentUtter());
            cell = row.createCell(6);
            if (vo.getProb().equals("1.0")) {
              cell.setCellValue("Y");
            } else {
              cell.setCellValue("N");
            }
            cell = row.createCell(7);
            cell.setCellValue(vo.getIntent());
            cell = row.createCell(8);
            cell.setCellValue(vo.getAnswer());
            cell = row.createCell(9);
            cell.setCellValue(vo.getProb());
            cell = row.createCell(10);
            cell.setCellValue(vo.getCreateDate());
          }
          cnt++;
        }
      });

      response.setHeader("Set-Cookie", "fileDownload=true; path=/");
      response
          .setHeader("Content-Disposition", String.format("attachment; filename=\"test.xlsx\""));
      wb.write(response.getOutputStream());

    } catch (Exception e) {

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

    } finally {
      sqlSession.close();

      // 디스크 적었던 임시파일을 제거합니다.
      wb.dispose();
      try {
        wb.close();
      } catch (Exception ignore) {
      }
    }

  }

  @Override
  public int countChatLog() throws Exception {
    return builderDAO.countChatLog();
  }

  @Override
  public List<LogVO> selectFlowNo(String session) throws Exception {
    return builderDAO.selectFlowNo(session);
  }

  private void updateIntent(Map<String, Object> commonMap) {

    try {
      List<Map<String, Object>> beforeList = builderDAO.selectBeforeData(commonMap);
      List<Map<String, Object>> commonList = new ArrayList<>();
      int account = (int) commonMap.get("account");
      int language = (int) commonMap.get("language");

      for (int i = 0; i < beforeList.size(); i++) {
        commonMap = new HashMap<>();
        commonMap.put("account", account);
        commonMap.put("language", language);
        String beforeIntent = (String) beforeList.get(i).get("TmpNextIntent");
        if (beforeIntent != null && !"".equals(beforeIntent)) {
          int beforeIntentNum = (int) beforeList.get(i).get("No");
          commonMap.put("no", beforeIntentNum);
          String[] data = beforeIntent.split(",");
          String afterIntent = "";
          for (int j = 0; j < data.length; j++) {
            commonMap.put("num", Integer.parseInt(data[j].substring(1)));
            int intentNum = builderDAO.checkIntentNum(commonMap);
            afterIntent += data[j].substring(0, 1) + intentNum + ",";
            if (j == data.length - 1) {
              commonMap.put("next", afterIntent.substring(0, afterIntent.length() - 1));
              commonList.add(commonMap);
            }
          }
        }
      }
      if(commonList.size() > 0){
        builderDAO.updateIntent(commonList);
      }
    } catch (Exception e) {
      logger.error("bertExcelUpload Exception:", e);
      throw e;
    }
  }

  @Override
  public List<Map<String, Object>> getChatbotList(Map<String, Object> map) throws Exception {
    return builderDAO.getChatbotList(map);
  }

  @Override
  public List<Map<String, Object>> getIntentStcList(Map<String, Object> map) throws Exception {
    return builderDAO.getIntentStcList(map);
  }

  @Override
  public List<Map<String, Object>> getIntentStcListV2(Map<String, Object> map) throws Exception {
    return builderDAO.getIntentStcListV2(map);
  }

  @Override
  public int getIntentCount(Map<String, Object> map) throws Exception {
    return builderDAO.getIntentCount(map);
  }

  @Override
  public List<Map<String, Object>> getSentenceList(Map<String, Object> map) throws Exception {
    return builderDAO.getSentenceList(map);
  }

  @Override
  public int getSentenceCount(Map<String, Object> map) throws Exception {
    return builderDAO.getSentenceCount(map);
  }
  
  @Override 
  public List<Map<String, String>> getAnswerList(Map<String, Object> map) throws Exception{
    return builderDAO.getAnswerList(map);
  }

  @Override
  public int getTaskCount(Map<String, Object> map) throws Exception {
    return builderDAO.getTaskCount(map);
  }

  @Override
  public int getTaskCountSearch(Map<String, Object> map) throws Exception {
    return builderDAO.getTaskCountSearch(map);
  }

  @Override
  public List<Map<String, Object>> getIntentDetail(Map <String, Object> map) throws Exception{
    return builderDAO.getIntentDetail(map);
  }

  @Override
  public List<Map<String, Object>> getIntentDetailByMain(Map <String, Object> map) throws Exception{
    return builderDAO.getIntentDetailByMain(map);
  }

  @Override
  public List<Map<String, Object>> getAnswerDetailSearch(Map <String, Object> map) throws Exception{
    return builderDAO.getAnswerDetailSearch(map);
  }

  @Override
  public List<Map<String, Object>> getAllIntents(Map <String, Object> map) throws Exception{
    return  builderDAO.getAllIntents(map);
  }

  @Override
  public List<Map<String, Object>> getAllBertIntents(Map <String, Object> map) throws Exception{
    return  builderDAO.getAllBertIntents(map);
  }

  @Override
  public List<Map<String, Object>> getImageCarousel(Map <String, Object> map) throws Exception{
    return  builderDAO.getImageCarousel(map);
  }

  @Override
  public List<Map<String, Object>> getIntentRelInAnsDetail(Map <String, Object> map) throws Exception{
    return  builderDAO.getIntentRelInAnsDetail(map);
  }

  @Override
  public List<Map<String, Object>> getSettingVal(Map <String, Object> map) throws Exception{
    return  builderDAO.getSettingVal(map);
  }

  @Override
  public List<Map<String, Object>> getStyleCSS(Map <String, Object> map) throws Exception{
    return  builderDAO.getStyleCSS(map);
  }

  @Override
  public List<Map<String, Object>> getTaskCheck(Map <String, Object> map) throws Exception{
    return  builderDAO.getTaskCheck(map);
  }

  @Override
  public List<Map<String, Object>> selectIntentForDelete(Map <String, Object> map) throws Exception{
    return  builderDAO.selectIntentForDelete(map);
  }

  @Override
  public List<Map<String, Object>> selectBackendInfoForDelete(Map <String, Object> map) throws Exception{
    return  builderDAO.selectBackendInfoForDelete(map);
  }

  @Override
  public List<Map<String, Object>> selectBertIntentForDelete(Map <String, Object> map) throws Exception{
    return  builderDAO.selectBertIntentForDelete(map);
  }

  @Override
  public List<Map<String, Object>> selectRegexIntentForDelete(Map <String, Object> map) throws Exception{
    return  builderDAO.selectRegexIntentForDelete(map);
  }

  @Override
  public List<Map<String, Object>> selectReplaceDict(Map <String, Object> map) throws Exception{
    return  builderDAO.selectReplaceDict(map);
  }

  @Override
  public List<Map<String, Object>> selectIntentBeforeDel(Map <String, Object> map) throws Exception{
    return  builderDAO.selectIntentBeforeDel(map);
  }

  @Override
  public List<Map<String, Object>> checkBertItfID(Map <String, Object> map) throws Exception{
    return  builderDAO.checkBertItfID(map);
  }

  @Override
  public int insertBertIntentNow(Map <String, Object> map) throws Exception{
    return builderDAO.insertBertIntentNow(map);
  }

  @Override
  public void insertStyleCSS(Map <String, Object> map) throws Exception{
    builderDAO.insertStyleCSS(map);
  }

  @Override
  public void updateStyleCSS(Map <String, Object> map) throws Exception{
    builderDAO.updateStyleCSS(map);
  }

  @Override
  public void updateAccount(Map <String, Object> map) throws Exception{
    builderDAO.updateAccount(map);
  }

  @Override
  public int insertAnswerNew(Map <String, Object> map) throws Exception{
    return builderDAO.insertAnswerNew(map);
  }

  @Override
  public int insertAnswerByAll(Map <String, Object> map) throws Exception{
    return builderDAO.insertAnswerByAll(map);
  }


  @Override
  public int insertIntentNew(Map <String, Object> map) throws Exception{
    return builderDAO.insertIntentNew(map);
  }

  @Override
  public int insertIntentNewAll(Map <String, Object> map) throws Exception{
    return builderDAO.insertIntentNewAll(map);
  }

  @Override
  public void insertIntentRelNew(Map <String, Object> map) throws Exception{
    builderDAO.insertIntentRelNew(map);
  }


  @Override
  public List<Map<String, Object>> selectIntentNoAnswer(Map <String, Object> map) throws Exception{
    return  builderDAO.selectIntentNoAnswer(map);
  }

  @Override
  public void deleteAccountByHost(Map <String, Object> map) throws Exception{
    builderDAO.deleteAccountByHost(map);
  }

  @Override
  @Transactional
  public void deleteAccountByHostV2(Map <String, Object> map) throws Exception{
    builderDAO.deleteAccountByHostV2(map);
  }

  @Override
  public void deleteIntentRelByHost(Map <String, Object> map) throws Exception{
    builderDAO.deleteIntentRelByHost(map);
  }

  @Override
  public void deleteBertIntentByHost(Map <String, Object> map) throws Exception{
    builderDAO.deleteBertIntentByHost(map);
  }

  @Override
  public void deleteRegexRuleByHost(Map <String, Object> map) throws Exception{
    builderDAO.deleteRegexRuleByHost(map);
  }

  @Override
  public void deleteBertSentenceByHost(Map <String, Object> map) throws Exception{
    builderDAO.deleteBertSentenceByHost(map);
  }

  @Override
  public void deleteAnswerByNo(Map <String, Object> map) throws Exception{
    builderDAO.deleteAnswerByNo(map);
  }

  @Override
  public void updateOldIntent(Map <String, Object> map) throws Exception{
    builderDAO.updateOldIntent(map);
  }

  @Override
  public void deleteOldIntentRel(Map <String, Object> map) throws Exception{
    builderDAO.deleteOldIntentRel(map);
  }

  @Override
  public void deleteOldIntentByLang(Map <String, Object> map) throws Exception{
    builderDAO.deleteOldIntentByLang(map);
  }

  @Override
  public void deleteIntentByNo(Map <String, Object> map) throws Exception{
    builderDAO.deleteIntentByNo(map);
  }

  @Override
  public List<Map<String, Object>> getRegexList(Map<String, Object> map) throws Exception {
    return builderDAO.getRegexList(map);
  }

  @Override
  public List<Map<String, Object>> getRegexListV2(Map<String, Object> map) throws Exception {
    return builderDAO.getRegexListV2(map);
  }

  @Override
  public List<Map<String, Object>> getRegexListAll(Map<String, Object> map) throws Exception {
    return builderDAO.getRegexListAll(map);
  }

  @Override
  public int getRegexCount(Map<String, Object> map) throws Exception {
    return builderDAO.getRegexCount(map);
  }

  @Transactional
  @Override
  public void insertRegex(Map<String, Object> map) throws Exception {
    int regexIntentNo = builderDAO.insertRegex(map);
    List<Map<String, Object>> list = new ArrayList<>();
    String ruleValue = (String) map.get("regexValue");
    String ruleType = (String) map.get("regexType");
    if (!"".equals(ruleValue) && ruleValue != null) {
      String[] ruleValueList = ruleValue.split(",");
      String[] ruleTypeList = ruleType.split(",");
      for (int i = 0; i < ruleValueList.length; i++) {
        map = new HashMap<>();
        if (ruleTypeList[i].contains("start")) {
          ruleTypeList[i] = "start";
        } else if (ruleTypeList[i].contains("match")) {
          ruleTypeList[i] = "match";
        } else if (ruleTypeList[i].contains("include")) {
          ruleTypeList[i] = "include";
        } else if (ruleTypeList[i].contains("exclude")) {
          ruleTypeList[i] = "exclude";
        } else {
          ruleTypeList[i] = "end";
        }
        map.put("ruleValue", ruleValueList[i]);
        map.put("ruleType", ruleTypeList[i]);
        map.put("regexIntentNo", regexIntentNo);
        list.add(map);
      }
      builderDAO.insertRegexRule(list);
    }
  }

  @Transactional
  @Override
  public void deleteRegex(Map<String, Object> map) throws Exception {
    builderDAO.deleteRegexRuleAll(map);
    builderDAO.deleteRegexIntentAll(map);
  }

  @Override
  public List<Map<String, Object>> getRegexDetail(Map<String, Object> map) throws Exception {
    return builderDAO.getRegexDetail(map);
  }

  @Transactional
  @Override
  public void updateRegex(Map<String, Object> map) throws Exception {
    builderDAO.updateRegex(map);
    List<Map<String, Object>> list = new ArrayList<>();
    String ruleValue = (String) map.get("regexValue");
    String ruleType = (String) map.get("regexType");
    String regexIntentNo = (String) map.get("regexIntentNo");
    map.put("update", "Y");
    builderDAO.deleteRegexRule(map);
    if (!"".equals(ruleValue) && ruleValue != null) {
      String[] ruleValueList = ruleValue.split(",");
      String [] ruleTypeList = ruleType.split(",");
      for (int i=0; i < ruleValueList.length; i++) {
        map = new HashMap<>();
        if (ruleTypeList[i].contains("start")) {
          ruleTypeList[i] = "start";
        } else if (ruleTypeList[i].contains("match")) {
          ruleTypeList[i] = "match";
        } else if (ruleTypeList[i].contains("include")) {
          ruleTypeList[i] = "include";
        } else if (ruleTypeList[i].contains("exclude")) {
          ruleTypeList[i] = "exclude";
        } else {
          ruleTypeList[i] = "end";
        }
        map.put("ruleValue", ruleValueList[i]);
        map.put("ruleType", ruleTypeList[i]);
        map.put("regexIntentNo", regexIntentNo);
        list.add(map);
      }
      builderDAO.insertRegexRule(list);
    }
  }

  @Override
  public void deleteIntention(Map<String, Object> map) throws Exception {
    builderDAO.deleteIntention(map);
  }

  @Override
  public int getIntentNo(Map <String, Object> map) throws Exception{
    return builderDAO.getIntentNo(map);
  }

  @Override
  public List<Map<String, Object>> selectIntention(Map <String, Object> map) throws Exception{
    return builderDAO.selectIntention(map);
  }

  @Override
  public void insertIntention(Map<String, Object> map) throws Exception {
    builderDAO.insertIntention(map);
  }

  @Override
  public int insertIntentionV2(Map<String, Object> map) throws Exception {
    return builderDAO.insertIntentionV2(map);
  }

  @Override
  public void deleteRegexRule(Map<String, Object> map) throws Exception {
    builderDAO.deleteRegexRule(map);
  }

  @Override
  public List<Map<String, Object>> getHostByAccount(Map <String, Object> map) throws Exception{
    return builderDAO.getHostByAccount(map);
  }

  @Override
  public List<Map<String, Object>> getHostName(Map <String, Object> map) throws Exception{
    return builderDAO.getHostName(map);
  }

  @Override
  public List<Map<String, Object>> getNewHostName(Map <String, Object> map) throws Exception{
    return builderDAO.getNewHostName(map);
  }

  @Override
  public List<Map<String, Object>> getDomain() throws Exception{
    return builderDAO.getDomain();
  }

  @Override
  public int addChatbot(Map <String, Object> map) throws Exception{
    return builderDAO.addChatbot(map);
  }

  @Override
  public int insertBertIntentNew(Map <String, Object> map) throws Exception{
    return builderDAO.insertBertIntentNew(map);
  }


  @Override
  public int insertBackendInfos(Map<String, Object> map) throws Exception {
    return builderDAO.insertBackendInfos(map);
  }

  @Override
  public List<Map<String, Object>> getBackendInfo(Map<String, Object> map) throws Exception{
    return builderDAO.getBackendInfo(map);
  }
  @Override
  public List<Map<String, Object>> getIntentAnswer(Map<String, Object> map) throws Exception{
    return builderDAO.getIntentAnswer(map);
  }

  @Override
  public List<Map<String, Object>> getIntentRelByAll(Map<String, Object> map) throws Exception{
    return builderDAO.getIntentRelByAll(map);
  }

  @Override
  public List<Map<String, Object>> getBertIntentByNo(Map<String, Object> map) throws Exception{
    return builderDAO.getBertIntentByNo(map);
  }

  @Override
  public void insertIntentRelByAll(Map<String, Object> map) throws Exception {
    builderDAO.insertIntentRelByAll(map);
  }

  @Override
  public void insertReplaceDict(Map<String, Object> map) throws Exception {
    builderDAO.insertReplaceDict(map);
  }

  @Override
  public List<Map<String, Object>> getChatInfo(Map<String, Object> map) throws Exception {
    return builderDAO.getChatInfo(map);
  }
  @Override
  public List<Map<String, Object>> getChatInfoHTask(Map<String, Object> map) throws Exception {
    return builderDAO.getChatInfoHTask(map);
  }

  @Override
  public List<Map<String, Object>> getBertIntent(Map<String, Object> map) throws Exception {
    return builderDAO.getBertIntent(map);
  }

  @Override
  public List<Map<String, Object>> getIntentRel(Map<String, Object> map) throws Exception {
    return builderDAO.getIntentRel(map);
  }

  @Override
  public List<Map<String, Object>> getFallback(Map<String, Object> map) throws Exception {
    return builderDAO.getFallback(map);
  }

  @Override
  public void updateStyleCSSSupplier(Map <String, Object> map) throws Exception{
    builderDAO.updateStyleCSSSupplier(map);
  }

  @Override
  public void changeAccountLang(Map <String, Object> map) throws Exception{
    builderDAO.changeAccountLang(map);
  }

  @Override
  public List<Map<String, Object>> getIntentNameByNo(Map<String, Object> map) throws Exception {
    return builderDAO.getIntentNameByNo(map);
  }

  @Override
  public int checkBertIntent(Map<String, Object> map) throws Exception {
    return builderDAO.checkBertIntent(map);
  }

  @Override
  public Map<String, Object> getChatLang(Map<String, Object> map) throws Exception {
    return builderDAO.getChatLang(map);
  }

  @Override
  public Map<String, Object> logDebug(Map<String, Object> map) throws Exception {
    Map<String, Object> resultMap = null;

    resultMap = builderDAO.logDebug(map);

    int whileCnt = 0;

    while(resultMap == null){
      whileCnt++;
      Thread.sleep(1000);
      resultMap = builderDAO.logDebug(map);
      if(resultMap != null || whileCnt == 3){
        break;
      }
    }

    return resultMap;
  }


  @Override
  public int checkRegex(Map<String, Object> map) throws Exception {
    return builderDAO.checkRegex(map);
  }
  @Override
  public int getReplaceDictCount(Map<String, Object> map) throws Exception {
    return builderDAO.getReplaceDictCount(map);
  }
  @Override
  public List<Map<String,Object>> getReplaceDictLst(Map<String, Object> map) throws Exception {
    return builderDAO.getReplaceDictLst(map);
  }
  @Override
  public int addReplaceDict(Map<String, Object> map) throws Exception {
    return builderDAO.addReplaceDict(map);
  }
  @Override
  public int updateReplaceDict(Map<String, Object> map) throws Exception {
    return builderDAO.updateReplaceDict(map);
  }
  @Override
  public int deleteReplaceDict(Map<String, Object> map) throws Exception {
    return builderDAO.deleteReplaceDict(map);
  }
  @Override
  public int addReplaceDictLst(List<Map<String, Object>> list) throws Exception {
    return builderDAO.addReplaceDictLst(list);
  }
  @Override
  public int deleteReplaceDictAll(Map<String, Object> map) throws Exception {
    return builderDAO.deleteReplaceDictAll(map);
  }

  @Override
  public String replaceDictExcelUpload(File destFile, Map<String, Object> param) throws Exception {
    String errorMsg = "";
    int errRow = 0;
    int columnSize = 0;
    
    try {
      InputStream is = new FileInputStream(destFile.getAbsolutePath());
	  XSSFWorkbook workbook = new XSSFWorkbook(is);
	
	  String sheetName = workbook.getSheetAt(0).getSheetName();
	  if(!sheetName.equals("치환사전")){
          throw new Exception("sheet Name Diff");
      }
	  
      List<Map<String, Object>> mapDataLst = new ArrayList<>();
      String []columns = {"A", "B"};

      ReadOption excelReadOption = new ReadOption();
      excelReadOption.setFilePath(destFile.getAbsolutePath());
      excelReadOption.setOutputColumns(columns);
      excelReadOption.setStartRow(1);
      builderDAO.deleteReplaceDictAll(param);

      List<Map<String, String>> excelContent = ExcelRead.read(excelReadOption);
      for (int i=0; i<columns.length; i++) {
          if (excelContent.get(0).get(columns[i]) != null && !"".equals(excelContent.get(0).get(columns[i]))) {
            columnSize++;
          }
      }
      
      if (!(columnSize == 2)) {
	        throw new Exception("excelform");
	  }
      
      for(int i = 1 ; i < excelContent.size() ; i++){
        errRow = i+1;
        Map<String, Object> mapData = new HashMap<>();
        mapData.put("host", param.get("host"));
        mapData.put("lang", param.get("lang"));
        mapData.put("replaceDictBefore",excelContent.get(i).get("A"));
        mapData.put("replaceDictAfter",excelContent.get(i).get("B"));
        if("".equals(mapData.get("replaceDictBefore")) || mapData.get("replaceDictBefore") == null){
          throw new Exception("input Before Null");
        }
        if("".equals(mapData.get("replaceDictAfter")) || mapData.get("replaceDictAfter") == null){
          throw new Exception("input After Null");
        }
        mapDataLst.add(0,mapData);
      }

      builderDAO.addReplaceDictLst(mapDataLst);

    } catch (Exception e) {
      e.printStackTrace();
      logger.error("### Exception Message : " + e.getMessage());
      logger.error("ChatbotExcelUpload Exception:", e);
      if ("excelform".equals(e.getMessage())) {
        errorMsg = "엑셀 형식이 잘못 되었습니다. 엑셀 컬럼을 다시 확인해주세요.";
      } else if (e.getMessage() != null && e.getMessage().contains("For input string")) {
        errorMsg = "데이터 형식이 잘못되었습니다. 잘못된 엑셀 데이터 : " + e.getMessage();
      } else if (e.getMessage() != null && e.getMessage().contains("input Before Null")) {
        errorMsg = "치환 전 데이터에 빈값을 입력할 수 없습니다. <br>치환 후 데이터 " + errRow + "행이 빈값 입니다.";
      } else if (e.getMessage() != null && e.getMessage().contains("input After Null")) {
        errorMsg = "치환 후 데이터에 빈값을 입력할 수 없습니다. <br>치환 후 데이터 " + errRow + "행이 빈값 입니다.";
      } else if (e.getMessage() == null) {
        errorMsg = "데이터 정합성 오류입니다. 데이터들이 정확하게 들어갔는지 확인해주세요.";
      } else if(e.getMessage() != null && e.getMessage().contains("sheet Name Diff")) {
    	errorMsg = "엑셀 형식이 잘못 되었습니다. 엑셀 시트명을 다시 확인해주세요.";
      } else {
        errorMsg = "예기치 못한 오류로 인해 업로드를 실패 했습니다.<br>관리자에게 문의해주세요.";
      }
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return errorMsg;
    }




    return "저장이 완료되었습니다.";
  }

  @Override
  public int replaceDictCnt(Map<String, Object> map) throws Exception {
    return builderDAO.replaceDictCnt(map);
  }
  @Override
  public Map<String, Object> replaceDictgetAfter(Map<String, Object> map) throws Exception {
    return builderDAO.replaceDictgetAfter(map);
  }
 
  @Override
  public List<Map<String, Object>> getIntentTaskDetail(Map map) {
	  return builderDAO.getIntentTaskDetail(map);
  }

  @Override
  public List<Map<String, Object>> getRegexInfoList(String host) {
	  return builderDAO.getRegexInfoList(host);
  }

  @Override
  public List<Map<String, Object>> getIntentList(String host) {
	  return builderDAO.getIntentList(host);
  }

  @Override
  public int insertRegexIntentList(List<Map<String, Object>> list) {
	  return builderDAO.insertRegexIntentList(list);
  }

  @Transactional
  @Override
  public String regexExcelUpload(File destFile, Map<String, Object> param) throws Exception {
    String []columns = {"A", "B", "C", "D", "E", "F", "G", "H"};
    ReadOption excelReadOption = new ReadOption();
    excelReadOption.setFilePath(destFile.getAbsolutePath());
    excelReadOption.setOutputColumns(columns);
    excelReadOption.setStartRow(1);

    List<Map<String, String>> excelContent = ExcelRead.read(excelReadOption);
    List<Map<String, Object>> bertIntentList = builderDAO.getIntentList(param.get("host").toString());
    List<Map<String, Object>> addBertIntentList = new ArrayList<>();
    List<Map<String, Object>> errBertIntentList = new ArrayList<>();

    int resultCnt = 0;
    String resultMsg = "";
    excelContent.remove(0);

    if(!"".equals(param.get("host")) || param.get("host") != null){
      builderDAO.deleteRegexRuleAll(param);
      builderDAO.deleteRegexIntentAll(param);
    }else{
      throw new RuntimeException("host 오류");
    }

    for (int i = 1; i < 5; i++){ //language code
      int j = 0; //언어별 컬럼 시작 위치

      if ( i == 1 ) j = 0; //한국어
      else if ( i == 2 ) j = 6; //영어
      else if ( i == 3 ) j = 4; //일본어
      else if ( i == 4 ) j = 2; //중국어

      for(int n = 0; n < excelContent.size(); n++){
        Map<String, Object> tmpMap = new HashMap<>();

        if(excelContent.get(n).get(columns[j]) != null && !"".equals(excelContent.get(n).get(columns[j])) &&
                excelContent.get(n).get(columns[j + 1]) != null && !"".equals(excelContent.get(n).get(columns[j + 1])) ) {
          tmpMap.put("host", param.get("host"));
          tmpMap.put("intentNm", excelContent.get(n).get(columns[j]));
          tmpMap.put("regex", excelContent.get(n).get(columns[j + 1]));
          tmpMap.put("lang", i);
          Object bertNo = bertIntentList.stream().filter(v -> v.get("Language").equals(tmpMap.get("lang")) && v.get("Name").equals(tmpMap.get("intentNm")))
                  .map(v -> v.get("No")).findFirst().orElse("noMatchBert");

          if("noMatchBert".equals(bertNo)){
            tmpMap.put("errIntent", tmpMap.get("intentNm"));
            errBertIntentList.add(tmpMap);
          }else{
            tmpMap.put("bertIntentNo", bertNo);
            addBertIntentList.add(tmpMap);
          }
        }
      }
    }

    if(errBertIntentList.size() > 0){
      resultMsg = "'"+errBertIntentList.get(0).get("errIntent")
              + (errBertIntentList.size() > 1 ? "' 외" +errBertIntentList.size() + "건의 ":"' ") +"의도와 정규식 매핑이 잘못되었습니다.";
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      throw new RuntimeException(resultMsg);
    }else{
      if(addBertIntentList.size() > 400){
        int maxSize = addBertIntentList.size();
        for(int i=0; i <= maxSize/400; i++){
          int endNum = (i+1)*400;
          List<Map<String, Object>> tmpList = new ArrayList<>(addBertIntentList.subList(i*400, maxSize < endNum ? maxSize: endNum));
          resultCnt += builderDAO.insertRegexIntentList(tmpList);
        }

      }else{
        resultCnt = builderDAO.insertRegexIntentList(addBertIntentList);
      }

      if(resultCnt == addBertIntentList.size()){
        resultMsg = "업로드가 완료 되었습니다.";
      }else{
        resultMsg = "일부 업로드 되지 못한 데이터가 있습니다.("+(addBertIntentList.size()-resultCnt)+"건)";
      }
    }

    return resultMsg;
  }

  @Override
  public List<Map<String, Object>> getNqaStatusChatbotList() {
    return builderDAO.getNqaStatusChatbotList();
  }

  @Override
  @Transactional
  public Map <String, Object> deleteRegexAllByHostV2(List<Map <String, Object>> list) {
    try {
      Map <String, Object> map = new HashMap<>();
      map.put("host", list.get(0).get("host"));
      builderDAO.deleteRegexRuleAll(map);
      builderDAO.deleteRegexIntentAll(map);
      for (Map<String, Object> param : list) {
        builderDAO.insertRegex(param);
      }
      return map;
    }catch (Exception e){
      e.printStackTrace();
      throw e;
    }
  }

  public List<Map<String, Object>> getEntitiesList(Map<String, Object> map){
    return builderDAO.getEntitiesList(map);
  }

  public void deleteEntities(int hostNum) {
    builderDAO.deleteEntities(hostNum);
  }

  public List<Map<String, Object>> selectEachSrcList(List<Map<String, Object>> list){
    return builderDAO.selectEachSrcList(list);
  }

  public void updateIntentTask(List<Map<String, Object>> map){
    builderDAO.updateIntentTask(map);
  }
  public void deleteNoAnswerFromIntent(Map<String, Object> map){
    builderDAO.deleteNoAnswerFromIntent(map);
  }

  public List<Map<String, Object>> getBertIntentByDestIntent(Map<String, Object> map){
    return builderDAO.getBertIntentByDestIntent(map);
  }
}
