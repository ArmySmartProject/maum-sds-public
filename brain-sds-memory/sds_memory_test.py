import os
import uuid

import grpc

from memory_pb2 import MemUserInfo, MemTurn, MemIntent
from memory_pb2_grpc import SdsMemoryStub

if __name__ == '__main__':
    with grpc.insecure_channel("127.0.0.1:7410") as channel:
        stub = SdsMemoryStub(channel)

        stub.UserInit(
            MemUserInfo(
                host="test",
                # session=str(uuid.uuid4()).split("-")[0]
                session="test2"
            )
        )

        stub.AddMemory(
            MemTurn(
                utter="test",
                intent=MemIntent(
                    intent="test.test",
                    hierachy=2,
                    entities=False
                ),
                entities=[],
                lifespan=3,
                user=MemUserInfo(
                    host="test",
                    session="test2"
                )
            )
        )
