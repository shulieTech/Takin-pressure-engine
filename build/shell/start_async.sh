#!/bin/bash

# 启动引擎
nohup /bin/sh /home/opt/flpt/pressure-engine/bin/start.sh -t "jmeter" -c "/etc/engine/config/engine.conf" -l "" -f y &

# 容器持久化
tail -f /etc/hosts