#!/usr/bin/env bash
export SDS_PROFILE="production"
export SDS_PROFILE_OFFSET="0"
export LOG_DIRECTORY="/home/minds/maum/sds/logs"
echo "sds profile ${SDS_PROFILE}, port offset ${SDS_PROFILE_OFFSET}"

java -jar \
 -Dbrain.sds.profile=${SDS_PROFILE} -Dbrain.sds.port.offset=${SDS_PROFILE_OFFSET} \
 -Dserver.port=28080 \
 ./jar/brain-sds-frontend.jar > ${LOG_DIRECTORY}/sds_frontend.log &
java -jar \
 -Dbrain.sds.profile=${SDS_PROFILE} -Dbrain.sds.port.offset=${SDS_PROFILE_OFFSET} \
 ./jar/brain-sds-adapter.jar > ${LOG_DIRECTORY}/sds_adapter.log &
java -jar \
 -Dbrain.sds.profile=${SDS_PROFILE} -Dbrain.sds.port.offset=${SDS_PROFILE_OFFSET} \
 ./jar/brain-sds-collector.jar > ${LOG_DIRECTORY}/sds_collector.log &
java -jar \
 -Dbrain.sds.profile=${SDS_PROFILE} -Dbrain.sds.port.offset=${SDS_PROFILE_OFFSET} \
 ./jar/brain-sds-maker.jar > ${LOG_DIRECTORY}/sds_maker.log &
java -jar \
 -Dbrain.sds.profile=${SDS_PROFILE} -Dbrain.sds.port.offset=${SDS_PROFILE_OFFSET} \
 ./jar/brain-sds-log-store.jar > ${LOG_DIRECTORY}/sds_logger.log &
java -jar \
 -Dbrain.sds.profile=${SDS_PROFILE} -Dbrain.sds.port.offset=${SDS_PROFILE_OFFSET} \
 ./jar/brain-sds-cache.jar > ${LOG_DIRECTORY}/sds_cache.log &

python3 ./brain-sds-memory/sds_memory.py --profile=${SDS_PROFILE} --port_offset=${SDS_PROFILE_OFFSET} &
echo "maum SDS up and running, port offset ${SDS_PROFILE_OFFSET}"
