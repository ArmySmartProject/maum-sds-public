package first.builder.service;

import first.builder.vo.SimpleBotVO;
import first.builder.vo.SimpleBotVO.Scenario;
import java.io.File;
import java.util.List;
import java.util.Map;

public interface SimpleBotService {

	List<Map> getSimpleBotListFromCompanyId(String companyId, String keyword) throws Exception;
	List<Map> getSimpleBotListFromUserId(String userId, String keyword) throws Exception;
	String applyScenario(int simplebotId, String userId, String companyId, String scenarioJson) throws Exception;
	String applyScenarioV2(int simplebotId, String userId, String companyId, String scenarioJson, String isExcelUpload, String campaignInfoObj) throws Exception;
	String uploadScenario(int simplebotId, File destFile) throws Exception;
	Map uploadScenarioV2(int simplebotId, File destFile) throws Exception;
	SimpleBotVO getSimplebotById(int simplebotId) throws Exception;
	int createSimpleBot(String userId, String companyId, String name, int lang) throws Exception;
	void deleteScenario(int host, int simplebotId) throws Exception;
	void deleteBySimplebotIdV2(int simplebotId) throws Exception;
	void deleteByHostV2(int host, int simplebotId, int lang) throws Exception;
	void deleteByIntentNo(int host, int lang, int no) throws Exception;

	String getTestCustData(int simplebotId) throws Exception;
	void saveTestCustData(int simplebotId, String custDataJson) throws Exception;

	void applyCallMeta(int simplebotId, Scenario scenario) throws Exception;

	long getContractNo(int simplebotId, String telNo) throws Exception;

	int getWaitingCustomer(int contractNo);
	int checkScenarioName(Map<String, Object> contractNo);
	void updateScenarioName(Map<String, Object> contractNo);

	void deleteAllByHostV2(Map<String, Object> map);
}
