package first.builder.service;

import com.google.protobuf.Empty;
import first.builder.vo.*;
import first.common.util.FileUtils;
import first.common.util.NQAGrpcInterfaceManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import first.common.util.NqaUploadStatusList;
import io.grpc.stub.StreamObserver;
import maum.brain.qa.nqa.Admin;
import maum.brain.qa.nqa.Admin.AddAnswerRequest;
import maum.brain.qa.nqa.Admin.AddAnswerResponse;
import maum.brain.qa.nqa.Admin.AddCategoryRequest;
import maum.brain.qa.nqa.Admin.AddCategoryResponse;
import maum.brain.qa.nqa.Admin.AnswerList;
import maum.brain.qa.nqa.Admin.AnswerList.QaSet;
import maum.brain.qa.nqa.Admin.CategoryList;
import maum.brain.qa.nqa.Admin.ChannelList;
import maum.brain.qa.nqa.Admin.CollectionType;
import maum.brain.qa.nqa.Admin.EditAnswerRequest;
import maum.brain.qa.nqa.Admin.EditCategoryRequest;
import maum.brain.qa.nqa.Admin.EditCategoryResponse;
import maum.brain.qa.nqa.Admin.GetAnswerByIdRequest;
import maum.brain.qa.nqa.Admin.GetAnswerListByCategoryRequest;
import maum.brain.qa.nqa.Admin.GetCategoryByIdRequest;
import maum.brain.qa.nqa.Admin.GetCategoryByIdResponse;
import maum.brain.qa.nqa.Admin.GetIndexedKeywordsRequest;
import maum.brain.qa.nqa.Admin.GetIndexedKeywordsRequest.Section;
import maum.brain.qa.nqa.Admin.GetIndexedKeywordsResponse;
import maum.brain.qa.nqa.Admin.IndexStatus;
import maum.brain.qa.nqa.Admin.IndexType;
import maum.brain.qa.nqa.Admin.IndexingRequest;
import maum.brain.qa.nqa.Admin.LayerList;
import maum.brain.qa.nqa.Admin.NQaAdminAnswer;
import maum.brain.qa.nqa.Admin.NQaAdminCategory;
import maum.brain.qa.nqa.Admin.NQaAdminChannel;
import maum.brain.qa.nqa.Admin.NQaAdminPaging;
import maum.brain.qa.nqa.Admin.NQaAdminQuestion;
import maum.brain.qa.nqa.Admin.RemoveAnswerRequest;
import maum.brain.qa.nqa.Admin.RemoveAnswerRequest.AnswerIdSet;
import maum.brain.qa.nqa.Admin.RemoveAnswerResponse;
import maum.brain.qa.nqa.Admin.RemoveCategoryRequest;
import maum.brain.qa.nqa.Admin.RemoveCategoryResponse;
import maum.brain.qa.nqa.Admin.RemoveChannelResponse;
import maum.brain.qa.nqa.Admin.SingleQaSet;
import maum.brain.qa.nqa.Admin.UploadAnswerListRequest;
import maum.brain.qa.nqa.Admin.UploadAnswerListResponse;
import first.builder.service.NQAIndexingHistoryService;
import maum.brain.qa.nqa.Admin.getQAsetByCategoryRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:nqa/config/nqa_config.properties")
public class NQAGrpcServiceImpl implements NQAGrpcService {

  private static final Logger logger = LoggerFactory.getLogger(NQAGrpcServiceImpl.class);

  @Value("${nqa.server.ip}")
  private String nqaIp;

  @Value("${nqa.server.port}")
  private int nqaPort;

  @Autowired
  private FileUtils fileUtils;

  @Value("${excel.nqa.item.filename}")
  private String nqaFileName;

  @Value("#{'${excel.nqa.item.columns}'.split(',')}")
  private List<String> nqaColumns;

  @Value("#{'${excel.nqa.item.headers}'.split(',')}")
  private List<String> nqaHeaders;

  @Autowired
  private NQAIndexingHistoryService nqaIndexingHistoryService;

  /**
   * Index Status 를 조회 합니다.
   */
  @Override
  public IndexStatusEntity getIndexingStatus(int channelId, int categoryId, Boolean checkTrainingHost) throws Exception {
    logger.debug("===== getIndexingStatus");
    try {
      IndexStatusEntity indexStatusEntity = new IndexStatusEntity();
      NQAGrpcInterfaceManager client = new NQAGrpcInterfaceManager(nqaIp, nqaPort);
      IndexStatus questionIndexStatus = client.getIndexingStatus(Empty.newBuilder().build());
      if (!questionIndexStatus.getStatus()) {
        // question 이 Indexing 중이 아니라면 answer 로 Indexing 중인지도 확인한다.
        IndexStatus answerIndexStatus = client.getIndexingStatus(Empty.newBuilder().build());
        if (answerIndexStatus.getStatus()) {
          indexStatusEntity.setCollectionType(1);
          indexStatusEntity.setStatus(true);
          indexStatusEntity.setTotal(answerIndexStatus.getTotal());
          indexStatusEntity.setFetched(answerIndexStatus.getFetched());
          indexStatusEntity.setProcessed(answerIndexStatus.getProcessed());
        } else {
          // answer도 인덱싱중이지않으니 현재는 Indexing중이지 않은상태 이럴경우 address 기준 가장마지막 history 보냄
          IndexingHistoryEntity lastIndexingHistory = nqaIndexingHistoryService
              .selectLastIndexingHistory(channelId, categoryId);
          indexStatusEntity.setLatestIndexingHistory(lastIndexingHistory);
          indexStatusEntity.setStatus(false);
        }
      } else {
        indexStatusEntity.setCollectionType(0);
        indexStatusEntity.setStatus(true);
        indexStatusEntity.setTotal(questionIndexStatus.getTotal());
        indexStatusEntity.setFetched(questionIndexStatus.getFetched());
        indexStatusEntity.setProcessed(questionIndexStatus.getProcessed());
      }

      // nqa 인덱싱중인 경우 어떤 데이터가 학습중인지 알 수 없기때문에 마지막 history를 조회하여 host를 비교하도록 한다.
      if (checkTrainingHost) {
        IndexingHistoryEntity lastIndexingHistory2 = nqaIndexingHistoryService.selectLastHistory();
        indexStatusEntity.setMessage(lastIndexingHistory2.getChannel_id().toString());
      } else {
        indexStatusEntity.setMessage(Integer.toString(channelId));
      }

      return indexStatusEntity;
    } catch (Exception e) {
      logger.error("===== getIndexingStatus :: {}", e.getMessage());
      throw e;
    }
  }

  /**
   * Indexed 된 Keyword들을 조회 해옵니다 (question, answer, attr1,attr2,attr3,attr4
   * layer1,layer2,layer3,layer4,tag) 모두 각각 요청을 날려서 가져와야합니다 layer1,2,3,4 와 attr1,2,3,4 는 5개씩 만 가져오고
   * 나머지는 10개 가져옵니다
   */
  @Override
  public HashMap<String, Object> getIndexedKeywords(int channelId, int categoryId)
      throws Exception {
    logger.debug("===== getIndexedKeywords ");
    try {
      HashMap<String, Object> result = new HashMap<>();
      NQAGrpcInterfaceManager client = new NQAGrpcInterfaceManager(nqaIp, nqaPort);
      GetIndexedKeywordsRequest.Builder getIndexedKeywordsRequest = GetIndexedKeywordsRequest
          .newBuilder();
      getIndexedKeywordsRequest.setNtop(1000);
      getIndexedKeywordsRequest.setChannelId(channelId);
      getIndexedKeywordsRequest.setCategoryId(categoryId);
      // get Question
      getIndexedKeywordsRequest.setSection(Section.QUESTION);
      GetIndexedKeywordsResponse questionRes = client
          .getIndexedKeywords(getIndexedKeywordsRequest.build());
      result.put("question", questionRes.getIndexWordsMap());
      // get Answer
      getIndexedKeywordsRequest.setSection(Section.ANSWER);
      GetIndexedKeywordsResponse answerRes = client
          .getIndexedKeywords(getIndexedKeywordsRequest.build());
      result.put("answer", answerRes.getIndexWordsMap());
      // get tag
      getIndexedKeywordsRequest.setSection(Section.TAGS_MORPH);
      GetIndexedKeywordsResponse tagRes = client
          .getIndexedKeywords(getIndexedKeywordsRequest.build());
      result.put("tag", tagRes.getIndexWordsMap());
      // attr, layer는 각각 1,2,3,4 5개씩 가져옴
      getIndexedKeywordsRequest.setNtop(1000);
      // get layer1
      getIndexedKeywordsRequest.setSection(Section.LAYER1);
      GetIndexedKeywordsResponse layer1Res = client
          .getIndexedKeywords(getIndexedKeywordsRequest.build());
      LinkedHashMap<String, Long> wordMap = new LinkedHashMap<>();
      SortedSet<String> keys = new TreeSet<>(layer1Res.getIndexWordsMap().keySet());
      for (String key : keys) {
        Long value = layer1Res.getIndexWordsMap().get(key);
        wordMap.put(key, value);
        // do something
      }
      result.put("layer1", wordMap);
      // get layer2
      getIndexedKeywordsRequest.setSection(Section.LAYER2);
      GetIndexedKeywordsResponse layer2Res = client
          .getIndexedKeywords(getIndexedKeywordsRequest.build());
      wordMap = new LinkedHashMap<>();
      keys = new TreeSet<>(layer2Res.getIndexWordsMap().keySet());
      for (String key : keys) {
        Long value = layer2Res.getIndexWordsMap().get(key);
        wordMap.put(key, value);
        // do something
      }
      result.put("layer2", wordMap);
      // get layer3
      getIndexedKeywordsRequest.setSection(Section.LAYER3);
      GetIndexedKeywordsResponse layer3Res = client
          .getIndexedKeywords(getIndexedKeywordsRequest.build());
      wordMap = new LinkedHashMap<>();
      keys = new TreeSet<>(layer3Res.getIndexWordsMap().keySet());
      for (String key : keys) {
        Long value = layer3Res.getIndexWordsMap().get(key);
        wordMap.put(key, value);
        // do something
      }
      result.put("layer3", wordMap);
      // get layer4
      getIndexedKeywordsRequest.setSection(Section.LAYER4);
      GetIndexedKeywordsResponse layer4Res = client
          .getIndexedKeywords(getIndexedKeywordsRequest.build());
      wordMap = new LinkedHashMap<>();
      keys = new TreeSet<>(layer4Res.getIndexWordsMap().keySet());
      for (String key : keys) {
        Long value = layer4Res.getIndexWordsMap().get(key);
        wordMap.put(key, value);
        // do something
      }
      result.put("layer4", wordMap);
      // get layer5
      getIndexedKeywordsRequest.setSection(Section.LAYER5);
      GetIndexedKeywordsResponse layer5Res = client
          .getIndexedKeywords(getIndexedKeywordsRequest.build());
      wordMap = new LinkedHashMap<>();
      keys = new TreeSet<>(layer5Res.getIndexWordsMap().keySet());
      for (String key : keys) {
        Long value = layer5Res.getIndexWordsMap().get(key);
        wordMap.put(key, value);
        // do something
      }
      result.put("layer5", wordMap);
      // get layer6
      getIndexedKeywordsRequest.setSection(Section.LAYER6);
      GetIndexedKeywordsResponse layer6Res = client
          .getIndexedKeywords(getIndexedKeywordsRequest.build());
      wordMap = new LinkedHashMap<>();
      keys = new TreeSet<>(layer6Res.getIndexWordsMap().keySet());
      for (String key : keys) {
        Long value = layer6Res.getIndexWordsMap().get(key);
        wordMap.put(key, value);
        // do something
      }
      result.put("layer6", wordMap);
      // get attribute1
      getIndexedKeywordsRequest.setSection(Section.ATTR1);
      GetIndexedKeywordsResponse attr1Res = client
          .getIndexedKeywords(getIndexedKeywordsRequest.build());
      result.put("attr1", attr1Res.getIndexWordsMap());
      // get attribute2
      getIndexedKeywordsRequest.setSection(Section.ATTR2);
      GetIndexedKeywordsResponse attr2Res = client
          .getIndexedKeywords(getIndexedKeywordsRequest.build());
      result.put("attr2", attr2Res.getIndexWordsMap());
      // get attribute3
      getIndexedKeywordsRequest.setSection(Section.ATTR3);
      GetIndexedKeywordsResponse attr3Res = client
          .getIndexedKeywords(getIndexedKeywordsRequest.build());
      result.put("attr3", attr3Res.getIndexWordsMap());
      // get attribute4
      getIndexedKeywordsRequest.setSection(Section.ATTR4);
      GetIndexedKeywordsResponse attr4Res = client
          .getIndexedKeywords(getIndexedKeywordsRequest.build());
      result.put("attr4", attr4Res.getIndexWordsMap());
      // get attribute5
      getIndexedKeywordsRequest.setSection(Section.ATTR5);
      GetIndexedKeywordsResponse attr5Res = client
          .getIndexedKeywords(getIndexedKeywordsRequest.build());
      result.put("attr5", attr5Res.getIndexWordsMap());
      // get attribute6
      getIndexedKeywordsRequest.setSection(Section.ATTR6);
      GetIndexedKeywordsResponse attr6Res = client
          .getIndexedKeywords(getIndexedKeywordsRequest.build());
      result.put("attr6", attr6Res.getIndexWordsMap());

      return result;
    } catch (Exception e) {
      logger.error("===== getIndexedKeywords :: {}", e.getMessage());
      throw e;
    }
  }

  /**
   * Full Indexing 요청을 합니다.
   */
  @Override
  public IndexStatusEntity fullIndexing(int channelId, int categoryId, int collectionType)
      throws Exception {
    logger.debug("===== fullIndexing :: channelId {}, categoryId {}, collectionType {}",
        channelId, categoryId, collectionType);
    try {
      IndexStatusEntity indexStatusEntity = new IndexStatusEntity();
      NQAGrpcInterfaceManager client = new NQAGrpcInterfaceManager(nqaIp, nqaPort);
      IndexingRequest.Builder fullIndexingRequest = IndexingRequest.newBuilder();
      fullIndexingRequest.setIndexType(IndexType.FULL);
      fullIndexingRequest.setChannelId(channelId);
      fullIndexingRequest.setCategoryId(categoryId);
      if (collectionType == 0) {
        fullIndexingRequest.setCollectionType(CollectionType.QUESTION);
      } else if (collectionType == 1) {
        fullIndexingRequest.setCollectionType(CollectionType.ANSWER);
      } else {
        fullIndexingRequest.setCollectionType(CollectionType.BOTH);
      }
      IndexStatus fullIndexStatus = client.fullIndexing(fullIndexingRequest.build());
      if (fullIndexStatus.getStatus()) {
        indexStatusEntity.setCollectionType(collectionType);
        indexStatusEntity.setStatus(true);
        indexStatusEntity.setTotal(fullIndexStatus.getTotal());
        indexStatusEntity.setFetched(fullIndexStatus.getFetched());
        indexStatusEntity.setProcessed(fullIndexStatus.getProcessed());
      } else {
        indexStatusEntity.setStatus(false);
      }
      // insert history
      IndexingHistoryEntity indexingHistoryEntity = new IndexingHistoryEntity();
      indexingHistoryEntity.setChannel_id(channelId);
      indexingHistoryEntity.setCategory_id(categoryId);
      indexingHistoryEntity.setType("fullIndexing");
      indexingHistoryEntity.setStatus(indexStatusEntity.isStatus());
      indexingHistoryEntity.setTotal(indexStatusEntity.getTotal());
      indexingHistoryEntity.setFetched(indexStatusEntity.getFetched());
      indexingHistoryEntity.setProcessed(indexStatusEntity.getProcessed());
      indexingHistoryEntity.setMessage(indexStatusEntity.getMessage());
      indexingHistoryEntity.setCreated_at(new Date());
      indexingHistoryEntity.setStop_yn(false);
      indexingHistoryEntity.setAddress(nqaIp + ":" + nqaPort);

      nqaIndexingHistoryService.insertIndexingHistory(indexingHistoryEntity);
      return indexStatusEntity;
    } catch (Exception e) {
      logger.error("===== fullIndexing :: {}", e.getMessage());
      throw e;
    }
  }

  /**
   * AdditionalIndexing을 실행합니다.
   */
//  @Override
//  public IndexStatusEntity additionalIndexing(int channelId, int categoryId, int collectionType)
//      throws Exception {
//    logger.debug("===== additionalIndexing :: channelId {}, categoryId {}, collectionType {}",
//        channelId, categoryId, collectionType);
//    try {
//      IndexStatusEntity indexStatusEntity = new IndexStatusEntity();
//      NQAGrpcInterfaceManager client = new NQAGrpcInterfaceManager(nqaIp, nqaPort);
//      IndexingRequest.Builder additionalIndexingRequest = IndexingRequest.newBuilder();
//      additionalIndexingRequest.setIndexType(IndexType.ADD);
//      additionalIndexingRequest.setChannelId(channelId);
//      additionalIndexingRequest.setCategoryId(categoryId);
//      if (collectionType == 0) {
//        additionalIndexingRequest.setCollectionType(CollectionType.QUESTION);
//      } else if (collectionType == 1) {
//        additionalIndexingRequest.setCollectionType(CollectionType.ANSWER);
//      } else {
//        additionalIndexingRequest.setCollectionType(CollectionType.BOTH);
//      }
//      IndexStatus additionalIndexStatus = client.fullIndexing(additionalIndexingRequest.build());
//      if (additionalIndexStatus.getStatus()) {
//        indexStatusEntity.setCollectionType(collectionType);
//        indexStatusEntity.setStatus(true);
//        indexStatusEntity.setTotal(additionalIndexStatus.getTotal());
//        indexStatusEntity.setFetched(additionalIndexStatus.getFetched());
//        indexStatusEntity.setProcessed(additionalIndexStatus.getProcessed());
//      } else {
//        indexStatusEntity.setStatus(false);
//      }
//
//      // insert history
//      IndexingHistoryEntity indexingHistoryEntity = new IndexingHistoryEntity();
//      indexingHistoryEntity.setType("additionalIndexing");
//      indexingHistoryEntity.setStatus(indexStatusEntity.isStatus());
//      indexingHistoryEntity.setTotal(indexStatusEntity.getTotal());
//      indexingHistoryEntity.setFetched(indexStatusEntity.getFetched());
//      indexingHistoryEntity.setProcessed(indexStatusEntity.getProcessed());
//      indexingHistoryEntity.setMessage(indexStatusEntity.getMessage());
//      indexingHistoryEntity.setCreatedAt(new Date());
//      indexingHistoryEntity.setStopYn(false);
//      indexingHistoryEntity.setAddress(nqaIp + ":" + nqaPort);
//
//      nqaIndexingHistoryService
//          .insertIndexingHistory(indexingHistoryEntity);
//      return indexStatusEntity;
//    } catch (Exception e) {
//      logger.error("===== additionalIndexing :: {}", e.getMessage());
//      throw e;
//    }
//  }

  /**
   * Indexing을 취소합니다.
   */
//  @Override
//  public IndexStatusEntity abortIndexing(int collectionType) throws Exception {
//    logger
//        .debug("===== abortIndexing :: categoryId {}, collectionType {}, clean {}", collectionType);
//    try {
//      IndexStatusEntity indexStatusEntity = new IndexStatusEntity();
//      NQAGrpcInterfaceManager client = new NQAGrpcInterfaceManager(nqaIp, nqaPort);
//      IndexingRequest.Builder abortIndexingRequest = IndexingRequest.newBuilder();
//      if (collectionType == 0) {
//        abortIndexingRequest.setCollectionType(CollectionType.QUESTION);
//      } else {
//        abortIndexingRequest.setCollectionType(CollectionType.ANSWER);
//      }
//      IndexStatus abortIndexingStatus = client.abortIndexing(abortIndexingRequest.build());
//      if (abortIndexingStatus.getStatus()) {
//        indexStatusEntity.setCollectionType(collectionType);
//        indexStatusEntity.setStatus(true);
//        indexStatusEntity.setTotal(abortIndexingStatus.getTotal());
//        indexStatusEntity.setFetched(abortIndexingStatus.getFetched());
//        indexStatusEntity.setProcessed(abortIndexingStatus.getProcessed());
//      } else {
//        indexStatusEntity.setStatus(false);
//      }
//
//      IndexingHistoryEntity lastIndexingHistory = nqaIndexingHistoryService
//          .selectLastIndexingHistory(nqaIp + ":" + nqaPort);
//      lastIndexingHistory.setUpdatedAt(new Date());
//      lastIndexingHistory.setStopYn(true);
//      lastIndexingHistory.setMessage(abortIndexingStatus.getMessage());
//      nqaIndexingHistoryService.updateIndexingHistory(lastIndexingHistory);
//      return indexStatusEntity;
//    } catch (Exception e) {
//      logger.error("===== abortIndexing :: {}", e.getMessage());
//      throw e;
//    }
//  }

  @Override
  public Map getIndexingHistory(int channelId, int categoryId) throws Exception {
    logger.debug("===== getIndexingHistory");
    try {
      IndexingHistoryEntity history = nqaIndexingHistoryService.selectLastIndexingHistory(channelId, categoryId);
      Map result = new HashMap();
      if (history != null) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        result.put("id", history.getId());
        result.put("channelId", history.getChannel_id());
        result.put("categoryId", history.getCategory_id());
        result.put("address", history.getAddress());
        result.put("createdAt", format.format(history.getCreated_at()));
        result.put("fetched", history.getFetched());
        result.put("processed", history.getProcessed());
        result.put("status", history.getStatus());
        result.put("stopYn", history.getStop_yn());
        result.put("total", history.getTotal());
        result.put("type", history.getType());
      }
      return result;
    } catch (Exception e) {
      logger.error("===== getIndexingHistory :: {}", e.getMessage());
      throw e;
    }
  }

  /**
   * nqa 엔진에서 answer 전체 목록을 카테고리 ID로 조회
   */
  @Override
  public List<Answer> getAnswerList(int categoryId, int startRow, int endRow,
      String sortModel, String sortType, List<HashMap<String, String>> searchList)
      throws Exception {
    logger.debug("===== getAnswerList");
    try {
      NQAGrpcInterfaceManager client = new NQAGrpcInterfaceManager(nqaIp, nqaPort);
      GetAnswerListByCategoryRequest.Builder getAnswerListByCategoryRequest
          = GetAnswerListByCategoryRequest.newBuilder();
      getAnswerListByCategoryRequest.setCategoryId(categoryId);
      getAnswerListByCategoryRequest.setStartRow(startRow);
      getAnswerListByCategoryRequest.setEndRow(endRow);
      NQaAdminPaging.Builder paging = NQaAdminPaging.newBuilder();

      for (int i = 0; i < searchList.size(); i++) {
        String aa = searchList.get(i).get("searchField");
        paging.addSearchFiled(searchList.get(i).get("searchField"));
        paging.addSearchValue(searchList.get(i).get("searchValue"));
      }

      getAnswerListByCategoryRequest.addSearchList(paging);

      if (sortModel.equals("undefined") || sortType.equals("undefined")) {
        sortModel = "";
        sortType = "";
      }
      getAnswerListByCategoryRequest.setSortModel(sortModel);
      getAnswerListByCategoryRequest.setSortType(sortType);
      AnswerList answerList = client.getAnswerList(getAnswerListByCategoryRequest.build());
      List<Answer> answerEntities = new ArrayList<>();
      if (answerList.getQaSetsCount() == 0) {
        Answer answerEntity = new Answer();
        answerEntity.setListCount(answerList.getTotalCount());
        answerEntity.setQuestionCount(answerList.getQuestionCount());
        answerEntities.add(answerEntity);
      }
      for (int i = 0; i < answerList.getQaSetsCount(); i++) {
        Answer answerEntity = new Answer();
        answerEntity.makeEntityByProto(answerList.getQaSets(i).getAnswer());
        List<Question> questionEntities = new ArrayList<>();
        for (int j = 0; j < answerList.getQaSets(i).getQuestionsCount(); j++) {
          questionEntities
              .add(new Question().makeEntityByProto(answerList.getQaSets(i).getQuestions(j)));
        }
        answerEntity.setListCount(answerList.getTotalCount());
        answerEntity.setQuestionCount(answerList.getQuestionCount());
        answerEntity.setQuestions(questionEntities);
        answerEntities.add(answerEntity);
      }

      return answerEntities;
    } catch (Exception e) {
      logger.error("===== getAnswerList :: {}", e.getMessage());
      throw e;
    }
  }

  @Override
  public List<Answer> getQAsetByCategory(int categoryId) throws Exception {
    logger.debug("===== getQAsetByCategory");
    try {
      NQAGrpcInterfaceManager client = new NQAGrpcInterfaceManager(nqaIp, nqaPort);
      getQAsetByCategoryRequest.Builder getQASetByCategoryRequest
          = getQAsetByCategoryRequest.newBuilder();
      getQASetByCategoryRequest.setCategoryId(categoryId);
      AnswerList answerList = client.getQAsetByCategory(getQASetByCategoryRequest.build());
      List<Answer> answerEntities = new ArrayList<>();
      if (answerList.getQaSetsCount() == 0) {
        Answer answerEntity = new Answer();
        answerEntity.setListCount(answerList.getTotalCount());
        answerEntity.setQuestionCount(answerList.getQuestionCount());
        answerEntities.add(answerEntity);
      }
      for (int i = 0; i < answerList.getQaSetsCount(); i++) {
        Answer answerEntity = new Answer();
        answerEntity.makeEntityByProto(answerList.getQaSets(i).getAnswer());
        List<Question> questionEntities = new ArrayList<>();
        for (int j = 0; j < answerList.getQaSets(i).getQuestionsCount(); j++) {
          questionEntities
              .add(new Question().makeEntityByProto(answerList.getQaSets(i).getQuestions(j)));
        }
        answerEntity.setListCount(answerList.getTotalCount());
        answerEntity.setQuestionCount(answerList.getQuestionCount());
        answerEntity.setQuestions(questionEntities);
        answerEntities.add(answerEntity);
      }

      return answerEntities;
    } catch (Exception e) {
      logger.error("===== getQAsetByCategory :: {}", e.getMessage());
      throw e;
    }
  }

  /**
   * nqa 엔진에서 answer 전체 목록을 카테고리 ID로 조회
   * q&a count 수를 return
   */
  @Override
  public Map<String, Integer> getQaCount(int categoryId) throws Exception {
    logger.debug("===== getQaCount");
    try {
      NQAGrpcInterfaceManager client = new NQAGrpcInterfaceManager(nqaIp, nqaPort);
      getQAsetByCategoryRequest.Builder req = getQAsetByCategoryRequest.newBuilder();
      req.setCategoryId(categoryId);
      AnswerList answerList = client.getQAsetByCategory(req.build());

      Map<String, Integer> result = new HashMap<>();
      result.put("totalCount", answerList.getTotalCount());
      result.put("questionCount", answerList.getQuestionCount());
      return result;
    } catch (Exception e) {
      logger.error("===== getQaCount :: {}", e.getMessage());
      throw e;
    }
  }

  /**
   * nqa 엔진에서 전체 channel List 조회
   */
  @Override
  public List<Channel> getChannelList() throws Exception {
    logger.debug("===== getChannelList");
    try {
      NQAGrpcInterfaceManager client = new NQAGrpcInterfaceManager(nqaIp, nqaPort);
      ChannelList channelList = client.getChannelList(Empty.newBuilder().build());
      List<Channel> channelEntities = new ArrayList<>();
      for (int i = 0; i < channelList.getChannelsCount(); i++) {
        channelEntities.add(new Channel().makeEntityByProto(channelList.getChannels(i)));
      }
      return channelEntities;
    } catch (Exception e) {
      logger.error("===== getChannelList :: {}", e.getMessage());
      throw e;
    }
  }

  /**
   * nqa 에서 특정 channel 정보 조회
   */
  @Override
  public Channel getChannelById(int channelId) throws Exception {
    logger.debug("===== getChannelById channelId [{}]", channelId);
    try {
      NQAGrpcInterfaceManager client = new NQAGrpcInterfaceManager(nqaIp, nqaPort);
      NQaAdminChannel channelResponse = client.getChannelById(channelId);
      return new Channel().makeEntityByProto(channelResponse);
    } catch (Exception e) {
      logger.error("===== getChannelById :: {}", e.getMessage());
      throw e;
    }
  }

  /**
   * nqa 엔진에서 Channel 별 category List 조회
   */
  @Override
  public List<Category> getCategoryListByChannelId(int channelId) throws Exception {
    logger.debug("===== getCategoryListByChannelId channelId [{}]", channelId);
    try {
      // nqa ip,port 설정
      NQAGrpcInterfaceManager client = new NQAGrpcInterfaceManager(nqaIp, nqaPort);

      CategoryList categoryList = client.getCategoryListByChannelId(channelId);
      List<Category> categoryEntities = new ArrayList<>();

      for (int i = 0; i < categoryList.getCategoriesCount(); i++) {
        categoryEntities.add(new Category().makeEntityByProto(categoryList.getCategories(i)));
      }
      return categoryEntities;
    } catch (Exception e) {
      logger.error("===== getCategoryListByChannelId :: {}", e.getMessage());
      throw e;
    }
  }

  /**
   * nqa 엔진에서 name으로 category List 검색
   */
  @Override
  public List<Category> getCategoryListByName(Category category) throws Exception {
    logger.debug("===== getCategoryListByName category [{}]", category);
    try {
      // nqa ip,port 설정
      NQAGrpcInterfaceManager client = new NQAGrpcInterfaceManager(nqaIp, nqaPort);

      CategoryList categoryList = client.getCategoryListByName(category);
      List<Category> categoryEntities = new ArrayList<>();

      for (int i = 0; i < categoryList.getCategoriesCount(); i++) {
        categoryEntities.add(new Category().makeEntityByProto(categoryList.getCategories(i)));
      }
      return categoryEntities;
    } catch (Exception e) {
      logger.error("===== getCategoryListByName :: {}", e.getMessage());
      throw e;
    }
  }

  /**
   * nqa 엔진에서 전체 layer list 조회
   */
  @Override
  public List<Layer> getLayerListByCategory(int categoryId) throws Exception {
    logger.debug("===== getLayerListByCategory");
    try {
      NQAGrpcInterfaceManager client = new NQAGrpcInterfaceManager(nqaIp, nqaPort);
      NQaAdminCategory.Builder category = NQaAdminCategory.newBuilder();
      category.setId(categoryId);
      LayerList layerList = client.getLayerListByCategory(category.build());
      List<Layer> layerEntities = new ArrayList<>();
      for (int i = 0; i < layerList.getLayersCount(); i++) {
        layerEntities.add(new Layer().makeEntityByProto(layerList.getLayers(i)));
      }
      return layerEntities;
    } catch (Exception e) {
      logger.error("===== getLayerListByCategory :: {}", e.getMessage());
      throw e;
    }
  }

  /**
   * nqa 엔진에서 answerId로 answer, tag, question 을 조회
   */
  @Override
  public List<Answer> getAnswerById(int answerId) throws Exception {
    logger.debug("===== getAnswerById :: answerId {}", answerId);
    try {
      NQAGrpcInterfaceManager client = new NQAGrpcInterfaceManager(nqaIp,
          nqaPort);
      GetAnswerByIdRequest.Builder request = GetAnswerByIdRequest.newBuilder();
      request.setId(answerId);
      AnswerList responseAnswerList = client.getAnswerById(request.build());
      List<Answer> answerEntityList = new ArrayList<>();
      for (int i = 0; i < responseAnswerList.getQaSetsCount(); i++) {
        QaSet qaSet = responseAnswerList.getQaSets(i);
        Answer answerEntity = new Answer();
        answerEntity.makeEntityByProto(qaSet.getAnswer());
        List<Question> questionEntities = new ArrayList<>();
        for (int j = 0; j < qaSet.getQuestionsCount(); j++) {
          questionEntities
              .add(new Question().makeEntityByProto(qaSet.getQuestions(j)));
        }
        answerEntity.setQuestions(questionEntities);
        answerEntityList.add(answerEntity);
      }
      return answerEntityList;
    } catch (Exception e) {
      logger.error("===== getAnswerById :: {}", e.getMessage());
      throw e;
    }
  }

  /**
   * nqa 엔진에서 category Id로 category 정보 조회
   */
  @Override
  public Category getCategoryById(int categoryId) throws Exception {
    logger.debug("===== getCategoryById :: categoryId {}", categoryId);
    try {
      NQAGrpcInterfaceManager client = new NQAGrpcInterfaceManager(nqaIp, nqaPort);
      GetCategoryByIdRequest.Builder request = GetCategoryByIdRequest.newBuilder();
      request.setId(categoryId);
      GetCategoryByIdResponse getCategoryByIdResponse = client.getCategoryById(request.build());
      Category categoryEntity = new Category();
      categoryEntity.makeEntityByProto(getCategoryByIdResponse.getCategory());
      return categoryEntity;
    } catch (Exception e) {
      logger.error("===== getCategoryById :: {}", e.getMessage());
      throw e;
    }
  }

  /**
   * nqa 엔진에 answer insert 요청
   */
  @Override
  public Answer addAnswer(Answer answer) throws Exception {
    logger.debug("===== addAnswer :: answer {}", answer);
    try {
      NQAGrpcInterfaceManager client = new NQAGrpcInterfaceManager(nqaIp, nqaPort);
      AddAnswerRequest.Builder addAnswerRequest = AddAnswerRequest.newBuilder();
      addAnswerRequest.setAnswer(answer.makeProto());
      for (int i = 0; i < answer.getAddedQuestions().size(); i++) {
        addAnswerRequest.addQuestions(i, answer.getAddedQuestions().get(i).makeProto());
      }
      AddAnswerResponse addAnswerResponse = client.addAnswer(addAnswerRequest.build());

      Answer result = new Answer();
      result.makeEntityByProto(addAnswerResponse.getAnswer());
      List<Question> resultQuestions = new ArrayList<>();
      for (int i = 0; i < addAnswerResponse.getQuestionsCount(); i++) {
        resultQuestions.add(new Question().makeEntityByProto(addAnswerResponse.getQuestions(i)));
      }
      result.setQuestions(resultQuestions);
      return result;
    } catch (Exception e) {
      logger.error("===== addAnswer :: {}", e.getMessage());
      throw e;
    }
  }

  /**
   * nqa 엔진에 channel insert 요청
   */
  @Override
  public Channel addChannel(Channel channel) throws Exception {
    logger.debug("===== addChannel :: channel {}", channel);
    try {
      NQAGrpcInterfaceManager client = new NQAGrpcInterfaceManager(nqaIp, nqaPort);
      NQaAdminChannel addChannelResponse = client.addChannel(channel.makeProto());
      return new Channel().makeEntityByProto(addChannelResponse);
    } catch (Exception e) {
      logger.error("===== addChannel :: {}", e.getMessage());
      throw e;
    }
  }

  /**
   * nqa 엔진에 category insert 요청
   */
  @Override
  public Category addCategory(Category category) throws Exception {
    logger.debug("===== addCategory :: category {}", category);
    try {
      NQAGrpcInterfaceManager client = new NQAGrpcInterfaceManager(nqaIp, nqaPort);
      AddCategoryRequest.Builder addCategoryRequest = AddCategoryRequest.newBuilder();
      addCategoryRequest.setCategory(category.makeProto());
      AddCategoryResponse addCategoryResponse = client.addCategory(addCategoryRequest.build());

      Category result = new Category();
      result.makeEntityByProto(addCategoryResponse.getCategory());
      return result;
    } catch (Exception e) {
      logger.error("===== addCategory :: {}", e.getMessage());
      throw e;
    }
  }

  /**
   * nqa 엔진에 answer update 요청
   */
  @Override
  public Answer editAnswer(Answer answer) throws Exception {
    logger.debug("===== editAnswer :: answer {}", answer);
    try {
      NQAGrpcInterfaceManager client = new NQAGrpcInterfaceManager(nqaIp, nqaPort);
      EditAnswerRequest.Builder editAnswerRequest = EditAnswerRequest.newBuilder();
      EditAnswerRequest.EditQaSet.Builder editQASet = EditAnswerRequest.EditQaSet.newBuilder();
      editQASet.setAnswer(answer.makeProto());
      for (int i = 0; i < answer.getAddedQuestions().size(); i++) {
        editQASet.addAddedQuestions(i, answer.getAddedQuestions().get(i).makeProto());
      }
      for (int i = 0; i < answer.getEditedQuestions().size(); i++) {
        // 기존에 등록된 question
        editQASet.addEditedQuestions(i, answer.getEditedQuestions().get(i).makeProto());
      }
      for (int i = 0; i < answer.getRemovedQuestions().size(); i++) {
        // 기존에 등록되었으나 삭제된 question
        editQASet.addRemovedQuestions(i, answer.getRemovedQuestions().get(i).makeProto());
      }

      editAnswerRequest.addEditQaSets(editQASet);
      AnswerList answerList = client.editAnswer(editAnswerRequest.build());
      Answer result = new Answer();
      result.makeEntityByProto(editAnswerRequest.getEditQaSets(0).getAnswer());
      List<Question> questionEntities = new ArrayList<>();
      for (int i = 0; i < answerList.getQaSets(0).getQuestionsCount(); i++) {
        questionEntities
            .add(new Question().makeEntityByProto(answerList.getQaSets(0).getQuestions(i)));
      }
      result.setQuestions(questionEntities);
      return result;
    } catch (Exception e) {
      logger.error("===== editAnswer :: {}", e.getMessage());
      throw e;
    }
  }

  /**
   * nqa 엔진에 channel update 요청
   */
  @Override
  public Channel editChannel(Channel channel) throws Exception {
    logger.debug("===== editChannel :: channel {}", channel);
    try {
      NQAGrpcInterfaceManager client = new NQAGrpcInterfaceManager(nqaIp, nqaPort);
      NQaAdminChannel editChannelResponse = client.editChannel(channel.makeProto());
      return new Channel().makeEntityByProto(editChannelResponse);
    } catch (Exception e) {
      logger.error("===== editChannel :: {}", e.getMessage());
      throw e;
    }
  }

  /**
   * nqa 엔진에 category update 요청
   */
  @Override
  public Category editCategory(Category category) throws Exception {
    logger.debug("===== editCategory :: category {}", category);
    try {
      NQAGrpcInterfaceManager client = new NQAGrpcInterfaceManager(nqaIp, nqaPort);
      EditCategoryRequest.Builder editCategoryRequest = EditCategoryRequest.newBuilder();
      editCategoryRequest.setCategory(category.makeProto());

      EditCategoryResponse editCategoryResponse = client.editCategory(editCategoryRequest.build());
      Category result = new Category();
      result.makeEntityByProto(editCategoryResponse.getCategory());
      return result;
    } catch (Exception e) {
      logger.error("===== editCategory :: {}", e.getMessage());
      throw e;
    }
  }

  /**
   * nqa 엔진에 answer delete 요청
   */
  @Override
  public int removeAnswers(List<Map<String, Integer>> answerIdSets) throws Exception {
    logger.debug("===== removeAnswers :: answerIdSets {}", answerIdSets);
    try {
      NQAGrpcInterfaceManager client = new NQAGrpcInterfaceManager(nqaIp, nqaPort);
      RemoveAnswerRequest.Builder removeAnswerRequest = RemoveAnswerRequest.newBuilder();
      for (int i = 0; i < answerIdSets.size(); i++) {
        AnswerIdSet.Builder answerIdSet = AnswerIdSet.newBuilder();
        if (answerIdSets.get(i).containsKey("id")) {
          answerIdSet.setId(answerIdSets.get(i).get("id"));
          if (answerIdSets.get(i).containsKey("copyId")) {
            answerIdSet.setCopyId(answerIdSets.get(i).get("copyId"));
          }
        } else {
          throw new Exception("No argument 'id'");
        }
        removeAnswerRequest.addAnswerIdSets(answerIdSet.build());
      }
      RemoveAnswerResponse removeAnswerResponse = client.removeAnswer(removeAnswerRequest.build());
      return removeAnswerResponse.getCount();
    } catch (Exception e) {
      logger.error("===== removeAnswers :: {}", e.getMessage());
      throw e;
    }
  }

  /**
   * nqa 엔진에 channel delete 요청
   */
  @Override
  public int removeChannel(int channelId) throws Exception {
    logger.debug("===== removeChannel :: channelId {}", channelId);
    try {
      NQAGrpcInterfaceManager client = new NQAGrpcInterfaceManager(nqaIp, nqaPort);
      RemoveChannelResponse removeChannelResponse = client.removeChannel(channelId);
      return removeChannelResponse.getCount();
    } catch (Exception e) {
      logger.error("===== removeChannel :: {}", e.getMessage());
      throw e;
    }
  }

  /**
   * nqa 엔진에 category delete 요청
   */
  @Override
  public int removeCategory(List<Integer> categoryIds) throws Exception {
    logger.debug("===== removeCategory :: categoryIds {}", categoryIds);
    try {
      NQAGrpcInterfaceManager client = new NQAGrpcInterfaceManager(nqaIp, nqaPort);
      RemoveCategoryRequest.Builder removeCategoryRequest = RemoveCategoryRequest.newBuilder();
      for (int i = 0; i < categoryIds.size(); i++) {
        removeCategoryRequest.addId(categoryIds.get(i));
      }
      RemoveCategoryResponse removeCategoryResponse = client
          .removeCategory(removeCategoryRequest.build());
      return removeCategoryResponse.getCount();
    } catch (Exception e) {
      logger.error("===== removeCategory :: {}", e.getMessage());
      throw e;
    }
  }

  @Override
  public UploadAnswerListResponse uploadFiles(List<Map<String, Object>> qaSets, Integer channelId,
      Integer categoryId) throws Exception {
    logger.debug("===== uploadFiles :: channelId : {}, categoryId : {}, \n qaSets : ",
        channelId, categoryId, qaSets);
    Map<String, Object> status = new HashMap<>();
    status.put("host", channelId);
    try {
      NQAGrpcInterfaceManager client = new NQAGrpcInterfaceManager(nqaIp, nqaPort);
      Admin.UploadAnswerListResponse response = null;
      NQaAdminChannel.Builder channel = NQaAdminChannel.newBuilder();

      Admin.UploadStreamAnswerListRequest.Builder streamRequest = Admin.UploadStreamAnswerListRequest.newBuilder();
      UploadAnswerListRequest.Builder request = UploadAnswerListRequest.newBuilder();
      List<Admin.UploadStreamAnswerListRequest> list = new ArrayList<>();
      List<Admin.UploadAnswerListResponse> responseList = new ArrayList<>();

      channel.setId(channelId);
      request.setChannel(channel.build());
      request.setCategoryId(categoryId);
      request.setTotalCount(qaSets.size());
      for (Map<String, Object> qaSet : qaSets) {
        SingleQaSet.Builder singleQaSet = SingleQaSet.newBuilder();
        NQaAdminAnswer answer = ((Answer) qaSet.get("answer")).makeProto();
        NQaAdminQuestion question = ((Question) qaSet.get("question")).makeProto();

        singleQaSet.setAnswer(answer);
        singleQaSet.setQuestions(question);

        streamRequest.setQaSets(singleQaSet.build());
        list.add(streamRequest.build());
        request.addQaSets(singleQaSet.build());
      }

      if(qaSets.size() >= 20000){
        response = client.uploadStreamAnswerList(list, channelId.toString());
      }else{
        response = client.uploadAnswerList(request.build());
        status.put("nqaUploadStatus", "success");
        NqaUploadStatusList.nqaUploadStatusUpdate(status);
      }

      return response;
    } catch (Exception e) {
      status.put("nqaUploadStatus", "fails");
      NqaUploadStatusList.nqaUploadStatusUpdate(status);
      logger.error("===== uploadFiles :: {}", e.getMessage());
      throw e;
    }
  }

  @Override
  public InputStreamResource downloadFile(Integer categoryId, Integer listCount, String sortModel,
      String sortType, List<HashMap<String, String>> searchList) throws Exception {
    logger.debug("===== downloadFile :: categoryId : {}", categoryId);
    try {
      List<ExcelQASet> resultList = new ArrayList<>();
      NQAGrpcInterfaceManager client = new NQAGrpcInterfaceManager(nqaIp, nqaPort);
      // todo : channel단위 download 일때는 for문에 아래 categoryList를 쓰면 됨.
//      CategoryList categoryList = client.getCategoryListByChannelId(channelId);
      List<NQaAdminCategory> categoryList = new ArrayList<>();

      GetCategoryByIdRequest categoryIdReq = GetCategoryByIdRequest.newBuilder().setId(categoryId)
          .build();
      GetCategoryByIdResponse categoryRes = client.getCategoryById(categoryIdReq);
      NQaAdminCategory category1 = categoryRes.getCategory();
      categoryList.add(category1);

      for (NQaAdminCategory category : categoryList) {
        GetAnswerListByCategoryRequest.Builder cateReq = GetAnswerListByCategoryRequest
            .newBuilder();
        NQaAdminPaging.Builder paging = NQaAdminPaging.newBuilder();
        for (int i = 0; i < searchList.size(); i++) {
          paging.addSearchFiled(searchList.get(i).get("searchField"));
          paging.addSearchValue(searchList.get(i).get("searchValue"));
        }
        if (sortModel.equals("undefined") || sortType.equals("undefined")) {
          sortModel = "";
          sortType = "";
        }
        cateReq.addSearchList(paging);
        cateReq.setCategoryId(category.getId());
        cateReq.setEndRow(listCount);
        cateReq.setSortType(sortType);
        cateReq.setSortModel(sortModel);
        AnswerList answerList = client.getAnswerList(cateReq.build());

        for (QaSet qaSet : answerList.getQaSetsList()) {
          StringBuffer tagStr = new StringBuffer();
          ExcelQASet excelQASet = new ExcelQASet();
          excelQASet.setIntent(qaSet.getAnswer().getAnswer());
          for (int i = 0; i < qaSet.getAnswer().getTagsCount(); i++) {
            String tag = qaSet.getAnswer().getTagsList().get(i);
            tagStr.append(tag);
            if (i != (qaSet.getAnswer().getTagsCount() - 1)) {
              tagStr.append(",");
            }
          }

          for (NQaAdminQuestion question : qaSet.getQuestionsList()) {
            excelQASet.setQuestion(question.getQuestion());
            // deep copy
            ExcelQASet tmpQASet = excelQASet.getClone();
            resultList.add(tmpQASet);
          }
        }
      }

      return fileUtils.ObjectToExcel(resultList, nqaHeaders, nqaColumns, nqaFileName);
    } catch (Exception e) {
      logger.error("===== uploadFiles :: {}", e.getMessage());
      throw e;
    }
  }


  /**
   *  channel, category insert 공통함수
   *  param : channelId, channelName 들어간 Channel
   *  return : insert 된 channel, category 객체
   */
  @Override
  public Map<String, Object> addDefaultChannelCategory(Channel channel) throws Exception {
    Map<String, Object> map = new HashMap<>();
    Category category = new Category();
    int channelId = channel.getId();
    String channelName = channel.getName();
    try {
      channel = getChannelById(channelId);
      if (channel.getId() == 0) {
        channel.setId(channelId);
        channel.setName(channelName);
        channel = addChannel(channel);
      }
      category.setChannelId(channelId);
      category.setName("공통");
      List<Category> result = getCategoryListByName(category);
      if (result.size() == 0) {
        category = addCategory(category);
      } else {
        category = result.get(0);
      }
    } catch (Exception e) {
      logger.error("{} => ", e.getMessage(), e);
    }
    map.put("channel", channel);
    map.put("category", category);
    return map;
  }
}
