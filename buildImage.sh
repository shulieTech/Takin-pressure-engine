#!/bin/bash

log() {
    echo -e "\033[40;37m$1\033[0m"
}

IMAGE_TAG=""
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
if [ -z "$IMAGE_TAG" ]; then
log ' >>> 必须指定参数 -t <<< '
exit 1
fi

# 工作目录
DIRNAME=$(dirname "$0")
WORKDIR=$(cd "$DIRNAME" || exit; pwd)
cd "$WORKDIR" || exit
# docker 名称
DOCKER_NAME=forcecop/pressure-engine
# 制品地址
BUILD_DIR="$WORKDIR"/.build

log ' >>> 开始打包 <<< '
sh ./buildTar.sh
log ' >>> 开始构建打包环境 <<< '
rm    -rf   "$BUILD_DIR"
mkdir       "$BUILD_DIR"
cp          "$WORKDIR"/build/target/pressure-engine.tar.gz "$BUILD_DIR"
tar   -zxf  "$BUILD_DIR"/pressure-engine.tar.gz -C "$BUILD_DIR"/
log ' >>> 构建docker镜像 <<< '
docker build \
--platform linux/amd64 \
-f "$WORKDIR"/Dockerfile \
-t "$DOCKER_NAME":"$IMAGE_TAG" \
"$BUILD_DIR"
log ' >>> 导出docker镜像 <<< '
docker save \
-o "$BUILD_DIR"/pressure-engine-"$IMAGE_TAG".tar \
"$DOCKER_NAME":"$IMAGE_TAG"
log ' >>> docker镜像构建完成 <<< '
docker images  -a --no-trunc |grep $DOCKER_NAME