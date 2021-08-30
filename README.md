# Takin-pressure-engine

# start.sh 使用方式

## 本地开发调试
```
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

#### 启动配置参数 启动配置指定了压测引擎启动的参数，压测模式，指标上传地址等配置项

```
-t 压测引擎类型  "jmeter"
-c 压测配置文件路径  
-f 前台启动 y
-d true  或者 -d 后面加一个数
```
#### run.json 内容
```
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
## 打包过程

  构建脚本一键构建：
  
    0. 修改buildTarImage.sh中参数
        JMETER_SOURCE_PATH             本地Jmeter源码根目录路径
        GRADLE_HOME                    本地Gradle安装目录，如果是idea自带gradle，可以在idea配置中查看路径
        MAVEN_SETTINGS_PATH            本地Maven配置文件位置
        PRESSURE_ENGINE_SOURCE_PATH    本地压测引擎项目源码根目录路径

    1. 执行脚本
        ./buildTarImage.sh

    2. 在用户目录下 develop/buildImages/pressure-engine文件夹下
        可以获得pressure-engine.tar.gz引擎包

## 插件开发
```
插件需要实现PressurePlugin和EnginePressureModeAbility接口。
```
API架构设计图如下：
<img src="https://raw.githubusercontent.com/shulieTech/Images/main/pressure_engine_api_struct.png" />
