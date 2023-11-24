import os
import sys
import glob
import argparse
import json
import time
from datetime import datetime

import grpc

from concurrent import futures

from memory_pb2 import MemMessage, MemTurn, MemUserInfo, MemIntent, MemEntity
from memory_pb2_grpc import SdsMemoryServicer, add_SdsMemoryServicer_to_server


class BrainSdsMemory(SdsMemoryServicer):
    def __init__(self, save_path="./tmp"):
        self.root_dir = save_path
        self.path_format = os.path.join(self.root_dir, "{}/{}_{}.json")
        self.success = MemMessage(status=200, message="success")
        if os.path.exists(os.path.join(self.root_dir)):
            if os.path.isdir(os.path.join(self.root_dir)):
                pass
            else:
                os.remove(os.path.join(self.root_dir))
                os.mkdir(os.path.join(self.root_dir))
        else:
            os.mkdir(os.path.join(self.root_dir))

    def UserInit(self, user_info, context):
        host, session = user_info.host, user_info.session

        if os.path.exists(os.path.join(self.root_dir, host)):
            if os.path.isdir(os.path.join(self.root_dir, host)):
                pass
            else:
                os.remove(os.path.join(self.root_dir, host))
                os.mkdir(os.path.join(self.root_dir, host))
        else:
            os.mkdir(os.path.join(self.root_dir, host))
        try:
            if not os.path.exists(self.path_format.format(host, host, session)):
                with open(self.path_format.format(host, host, session), "w", encoding="utf-8") as f:
                    json.dump([], f, ensure_ascii=False)
        except Exception:
            return MemMessage(status=500, message="critical error")

        return self.success

    def UserEnd(self, user_info, context):
        host, session = user_info.host, user_info.session

        try:
            os.remove(self.path_format.format(host, host, session))
        finally:
            return self.success

    def PassTurn(self, user_info, context):
        host, session = user_info.host, user_info.session

        self.pass_turn(self.get_memory_internal(host, session), host, session)

        return self.success

    def AddMemory(self, mem_turn, context):
        user_info = mem_turn.user
        host, session = user_info.host, user_info.session

        memory_list = self.pass_turn(self.get_memory_internal(host, session), host, session)

        entities = []
        for entity in mem_turn.entities:
            entities.append(
                {
                    "entity_name": entity.entity_name,
                    "entity_value": entity.entity_value
                }
            )

        memory = {
            "utter": mem_turn.utter,
            "intent": {
                "intent_name": mem_turn.intent.intent,
                "intent_hierachy": mem_turn.intent.hierachy,
                "req_entities": mem_turn.intent.entities
            },
            "entities": entities,
            "entitySet": dict(mem_turn.entitySet),
            "lifespan": mem_turn.lifespan
        }

        memory_list.append(memory)

        self.save_memory(memory_list, host, session)

        return self.success

    def GetLastMemory(self, user_info, context):
        host, session = user_info.host, user_info.session
        memory_list = self.get_memory_internal(host, session)
        try:
            if len(memory_list) == 0:
                return self.return_none(host, session)
            return self.make_memory(memory_list[-1], host, session)
        finally:
            self.pass_turn(memory_list, host, session)

    def GetAllMemory(self, user_info, context):
        host, session = user_info.host, user_info.session
        memory_list = self.get_memory_internal(host, session)
        for memory in memory_list:
            yield self.make_memory(memory, host, session)

    def ClearMemory(self, user_info, context):
        host, session = user_info.host, user_info.session
        os.remove(self.path_format.format(host, host, session))
        return self.success

    def get_memory_internal(self, host, session):
        if not os.path.exists(self.path_format.format(host, host, session)):
            return []

        try:
            with open(self.path_format.format(host, host, session), encoding="utf-8") as f:
                return json.load(f)
        except json.decoder.JSONDecodeError:
            os.remove(self.path_format.format(host, host, session))
            return []

    def pass_turn(self, mem_list, host, session):
        # for memory in mem_list:
        #     memory["lifespan"] -= 1
        #     if memory["lifespan"] < 1:
        #         mem_list.remove(memory)
        with open(self.path_format.format(host, host, session), "w", encoding="utf-8") as f:
            json.dump(mem_list, f, ensure_ascii=False)
        return mem_list

    def save_memory(self, mem_list, host, session):
        with open(self.path_format.format(host, host, session), "w", encoding="utf-8") as f:
            json.dump(mem_list, f, ensure_ascii=False)

    @staticmethod
    def make_memory(memory_raw: dict, host, session):
        entities = []
        for entity in memory_raw["entities"]:
            entities.append(
                MemEntity(
                    entity_name=entity["entity_name"],
                    entity_value=entity["entity_value"]
                )
            )
        return MemTurn(
            utter=memory_raw["utter"],
            intent=MemIntent(
                intent=memory_raw["intent"]["intent_name"],
                hierachy=memory_raw["intent"]["intent_hierachy"],
                entities=memory_raw["intent"]["req_entities"]
            ),
            entities=entities,
            lifespan=memory_raw["lifespan"],
            user=MemUserInfo(
                host=host,
                session=session
            )
        )

    @staticmethod
    def return_none(host, session):
        return MemTurn(
            utter=None,
            intent=None,
            entities=None,
            lifespan=None,
            user=MemUserInfo(
                host=host,
                session=session
            )
        )


def get_filenames(dir_path, prefixes='', extensions='', exit_=False):
    filenames = []
    if os.path.isfile(dir_path):
        filenames.append(dir_path)
    for path, _, files in os.walk(dir_path):
        for name in files:
            if not name.startswith(".DS"):
                if name.startswith(tuple(prefixes)):
                    filenames.append(os.path.join(path, name))
                if name.endswith(tuple(extensions)):
                    filenames.append(os.path.join(path, name))
    if (not filenames) and (not extensions) and (not prefixes):
        filenames = glob.glob(dir_path + "*")

    return filenames


if __name__ == '__main__':
    parser = argparse.ArgumentParser(
        description="Sds Memory Engine"
    )
    parser.add_argument("-r", "--root_path", default="./memory_storage", type=str, help="root directory")
    parser.add_argument("-p", "--port", type=int, default=7410)
    parser.add_argument("-P", "--profile", type=str)
    parser.add_argument("-o", "--port_offset", type=int, default=0)

    args = parser.parse_args()

    sds_memory_server = BrainSdsMemory(args.root_path)
    grpc_server = grpc.server(futures.ThreadPoolExecutor(max_workers=4),)

    sds_memory_servicer = add_SdsMemoryServicer_to_server(sds_memory_server, grpc_server)
    grpc_server.add_insecure_port("[::]:{}".format(args.port + args.port_offset))
    grpc_server.start()

    print("brain sds memory engine start at 0.0.0.0:{}, profile: {}".format(
        args.port + args.port_offset, args.profile
    ))

    try:
        while True:
            time.sleep(30)
            now = datetime.now()
            memory_files = get_filenames(args.root_path, extensions=".json")
            for memory_file in memory_files:
                if (now - datetime.fromtimestamp(os.path.getmtime(memory_file))).seconds > 300:
                    os.remove(memory_file)
    except KeyboardInterrupt:
        grpc_server.stop(0)
