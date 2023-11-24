package first.builder.dao;

import first.builder.vo.CallTaskMetaVO;
import first.builder.vo.SimpleBotVO;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository("simpleBotDAO")
public class SimpleBotDAO extends AbstractDAO2 {

  public SimpleBotVO getSimpleBotVO(int simpleBotId) {
    SimpleBotVO simpleBotVO = new SimpleBotVO();
    simpleBotVO.setId(simpleBotId);

    simpleBotVO = (SimpleBotVO) selectOne("simplebot.selectSimpleBotById", simpleBotVO);

    return simpleBotVO;
  }

  public int insertSimpleBot(String name, int lang, String scenarioJson, String userId, String companyId) {
    Map<String, Object> param = new HashMap<>();
    param.put("name", name);
    param.put("lang", lang);
    param.put("scenarioJson", scenarioJson);
    param.put("userId", userId);
    param.put("companyId", companyId);
    Date time = new Date();
    param.put("createdAt", time);

    insert("simplebot.insertSimpleBot", param);

    return Integer.parseInt(param.get("id").toString());
  }

  public int saveScenarioJson(int simpleBotId, String scenarioJson) {
    SimpleBotVO simpleBotVO = new SimpleBotVO();
    simpleBotVO.setId(simpleBotId);
    simpleBotVO.setScenarioJson(scenarioJson);
    Date time = new Date();
    simpleBotVO.setUpdatedAt(time);

    Object obj = update("simplebot.updateScenarioJson", simpleBotVO);

    return (Integer) obj;
  }

  public SimpleBotVO updateApplied(int simpleBotId) {
    SimpleBotVO simpleBotVO = new SimpleBotVO();
    simpleBotVO.setId(simpleBotId);
    Date time = new Date();
    simpleBotVO.setAppliedAt(time);

    int updateCnt = (Integer) update("simplebot.updateApplied", simpleBotVO);
    if (updateCnt == 1) {
      simpleBotVO = (SimpleBotVO) selectOne("simplebot.selectSimpleBotById", simpleBotVO);
    }

    return simpleBotVO;
  }

  public void updateHostForSimpleBot(SimpleBotVO simpleBot) {

    selectOne("simplebot.updateHostForSimpleBot", simpleBot);
  }

  public List<Map> getSimpleBotListFromCompanyId (String companyId, String keyword) {
    Map<String, Object> param = new HashMap<>();
    param.put("companyId", companyId);
    param.put("keyword", keyword);
    List<Map> simpleBotList = selectList("simplebot.selectSimpleBotListFromCompanyId", param);
    return simpleBotList;
  }

  public List<Map> getSimpleBotListFromUserId (String userId, String keyword) {
    Map<String, Object> param = new HashMap<>();
    param.put("userId", userId);
    param.put("keyword", keyword);
    List<Map> simpleBotList = selectList("simplebot.selectSimpleBotListFromUserId", param);
    return simpleBotList;
  }

  public void deleteSimpleBot(int simplebotId){
    Map<String, Object> param = new HashMap<>();
    param.put("simplebotId", simplebotId);
    delete("simplebot.deleteSimpleBotFromId", param);
  }

  public void deleteCallTaskMeta(int simplebotId) {
    Map<String, Object> param = new HashMap<>();
    param.put("simplebotId", simplebotId);
    delete("simplebot.deleteCallTaskMetaFromId", param);
  }

  public void saveCallTaskMeta(List<CallTaskMetaVO> param) {
    insert("simplebot.insertCallTaskMeta", param);
  }

  public String getTestCustData(int simplebotId) {
    Map<String, Object> param = new HashMap<>();
    param.put("simplebotId", simplebotId);
    String custData = (String) selectOne("simplebot.selectTestCustData", param);
    return custData;
  }

  public int saveTestCustData(int simplebotId, String custDataJson) {
    Map<String, Object> param = new HashMap<>();
    param.put("simplebotId", simplebotId);
    param.put("custDataJson", custDataJson);
    int cnt = (Integer) update("simplebot.updateTestCustData", param);
    return cnt;
  }

  public long getContractNo(int simplebotId, String telNo) {
    // 심플봇 캠패인 아이디
	int campaignId = 116;
    long contractNo = 0;
    Map<String, Object> param = new HashMap<>();
    param.put("telNo", telNo);
    param.put("campaignId", campaignId);
    param.put("simplebotId", simplebotId);

    // 기존에는 CM_CONTRACT에서 검색해서 있으면 가져다 썼지만,
    // simplebot마다 독립적인 contractNo를 가지기 위해 mapping된 값이 없으면 무조건 insert 하도록 바꿈
    // Object contractObj = selectOne("simplebot.selectContractNo", param);

    insert("simplebot.insertContract", param);
    contractNo = (Long) param.get("contractNo");

    // 심플봇 테이블에 업데이트 // cnt=1이면 성공
    int cnt = (Integer) update("simplebot.updateTestContractNo", param);

    return contractNo;
  }
  
  public int getWaitingCustomer(int contractNo) {
		Map<String, Object> param = new HashMap<>();
		param.put("contractNo",contractNo);
		param.put("campaignId", 116);
		int waitingCount = (Integer) selectOne("simplebot.getWaitingCustomer", param);
		return waitingCount;
	}

  public int checkScenarioName(Map<String, Object> inputParam) {
    int checkCount = (Integer) selectOne("simplebot.checkScenarioName", inputParam);
    return checkCount;
  }

  public void updateScenarioName(Map<String, Object> inputParam) {
    update("simplebot.updateScenarioName", inputParam);
  }

  public List<String> getCampaignList(int simpleBotId) {

    return selectList("simplebot.getCampaignList", simpleBotId);
  }

  public void insertCampaignInfo(Map<String, Object> campaignInfoMap){
    insert("simplebot.insertCampaignInfo", campaignInfoMap);
  }

  public List<String> getCampaignInfoTask(String campaignId){
    return selectList("simplebot.getCampaignInfoTask", campaignId);
  }

  public void updateCampaignInfo(Map<String, Object> campaignInfoMap){
    update("simplebot.updateCampaignInfo", campaignInfoMap);
  }

  public void updateCampaignInfoTask(Map<String, Object> campaignInfoMap){
    update("simplebot.updateCampaignInfoTask", campaignInfoMap);
  }
  public void updateOldCampaignInfoTask(Map<String, Object> updateCampaignInfoMap){
    update("simplebot.updateOldCampaignInfoTask", updateCampaignInfoMap);
  }
}
