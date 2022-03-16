#!/bin/bash

# 工作目录
DIRNAME=$(dirname "$0")
WORKDIR=$(cd "$DIRNAME"/../.. || exit; pwd)
# 压测引擎项目源码路径
PRESSURE_ENGINE_SOURCE_PATH=$WORKDIR
# 压测引擎Jmeter目录
PRESSURE_ENGINE_JMETER="$PRESSURE_ENGINE_SOURCE_PATH"/pressure-engine-jmeter/
# JMETER源码路径
JMETER_SOURCE_PATH="$PRESSURE_ENGINE_SOURCE_PATH"/../takin-jmeter

log() {
    echo -e "\033[40;37m$DATE $1\033[0m"
}

log ' >>> 校验目录 <<< '
if [ ! -d "${JMETER_SOURCE_PATH}" ];then
    echo "JMETER源码路径不存在"
    exit 1;
fi
if [ ! -d "${PRESSURE_ENGINE_SOURCE_PATH}" ];then
    echo "压测引擎项目源码路径不存在"
    exit 1;
fi
if [ ! -d "$PRESSURE_ENGINE_JMETER" ]; then
  mkdir -p "$PRESSURE_ENGINE_JMETER"
fi

log ' >>> 编译 JMeter <<< '
cd "$JMETER_SOURCE_PATH" || exit
./gradlew src:build -PskipCheckstyle -PchecksumIgnore -Prat -PskipSpotless -x test -q
./gradlew src:dist:createDist -q

log ' >>> 压缩、移动、解压 JMeter <<< '
zip -q -r jmeter.zip bin lib config
sh "$PRESSURE_ENGINE_JMETER"/clean.sh
cp jmeter.zip "$PRESSURE_ENGINE_JMETER"
cd "$PRESSURE_ENGINE_JMETER" || exit
unzip -q jmeter.zip && rm -f jmeter.zip

log ' >>> 打包压测引擎 <<< '
cd "$PRESSURE_ENGINE_SOURCE_PATH" || exit
mvn clean package -q -D"maven.test.skip"=true
log 制品位于:"$PRESSURE_ENGINE_SOURCE_PATH"/build/target/pressure-engine.tar.gz
log ' >>> finish ^ . ^ bye ! <<< '

