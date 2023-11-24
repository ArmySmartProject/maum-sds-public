package maum.brain.sds.collector.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.ArrayList;
import java.util.Collection;
import maum.brain.sds.collector.data.memory.SdsMemoryDataConverter;
import maum.brain.sds.collector.data.memory.SdsMemoryDto;
import maum.brain.sds.memory.Memory;
import maum.brain.sds.memory.SdsMemoryGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
public class SdsMemoryClient {

    private static final Logger logger = LoggerFactory.getLogger(SdsMemoryClient.class);

    private int port = 7410;

    public void setPortOffset(int portOffset){
        this.port = 7410 + portOffset;
    }

    public Memory.MemMessage initUser(String host, String session){
        return this.memMessageType("initUser", host, session);
    }

    public Memory.MemMessage endUser(String host, String session){
        return this.memMessageType("endUser", host, session);
    }

    public Memory.MemMessage passTurn(String host, String session){
        return this.memMessageType("passTurn", host, session);
    }

    public Memory.MemMessage addMemory(SdsMemoryDto memoryRequest){
        ManagedChannel channel = null;
        Memory.MemMessage memMessage = null;

        try {
            channel =  ManagedChannelBuilder.forAddress("127.0.0.1", port).usePlaintext().build();
            SdsMemoryGrpc.SdsMemoryBlockingStub blockingStub = SdsMemoryGrpc.newBlockingStub(channel);
            memMessage = blockingStub.addMemory(SdsMemoryDataConverter.convert(memoryRequest));
        } catch (Exception e) {
            logger.error("[MEMORY-CLIENT] ERR: {}", e.getMessage());
            try{
                logger.error("[MEMORY-CLIENT] addMemory :: input SdsMemoryDto: {}", memoryRequest.toString());
            }catch (Exception e2){}
        } finally {
            if (channel != null) {
                channel.shutdown();
            }
        }
        return memMessage;
    }

    public Memory.MemTurn getLastMemory(String host, String session){
        ManagedChannel channel = null;
        Memory.MemTurn memMessage = null;

        try {
            channel =  ManagedChannelBuilder.forAddress("127.0.0.1", port).usePlaintext().build();
            SdsMemoryGrpc.SdsMemoryBlockingStub blockingStub = SdsMemoryGrpc.newBlockingStub(channel);
            memMessage = blockingStub.getLastMemory(SdsMemoryDataConverter.convert(host, session));
        } catch (Exception e) {
            logger.error("[MEMORY-CLIENT] ERR: {}", e.getMessage());
            try{
                logger.error("[MEMORY-CLIENT] getLastMemory :: input host, session : {}, {}", host, session);
            }catch (Exception e2){}
        } finally {
            if (channel != null) {
                channel.shutdown();
            }
        }
        return memMessage;
    }

    public Iterator<Memory.MemTurn> getAllMemory(String host, String session){
        ManagedChannel channel = null;
        Iterator<Memory.MemTurn> memMessage = null;
        Collection<Memory.MemTurn> cache = new ArrayList<>();
        Iterator<Memory.MemTurn> result = null;

        try {
            channel =  ManagedChannelBuilder.forAddress("127.0.0.1", port).usePlaintext().build();
            SdsMemoryGrpc.SdsMemoryBlockingStub blockingStub = SdsMemoryGrpc.newBlockingStub(channel);
            memMessage = blockingStub.getAllMemory(SdsMemoryDataConverter.convert(host, session));

            // memMessage가 초기화되는 현상 발생하여 다른 iterator에 copy
            while (memMessage.hasNext()) {
                Memory.MemTurn memory = memMessage.next();
//                System.out.println(memory);
                cache.add(memory);
            }
            result = cache.iterator();
            return result;
        } catch (Exception e) {
            logger.error("[MEMORY-CLIENT] ERR: {}", e.getMessage());
            try{
                logger.error("[MEMORY-CLIENT] getAllMemory :: input host, session : {}, {}", host, session);
            }catch (Exception e2){}
        }
        finally {
            if (channel != null) {
                channel.shutdown();
            }
        }
        return result;
    }

    public Memory.MemMessage clearMemory(String host, String session){
        return this.memMessageType("clearMemory", host, session);
    }

    private Memory.MemMessage memMessageType(String method, String host, String session) {
        ManagedChannel channel = null;
        Memory.MemMessage memMessage = null;

        try {
            channel =  ManagedChannelBuilder.forAddress("127.0.0.1", port).usePlaintext().build();
            SdsMemoryGrpc.SdsMemoryBlockingStub blockingStub = SdsMemoryGrpc.newBlockingStub(channel);

            if (method.equals("initUser")) {
                memMessage = blockingStub.userInit(SdsMemoryDataConverter.convert(host, session));
            } else if (method.equals("endUser")) {
                memMessage = blockingStub.userEnd(SdsMemoryDataConverter.convert(host, session));
            } else if (method.equals("passTurn")) {
                memMessage = blockingStub.passTurn(SdsMemoryDataConverter.convert(host, session));
            } else if (method.equals("clearMemory")) {
                memMessage = blockingStub.clearMemory(SdsMemoryDataConverter.convert(host, session));
            }
        } catch (Exception e) {
            logger.error("[MEMORY-CLIENT] ERR: {}", e.getMessage());
            try{
                logger.error("[MEMORY-CLIENT] memMessageType :: input method, host, session : {}, {}, {}", method, host, session);
            }catch (Exception e2){}
        } finally {
            if (channel != null) {
                channel.shutdown();
            }
        }
        return memMessage;
    }
}
