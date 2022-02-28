#!/bin/bash

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