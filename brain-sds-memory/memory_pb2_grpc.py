# Generated by the gRPC Python protocol compiler plugin. DO NOT EDIT!
import grpc

import memory_pb2 as memory__pb2


class SdsMemoryStub(object):
  # missing associated documentation comment in .proto file
  pass

  def __init__(self, channel):
    """Constructor.

    Args:
      channel: A grpc.Channel.
    """
    self.UserInit = channel.unary_unary(
        '/SdsMemory/UserInit',
        request_serializer=memory__pb2.MemUserInfo.SerializeToString,
        response_deserializer=memory__pb2.MemMessage.FromString,
        )
    self.UserEnd = channel.unary_unary(
        '/SdsMemory/UserEnd',
        request_serializer=memory__pb2.MemUserInfo.SerializeToString,
        response_deserializer=memory__pb2.MemMessage.FromString,
        )
    self.PassTurn = channel.unary_unary(
        '/SdsMemory/PassTurn',
        request_serializer=memory__pb2.MemUserInfo.SerializeToString,
        response_deserializer=memory__pb2.MemMessage.FromString,
        )
    self.AddMemory = channel.unary_unary(
        '/SdsMemory/AddMemory',
        request_serializer=memory__pb2.MemTurn.SerializeToString,
        response_deserializer=memory__pb2.MemMessage.FromString,
        )
    self.GetLastMemory = channel.unary_unary(
        '/SdsMemory/GetLastMemory',
        request_serializer=memory__pb2.MemUserInfo.SerializeToString,
        response_deserializer=memory__pb2.MemTurn.FromString,
        )
    self.GetAllMemory = channel.unary_stream(
        '/SdsMemory/GetAllMemory',
        request_serializer=memory__pb2.MemUserInfo.SerializeToString,
        response_deserializer=memory__pb2.MemTurn.FromString,
        )
    self.GetMemoryByIntent = channel.unary_stream(
        '/SdsMemory/GetMemoryByIntent',
        request_serializer=memory__pb2.MemIntent.SerializeToString,
        response_deserializer=memory__pb2.MemTurn.FromString,
        )
    self.GetMemoryByEntity = channel.unary_stream(
        '/SdsMemory/GetMemoryByEntity',
        request_serializer=memory__pb2.MemEntity.SerializeToString,
        response_deserializer=memory__pb2.MemTurn.FromString,
        )
    self.ClearMemory = channel.unary_unary(
        '/SdsMemory/ClearMemory',
        request_serializer=memory__pb2.MemUserInfo.SerializeToString,
        response_deserializer=memory__pb2.MemMessage.FromString,
        )


class SdsMemoryServicer(object):
  # missing associated documentation comment in .proto file
  pass

  def UserInit(self, request, context):
    # missing associated documentation comment in .proto file
    pass
    context.set_code(grpc.StatusCode.UNIMPLEMENTED)
    context.set_details('Method not implemented!')
    raise NotImplementedError('Method not implemented!')

  def UserEnd(self, request, context):
    # missing associated documentation comment in .proto file
    pass
    context.set_code(grpc.StatusCode.UNIMPLEMENTED)
    context.set_details('Method not implemented!')
    raise NotImplementedError('Method not implemented!')

  def PassTurn(self, request, context):
    # missing associated documentation comment in .proto file
    pass
    context.set_code(grpc.StatusCode.UNIMPLEMENTED)
    context.set_details('Method not implemented!')
    raise NotImplementedError('Method not implemented!')

  def AddMemory(self, request, context):
    # missing associated documentation comment in .proto file
    pass
    context.set_code(grpc.StatusCode.UNIMPLEMENTED)
    context.set_details('Method not implemented!')
    raise NotImplementedError('Method not implemented!')

  def GetLastMemory(self, request, context):
    # missing associated documentation comment in .proto file
    pass
    context.set_code(grpc.StatusCode.UNIMPLEMENTED)
    context.set_details('Method not implemented!')
    raise NotImplementedError('Method not implemented!')

  def GetAllMemory(self, request, context):
    # missing associated documentation comment in .proto file
    pass
    context.set_code(grpc.StatusCode.UNIMPLEMENTED)
    context.set_details('Method not implemented!')
    raise NotImplementedError('Method not implemented!')

  def GetMemoryByIntent(self, request, context):
    # missing associated documentation comment in .proto file
    pass
    context.set_code(grpc.StatusCode.UNIMPLEMENTED)
    context.set_details('Method not implemented!')
    raise NotImplementedError('Method not implemented!')

  def GetMemoryByEntity(self, request, context):
    # missing associated documentation comment in .proto file
    pass
    context.set_code(grpc.StatusCode.UNIMPLEMENTED)
    context.set_details('Method not implemented!')
    raise NotImplementedError('Method not implemented!')

  def ClearMemory(self, request, context):
    # missing associated documentation comment in .proto file
    pass
    context.set_code(grpc.StatusCode.UNIMPLEMENTED)
    context.set_details('Method not implemented!')
    raise NotImplementedError('Method not implemented!')


def add_SdsMemoryServicer_to_server(servicer, server):
  rpc_method_handlers = {
      'UserInit': grpc.unary_unary_rpc_method_handler(
          servicer.UserInit,
          request_deserializer=memory__pb2.MemUserInfo.FromString,
          response_serializer=memory__pb2.MemMessage.SerializeToString,
      ),
      'UserEnd': grpc.unary_unary_rpc_method_handler(
          servicer.UserEnd,
          request_deserializer=memory__pb2.MemUserInfo.FromString,
          response_serializer=memory__pb2.MemMessage.SerializeToString,
      ),
      'PassTurn': grpc.unary_unary_rpc_method_handler(
          servicer.PassTurn,
          request_deserializer=memory__pb2.MemUserInfo.FromString,
          response_serializer=memory__pb2.MemMessage.SerializeToString,
      ),
      'AddMemory': grpc.unary_unary_rpc_method_handler(
          servicer.AddMemory,
          request_deserializer=memory__pb2.MemTurn.FromString,
          response_serializer=memory__pb2.MemMessage.SerializeToString,
      ),
      'GetLastMemory': grpc.unary_unary_rpc_method_handler(
          servicer.GetLastMemory,
          request_deserializer=memory__pb2.MemUserInfo.FromString,
          response_serializer=memory__pb2.MemTurn.SerializeToString,
      ),
      'GetAllMemory': grpc.unary_stream_rpc_method_handler(
          servicer.GetAllMemory,
          request_deserializer=memory__pb2.MemUserInfo.FromString,
          response_serializer=memory__pb2.MemTurn.SerializeToString,
      ),
      'GetMemoryByIntent': grpc.unary_stream_rpc_method_handler(
          servicer.GetMemoryByIntent,
          request_deserializer=memory__pb2.MemIntent.FromString,
          response_serializer=memory__pb2.MemTurn.SerializeToString,
      ),
      'GetMemoryByEntity': grpc.unary_stream_rpc_method_handler(
          servicer.GetMemoryByEntity,
          request_deserializer=memory__pb2.MemEntity.FromString,
          response_serializer=memory__pb2.MemTurn.SerializeToString,
      ),
      'ClearMemory': grpc.unary_unary_rpc_method_handler(
          servicer.ClearMemory,
          request_deserializer=memory__pb2.MemUserInfo.FromString,
          response_serializer=memory__pb2.MemMessage.SerializeToString,
      ),
  }
  generic_handler = grpc.method_handlers_generic_handler(
      'SdsMemory', rpc_method_handlers)
  server.add_generic_rpc_handlers((generic_handler,))
