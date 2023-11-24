package first.builder.service;

import first.builder.vo.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import maum.brain.qa.nqa.Admin.UploadAnswerListResponse;
import org.springframework.core.io.InputStreamResource;

public interface NQAGrpcService {

  // IndexingHistoryEntity 관련 함수들 임시 주석처리
  IndexStatusEntity getIndexingStatus(int channelId, int categoryId, Boolean checkTrainingHost) throws Exception;

  HashMap<String, Object> getIndexedKeywords(int channelId, int categoryId) throws Exception;

  IndexStatusEntity fullIndexing(int channelId, int categoryId, int collectionType) throws Exception;

  Map getIndexingHistory(int channelId, int categoryId) throws Exception;

//  IndexStatusEntity additionalIndexing(int channelId, int categoryId, int collectionType) throws Exception;

//  IndexStatusEntity abortIndexing(int collectionType) throws Exception;

  List<Answer> getAnswerList(int categoryId, int startRow, int endRow, String sortModel,
      String sortType, List<HashMap<String, String>> searchList) throws Exception;

  List<Answer> getQAsetByCategory(int categoryId) throws Exception;

  Map<String, Integer> getQaCount(int categoryId) throws Exception;

  List<Channel> getChannelList() throws Exception;

  Channel getChannelById(int channelId) throws Exception;

  List<Category> getCategoryListByChannelId(int channelId) throws Exception;

  List<Category> getCategoryListByName(Category category) throws Exception;

  List<Layer> getLayerListByCategory(int categoryId) throws Exception;

  List<Answer> getAnswerById(int answerId) throws Exception;

  Category getCategoryById(int categoryId) throws Exception;

  Answer addAnswer(Answer answer) throws Exception;

  Channel addChannel(Channel channel) throws Exception;

  Category addCategory(Category category) throws Exception;

  Answer editAnswer(Answer answer) throws Exception;

  Channel editChannel(Channel channel) throws Exception;

  Category editCategory(Category category) throws Exception;

  int removeAnswers(List<Map<String, Integer>> answerIdSets) throws Exception;

  int removeChannel(int channelId) throws Exception;

  int removeCategory(List<Integer> categoryIds) throws Exception;

  UploadAnswerListResponse uploadFiles(List<Map<String, Object>> qaSets, Integer channelId,
      Integer categoryId) throws Exception;

  InputStreamResource downloadFile(Integer channelId, Integer listCount, String sortModel,
      String sortType, List<HashMap<String, String>> searchList) throws Exception;

  Map<String, Object> addDefaultChannelCategory(Channel channel) throws Exception;
}
