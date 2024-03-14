#!/bin/bash

# 镜像tag
IMAGE_TAG=""
# 基础镜像md5
BASIC_VERSION=""
# 基础镜像名称
BASIC_DOCKER_NAME="swr.cn-east-3.myhuaweicloud.com/shulie-hangzhou/pressure-engine-basic"

# 工作目录
DIR_NAME=$(dirname "$0")
WORK_DIR=$(cd "$DIR_NAME"/../.. || exit; pwd)
cd "$WORK_DIR" || exit
# 读取参数
while getopts ':t:v:' opt
do
  case $opt in
  t)
    IMAGE_TAG=$OPTARG
    ;;
  v)
    BASIC_VERSION=$OPTARG
    ;;
  ?)
    echo "未知参数"
    ;;
  esac
done
if [ -z "$IMAGE_TAG" ]; then
echo '缺少 -t .'
exit 1
fi
if [ -z "$BASIC_VERSION" ]; then
echo '缺少 -v .可选值列表(版本,MD5值)'
docker images  -a --no-trunc |grep "${BASIC_DOCKER_NAME}"|awk '{sub("sha256:","");print $2,$3 }'
exit 1
fi
# docker 名称
DOCKER_NAME=forcecop/pressure-engine
# 制品地址
BUILD_DIR="$WORK_DIR"/.build

echo ' >>> 开始打包 <<< '
sh ./build/cmd/tar.sh
echo ' >>> 开始构建打包环境 <<< '
rm    -rf   "$BUILD_DIR"
mkdir       "$BUILD_DIR"
cp          "$WORK_DIR"/build/target/pressure-engine.tar.gz "$BUILD_DIR"
tar   -zxf  "$BUILD_DIR"/pressure-engine.tar.gz -C "$BUILD_DIR"/
echo " >>> 预生成DockerFile <<<"
# 创建临时目录
TEMP_FILE=$(mktemp -qt Dockerfile.XXXX)
TEMP_FILE="${WORK_DIR}/build/${TEMP_FILE##*/}"
echo "临时文件${TEMP_FILE}"
# 写入FROM
echo "FROM ${BASIC_DOCKER_NAME}:${BASIC_VERSION}" > "${TEMP_FILE}"
cat "${WORK_DIR}/build/dockerfile/main" >> "${TEMP_FILE}"
cat "${TEMP_FILE}"
echo ' >>> 构建docker镜像 <<< '
docker build \
--platform linux/amd64 \
-f "${TEMP_FILE}" \
-t "$DOCKER_NAME":"$IMAGE_TAG" \
"$BUILD_DIR"
rm -f "${TEMP_FILE}"
echo ' >>> 导出docker镜像 <<< '
docker save \
-o "$BUILD_DIR"/pressure-engine-"$IMAGE_TAG".tar \
"$DOCKER_NAME":"$IMAGE_TAG"
echo ' >>> docker镜像构建完成 <<< '
docker images  -a --no-trunc |grep $DOCKER_NAME