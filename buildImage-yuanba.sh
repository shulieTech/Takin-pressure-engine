#!/bin/bash
#
# Copyright 2021 Shulie Technology, Co.Ltd
# Email: shulie@shulie.io
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a zy of the License at
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
#IMAGE_TAG="latest"

#自定义参数 （以下变量需要根据自己实际情况进行修改）
#JMETER源码路径
JMETER_SOURCE_PATH=~/Documents/job2/Takin-jmeter
#Gradle目录
GRADLE_HOME=~/.gradle/wrapper/dists/gradle-6.6-bin/dflktxzwamd4bv66q00iv4ga9/gradle-6.6
#Maven settings文件路径
MAVEN_SETTINGS_PATH=~/.m2/settings.xml
#压测引擎项目源码根目录
PRESSURE_ENGINE_SOURCE_PATH=~/Documents/job2/Takin-pressure-engine
#仓库地址
HARBOR_IP=192.168.1.119

log() {
    echo -e "\033[40;37m $1 \033[0m"
}

# 参数
# -t 镜像TAG，如：-t v4.9.2.15
# -p 推送到仓库
# -s 保存到本地
# -r 删除本地镜像

while getopts 't:psr' OPT; do
    case $OPT in
        t) IMAGE_TAG="$OPTARG"
          if [[ $IMAGE_TAG == -* ]]; then
            echo '缺少TAG,请在-t参数后面输入TAG值！如-t v4.9.2.15'
            exit 1;
          fi
          ;;
        p) ACT='y';;
        s) ACT='s';;
        r) ACT='r';;
        ?) echo '未知参数';;
    esac
done
echo "IMAGE_TAG="$IMAGE_TAG
echo "ACT="$ACT

if [ "x${IMAGE_TAG}" == "x" ];
then
  echo '缺少TAG,请在-t参数后面输入TAG值！如-t v4.9.2.15'
  exit 1;
fi

#使用gradle给jmeter编译
log ' >>> building jmeter.. <<< '
sleep 2
#清空jmeter日志
echo "" > $JMETER_SOURCE_PATH/bin/jmeter.log
cd $JMETER_SOURCE_PATH
git pull
$GRADLE_HOME/bin/gradle src:build -PskipCheckstyle -PchecksumIgnore -Prat -PskipSpotless -x test
$GRADLE_HOME/bin/gradle src:dist:createDist

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
git pull
#mvn clean deploy -Dmaven.test.skip=true --settings $MAVEN_SETTINGS_PATH
mvn clean install -Dmaven.test.skip=true --settings $MAVEN_SETTINGS_PATH
#打包上传后移除jmeter
rm -rf $PRESSURE_ENGINE_SOURCE_PATH/jmeter/pressure-engine-jmeter/lib/*

#将pressure-engine项目打包
log ' >>> 打包pressure-engine.. <<< '
sleep 2
cd $PRESSURE_ENGINE_SOURCE_PATH
mvn clean package -Dmaven.test.skip=true -X --settings $MAVEN_SETTINGS_PATH

#将打包后的pressure-engine拷贝到打docker镜像处
log ' >>> 构建docker镜像.. <<< '
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
#将Dockerfile复制过来
cp -f $PRESSURE_ENGINE_SOURCE_PATH/Dockerfile .
#将打包好的压测引擎包移动到这里
mv $PRESSURE_ENGINE_SOURCE_PATH/build/target/pressure-engine.tar.gz .
#删除原先解压后的压测引擎目录
rm -rf pressure-engine/
#解压新的打包的压测引擎
tar -zxvf pressure-engine.tar.gz
rm -rf pressure-engine.tar.gz
#docker构建
docker build --platform linux/amd64 -t forcecop/pressure-engine:$IMAGE_TAG .
#导出镜像
#log ' >>> 开始导出镜像.. <<< '
#sleep 2
#cd $BUILD_IMAGE_PATH/images
#将打好的镜像导出到images目录
#docker save -o pressure-engine-$IMAGE_TAG.tar forcecop/pressure-engine:$IMAGE_TAG

#log " >>> 镜像pressure-engine-${IMAGE_TAG}.tar已经导出到${BUILD_IMAGE_PATH}/images, 请查看 <<< "

log ' >>> jmeter库git打tag.. <<< '
sleep 2
cd $JMETER_SOURCE_PATH
git tag $IMAGE_TAG
git push origin $IMAGE_TAG
log ' >>> pressure-engine库git打tag.. <<< '
cd $PRESSURE_ENGINE_SOURCE_PATH
git tag $IMAGE_TAG
git push origin $IMAGE_TAG

imageId=`docker images|grep 'forcecop/pressure-engine'|grep $IMAGE_TAG|awk '{print $3}'`
if [ $ACT == 'y' ]; then
  log ' >>> push to library <<< '
  docker tag forcecop/pressure-engine:$IMAGE_TAG $HARBOR_IP/library/pressure-engine:$IMAGE_TAG
#      docker login $HARBOR_IP
  docker push $HARBOR_IP/library/pressure-engine:$IMAGE_TAG
elif [ $ACT == 's' ]; then
  log ' >>> save to local <<< '
  docker save -o pressure-engine-$IMAGE_TAG.tar forcecop/pressure-engine:$IMAGE_TAG
  open ./
elif [ $ACT == 'r' ]; then
  log ' >>> delete image tag from local <<< '
  echo 'docker rmi '$imageId' --force'
  docker rmi $imageId --force
fi

echo 'tag : docker tag forcecop/pressure-engine:'$IMAGE_TAG $HARBOR_IP'/library/pressure-engine:'$IMAGE_TAG
echo '保存到本地：docker save -o pressure-engine-'$IMAGE_TAG'.tar forcecop/pressure-engine:'$IMAGE_TAG
echo 'push : '
echo '     docker login '$HARBOR_IP
echo '     docker push '$HARBOR_IP'/library/pressure-engine:'$IMAGE_TAG
echo '删除： docker rmi '$imageId' --force'
echo '删除镜像和tag： ./deleteTag.sh -t '$IMAGE_TAG

log ' >>> finish ^ . ^ bye ! <<< '