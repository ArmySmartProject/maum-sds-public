package first.builder.service;

import first.builder.vo.LogVO;
import first.common.util.Criteria;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;
import java.util.Map;

public interface BuilderService {

	String chatExcelUpload(File destFile, Map<String, Object> map) throws Exception;
	String chatExcelUploadHTask(File destFile, Map<String, Object> map) throws Exception;

	void excelUpload(File destFile, Map<String, Object> map) throws Exception;

	void bertExcelUpload(File destFile, Map<String, Object> map) throws Exception;

	void insertBackendInfo(Map<String, Object> map) throws Exception;

	List<LogVO> selectChatLogList(Criteria cri) throws Exception;

	void selectChatLogListExcel(HttpServletResponse response) throws Exception;

	int countChatLog() throws Exception;

	List<LogVO> selectFlowNo(String session) throws Exception;

	List<Map<String, Object>> getChatbotList(Map<String, Object> map) throws Exception;

	List<Map<String, Object>> getIntentStcList(Map<String, Object> map) throws Exception;

	List<Map<String, Object>> getIntentStcListV2(Map<String, Object> map) throws Exception;

	int getIntentCount(Map<String, Object> map) throws Exception;

	List<Map<String, Object>> getSentenceList(Map<String, Object> map) throws Exception;

	int getSentenceCount(Map<String, Object> map) throws Exception;

	List<Map<String, String>> getAnswerList(Map<String, Object> map) throws Exception;
	
	int getTaskCount(Map<String, Object> map) throws Exception;

	int getTaskCountSearch(Map<String, Object> map) throws Exception;
	
	List<Map<String, Object>> getIntentDetail(Map <String, Object> map) throws Exception;

	List<Map<String, Object>> getIntentDetailByMain(Map <String, Object> map) throws Exception;

	List<Map<String, Object>> getAnswerDetailSearch(Map <String, Object> map) throws Exception;

	List<Map<String, Object>> getAllIntents(Map <String, Object> map) throws Exception;

	List<Map<String, Object>> getAllBertIntents(Map <String, Object> map) throws Exception;

	List<Map<String, Object>> getImageCarousel(Map <String, Object> map) throws Exception;

	List<Map<String, Object>> getIntentRelInAnsDetail(Map <String, Object> map) throws Exception;

	List<Map<String, Object>> getSettingVal(Map <String, Object> map) throws Exception;

	List<Map<String, Object>> getStyleCSS(Map <String, Object> map) throws Exception;

	List<Map<String, Object>> getTaskCheck(Map <String, Object> map) throws Exception;

	List<Map<String, Object>> selectIntentForDelete(Map <String, Object> map) throws Exception;

	List<Map<String, Object>> selectBackendInfoForDelete(Map <String, Object> map) throws Exception;

	List<Map<String, Object>> selectBertIntentForDelete(Map <String, Object> map) throws Exception;

	List<Map<String, Object>> selectRegexIntentForDelete(Map <String, Object> map) throws Exception;

	List<Map<String, Object>> selectReplaceDict(Map <String, Object> map) throws Exception;

	List<Map<String, Object>> selectIntentBeforeDel(Map <String, Object> map) throws Exception;

	List<Map<String, Object>> checkBertItfID(Map <String, Object> map) throws Exception;

	int insertBertIntentNow(Map <String, Object> map) throws Exception;

	void insertStyleCSS(Map <String, Object> map) throws Exception;

	void updateStyleCSS(Map <String, Object> map) throws Exception;

	void updateAccount(Map <String, Object> map) throws Exception;

	int insertAnswerNew(Map <String, Object> map) throws Exception;

	int insertAnswerByAll(Map <String, Object> map) throws Exception;

	int insertIntentNew(Map <String, Object> map) throws Exception;

	int insertIntentNewAll(Map <String, Object> map) throws Exception;

	void insertIntentRelNew(Map <String, Object> map) throws Exception;

	List<Map<String, Object>> selectIntentNoAnswer(Map <String, Object> map) throws Exception;

	void deleteAccountByHost(Map <String, Object> map) throws Exception;

	void deleteAccountByHostV2(Map <String, Object> map) throws Exception;

	void deleteIntentRelByHost(Map <String, Object> map) throws Exception;

	void deleteBertIntentByHost(Map <String, Object> map) throws Exception;

	void deleteRegexRuleByHost(Map <String, Object> map) throws Exception;

	void deleteBertSentenceByHost(Map <String, Object> map) throws Exception;

	void deleteAnswerByNo(Map <String, Object> map) throws Exception;

	void updateOldIntent(Map <String, Object> map) throws Exception;

	void deleteOldIntentRel(Map <String, Object> map) throws Exception;

	void deleteOldIntentByLang(Map <String, Object> map) throws Exception;

	void deleteIntentByNo(Map <String, Object> map) throws Exception;

	List<Map<String, Object>> getRegexList(Map<String, Object> map) throws Exception;

	List<Map<String, Object>> getRegexListV2(Map<String, Object> map) throws Exception;

	List<Map<String, Object>> getRegexListAll(Map<String, Object> map) throws Exception;

	int getRegexCount(Map<String, Object> map) throws Exception;

	void insertRegex(Map<String, Object> map) throws Exception;

	void deleteRegex(Map<String, Object> map) throws Exception;

	List<Map<String, Object>> getRegexDetail(Map<String, Object> map) throws Exception;

	void updateRegex(Map<String, Object> map) throws Exception;

	void deleteIntention(Map<String, Object> map) throws Exception;

	int getIntentNo(Map<String, Object> map) throws Exception;

	List<Map<String, Object>> selectIntention(Map<String, Object> map) throws Exception;

	void insertIntention(Map<String, Object> map) throws Exception;

	int insertIntentionV2(Map<String, Object> map) throws Exception;

	void deleteRegexRule(Map<String, Object> map) throws Exception;

	List<Map<String, Object>> getHostByAccount(Map<String, Object> map) throws Exception;


	List<Map<String, Object>> getHostName(Map<String, Object> map) throws Exception;

	List<Map<String, Object>> getNewHostName(Map<String, Object> map) throws Exception;

	List<Map<String, Object>> getDomain() throws Exception;

	int addChatbot(Map <String, Object> map) throws Exception;

	int insertBertIntentNew(Map <String, Object> map) throws Exception;

	int insertBackendInfos(Map<String, Object> map) throws Exception;

	List<Map<String, Object>> getBackendInfo(Map<String, Object> map) throws Exception;

	List<Map<String, Object>> getIntentAnswer(Map<String, Object> map) throws Exception;

	List<Map<String, Object>> getIntentRelByAll(Map<String, Object> map) throws Exception;

	List<Map<String, Object>> getBertIntentByNo(Map<String, Object> map) throws Exception;

	void insertIntentRelByAll(Map<String, Object> map) throws Exception;

	void insertReplaceDict(Map<String, Object> map) throws Exception;

	List<Map<String, Object>> getChatInfo(Map<String, Object> map) throws Exception;
	List<Map<String, Object>> getChatInfoHTask(Map<String, Object> map) throws Exception;

	List<Map<String, Object>> getBertIntent(Map<String, Object> map) throws Exception;

	List<Map<String, Object>> getIntentRel(Map<String, Object> map) throws Exception;

	List<Map<String, Object>> getFallback(Map<String, Object> map) throws Exception;

	void updateStyleCSSSupplier(Map<String, Object> map) throws Exception;

	void changeAccountLang(Map<String, Object> map) throws Exception;

	List<Map<String, Object>> getIntentNameByNo(Map<String, Object> map) throws Exception;

	int checkBertIntent(Map<String, Object> map) throws Exception;

	Map<String, Object> getChatLang(Map<String, Object> map) throws Exception;

	Map<String, Object> logDebug(Map<String, Object> map) throws Exception;

	int checkRegex(Map<String, Object> map) throws Exception;

	int getReplaceDictCount(Map<String, Object> map) throws Exception;
	List<Map<String, Object>> getReplaceDictLst(Map<String, Object> map) throws Exception;
	int addReplaceDict(Map<String, Object> map) throws Exception;
	int updateReplaceDict(Map<String, Object> map) throws Exception;
	int deleteReplaceDict(Map<String, Object> map) throws Exception;
	int addReplaceDictLst(List<Map<String, Object>> list) throws Exception;
	int deleteReplaceDictAll(Map<String, Object> map) throws Exception;
	String replaceDictExcelUpload(File destFile, Map<String, Object> map) throws Exception;

	int replaceDictCnt(Map<String, Object> map) throws Exception;
	Map<String, Object> replaceDictgetAfter(Map<String, Object> map) throws Exception;

	List<Map<String, Object>> getIntentTaskDetail(Map map);

	List<Map<String, Object>> getRegexInfoList(String host);

	List<Map<String, Object>> getIntentList(String host);

	int insertRegexIntentList(List<Map<String, Object>> list) throws Exception;
	String regexExcelUpload(File destFile, Map<String, Object> map) throws Exception;

	List<Map<String, Object>> getNqaStatusChatbotList();
	Map<String, Object> deleteRegexAllByHostV2(List<Map <String, Object>> list);

	List<Map<String, Object>> getEntitiesList(Map<String, Object> map);
	void deleteEntities(int hostNum);

	List<Map<String, Object>> selectEachSrcList(List<Map<String, Object>> list);
	void updateIntentTask(List<Map<String, Object>> map);
	void deleteNoAnswerFromIntent(Map<String, Object> map);
	List<Map<String, Object>> getBertIntentByDestIntent(Map<String, Object> map);
}
