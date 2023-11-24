package first.builder.service;

import first.builder.dao.NqaDAO;
import first.builder.vo.IndexingHistoryEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class NQAIndexingHistoryServiceImpl implements NQAIndexingHistoryService {

  private static final Logger logger = LoggerFactory
      .getLogger(NQAIndexingHistoryServiceImpl.class);

  @Resource(name = "nqaDAO")
  private NqaDAO nqaDAO;

  /**
   * 가장 마지막 history DB에서 조회
   * @return
   * @throws Exception
   */
  @Override
  public IndexingHistoryEntity selectLastHistory() throws Exception {
    logger.debug("===== selectLastHistory");
    try {
      return nqaDAO.selectLastHistory();
    } catch (Exception e) {
      logger.error("===== selectLastIndexingHistory :: {}", e.getMessage());
      throw e;
    }
  }

  /**
   * address 기준 가장 마지막 history DB에서 조회
   * @param address
   * @return
   * @throws Exception
   */
  @Override
  public IndexingHistoryEntity selectLastIndexingHistory(int channelId, int categoryId) throws Exception {
    logger.debug("===== selectLastIndexingHistory channelId {}, categoryId {}", channelId, categoryId);
    try {
      return nqaDAO.selectHistoryByAddress(channelId, categoryId);
    } catch (Exception e) {
      logger.error("===== selectLastIndexingHistory :: {}", e.getMessage());
      throw e;
    }
  }

  /**
   * indexingHistory DB Insert
   * @param indexingHistoryEntity
   * @return
   * @throws Exception
   */
  @Override
  public IndexingHistoryEntity insertIndexingHistory(IndexingHistoryEntity indexingHistoryEntity) throws Exception {
    logger.debug("===== insertIndexingHistory indexingHistoryEntity {}", indexingHistoryEntity);
    try {
      return nqaDAO.insertIndexingHistory(indexingHistoryEntity);
    } catch (Exception e) {
      logger.error("===== insertIndexingHistory :: {}", e.getMessage());
      throw e;
    }
  }

  /**
   * indexingHistory DB Update
   * @param indexingHistoryEntity
   * @return
   * @throws Exception
   */
  @Override
  public IndexingHistoryEntity updateIndexingHistory(IndexingHistoryEntity indexingHistoryEntity) throws Exception {
    logger.debug("===== updateIndexingHistory indexingHistoryEntity {}", indexingHistoryEntity);
    try {
      return nqaDAO.updateIndexingHistory(indexingHistoryEntity);
    } catch (Exception e) {
      logger.error("===== updateIndexingHistory :: {}", e.getMessage());
      throw e;
    }
  }
}
