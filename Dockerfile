FROM geekidea/alpine-a:3.9
MAINTAINER shulie <dev@shulie.io>
COPY pressure-engine /home/opt/flpt/pressure-engine
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories && \
    apk update && \
    apk add openjdk8 && \
    apk add --no-cache curl && \
    apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone
ENV TZ Asia/Shanghai
ENV engineType="jmeter"
ENV confPath="/etc/engine/config/engine.conf"
EXPOSE 8087
ENTRYPOINT ["sh", "-c", "/bin/sh /home/opt/flpt/pressure-engine/bin/start.sh -t $engineType -c $confPath -f y"]