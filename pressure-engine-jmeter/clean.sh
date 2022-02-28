#!/bin/bash

# 工作目录
DIRNAME=$(dirname "$0")
WORKDIR=$(cd "$DIRNAME" || exit; pwd)
cd "$WORKDIR" || exit
# 开始删除
shopt -s extglob
rm -rf "${JMETER_SOURCE_PATH:- --help }"/* !(.gitignore|clean.sh|assembly.xml|pom.xml)
shopt -u extglob