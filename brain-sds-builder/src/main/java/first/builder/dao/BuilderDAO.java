package first.builder.dao;

import first.builder.vo.AnswerVO;
import first.builder.vo.IntentVO;
import first.builder.vo.LogVO;
import first.common.util.Criteria;
import first.common.util.NqaUploadStatusList;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("builderDAO")
public class BuilderDAO extends AbstractDAO {

  public Map<String, Object> accountCheck(Map<String, Object> map) {
    return (Map<String, Object>) selectOne("builder.accountCheck", map);
  }

  public void insertAccount(Map<String, Object> map) {
    insert("builder.insertAccount", map);
  }

  public void backupIntent(Map<String, Object> map) {
    insert("builder.backupIntent", map);
  }

  public void backupAnswer(Map<String, Object> map) {
    insert("builder.backupAnswer", map);
  }

  public void insertAnswer(List<AnswerVO> answerVO) {
    insert("builder.insertAnswer", answerVO);
  }

  public int countAnswer(AnswerVO answerVO) {
    return (int) selectOne("builder.countAnswer", answerVO);
  }

  public int checkAnswerNum(Map<String, Object> map) {
    return (int) selectOne("builder.checkAnswerNum", map);
  }

  public void insertIntent(List<IntentVO> intentVO) {
    insert("builder.insertIntent", intentVO);
  }
  public void insertIntentHTask(List<IntentVO> intentVO) {
    insert("builder.insertIntentHTask", intentVO);
  }

  public void deleteIntent(IntentVO intentVO) {
    delete("builder.deleteIntent", intentVO);
  }

  public void deleteAnswer(AnswerVO answerVO) {
    delete("builder.deleteAnswer", answerVO);
  }

  public void updateScenario(Map<String, Object> map) {
    update("builder.updateScenario", map);
  }

  public List<Map<String, Object>> selectBeforeData(Map<String, Object> map) {
    return (List<Map<String, Object>>) selectList("builder.selectBeforeData", map);
  }

  public int checkIntentNum(Map<String, Object> map) {
    return (int) selectOne("builder.checkIntentNum", map);
  }

  public void updateIntent(List<Map<String, Object>> map) {
    update("builder.updateIntent", map);
  }

  public void insertBackendInfo(Map<String, Object> map) {
    insert("builder.insertBackendInfo", map);
  }

  public List<LogVO> selectChatLogList(Criteria cri) {
    return (List<LogVO>) selectList("builder.selectChatLogList", cri);
  }

  public int countChatLog() {
    return (Integer) selectOne("builder.countChatLog");
  }

  public List<LogVO> selectFlowNo(String session) {
    return (List<LogVO>) selectList("builder.selectFlowNo", session);
  }

  public int getIntentNum(Map<String, Object> map) {
    return (int) selectOne("builder.getIntentNum", map);
  }

  public void insertBertIntent(List<Map<String, Object>> map) {
    insert("builder.insertBertIntent", map);
  }

  public int getBertIntentNum(Map<String, Object> map) {
    return (int) selectOne("builder.getBertIntentNum", map);
  }

  public void insertIntentRel(List<Map<String, Object>> map) {
    insert("builder.insertIntentRel", map);
  }

  public Map<String, Object> getHostIdBackEndId(Map<String, Object> map) {
    return (Map<String, Object>) selectOne("builder.getHostIdBackEndId", map);
  }

  public void delInsertFallback(List<Map<String, Object>> map) {
    insert("builder.delInsertFallback", map);
  }

  public void bakDelBertIntent(Map<String, Object> map) {
    delete("builder.bakDelBertIntent", map);
  }

  public void bakDelIntentRel(Map<String, Object> map) {
    delete("builder.bakDelIntentRel", map);
  }

  public List<Map<String, Object>> getChatbotList(Map<String, Object> map) {
    return selectList("builder.getChatbotList", map);
  }

  public List<Map<String, Object>> getIntentStcList(Map<String, Object> map) {
    return selectList("builder.getIntentStcList", map);
  }

  public List<Map<String, Object>> getIntentStcListV2(Map<String, Object> map) {
    return selectList("builder.getIntentStcListV2", map);
  }

  public int getIntentCount(Map<String, Object> map) {
    return (int) selectOne("builder.getIntentCount", map);
  }

  public List<Map<String, Object>> getSentenceList(Map<String, Object> map) {
    return selectList("builder.getSentenceList", map);
  }

  public int getSentenceCount(Map<String, Object> map) {
    return (int) selectOne("builder.getSentenceCount", map);
  }

  public List<Map<String, String>> getAnswerList(Map<String, Object> map){
    return selectList("builder.getAnswerList",map);
  }

  public int getTaskCount(Map<String, Object> map){
    return (int) selectOne("builder.getTaskCount", map);
  }

  public int getTaskCountSearch(Map<String, Object> map){
    return (int) selectOne("builder.getTaskCountSearch", map);
  }

  public List<Map<String, Object>> getIntentDetail(Map <String, Object> map){
    return selectList("builder.getAnswerDetail", map);
  }

  public List<Map<String, Object>> getIntentDetailByMain(Map <String, Object> map){
    return selectList("builder.getAnswerDetailByMain", map);
  }

  public List<Map<String, Object>> getAnswerDetailSearch(Map <String, Object> map){
    return selectList("builder.getAnswerDetailSearch", map);
  }

  public List<Map<String, Object>> getAllIntents(Map <String, Object> map){
    return selectList("builder.getAllIntents", map);
  }

  public List<Map<String, Object>> getAllBertIntents(Map <String, Object> map){
    return selectList("builder.getAllBertIntents", map);
  }

  public List<Map<String, Object>> getImageCarousel(Map <String, Object> map){
    return selectList("builder.getImageCarousel", map);
  }

  public List<Map<String, Object>> getIntentRelInAnsDetail(Map <String, Object> map){
    return selectList("builder.getIntentRelInAnsDetail", map);
  }

  public List<Map<String, Object>> getSettingVal(Map <String, Object> map){
    return selectList("builder.getSettingVal", map);
  }

  public List<Map<String, Object>> getStyleCSS(Map <String, Object> map){
    return selectList("builder.getStyleCSS", map);
  }

  public List<Map<String, Object>> getTaskCheck(Map <String, Object> map){
    return selectList("builder.getTaskCheck", map);
  }

  public List<Map<String, Object>> selectIntentForDelete(Map <String, Object> map){
    return selectList("builder.selectIntentForDelete", map);
  }

  public List<Map<String, Object>> selectBackendInfoForDelete(Map <String, Object> map){
    return selectList("builder.selectBackendInfoForDelete", map);
  }

  public List<Map<String, Object>> selectBertIntentForDelete(Map <String, Object> map){
    return selectList("builder.selectBertIntentForDelete", map);
  }

  public List<Map<String, Object>> selectRegexIntentForDelete(Map <String, Object> map){
    return selectList("builder.selectRegexIntentForDelete", map);
  }

  public List<Map<String, Object>> selectReplaceDict(Map <String, Object> map){
    return selectList("builder.selectReplaceDict", map);
  }

  public List<Map<String, Object>> selectIntentBeforeDel(Map <String, Object> map){
    return selectList("builder.selectIntentBeforeDel", map);
  }

  public List<Map<String, Object>> checkBertItfID(Map <String, Object> map){
    return selectList("builder.checkBertItfID", map);
  }

  public int insertBertIntentNow(Map <String, Object> map){
    insert("builder.insertBertIntentNow", map);
    int insertedBert =  Integer.parseInt(map.get("BertNo").toString());
    return insertedBert;

  }

  public void insertStyleCSS(Map <String, Object> map){
    insert("builder.insertStyleCSS", map);
  }

  public void updateStyleCSS(Map <String, Object> map){
    update("builder.updateStyleCSS", map);
  }

  public void updateAccount(Map <String, Object> map){
    update("builder.updateAccountSetting", map);
  }


  public int insertAnswerNew(Map <String, Object> map){
    insert("builder.insertAnswerNew", map);
    int insertedAnswerKey =  Integer.parseInt(map.get("AnswerNo").toString());
    return insertedAnswerKey;
  }

  public int insertAnswerByAll(Map <String, Object> map){
    insert("builder.insertAnswerByAll", map);
    int insertedAnswerKey =  Integer.parseInt(map.get("AnswerNo").toString());
    return insertedAnswerKey;
  }

  public int insertIntentNew(Map <String, Object> map){
    insert("builder.insertIntentNew", map);
    int insertedIntentKey =  Integer.parseInt(map.get("IntentNo").toString());
    return insertedIntentKey;
  }

  public int insertIntentNewAll(Map <String, Object> map){
    insert("builder.insertIntentNewAll", map);
    int insertedIntentKey =  Integer.parseInt(map.get("IntentNo").toString());
    return insertedIntentKey;
  }

  public void insertIntentRelNew(Map <String, Object> map){
    insert("builder.insertIntentRelNew", map);
  }

  public List<Map<String, Object>> selectIntentNoAnswer(Map <String, Object> map){
    return selectList("builder.selectIntentNoAnswer", map);
  }

  public void deleteAccountByHost(Map <String, Object> map){
    delete("builder.deleteAccountByHost", map);
  }

  public void deleteAccountByHostV2(Map <String, Object> map){
    delete("builder.deleteAccountByHostV2", map);
  }

  public void deleteIntentRelByHost(Map <String, Object> map){
    delete("builder.deleteIntentRelByHost", map);
  }

  public void deleteBertIntentByHost(Map <String, Object> map){
    delete("builder.deleteBertIntentByHost", map);
  }

  public void deleteRegexRuleByHost(Map <String, Object> map){
    delete("builder.deleteRegexRuleByHost", map);
  }

  public void deleteBertSentenceByHost(Map <String, Object> map){
    delete("builder.deleteBertSentenceByHost", map);
  }

  public void deleteAnswerByNo(Map <String, Object> map){
    delete("builder.deleteAnswerByNo", map);
  }

  public void updateOldIntent(Map <String, Object> map){
    update("builder.updateOldIntent", map);
  }

  public void deleteOldIntentRel(Map <String, Object> map){
    delete("builder.deleteOldIntentRel", map);
  }

  public void deleteOldIntentByLang(Map <String, Object> map){
    delete("builder.deleteOldIntentByLang", map);
  }

  public void deleteIntentByNo(Map <String, Object> map){
    delete("builder.deleteIntentByNo", map);
  }

  public List<Map<String, Object>> getRegexList(Map<String, Object> map) {
    return selectList("builder.getRegexList", map);
  }

  public List<Map<String, Object>> getRegexListV2(Map<String, Object> map) {
    return selectList("builder.getRegexListV2", map);
  }

  public List<Map<String, Object>> getRegexListAll(Map<String, Object> map) {
    return selectList("builder.getRegexListAll", map);
  }

  public int getRegexCount(Map<String, Object> map) {
    return (int) selectOne("builder.getRegexCount", map);
  }

  public int insertRegex(Map<String, Object> map) {
    insert("builder.insertRegex", map);
    return Integer.parseInt(map.get("no").toString());
  }

  public void deleteRegexRuleAll(Map<String, Object> map) {
    delete("builder.deleteRegexRuleAll", map);
  }

  public void deleteRegexIntentAll(Map<String, Object> map) {
    delete("builder.deleteRegexIntentAll", map);
  }

  public void insertRegexRule(List<Map<String, Object>> map) {
    insert("builder.insertRegexRule", map);
  }

  public List<Map<String, Object>> getRegexDetail(Map<String, Object> map) {
    return (List<Map<String, Object>>) selectList("builder.getRegexDetail", map);
  }

  public void updateRegex(Map<String, Object> map) {
    update("builder.updateRegex", map);
  }

  public void deleteRegexRule(Map<String, Object> map) {
    delete("builder.deleteRegexRule", map);
  }

  public void deleteIntention(Map<String, Object> map) {
    delete("builder.deleteIntention", map);
  }

  public int getIntentNo(Map<String, Object> map) {
    return (int) selectOne("builder.getIntentNo", map);
  }

  public List<Map<String, Object>> selectIntention(Map<String, Object> map) {
    return selectList("builder.selectIntention", map);
  }

  public void insertIntention(Map<String, Object> map) {
    insert("builder.insertIntention", map);
  }

  public int insertIntentionV2(Map<String, Object> map) {
    insert("builder.insertIntentionV2", map);
    int BertIntentNo =  Integer.parseInt(map.get("BertIntentNo").toString());
    return BertIntentNo;
  }

  public List<Map<String, Object>> getHostByAccount(Map<String, Object> map) {
    return selectList("builder.getHostByAccount", map);
  }

  public List<Map<String, Object>> getHostName(Map<String, Object> map) {
    return selectList("builder.getHostName", map);
  }

  public List<Map<String, Object>> getNewHostName(Map<String, Object> map) {
    return selectList("builder.getNewHostName", map);
  }

  public List<Map<String, Object>> getDomain() {
    return selectList("builder.getDomain");
  }


  public int addChatbot(Map <String, Object> map){
    insert("builder.addChatbot", map);
    int insertedAccountKey =  Integer.parseInt(map.get("AccountNo").toString());
    Map<String, Object> status = new HashMap<>();
    status.put("host", insertedAccountKey);
    status.put("nqaUploadStatus", "preparation");
    NqaUploadStatusList.addNqaUploadStatus(status);
    return insertedAccountKey;
  }

  public int insertBertIntentNew(Map <String, Object> map){
    insert("builder.insertBertIntentNew", map);
    int insertedBertIntentKey =  Integer.parseInt(map.get("BertIntentNo").toString());
    return insertedBertIntentKey;
  }

  public int insertBackendInfos(Map<String, Object> map) {
    insert("builder.insertBackendInfos", map);
    int insertedBackendInfoPK =  Integer.parseInt(map.get("backendInfoNo").toString());
    return insertedBackendInfoPK;
  }

  public List<Map<String, Object>> getBackendInfo(Map<String, Object> map) {
    return selectList("builder.getBackendInfo", map);
  }

  public List<Map<String, Object>> getIntentAnswer(Map<String, Object> map) {
    return selectList("builder.getIntentAnswer", map);
  }

  public List<Map<String, Object>> getIntentRelByAll(Map<String, Object> map) {
    return selectList("builder.getIntentRelByAll", map);
  }

  public List<Map<String, Object>> getBertIntentByNo(Map<String, Object> map) {
    return selectList("builder.getBertIntentByNo", map);
  }

  public void insertIntentRelByAll(Map<String, Object> map) {
    insert("builder.insertIntentRelByAll", map);
  }

  public void insertReplaceDict(Map<String, Object> map) {
    insert("builder.insertReplaceDict", map);
  }

  public List<Map<String, Object>> getChatInfo(Map<String, Object> map) {
    return selectList("builder.getChatInfo", map);
  }
  public List<Map<String, Object>> getChatInfoHTask(Map<String, Object> map) {
    return selectList("builder.getChatInfoHTask", map);
  }

  public List<Map<String, Object>> getBertIntent(Map<String, Object> map) {
    return selectList("builder.getBertIntent", map);
  }

  public List<Map<String, Object>> getIntentRel(Map<String, Object> map) {
    return selectList("builder.getIntentRel", map);
  }

  public List<Map<String, Object>> getFallback(Map<String, Object> map) {
    return selectList("builder.getFallback", map);
  }

  public void updateRegexIntent() {
    update("builder.updateRegexIntent", "");
  }

  public void updateRegexBertNo(Map<String, Object> map) {
    update("builder.updateRegexBertNo", map);
  }

  public void updateStyleCSSSupplier(Map<String, Object> map) {
    update("builder.updateStyleCSSSupplier", map);
  }

  public void changeAccountLang(Map<String, Object> map) {
    update("builder.changeAccountLang", map);
  }

  public List<Map<String, Object>> getIntentNameByNo(Map<String, Object> map) {
    return selectList("builder.getIntentNameByNo", map);
  }

  public int checkBertIntent(Map<String, Object> map) {
    return (int) selectOne("builder.checkBertIntent", map);
  }

  public 	Map<String, Object> getChatLang(Map<String, Object> map) {
    return (Map<String, Object>) selectOne("builder.getChatLang", map);
  }

  public Map<String, Object> logDebug(Map<String, Object> map) {
    return (Map<String, Object>) selectOne("builder.logDebug", map);
  }

  public int checkRegex(Map<String, Object> map) {
    return (int) selectOne("builder.checkRegex", map);
  }

  public int getReplaceDictCount(Map<String, Object> map) {
    return (int) selectOne("builder.getReplaceDictCount", map);
  }
  public List<Map<String, Object>> getReplaceDictLst(Map<String, Object> map) {
    return selectList("builder.getReplaceDictLst", map);
  }

  public int addReplaceDict(Map<String, Object> map) {
    return (int)insert("builder.addReplaceDict", map);
  }
  public int updateReplaceDict(Map<String, Object> map) {
    return (int)update("builder.updateReplaceDict", map);
  }
  public int deleteReplaceDict(Map<String, Object> map) {
    return (int)delete("builder.deleteReplaceDict", map);
  }
  public int deleteReplaceDictAll(Map<String, Object> map) {
    return (int)delete("builder.deleteReplaceDictAll", map);
  }
  public int addReplaceDictLst(List<Map<String, Object>> list) {
    return (int)insert("builder.addReplaceDictLst", list);
  }
  public int replaceDictCnt(Map<String, Object> map) {
    return (int)selectOne("builder.replaceDictCnt", map);
  }
  public Map<String, Object> replaceDictgetAfter(Map<String, Object> map) {
    return (Map<String, Object>)selectList("builder.replaceDictgetAfter", map).get(0);
  }
  public List<Map<String, Object>> getIntentTaskDetail(Map map) {
	  return selectList("builder.getIntentTaskDetail", map);
  }

  public List<Map<String, Object>> getRegexInfoList(String host) {
    return selectList("builder.getRegexInfoList", host);
  }

  public List<Map<String, Object>> getIntentList(String host) {
    return selectList("builder.getIntentList", host);
  }

  public int insertRegexIntentList(List<Map<String, Object>> list) {
    return (int)insert("builder.insertRegexIntentList", list);
  }

  public List<Map<String, Object>> getNqaStatusChatbotList(){
    return selectList("builder.getNqaStatusChatbotList");
  }

  public void deleteEntities(int hostNum) {
    if(hostNum > 0) {
      delete("builder.deleteEntities", hostNum);
    }
  }
  public void insertEntitiesList(List<Map<String, Object>> list) {
    insert("builder.insertEntitiesList", list);
  }
  public void updateEntityOfIntent(int hostNum) {
    update("builder.updateEntityOfIntent", hostNum);
  }

  public List<Map<String, Object>> getEntitiesList(Map<String, Object> map) {
    return selectList("builder.getEntitiesList", map);
  }

  public List<Map<String, Object>> selectSrcList(int bertItfId) {

    List<Map<String, Object>> srcList = selectList("builder.selectSrcList", bertItfId);
    return srcList;
  }

  public List<Map<String, Object>> selectEachSrcList(List<Map<String, Object>> list) {
    return selectList("builder.selectEachSrcList", list);
  }

  public void updateIntentTask(List<Map<String, Object>> map) {
    for (int i=0; i<map.size(); i++) {
      if (map.get(i).get("Task") != null ) {
        String task = map.get(i).get("Task").toString();
        //task = task.substring(0, task.length()-1);
        map.get(i).put("Task", task);
      }
    }
    update("maumsds.updateIntentTask", map);
  }

  public void deleteNoAnswerFromIntent(Map <String, Object> map){
    delete("builder.deleteNoAnswerFromIntent", map);
  }

  public List<Map<String, Object>> getBertIntentByDestIntent(Map<String, Object> map) {
    return selectList("builder.getBertIntentByDestIntent", map);
  }

}
