package first.builder.dao;

import first.builder.vo.IndexingHistoryEntity;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository("nqaDAO")
public class NqaDAO extends AbstractDAO2 {

  public IndexingHistoryEntity selectLastHistory() {
    IndexingHistoryEntity indexingHistoryEntity =
        (IndexingHistoryEntity) selectOne("nqa.selectLastHistory");
    return indexingHistoryEntity;
  }

  public IndexingHistoryEntity selectHistoryByAddress(int channelId, int categoryId) {
    Map<String, Object> param = new HashMap<>();
    param.put("channelId", channelId);
    param.put("categoryId", categoryId);
    IndexingHistoryEntity indexingHistoryEntity =
        (IndexingHistoryEntity) selectOne("nqa.selectHistoryByAddress", param);
    return indexingHistoryEntity;
  }

  public IndexingHistoryEntity insertIndexingHistory(IndexingHistoryEntity indexingHistoryEntity) {

    insert("nqa.insertIndexingHistory", indexingHistoryEntity);

    return indexingHistoryEntity;
  }

  public IndexingHistoryEntity updateIndexingHistory(IndexingHistoryEntity indexingHistoryEntity) {
    Map<String, Object> param = new HashMap<>();
//    param.put("name", name);
//    param.put("lang", lang);
//    param.put("scenarioJson", scenarioJson);
//    param.put("userId", userId);
//    param.put("companyId", companyId);
//    Date time = new Date();
//    param.put("createdAt", time);

//    int updateCnt = (Integer) update("nqa.updateIndexingHistory", indexingHistoryEntity);
    //

    return indexingHistoryEntity;
  }
}
