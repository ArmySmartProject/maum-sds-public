#!/usr/bin/env bash

if [ "0" == "$#" ]; then
    export SDS_PROFILE="dev"
else
    export SDS_PROFILE="$1"
fi

count=`ps -ef | grep brain-sds-frontend | grep ${SDS_PROFILE} | grep -v grep | awk '{print $2}'| wc -l`
if [ $count -gt 0 ]; then
 pid=`ps -ef | grep brain-sds-frontend | grep ${SDS_PROFILE}| grep -v grep | awk '{print $2}'`
 echo "shutting down frontent"
 kill -9 $pid
fi
count=`ps -ef | grep brain-sds-adapter | grep ${SDS_PROFILE}| grep -v grep | awk '{print $2}'| wc -l`
if [ $count -gt 0 ]; then
 pid=`ps -ef | grep brain-sds-adapter | grep ${SDS_PROFILE}| grep -v grep | awk '{print $2}'`
 echo "shutting down adapter"
 kill -9 $pid
fi
count=`ps -ef | grep brain-sds-collector | grep ${SDS_PROFILE}| grep -v grep | awk '{print $2}'| wc -l`
if [ $count -gt 0 ]; then
 pid=`ps -ef | grep brain-sds-collector | grep ${SDS_PROFILE}| grep -v grep | awk '{print $2}'`
 echo "shutting down collector"
 kill -9 $pid
fi
count=`ps -ef | grep brain-sds-maker | grep ${SDS_PROFILE}| grep -v grep | awk '{print $2}'| wc -l`
if [ $count -gt 0 ]; then
 pid=`ps -ef | grep brain-sds-maker | grep ${SDS_PROFILE}| grep -v grep | awk '{print $2}'`
 echo "shutting down maker"
 kill -9 $pid
fi
count=`ps -ef | grep brain-sds-log | grep ${SDS_PROFILE}| grep -v grep | awk '{print $2}'| wc -l`
if [ $count -gt 0 ]; then
 pid=`ps -ef | grep brain-sds-log | grep ${SDS_PROFILE}| grep -v grep | awk '{print $2}'`
 echo "shutting down logger"
 kill -9 $pid
fi
count=`ps -ef | grep brain-sds-memory | grep ${SDS_PROFILE}| grep -v grep | awk '{print $2}'| wc -l`
if [ $count -gt 0 ]; then
 pid=`ps -ef | grep brain-sds-memory | grep ${SDS_PROFILE}| grep -v grep | awk '{print $2}'`
 echo "shutting down memory"
 kill -9 $pid
fi
count=`ps -ef | grep brain-sds-cache | grep ${SDS_PROFILE}| grep -v grep | awk '{print $2}'| wc -l`
if [ $count -gt 0 ]; then
 pid=`ps -ef | grep brain-sds-cache | grep ${SDS_PROFILE}| grep -v grep | awk '{print $2}'`
 echo "shutting down cache server"
 kill -9 $pid
fi

echo "Kill maum SDS with profile ${SDS_PROFILE}"