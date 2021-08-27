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

source /etc/profile

CURRENT_DIR=$(cd "$(dirname "$0")"; pwd)
BASE_DIR=$(cd "$(dirname "$0")"; pwd)/..
CURRENT_USER=`whoami`
SCENE_ID=""
PID=""

while getopts ':s:p:' OPTION;do
    case ${OPTION} in
        s)
            SCENE_ID=${OPTARG}
            ;;
        p)
            PID=${OPTARG}
            ;;
        ?)
            echo "Usage: ./start.sh -s '1234567890' -p '10012'"
            exit 0
            ;;
    esac
done

kill ${PID}
sleep 1
echo "terminated."