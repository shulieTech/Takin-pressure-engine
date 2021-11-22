#!/bin/bash

#
# Copyright 2021 Shulie Technology, Co.Ltd
# Email: shulie@shulie.io
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# See the License for the specific language governing permissions and
# limitations under the License.
#

sleep 3

BEGIN_TIME=`date "+%Y-%m-%d %H:%M:%S"`

source /etc/profile

CURRENT_DIR=$(cd "$(dirname "$0")"; pwd)
BASE_DIR=$(cd "$(dirname "$0")/.."; pwd)
CURRENT_USER=`whoami`

CONFIGURATIONS=""
ENGINE_TYPE=""
START_MODE="double" # double single

POD_NUM="" #TODO 从环境变量中取POD序号
POD_NUM=${POD_NUMBER}

DEBUG=0
FOREGROUND=0

echo ""
echo ""
echo "-------------------------------------------"
echo "${BEGIN_TIME}"
echo "start params: $*"

while getopts ':t:c:d:f:m:' OPTION;do
    case ${OPTION} in
        t)
            ENGINE_TYPE=${OPTARG}
            ;;
        c)
            CONFIGURATIONS=${OPTARG}
            ;;
        d)
            DEBUG=1
            ;;
        f)
            FOREGROUND=1
            ;;
        m)
            START_MODE=${OPTARG}
            ;;
        ?)
            echo -n ""
            ;;
    esac
done

if [[ ${CONFIGURATIONS} = "" ]]; then
    echo "-c is null"
    exit -1
fi

if [[ ${ENGINE_TYPE} = "" ]]; then
    echo "-t is null"
    exit -1
fi

# 目录检查
ENGINE_LOG_DIR="${BASE_DIR}/logs"
LOG_DIR="/home/opt/flpt/pressure-task/logs"
RES_DIR="/home/opt/flpt/pressure-task/resources"
if [[ -e ${ENGINE_LOG_DIR} ]]; then
    echo "${ENGINE_LOG_DIR} exist."
else
    mkdir -p ${ENGINE_LOG_DIR}
    echo "${ENGINE_LOG_DIR} created."
fi
if [[ -e ${LOG_DIR} ]]; then
    echo "${LOG_DIR} exist."
else
    mkdir -p ${LOG_DIR}
    echo "${LOG_DIR} created."
fi
if [[ -e ${RES_DIR} ]]; then
    echo "${RES_DIR} exist."
else
    mkdir -p ${RES_DIR}
    echo "${RES_DIR} created."
fi

CLASSPATH=${CLASSPATH}
CLASSPATH="${CLASSPATH}:${BASE_DIR}/lib/*"
JAVA_OPTS="-Dwork.dir=${BASE_DIR}"
JAVA_OPTS="${JAVA_OPTS} -Dengine.type=${ENGINE_TYPE}"
JAVA_OPTS="${JAVA_OPTS} -Dconfigurations=${CONFIGURATIONS}"
JAVA_OPTS="${JAVA_OPTS} -Dstart.mode=${START_MODE}"
JAVA_OPTS="${JAVA_OPTS} -Dpod.number=${POD_NUM}"
JAVA_OPTS="${JAVA_OPTS} -Djava.net.preferIPv4Stack=true"
JAVA_OPTS="${JAVA_OPTS} -Duser.timezone=Asia/Shanghai"
DEBUG_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=10030"
MAIN_CLASS="io.shulie.flpt.pressure.engine.Bootstrap"

# 组装classpath
if [[ ${ENGINE_TYPE} = "jmeter" ]]; then
    JAVA_OPTS="${JAVA_OPTS} -Djmeter.home=${BASE_DIR}/engines/jmeter"
    CLASSPATH="${CLASSPATH}:${BASE_DIR}/engines/jmeter/bin/ApacheJMeter.jar"
fi

if [[ ${DEBUG} == 1 ]]; then
    JAVA_OPTS="${JAVA_OPTS} -Djmeter.debug=true"
    echo "CMD: java ${JAVA_OPTS} ${DEBUG_OPTS} -classpath ${CLASSPATH} ${MAIN_CLASS}"
    java ${JAVA_OPTS} ${DEBUG_OPTS} -classpath ${CLASSPATH} ${MAIN_CLASS}
elif [[ ${FOREGROUND} == 1 ]]; then
    echo "CMD: java ${JAVA_OPTS} -classpath ${CLASSPATH} ${MAIN_CLASS}"
    java ${JAVA_OPTS} -classpath ${CLASSPATH} ${MAIN_CLASS}
else
    echo "CMD: nohup java ${JAVA_OPTS} -classpath ${CLASSPATH} ${MAIN_CLASS} >> /dev/null 2>&1 &"
    nohup java ${JAVA_OPTS} -classpath ${CLASSPATH} ${MAIN_CLASS} >> /dev/null 2>&1 &
#    echo "CMD: nohup java ${JAVA_OPTS} -classpath ${CLASSPATH} ${MAIN_CLASS} >> /etc/engine/script/logs/presssure_engine.log 2>&1 &"
#    nohup java ${JAVA_OPTS} -classpath ${CLASSPATH} ${MAIN_CLASS} >> /etc/engine/script/logs/presssure_engine.log 2>&1 &
fi