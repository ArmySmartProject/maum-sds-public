package first.builder.service;

import first.builder.vo.IndexingHistoryEntity;

public interface NQAIndexingHistoryService {

  IndexingHistoryEntity selectLastHistory() throws Exception;

  IndexingHistoryEntity selectLastIndexingHistory(int channelId, int categoryId) throws Exception;

  IndexingHistoryEntity insertIndexingHistory(IndexingHistoryEntity indexingHistoryEntity) throws Exception;

  IndexingHistoryEntity updateIndexingHistory(IndexingHistoryEntity indexingHistoryEntity) throws Exception;
}
