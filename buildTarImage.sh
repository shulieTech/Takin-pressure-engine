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

# *******功能：    一键构建压测引擎镜像  *********
# *******版本：    v1.0              *********
# *******作者：    lipeng            *********
# *******创建时间： 2021/02/26        *********
# Tips
# 1. 运行此脚本前请确保docker处于运行状态
# 2. 运行此脚本前如果涉及maven版本号需要修改，需要自行在POM中修改.
#默认镜像TAG -t参数可以修改TAG
IMAGE_TAG="latest"

#自定义参数 （以下变量需要根据自己实际情况进行修改）
#JMETER源码路径
JMETER_SOURCE_PATH=/Users/allen/tmp/renshou/rs_jmeter
#Maven settings文件路径
MAVEN_SETTINGS_PATH=/Users/allen/.m2/settings.xml
#压测引擎项目源码根目录
PRESSURE_ENGINE_SOURCE_PATH=/Users/allen/tmp/renshou/rs_pressure-engine

log() {
    echo -e "\033[40;37m $1 \033[0m"
}

while getopts ':t:' opt
do
    case $opt in
        t)
	    IMAGE_TAG=$OPTARG
	    ;;
	?)
	    echo "未知参数"
	    ;;
    esac
done

#校验目录
log ' >>> check dir.. <<< '
if [ ! -d "${JMETER_SOURCE_PATH}" ];then
    echo "JMETER_SOURCE_PATH IS NOT EXISTS"
    exit 1;
fi
if [ ! -d "${PRESSURE_ENGINE_SOURCE_PATH}" ];then
    echo "PRESSURE_ENGINE_SOURCE_PATH IS NOT EXISTS"
    exit 1;
fi

#使用gradle给jmeter编译
log ' >>> building jmeter.. <<< '
sleep 2
#清空jmeter日志
echo "" > $JMETER_SOURCE_PATH/bin/jmeter.log
cd $JMETER_SOURCE_PATH
./gradlew src:build -PskipCheckstyle -PchecksumIgnore -Prat -PskipSpotless -x test
./gradlew src:dist:createDist -PskipCheckstyle -PchecksumIgnore

#jmeter 打zip
log ' >>> 压缩jmeter.. <<< '
sleep 2
#到jmeter上层目录
cd ..
zip -r jmeter.zip Takin-jmeter/

#jmeter.zip 移动到pressure-engine-jmeter项目lib
log ' >>> 移动jmeter.zip到pressure-engine-jmeter并解压.. <<< '
sleep 2
if [ ! -d "$PRESSURE_ENGINE_SOURCE_PATH/jmeter/pressure-engine-jmeter/lib" ]; then
  mkdir -p $PRESSURE_ENGINE_SOURCE_PATH/jmeter/pressure-engine-jmeter/lib
fi
mv jmeter.zip $PRESSURE_ENGINE_SOURCE_PATH/jmeter/pressure-engine-jmeter/lib
#解压并删除无效文件
cd $PRESSURE_ENGINE_SOURCE_PATH/jmeter/pressure-engine-jmeter/lib
unzip jmeter.zip
log ' >>> 移除多余文件.. <<< '
sleep 2
cd Takin-jmeter
#删除无效文件
rm -rf `ls | egrep -v '(bin|config|lib)'`
rm -rf .*

#打包上传
log ' >>> 打包，上传.. <<< '
sleep 2
cd $PRESSURE_ENGINE_SOURCE_PATH/jmeter/pressure-engine-jmeter
#mvn clean deploy -Dmaven.test.skip=true --settings $MAVEN_SETTINGS_PATH
mvn clean install -Dmaven.test.skip=true --settings $MAVEN_SETTINGS_PATH
#打包上传后移除jmeter
rm -rf $PRESSURE_ENGINE_SOURCE_PATH/jmeter/pressure-engine-jmeter/lib/*

#将pressure-engine项目打包
log ' >>> 打包pressure-engine.. <<< '
sleep 2
cd $PRESSURE_ENGINE_SOURCE_PATH
mvn clean package -Dmaven.test.skip=true --settings $MAVEN_SETTINGS_PATH

#将打包后的pressure-engine拷贝到打docker镜像处
sleep 2
cd ~
BUILD_IMAGE_PATH=`pwd`/develop/buildImages
#校验构建目录是否存在，不存在则创建
if [ ! -d "${BUILD_IMAGE_PATH}" ];then
    mkdir -p $BUILD_IMAGE_PATH
fi
if [ ! -d "${BUILD_IMAGE_PATH}/images" ];then
    mkdir -p $BUILD_IMAGE_PATH/images
fi
if [ ! -d "${BUILD_IMAGE_PATH}/pressure-engine" ];then
    mkdir -p $BUILD_IMAGE_PATH/pressure-engine
fi
cd $BUILD_IMAGE_PATH/pressure-engine
#将打包好的压测引擎包移动到这里
mv $PRESSURE_ENGINE_SOURCE_PATH/build/target/pressure-engine.tar.gz .

log ' >>> finish ^ . ^ bye ! <<< '

