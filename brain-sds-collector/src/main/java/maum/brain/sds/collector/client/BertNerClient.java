package maum.brain.sds.collector.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import maum.brain.ner.NamedEntityRecognitionGrpc;
import maum.brain.ner.NerV2;
import maum.brain.sds.collector.component.SdsCollectorDao;
import maum.brain.sds.collector.data.SdsBackendDto;
import maum.brain.sds.data.vo.SdsEntity;
import maum.brain.sds.data.vo.SdsEntityList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BertNerClient {
    private static final String SERVICE_NAME = "NER";

    @Autowired
    public SdsCollectorDao dao;

    public SdsEntityList getEntities(String utter, String host){
        ManagedChannel channel = null;
        SdsEntityList entityList = new SdsEntityList();
        try {
            SdsBackendDto destinationInfo = dao.getBackendDestination(host, SERVICE_NAME);

            channel = ManagedChannelBuilder.forAddress(destinationInfo.getIp(), destinationInfo.getPort())
                .usePlaintext()
                .build();

            NamedEntityRecognitionGrpc.NamedEntityRecognitionBlockingStub stub = NamedEntityRecognitionGrpc.newBlockingStub(channel);
            NerV2.NerResponse dic = stub.getNamedEntities(NerV2.NerRequest.newBuilder().setText(utter).build());

            entityList = this.toEntityList(dic);
        } catch (Exception e) {
            System.out.println("[BERT-NER] ERR: " + e.getMessage());
        } finally {
            if (channel != null) {
                channel.shutdown();
            }
            return entityList;
        }
    }

    private SdsEntityList toEntityList(NerV2.NerResponse answers){
        SdsEntityList ret = new SdsEntityList();
        for(NerV2.Entity entity : answers.getEntitiesList())
            ret.setEntity(
                new SdsEntity(
                    entity.getCategory(),
                    entity.getText()
                )
            );

        return ret;
    }
}
