#!/bin/bash

# 版本
VERSION=""
# 平台
PLATFORM=""
# 镜像名称
DOCKER_NAME="swr.cn-east-3.myhuaweicloud.com/shulie-hangzhou/pressure-engine-basic"
# 工作目录
DIR_NAME=$(dirname "$0")
BUILD_DIR=$(cd "${DIR_NAME}/.." || exit; pwd)
echo "工作目录位于：${BUILD_DIR}";
# 读取入参
while getopts ':v:p:' opt
do
  case $opt in
  v)
    VERSION=$OPTARG
    ;;
  p)
    PLATFORM=$OPTARG
    ;;
  ?)
    echo "未知参数"
    ;;
  esac
done
if [ -z "$VERSION" ]; then
echo '缺少 -v .'
exit 1
fi
if [ -z "$PLATFORM" ]; then
echo "缺少 -p .参考: https://github.com/wm5920/docker-platform"
echo "例如        :linux/amd64 linux/arm64 windows/amd64"
exit 1
fi

echo '构建镜像'
docker build --platform "${PLATFORM}" -t "${DOCKER_NAME}":"${VERSION}" -f "${BUILD_DIR}"/dockerfile/basic "${BUILD_DIR}"
echo '列出镜像'
docker images  -a --no-trunc |grep $DOCKER_NAME