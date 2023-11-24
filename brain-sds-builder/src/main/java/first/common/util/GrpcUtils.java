package first.common.util;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrpcUtils {

  static final Logger logger = LoggerFactory.getLogger(GrpcUtils.class);

  public static ManagedChannel getChannel(String host, int port) {
    return ManagedChannelBuilder.forAddress(host, port)
        .usePlaintext()
        .maxInboundMessageSize(1024 * 1024 * 64)
        .build();
  }

  public static void closeChannel(ManagedChannel channel) {
    channel.shutdown();
    try {
      channel.awaitTermination(5, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      logger.error("closeChannel e : " , e);
      Thread.currentThread().interrupt();
    } finally {
      channel.shutdownNow();
    }
  }
}
