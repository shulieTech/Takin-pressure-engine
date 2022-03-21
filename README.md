# Takin-pressure-engine

## 构建

1. [基础包](./build/cmd/basic.sh)

    ```sh ./build/cmd/basic.sh```

2. [tar包](./build/cmd/tar.sh)

    ```sh ./build/cmd/tar.sh```

3. [docker镜像](./build/cmd/image.sh)

    ```sh ./build/cmd/image.sh```

## start.sh 使用方式

### 本地开发调试
```shell
// 先修改配置文件
pressure-engine.properties
// 打包
mvn clean package -Dmaven.test.skip=true 或者 mvn clean package
// 进入工作路径
cd build/target/pressure-engine/pressure-engine/bin
// 执行下面命令
./start.sh -t "jmeter" -c "/test/run.json" -f y -d true
// 开始 Remote 调试 配置如下
```
### 解释说明

#### 启动配置参数

启动配置指定了压测引擎启动的参数，压测模式，指标上传地址等配置项

```
-t "jmeter" 			# 压测引擎类型
-c xxx/xxx.jmx   	# 压测配置文件路径
-f y							# 前台启动
-d true  					# 或者 -d 后面加一个数
```
#### run.json 内容
```json
{
    "scriptPath": "/test/3/resources/test.jmx",
    "fileSets":
    [
        {
            "name": "c__bills_1.csv",
            "path": "/test/3/c__bills_1.csv",
            "split": false
        },
        {
            "name": "c__bills_2.csv",
            "path": "/test/3/c__bills_2.csv",
            "split": false
        }
    ],
    "continuedTime": 3600,
    "memSetting": "-Xmx4096m -Xms4096m -Xss256K -XX:MaxMetaspaceSize=256m",
    "extJarPath": "",
    "podCount": 1,
    "pressureMode": "fixed",
    "Takin-Task-ID": 231,
    "consoleUrl": "http://localhost:10010/takin-cloud/api/collector/receive?sceneId=3&reportId=231&customerId=9725",
    "expectThroughput": 10,
    "sceneId": 3,
    "Takin-Scene-ID": 3,
    "Takin-Customer-ID": 9725,
    "businessMap": "{\n  \"HTTP\u8bf7\u6c42\": \"100\"\n}",
    "takinCloudCallbackUrl": "http://localhost:10010/takin-cloud/api/engine/callback",
    "taskId": 231,
    "rampUp": 0
}
```
## 插件开发
> 插件实现均需要在plugins目录下
> 插件需要实现PressurePlugin和EnginePressureModeAbility接口

API架构设计图如下：
<img src="https://raw.githubusercontent.com/shulieTech/Images/main/pressure_engine_api_struct.png" />
