package first.common.util;

import com.google.protobuf.Empty;
import first.builder.vo.Category;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import maum.brain.qa.nqa.Admin;
import maum.brain.qa.nqa.Admin.AddAnswerRequest;
import maum.brain.qa.nqa.Admin.AddAnswerResponse;
import maum.brain.qa.nqa.Admin.AddCategoryRequest;
import maum.brain.qa.nqa.Admin.AddCategoryResponse;
import maum.brain.qa.nqa.Admin.AnswerList;
import maum.brain.qa.nqa.Admin.CategoryList;
import maum.brain.qa.nqa.Admin.ChannelList;
import maum.brain.qa.nqa.Admin.EditAnswerRequest;
import maum.brain.qa.nqa.Admin.EditCategoryRequest;
import maum.brain.qa.nqa.Admin.EditCategoryResponse;
import maum.brain.qa.nqa.Admin.GetAnswerByIdRequest;
import maum.brain.qa.nqa.Admin.GetAnswerListByCategoryRequest;
import maum.brain.qa.nqa.Admin.GetCategoryByIdRequest;
import maum.brain.qa.nqa.Admin.GetCategoryByIdResponse;
import maum.brain.qa.nqa.Admin.GetIndexedKeywordsRequest;
import maum.brain.qa.nqa.Admin.GetIndexedKeywordsResponse;
import maum.brain.qa.nqa.Admin.IndexStatus;
import maum.brain.qa.nqa.Admin.IndexingRequest;
import maum.brain.qa.nqa.Admin.LayerList;
import maum.brain.qa.nqa.Admin.NQaAdminCategory;
import maum.brain.qa.nqa.Admin.NQaAdminChannel;
import maum.brain.qa.nqa.Admin.RemoveAnswerRequest;
import maum.brain.qa.nqa.Admin.RemoveAnswerResponse;
import maum.brain.qa.nqa.Admin.RemoveCategoryRequest;
import maum.brain.qa.nqa.Admin.RemoveCategoryResponse;
import maum.brain.qa.nqa.Admin.RemoveChannelRequest;
import maum.brain.qa.nqa.Admin.RemoveChannelResponse;
import maum.brain.qa.nqa.Admin.UploadStreamAnswerListRequest;
import maum.brain.qa.nqa.Admin.UploadAnswerListRequest;
import maum.brain.qa.nqa.Admin.UploadAnswerListResponse;
import maum.brain.qa.nqa.Admin.getQAsetByCategoryRequest;
import maum.brain.qa.nqa.NQaAdminServiceGrpc;
import maum.brain.qa.nqa.NQaAdminServiceGrpc.NQaAdminServiceBlockingStub;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NQAGrpcInterfaceManager {

  static final org.slf4j.Logger logger = LoggerFactory.getLogger(NQAGrpcInterfaceManager.class);

  private String host;
  private int port;

  public NQAGrpcInterfaceManager(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public IndexStatus getIndexingStatus(Empty request) {
    logger.debug("===== NQAGrpcInterfaceManager getIndexingStatus");
    final ManagedChannel channel = GrpcUtils.getChannel(this.host, this.port);
    try {
      NQaAdminServiceBlockingStub stub = NQaAdminServiceGrpc.newBlockingStub(channel);
      IndexStatus result = stub.getIndexingStatus(request);
      if (channel != null) {
        GrpcUtils.closeChannel(channel);
      }
      return result;
    } catch (StatusRuntimeException e) {
      logger.error("===== NQAGrpcInterfaceManager getIndexingStatus :: RPC failed: {}",
          e.getStatus());
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e1) {
        // TODO Auto-generated catch block
        logger.error("getIndexingStatus e1 : " , e1);
      } finally {
        if (channel != null) {
          GrpcUtils.closeChannel(channel);
        }
      }
      return null;
    }
  }

  public GetIndexedKeywordsResponse getIndexedKeywords(GetIndexedKeywordsRequest request) {
    logger
        .debug("===== NQAGrpcInterfaceManager getIndexedKeywords :: GetIndexedKeywordsResponse {}",
            request);
    final ManagedChannel channel = GrpcUtils.getChannel(this.host, this.port);
    try {
      NQaAdminServiceBlockingStub stub = NQaAdminServiceGrpc.newBlockingStub(channel);
      GetIndexedKeywordsResponse result = stub.getIndexedKeywords(request);
      if (channel != null) {
        GrpcUtils.closeChannel(channel);
      }
      return result;
    } catch (StatusRuntimeException e) {
      logger.error("===== NQAGrpcInterfaceManager getIndexedKeywords :: RPC failed: {}",
          e.getStatus());
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e1) {
        // TODO Auto-generated catch block
        logger.error("getIndexedKeywords e1 : " , e1);
      } finally {
        if (channel != null) {
          GrpcUtils.closeChannel(channel);
        }
      }
      return null;
    }
  }

  public IndexStatus fullIndexing(IndexingRequest request) {
    logger.debug("===== NQAGrpcInterfaceManager fullIndexing :: IndexingRequest {}", request);
    final ManagedChannel channel = GrpcUtils.getChannel(this.host, this.port);
    try {
      NQaAdminServiceBlockingStub stub = NQaAdminServiceGrpc.newBlockingStub(channel);
      IndexStatus result = stub.indexing(request);
      if (channel != null) {
        GrpcUtils.closeChannel(channel);
      }
      return result;
    } catch (StatusRuntimeException e) {
      logger.error("===== NQAGrpcInterfaceManager fullIndexing :: RPC failed: {}", e.getStatus());
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e1) {
        // TODO Auto-generated catch block
        logger.error("fullIndexing e1 : " , e1);
      } finally {
        if (channel != null) {
          GrpcUtils.closeChannel(channel);
        }
      }
      return null;
    }
  }

  public IndexStatus additionalIndexing(IndexingRequest request) {
    logger.debug("===== NQAGrpcInterfaceManager additionalIndexing :: IndexingRequest {}", request);
    final ManagedChannel channel = GrpcUtils.getChannel(this.host, this.port);
    try {
      NQaAdminServiceBlockingStub stub = NQaAdminServiceGrpc.newBlockingStub(channel);
      IndexStatus result = stub.indexing(request);
      if (channel != null) {
        GrpcUtils.closeChannel(channel);
      }
      return result;
    } catch (StatusRuntimeException e) {
      logger.error("===== NQAGrpcInterfaceManager additionalIndexing :: RPC failed: {}",
          e.getStatus());
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e1) {
        // TODO Auto-generated catch block
        logger.error("additionalIndexing e1 : " , e1);
      } finally {
        if (channel != null) {
          GrpcUtils.closeChannel(channel);
        }
      }
      return null;
    }
  }

  public IndexStatus abortIndexing(IndexingRequest request) {
    logger.debug("===== NQAGrpcInterfaceManager abortIndexing :: IndexingRequest {}", request);
    final ManagedChannel channel = GrpcUtils.getChannel(this.host, this.port);
    try {
      NQaAdminServiceBlockingStub stub = NQaAdminServiceGrpc.newBlockingStub(channel);
      IndexStatus result = stub.abortIndexing(request);
      if (channel != null) {
        GrpcUtils.closeChannel(channel);
      }
      return result;
    } catch (StatusRuntimeException e) {
      logger.error("===== NQAGrpcInterfaceManager abortIndexing :: RPC failed: {}", e.getStatus());
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e1) {
        // TODO Auto-generated catch block
        logger.error("abortIndexing e1 : " , e1);
      } finally {
        if (channel != null) {
          GrpcUtils.closeChannel(channel);
        }
      }
      return null;
    }
  }

  public AnswerList getAnswerList(GetAnswerListByCategoryRequest request) {
    logger.debug("===== NQAGrpcInterfaceManager getAnswerList :: GetAnswerListByCategoryRequest {}",
        request);
    final ManagedChannel channel = GrpcUtils.getChannel(this.host, this.port);
    try {
      NQaAdminServiceBlockingStub stub = NQaAdminServiceGrpc.newBlockingStub(channel);
      AnswerList result = stub.getAnswerListByCategory(request);
      if (channel != null) {
        GrpcUtils.closeChannel(channel);
      }
      return result;
    } catch (StatusRuntimeException e) {
      logger.error("===== NQAGrpcInterfaceManager getAnswerList :: RPC failed: {}", e.getStatus());
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e1) {
        // TODO Auto-generated catch block
        logger.error("getAnswerList e1 : " , e1);
      } finally {
        if (channel != null) {
          GrpcUtils.closeChannel(channel);
        }
      }
      return null;
    }
  }

  public AnswerList getQAsetByCategory(getQAsetByCategoryRequest request) {
    logger.debug("===== NQAGrpcInterfaceManager getQAsetByCategory :: getQAsetByCategoryRequest {}",
        request);
    final ManagedChannel channel = GrpcUtils.getChannel(this.host, this.port);
    try {
      NQaAdminServiceBlockingStub stub = NQaAdminServiceGrpc.newBlockingStub(channel);
      AnswerList result = stub.getQAsetByCategory(request);
      if (channel != null) {
        GrpcUtils.closeChannel(channel);
      }
      return result;
    } catch (StatusRuntimeException e) {
      logger.error("===== NQAGrpcInterfaceManager getQAsetByCategory :: RPC failed: {}", e.getStatus());
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e1) {
        // TODO Auto-generated catch block
        logger.error("getQAsetByCategory e1 : " , e1);
      } finally {
        if (channel != null) {
          GrpcUtils.closeChannel(channel);
        }
      }
      return null;
    }
  }

  public ChannelList getChannelList(com.google.protobuf.Empty request) {
    logger.debug("===== NQAGrpcInterfaceManager getChannelList");
    final ManagedChannel channel = GrpcUtils.getChannel(this.host, this.port);
    try {
      NQaAdminServiceBlockingStub stub = NQaAdminServiceGrpc.newBlockingStub(channel);
      ChannelList result = stub.getChannelList(request);
      if (channel != null) {
        GrpcUtils.closeChannel(channel);
      }
      return result;
    } catch (StatusRuntimeException e) {
      logger.error("===== NQAGrpcInterfaceManager getChannelList :: RPC failed: {}", e.getStatus());
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e1) {
        // TODO Auto-generated catch block
        logger.error("getChannelList e1 : " , e1);
      } finally {
        if (channel != null) {
          GrpcUtils.closeChannel(channel);
        }
      }
      return null;
    }
  }

  public NQaAdminChannel getChannelById(int channelId) {
    logger.debug("===== NQAGrpcInterfaceManager getChannelById");
    final ManagedChannel channel = GrpcUtils.getChannel(this.host, this.port);
    try {
      NQaAdminServiceBlockingStub stub = NQaAdminServiceGrpc.newBlockingStub(channel);
      NQaAdminChannel.Builder getChannelByIdRequest = NQaAdminChannel.newBuilder();
      getChannelByIdRequest.setId(channelId);
      NQaAdminChannel result = stub.getChannelById(getChannelByIdRequest.build());
      if (channel != null) {
        GrpcUtils.closeChannel(channel);
      }
      return result;
    } catch (StatusRuntimeException e) {
      logger.error("===== NQAGrpcInterfaceManager getChannelById :: RPC failed: {}", e.getStatus());
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e1) {
        logger.error("getChannelById e1 : " , e1);
      } finally {
        if (channel != null) {
          GrpcUtils.closeChannel(channel);
        }
      }
      return null;
    }
  }

  public CategoryList getCategoryListByChannelId(int channelId) {
    logger.debug("===== NQAGrpcInterfaceManager getCategoryListByChannelId");
    final ManagedChannel channel = GrpcUtils.getChannel(this.host, this.port);
    try {
      NQaAdminServiceBlockingStub stub = NQaAdminServiceGrpc.newBlockingStub(channel);
      NQaAdminChannel.Builder channelRequest = NQaAdminChannel.newBuilder();
      channelRequest.setId(channelId);
      CategoryList result = stub.getCategoryListByChannelId(channelRequest.build());
      if (channel != null) {
        GrpcUtils.closeChannel(channel);
      }
      return result;
    } catch (StatusRuntimeException e) {
      logger.error("===== NQAGrpcInterfaceManager getCategoryListByWorkspace :: RPC failed: {}",
          e.getStatus());
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e1) {
        // TODO Auto-generated catch block
        logger.error("getCategoryListByChannelId e1 : " , e1);
      } finally {
        if (channel != null) {
          GrpcUtils.closeChannel(channel);
        }
      }
      return null;
    }
  }

  public CategoryList getCategoryListByName(Category category) {
    logger.debug("===== NQAGrpcInterfaceManager getCategoryListByName");
    final ManagedChannel channel = GrpcUtils.getChannel(this.host, this.port);
    try {
      NQaAdminServiceBlockingStub stub = NQaAdminServiceGrpc.newBlockingStub(channel);
      CategoryList result = stub.getCategoryListByName(category.makeProto());
      if (channel != null) {
        GrpcUtils.closeChannel(channel);
      }
      return result;
    } catch (StatusRuntimeException e) {
      logger.error("===== NQAGrpcInterfaceManager getCategoryListByName :: RPC failed: {}",
          e.getStatus());
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e1) {
        // TODO Auto-generated catch block
        logger.error("getCategoryListByName e1 : " , e1);
      } finally {
        if (channel != null) {
          GrpcUtils.closeChannel(channel);
        }
      }
      return null;
    }
  }

  public LayerList getLayerListByCategory(NQaAdminCategory request) {
    logger.debug("===== NQAGrpcInterfaceManager getLayerList");
    final ManagedChannel channel = GrpcUtils.getChannel(this.host, this.port);
    try {
      NQaAdminServiceBlockingStub stub = NQaAdminServiceGrpc.newBlockingStub(channel);
      LayerList result = stub.getLayerListByCategory(request);
      if (channel != null) {
        GrpcUtils.closeChannel(channel);
      }
      return result;
    } catch (StatusRuntimeException e) {
      logger.error("===== NQAGrpcInterfaceManager getLayerList :: RPC failed: {}", e.getStatus());
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e1) {
        // TODO Auto-generated catch block
        logger.error("getLayerListByCategory e1 : " , e1);
      } finally {
        if (channel != null) {
          GrpcUtils.closeChannel(channel);
        }
      }
      return null;
    }
  }

  public AnswerList getAnswerById(GetAnswerByIdRequest request) {
    logger.debug("===== NQAGrpcInterfaceManager getAnswerById :: GetAnswerByIdRequest {}", request);
    final ManagedChannel channel = GrpcUtils.getChannel(this.host, this.port);
    try {
      NQaAdminServiceBlockingStub stub = NQaAdminServiceGrpc.newBlockingStub(channel);
      AnswerList result = stub.getAnswerById(request);
      if (channel != null) {
        GrpcUtils.closeChannel(channel);
      }
      return result;
    } catch (StatusRuntimeException e) {
      logger.error("===== NQAGrpcInterfaceManager getAnswerById :: RPC failed: {}", e.getStatus());
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e1) {
        // TODO Auto-generated catch block
        logger.error("getAnswerById e1 : " , e1);
      } finally {
        if (channel != null) {
          GrpcUtils.closeChannel(channel);
        }
      }
      return null;
    }
  }

  public GetCategoryByIdResponse getCategoryById(GetCategoryByIdRequest request) {
    logger.debug("===== NQAGrpcInterfaceManager getCategoryById :: GetCategoryByIdRequest {}",
        request);
    final ManagedChannel channel = GrpcUtils.getChannel(this.host, this.port);
    try {
      NQaAdminServiceBlockingStub stub = NQaAdminServiceGrpc.newBlockingStub(channel);
      GetCategoryByIdResponse result = stub.getCategoryById(request);
      if (channel != null) {
        GrpcUtils.closeChannel(channel);
      }
      return result;
    } catch (StatusRuntimeException e) {
      logger
          .error("===== NQAGrpcInterfaceManager getCategoryById :: RPC failed: {}", e.getStatus());
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e1) {
        // TODO Auto-generated catch block
        logger.error("getCategoryById e1 : " , e1);
      } finally {
        if (channel != null) {
          GrpcUtils.closeChannel(channel);
        }
      }
      return null;
    }
  }

  public AddAnswerResponse addAnswer(AddAnswerRequest request) {
    logger.debug("===== NQAGrpcInterfaceManager addAnswer :: AddAnswerRequest {}", request);
    final ManagedChannel channel = GrpcUtils.getChannel(this.host, this.port);
    try {
      NQaAdminServiceBlockingStub stub = NQaAdminServiceGrpc.newBlockingStub(channel);
      AddAnswerResponse result = stub.addAnswer(request);
      if (channel != null) {
        GrpcUtils.closeChannel(channel);
      }
      return result;
    } catch (StatusRuntimeException e) {
      logger.error("===== NQAGrpcInterfaceManager addAnswer :: RPC failed: {}", e.getStatus());
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e1) {
        // TODO Auto-generated catch block
        logger.error("addAnswer e1 : " , e1);
      } finally {
        if (channel != null) {
          GrpcUtils.closeChannel(channel);
        }
      }
      return null;
    }
  }

  public NQaAdminChannel addChannel(NQaAdminChannel request) {
    logger.debug("===== NQAGrpcInterfaceManager addChannel :: AddChannelRequest {}", request);
    final ManagedChannel channel = GrpcUtils.getChannel(this.host, this.port);
    try {
      NQaAdminServiceBlockingStub stub = NQaAdminServiceGrpc.newBlockingStub(channel);
      NQaAdminChannel result = stub.addChannel(request);
      if (channel != null) {
        GrpcUtils.closeChannel(channel);
      }
      return result;
    } catch (StatusRuntimeException e) {
      logger.error("===== NQAGrpcInterfaceManager addChannel :: RPC failed: {}", e.getStatus());
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e1) {
        logger.error("addChannel e1 : " , e1);
      } finally {
        if (channel != null) {
          GrpcUtils.closeChannel(channel);
        }
      }
      return null;
    }
  }

  public AddCategoryResponse addCategory(AddCategoryRequest request) {
    logger.debug("===== NQAGrpcInterfaceManager addCategory :: AddCategoryRequest {}", request);
    final ManagedChannel channel = GrpcUtils.getChannel(this.host, this.port);
    try {
      NQaAdminServiceBlockingStub stub = NQaAdminServiceGrpc.newBlockingStub(channel);
      AddCategoryResponse result = stub.addCategory(request);
      if (channel != null) {
        GrpcUtils.closeChannel(channel);
      }
      return result;
    } catch (StatusRuntimeException e) {
      logger.error("===== NQAGrpcInterfaceManager addCategory :: RPC failed: {}", e.getStatus());
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e1) {
        // TODO Auto-generated catch block
        logger.error("addCategory e1 : " , e1);
      } finally {
        if (channel != null) {
          GrpcUtils.closeChannel(channel);
        }
      }
      return null;
    }
  }


  public AnswerList editAnswer(EditAnswerRequest request) {
    logger.debug("===== NQAGrpcInterfaceManager editAnswer");
    final ManagedChannel channel = GrpcUtils.getChannel(this.host, this.port);
    try {
      NQaAdminServiceBlockingStub stub = NQaAdminServiceGrpc.newBlockingStub(channel);
      AnswerList result = stub.editAnswer(request);
      if (channel != null) {
        GrpcUtils.closeChannel(channel);
      }
      return result;
    } catch (StatusRuntimeException e) {
      logger.error("===== NQAGrpcInterfaceManager editAnswer :: RPC failed: {}", e.getStatus());
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e1) {
        // TODO Auto-generated catch block
        logger.error("editAnswer e1 : " , e1);
      } finally {
        if (channel != null) {
          GrpcUtils.closeChannel(channel);
        }
      }
      return null;
    }
  }

  public EditCategoryResponse editCategory(EditCategoryRequest request) {
    logger.debug("===== NQAGrpcInterfaceManager editCategory");
    final ManagedChannel channel = GrpcUtils.getChannel(this.host, this.port);
    try {
      NQaAdminServiceBlockingStub stub = NQaAdminServiceGrpc.newBlockingStub(channel);
      EditCategoryResponse result = stub.editCategory(request);
      if (channel != null) {
        GrpcUtils.closeChannel(channel);
      }
      return result;
    } catch (StatusRuntimeException e) {
      logger.error("===== NQAGrpcInterfaceManager editCategory :: RPC failed: {}", e.getStatus());
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e1) {
        // TODO Auto-generated catch block
        logger.error("editCategory e1 : " , e1);
      } finally {
        if (channel != null) {
          GrpcUtils.closeChannel(channel);
        }
      }
      return null;
    }
  }

  public NQaAdminChannel editChannel(NQaAdminChannel request) {
    logger.debug("===== NQAGrpcInterfaceManager editChannel");
    final ManagedChannel channel = GrpcUtils.getChannel(this.host, this.port);
    try {
      NQaAdminServiceBlockingStub stub = NQaAdminServiceGrpc.newBlockingStub(channel);
      NQaAdminChannel result = stub.editChannel(request);
      if (channel != null) {
        GrpcUtils.closeChannel(channel);
      }
      return result;
    } catch (StatusRuntimeException e) {
      logger.error("===== NQAGrpcInterfaceManager editChannel :: RPC failed: {}", e.getStatus());
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e1) {
        logger.error("editChannel e1 : " , e1);
      } finally {
        if (channel != null) {
          GrpcUtils.closeChannel(channel);
        }
      }
      return null;
    }
  }

  public RemoveAnswerResponse removeAnswer(RemoveAnswerRequest request) {
    logger.debug("===== NQAGrpcInterfaceManager removeAnswer");
    final ManagedChannel channel = GrpcUtils.getChannel(this.host, this.port);
    try {
      NQaAdminServiceBlockingStub stub = NQaAdminServiceGrpc.newBlockingStub(channel);
      RemoveAnswerResponse result = stub.removeAnswer(request);
      if (channel != null) {
        GrpcUtils.closeChannel(channel);
      }
      return result;
    } catch (StatusRuntimeException e) {
      logger.error("===== NQAGrpcInterfaceManager removeAnswer :: RPC failed: {}", e.getStatus());
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e1) {
        // TODO Auto-generated catch block
        logger.error("removeAnswer e1 : " , e1);
      } finally {
        if (channel != null) {
          GrpcUtils.closeChannel(channel);
        }
      }
      return null;
    }
  }

  public RemoveCategoryResponse removeCategory(RemoveCategoryRequest request) {
    logger.debug("===== NQAGrpcInterfaceManager removeCategory");
    final ManagedChannel channel = GrpcUtils.getChannel(this.host, this.port);
    try {
      NQaAdminServiceBlockingStub stub = NQaAdminServiceGrpc.newBlockingStub(channel);
      RemoveCategoryResponse result = stub.removeCategory(request);
      if (channel != null) {
        GrpcUtils.closeChannel(channel);
      }
      return result;
    } catch (StatusRuntimeException e) {
      logger.error("===== NQAGrpcInterfaceManager removeCategory:: RPC failed: {}", e.getStatus());
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e1) {
        // TODO Auto-generated catch block
        logger.error("removeCategory e1 : " , e1);
      } finally {
        if (channel != null) {
          GrpcUtils.closeChannel(channel);
        }
      }
      return null;
    }
  }

  public RemoveChannelResponse removeChannel(int channelId) {
    logger.debug("===== NQAGrpcInterfaceManager removeChannel");
    final ManagedChannel channel = GrpcUtils.getChannel(this.host, this.port);
    try {
      NQaAdminServiceBlockingStub stub = NQaAdminServiceGrpc.newBlockingStub(channel);
      RemoveChannelRequest.Builder request = RemoveChannelRequest.newBuilder();
      request.setId(channelId);
      RemoveChannelResponse result = stub.removeChannel(request.build());
      if (channel != null) {
        GrpcUtils.closeChannel(channel);
      }
      return result;
    } catch (StatusRuntimeException e) {
      logger.error("===== NQAGrpcInterfaceManager removeChannel:: RPC failed: {}", e.getStatus());
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e1) {
        // TODO Auto-generated catch block
        logger.error("removeChannel e1 : " , e1);
      } finally {
        if (channel != null) {
          GrpcUtils.closeChannel(channel);
        }
      }
      return null;
    }
  }

  public UploadAnswerListResponse uploadAnswerList(UploadAnswerListRequest request) {
    logger.debug("===== NQAGrpcInterfaceManager uploadAnswerList");
    final ManagedChannel channel = GrpcUtils.getChannel(this.host, this.port);
    try {
      NQaAdminServiceBlockingStub stub = NQaAdminServiceGrpc.newBlockingStub(channel);
      UploadAnswerListResponse result = stub.uploadAnswerList(request);
      if (channel != null) {
        GrpcUtils.closeChannel(channel);
      }
      return result;
    } catch (StatusRuntimeException e) {
      logger
          .error("===== NQAGrpcInterfaceManager uploadAnswerList:: RPC failed: {}", e.getStatus());
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e1) {
        logger.error("===== Thread Sleep failed: {}", e1.getMessage());
      } finally {
        if (channel != null) {
          GrpcUtils.closeChannel(channel);
        }
      }
      return null;
    }
  }

  public UploadAnswerListResponse uploadStreamAnswerList(List<UploadStreamAnswerListRequest> requestList, String host) {
    logger.debug("===== NQAGrpcInterfaceManager uploadStreamAnswerList");
    final ManagedChannel channel = GrpcUtils.getChannel(this.host, this.port);
    Map<String, Object> map = new HashMap<>();
    map.put("host", host);
    try {
      NQaAdminServiceGrpc.NQaAdminServiceStub stub = NQaAdminServiceGrpc.newStub(channel);
      UploadAnswerListResponse.Builder response = UploadAnswerListResponse.newBuilder().setSuccessCount(0);
      StreamObserver<UploadAnswerListResponse> streamResponce = new StreamObserver<UploadAnswerListResponse>() {
        @Override
        public void onNext(UploadAnswerListResponse value) {
          logger.debug("### NQA UploadStreamAnswerList onNext !!!");
          response.setSuccessCount(value.getSuccessCount());
        }

        @Override
        public void onError(Throwable t) {
          logger.debug("### NQA UploadStreamAnswerList onError !!!");
          t.printStackTrace();
          map.put("nqaUploadStatus", "fail");
          NqaUploadStatusList.nqaUploadStatusUpdate(map);
          GrpcUtils.closeChannel(channel);
        }

        @Override
        public void onCompleted() {
          logger.debug("### NQA UploadStreamAnswerList onCompleted !!!");
          map.put("nqaUploadStatus", "success");
          NqaUploadStatusList.nqaUploadStatusUpdate(map);
          GrpcUtils.closeChannel(channel);
        }
      };

      StreamObserver<UploadStreamAnswerListRequest> streamRequest = stub.uploadStreamAnswerList(streamResponce);

      for (UploadStreamAnswerListRequest tmpRequest : requestList){
        streamRequest.onNext(tmpRequest);
      }
      streamRequest.onCompleted();

      return response.build();
    } catch (StatusRuntimeException e) {
      map.put("nqaUploadStatus", "fail");
      NqaUploadStatusList.nqaUploadStatusUpdate(map);
      logger.error("===== NQAGrpcInterfaceManager uploadStreamAnswerList:: RPC failed: {}", e.getMessage());
      e.printStackTrace();
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e1) {
        logger.error("===== Thread Sleep failed: {}", e1.getMessage());
      } finally {
        if (channel != null) {
          GrpcUtils.closeChannel(channel);
        }
      }

      return null;
    }
  }

}
