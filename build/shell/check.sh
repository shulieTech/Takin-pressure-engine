#!/bin/bash

status="failed"
msg="启动失败"
echo "start checking ..."
for a in {1..20}
do
  pid=`ps -ef|grep 'io.shulie.flpt.pressure.engine.Bootstrap '${1}''|grep -v grep |awk '{print$2}'`
  echo ${1} ${2} ${3} ${4}
  echo "PID:" ${pid}
  okcount=`more ${3}/logs/pressure-engine-${pid}.log |grep 'Startup script completed'|wc -l`
  if [ $okcount -eq 1 ]
   then
          status="started"
          msg="启动成功"
      break
   fi
  sleep 1
done
curl ${4} -X POST -H 'Content-Type:application/json' -d '{"sceneId":"'${1}'","taskId":"'${2}'","status":"'${status}'","msg":"'${msg}'"}'
echo "finished"