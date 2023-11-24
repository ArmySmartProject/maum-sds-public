package maum.brain.sds.collector.client;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.HashMap;
import java.util.Map;
import maum.brain.bert.intent.BertIntentFinderGrpc;
import maum.brain.bert.intent.Itc;
import maum.brain.sds.collector.component.SdsCollectorDao;
import maum.brain.sds.collector.data.SdsBackendDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BertItfClient {

    @Autowired
    private SdsCollectorDao dao;

    private static final String SERVICE_NAME = "ITF";
    private static final Logger logger = LoggerFactory.getLogger(BertItfClient.class);

    public Map<String, Object> getIntent(String host, String utter) {

        Map<String, Object> map = new HashMap<>();
        Itc.Intent intent;
        ManagedChannel channel = null;
        String bertLogString = "";
        try {

            SdsBackendDto destinationInfo = dao.getBackendDestination(host, SERVICE_NAME);
            channel = ManagedChannelBuilder.forAddress(destinationInfo.getIp(), destinationInfo.getPort())
                .usePlaintext()
                .build();

            BertIntentFinderGrpc.BertIntentFinderBlockingStub stub = BertIntentFinderGrpc.newBlockingStub(channel);

            intent = stub.getIntentSimple(Itc.Utterance.newBuilder().setUtter(utter).build());
            map.put("intent", intent);
            map.put("bertItfId", destinationInfo.getId());
            logger.info("[BERT-ITC] Input: {}, Intent: {}", utter, intent.getIntent());
            bertLogString = "[BERT-ITC] Input: " + utter + ", Intent: " + intent.getIntent();
            channel.shutdown();

        } catch (Exception e) {
            logger.error("[BERT-ITC] ERR: {}", e.getMessage());
            bertLogString = "[BERT-ITC] ERR: " + e.getMessage();
        } finally {
            if (channel != null) {
                channel.shutdown();
            }
        }
        map.put("logString", bertLogString);
        return map;
    }

    public Map<String, Object> getIntent(SdsBackendDto destinationInfo, String utter) {

        Map<String, Object> map = new HashMap<>();
        Itc.Intent intent;
        String bertLogString = "";
        ManagedChannel channel = null;
        try {
            channel = ManagedChannelBuilder.forAddress(destinationInfo.getIp(), destinationInfo.getPort())
                .usePlaintext()
                .build();

            BertIntentFinderGrpc.BertIntentFinderBlockingStub stub = BertIntentFinderGrpc.newBlockingStub(channel);

            intent = stub.getIntentSimple(Itc.Utterance.newBuilder().setUtter(utter).build());
            map.put("intent", intent);
            map.put("prob",intent.getProb());
            logger.info("[BERT-ITC] Input: {}, Intent: {}, Prob: {}", utter, intent.getIntent(), intent.getProb());
            bertLogString = "[BERT-ITC] Input: " + utter
                + ", Intent: " + intent.getIntent()
                + ", Prob: " + intent.getProb();

        } catch (Exception e) {
            logger.error("[BERT-ITC] ERR: {}", e.getMessage());
            bertLogString = "[BERT-ITC] ERR: {}" + e.getMessage();
        } finally {
            if (channel != null) {
                channel.shutdown();
            }
        }
        map.put("logString", bertLogString);

        return map;
    }
}
