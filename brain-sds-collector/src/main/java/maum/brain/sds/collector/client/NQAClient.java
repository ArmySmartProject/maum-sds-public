package maum.brain.sds.collector.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import maum.brain.bert.intent.Itc;
import maum.brain.qa.nqa.Admin.AnswerList;
import maum.brain.qa.nqa.Admin.GetAnswerByIdRequest;
import maum.brain.qa.nqa.Admin.NQaAdminChannel;
import maum.brain.qa.nqa.NQaAdminServiceGrpc;
import maum.brain.qa.nqa.NQaAdminServiceGrpc.NQaAdminServiceBlockingStub;
import maum.brain.qa.nqa.NQaServiceGrpc;
import maum.brain.qa.nqa.NQaServiceGrpc.NQaServiceBlockingStub;
import maum.brain.qa.nqa.Nqa;
import maum.brain.qa.nqa.Nqa.*;
import maum.brain.sds.collector.component.SdsCollectorDao;
import maum.brain.sds.collector.data.SdsBackendDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class NQAClient {

    @Autowired
    private SdsCollectorDao dao;
    private static final String SERVICE_NAME = "NQA";

    private static final Logger logger = LoggerFactory.getLogger(NQAClient.class);

    private String nqaHost;
    private int nqaPort;

    public void setDestinationInfo() {
        SdsBackendDto destinationInfo = dao.getBackendDestination("", SERVICE_NAME);
        if (destinationInfo != null) {
            nqaHost = destinationInfo.getIp();
            nqaPort = destinationInfo.getPort();
        }
        logger.info("[NQA] server addr => " + nqaHost + ":" + nqaPort);
    }

    public String getNqaHost() {
        return nqaHost;
    }

    public int getNqaPort() {
        return nqaPort;
    }

    /**
     * NQA에서 Intent를 가져오는 함수
     * NQA의 Answer로 답변 대신 Intent를 사용하고 있다.
     * ex) Q: 골든튤립 스위트룸, A: 객실 종류 (Intent)
     **/
    public List<Object> getIntent(String host, String utter, String task) {
      List<Object> returnList = new ArrayList<>();
        try {
          Map<String, Object> nqaResponse = this.getAnswerFromNQA(host, utter, task);
          Itc.Intent intent = this.toIntentProto(nqaResponse);
          String nqaLogString = (String) nqaResponse.get("logString");
          returnList.add(intent);
          returnList.add(nqaLogString);
          returnList.add(nqaResponse.get("scope"));
          return returnList;

        } catch (Exception e) {
            logger.error("[NQA] ERR: {}", e.getMessage());
            String nqaLogString = "[NQA] ERR: " + e.getMessage();
            returnList.add(Itc.Intent.newBuilder().build());
            returnList.add(nqaLogString);
            returnList.add(0);
            return returnList;
        }
    }

    /**
     * NQA와 통신하는 함수
     * 이전 계층 정보를 포함한 context, 질문(계층), 이후계층을 저장할 context를 입력받음.
     * 답변 결과들을 리턴한다.
     **/
    private Map<String, Object> getAnswerFromNQA(String host, String question, String task) throws Exception {
        Map<String, Object> answerOutputs = new HashMap<>();
        String nqaLogString = "";

        String channelName = getChannelById(Integer.parseInt(host));
        if (channelName == null || channelName.equals("")) {
          logger.info("[NQA] No NQA Data");
          nqaLogString = "[NQA] No NQA Data";
          answerOutputs.put("logString",nqaLogString);
          return answerOutputs;
        }
        Map<String, Object> questionResponse = searchQuestion(channelName, question, task);  // 답변 검색할 ID 리스트

        if (questionResponse.get("answerId") == null) {
            logger.info("[NQA] No Match, Question: {}, Task: {}", question, task);
            nqaLogString = "[NQA] No Match, Question: " + question + ", Task: " + task;
        } else {
            String []idList = questionResponse.get("answerId").toString().split("-");
            answerOutputs = getAnswerById(Integer.parseInt(idList[0]));
            answerOutputs.put("scope", questionResponse.get("scope"));
            logger.info("[NQA] Matched, Answer: {}, Question: {}, Task: {}, AnswerId: {}",
              answerOutputs.get("answer"), question, questionResponse.get("task"), idList[0]);
            nqaLogString = "[NQA] Matched, Answer: " +answerOutputs.get("answer")
                + ", Question: " + question
                + ", Task: "+questionResponse.get("task")
                + ", AnswerId: " + idList[0];
        }
        answerOutputs.put("logString",nqaLogString);
        answerOutputs.put("scope",questionResponse.get("scope"));
        return answerOutputs;
    }

    /**
     * NQA와 통신하여 채널의 이름을 받아오는 함수.
     *
     * @param host 챗봇 host.
     * @return 해당 host의 해당하는 채널이름 리턴.
     */
    private String getChannelById(int host) throws Exception {
      ManagedChannel channel = ManagedChannelBuilder.forAddress(nqaHost, nqaPort).usePlaintext(true)
          .build();
      NQaAdminServiceBlockingStub stub = NQaAdminServiceGrpc.newBlockingStub(channel);
      NQaAdminChannel.Builder getChannelByIdRequest = NQaAdminChannel.newBuilder();
      getChannelByIdRequest.setId(host);
      NQaAdminChannel result = stub.withDeadlineAfter(1, TimeUnit.SECONDS)
          .getChannelById(getChannelByIdRequest.build());

        channel.shutdown();
        try{
            channel.awaitTermination(5, TimeUnit.SECONDS);
        }catch (InterruptedException e){
            logger.error("[NQA] channelShutdownError - getChannelById : ", e);
            Thread.currentThread().interrupt();
        }finally {
            channel.shutdownNow();
        }

      return result.getName();
    }

    /**
     * NQA와 통신하여 질문 검색을 하는 함수.
     *
     * @param question 질문내용.
     * @return 질문을 검색하여 나온 답변의 ID들을 리턴.
     */
    private Map<String, Object> searchQuestion(String channelName, String question, String task) throws Exception {
        Map<String, Object> questionMap = new HashMap<>();
        ManagedChannel channel = ManagedChannelBuilder.forAddress(nqaHost, nqaPort).usePlaintext(true).build();
        NQaServiceBlockingStub stub = NQaServiceGrpc.newBlockingStub(channel);
        /*
        SearchQuestionRequest searchQuestionInput = SearchQuestionRequest.newBuilder()
                .setNtop(5)  // 검색 결과로 뽑아올 최대 ID 갯수
                .setScore(6)  // 검색 결과의 최소 스코어
                .setMaxSize(2)  // 검색 결과의 최대 사이즈(형태소 분석 결과 갯수)
                .setQueryType(QueryType.ALL)  // 검색 타입
                .setUserQuery(QueryUnit.newBuilder().setPhrase(question))
                .setChannel("REDTIE") // 임시
                .addCategory(host)
                .build();
                */
        SearchQuestionRequest searchQuestionInput = SearchQuestionRequest.newBuilder()
            .setNtop(100)
            .setIndexMm(0.73f)
            .setChannel(channelName)
            .addCategory(task) // 임시
            .addCategory("공통")
            .setQueryType(QueryType.AND)
            .setQueryTarget(Nqa.QueryTarget.WQ)
            .setUserQuery(Nqa.QueryUnit.newBuilder().setPhrase(question).build()).build();

        SearchQuestionResponse questionOutput = stub.withDeadlineAfter(1, TimeUnit.SECONDS)
            .searchQuestion(searchQuestionInput);

        if (questionOutput.getQuestionsList().size() > 0) {
          for (int i = 0; i < questionOutput.getQuestionsList().size(); i++) {
            if (questionOutput.getQuestions(i).getCategory().equals(task)) {
              questionMap.put("answerId", questionOutput.getQuestions(i).getAnswerId());
              questionMap.put("scope", questionOutput.getQuestions(i).getScore());
              questionMap.put("task", task);
              break;
            }
          }
          if (questionMap.isEmpty()) {
            questionMap.put("answerId", questionOutput.getQuestions(0).getAnswerId());
            questionMap.put("scope", questionOutput.getQuestions(0).getScore());
            questionMap.put("task", "공통");
          }
        }

        channel.shutdown();
        try{
            channel.awaitTermination(5, TimeUnit.SECONDS);
        }catch (InterruptedException e){
            logger.error("[NQA] channelShutdownError - searchQuestion : ", e);
            Thread.currentThread().interrupt();
        }finally {
            channel.shutdownNow();
        }

        return questionMap;
    }

    /**
     * NQA와 통신하여 답변 정보를 가져오는 함수.
     *
     * @param answerId 답변id.
     * @return 해당 답변id의 답변 정보를 리턴.
     */
    private Map<String, Object> getAnswerById(int answerId) throws Exception {
      Map<String, Object> answerOutputs = new HashMap<>();
      ManagedChannel channel = ManagedChannelBuilder.forAddress(nqaHost, nqaPort).usePlaintext(true).build();
      NQaAdminServiceBlockingStub stub = NQaAdminServiceGrpc.newBlockingStub(channel);
      GetAnswerByIdRequest.Builder request = GetAnswerByIdRequest.newBuilder();
      request.setId(answerId);
      AnswerList result = stub.withDeadlineAfter(1, TimeUnit.SECONDS)
          .getAnswerById(request.build());
      answerOutputs.put("answer", result.getQaSets(0).getAnswer().getAnswer());

      channel.shutdown();
      try{
          channel.awaitTermination(5, TimeUnit.SECONDS);
      }catch (InterruptedException e){
          logger.error("[NQA] channelShutdownError - getAnswerById : ", e);
          Thread.currentThread().interrupt();
      }finally {
          channel.shutdownNow();
      }

      return answerOutputs;
    }

    /**
     * NQA와 통신하여 답변 검색을 하는 함수.
     *
     * param idList 질문 검색 결과(답변 ID 리스트)
     * @return 답변 검색 결과들
     */
//    private SearchAnswerResponse searchAnswer(List<String> idList) throws Exception {
//        ManagedChannel channel = ManagedChannelBuilder.forAddress(nqaHost, nqaPort).usePlaintext(true).build();
//        NQaServiceBlockingStub stub = NQaServiceGrpc.newBlockingStub(channel);
//        /*
//        SearchAnswerRequest searchAnswerInput = SearchAnswerRequest.newBuilder()
//                .setQueryTarget(QueryTarget.AID)
//                .setQueryType(QueryType.ALL)
//                .addAllIds(idList)
//                .setNtop(5)
//                .build();
//        */
//        SearchAnswerRequest searchAnswerInput = SearchAnswerRequest.newBuilder()
//            .setQueryTarget(QueryTarget.AID)
//            .addAllIds(idList)
//            .build();
//
//        SearchAnswerResponse answerOutputs = stub.searchAnswer(searchAnswerInput);
//
//        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
//
//        return answerOutputs;
//    }

    private Itc.Intent toIntentProto(Map<String, Object> nqaResponse) throws Exception {
        if (nqaResponse.get("answer") == null) {
            return Itc.Intent.newBuilder().build();
        }

        Itc.Intent.Builder intentProto = Itc.Intent.newBuilder();

        intentProto.setIntent(nqaResponse.get("answer").toString());
        intentProto.setProb((Float) nqaResponse.get("scope"));

        return intentProto.build();
    }

}
