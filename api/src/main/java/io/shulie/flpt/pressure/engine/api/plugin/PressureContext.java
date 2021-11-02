/*
 * Copyright 2021 Shulie Technology, Co.Ltd
 * Email: shulie@shulie.io
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.shulie.flpt.pressure.engine.api.plugin;

import io.shulie.flpt.pressure.engine.api.entity.*;
import io.shulie.flpt.pressure.engine.api.enums.PressureSceneEnum;
import lombok.Data;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Create by xuyh at 2020/4/19 22:27.
 */
@Data
public class PressureContext extends AbstractEntry {
    //场景id
    private Long sceneId;
    //报告id
    private Long reportId;
    //客户id
    private Long customerId;
    //脚本路径
    private String scriptPath;
    //任务目录
    private String taskDir;
    //日志文件目录
    private String logDir;
    //ptl文件目录
    private String ptlDir;
    //资源文件目录
    private String resourcesDir;
    //压测内存设置
    private String memSetting;
    /**
     * 是否是新版，新版标识是压测场景有脚本解析的结果
     */
    private boolean newVersion;
    //运行时间
    private Integer duration;
    //引擎插件路径
    private List<String> enginePluginsFilePath;
//    //施压模式   固定压力值  线性递增  阶梯递增
//    private String pressureMode;
    //期望目标值， 并发模式下为并发，  tps模式为tps
    private Long expectThroughput;
//    //递增时长
//    private Long rampUp;
//    //阶梯层数
//    private Long steps;
    //循环次数
    private Long fix;
    private Long loops;
    //metric上报数据url
    private String metricCollectorUrl;
    //额外参数，目前这里是记录业务活动对应的目标rt
    private Map<String, String> businessMap;
    //压测数据信息
    private List<Map<String, Object>> dataFileSets;
    //pod数量
    private Integer podCount;
    /**
     * pod 的序号
     */
    private String podNumber;
    /**
     * 压测配置信息
     */
    private EnginePressureConfig pressureConfig;
    //引擎压测参数
//    private Map<String, Object> enginePressureParams;
    //引擎压测模式
    private Integer enginePressureMode;
    //当前压测模式枚举
    private PressureSceneEnum pressureScene;
    //启动模式  single double
    private String startMode;
    //采样率
    private Integer traceSampling;
    //cloud回调地址
    private String cloudCallbackUrl;
    //jar文件地址
    private List<String> jarFilePathList;
    //脚本文件
    private File scriptFile;
    //压力引擎后端监听器对列长度
    private String pressureEngineBackendQueueCapacity;

    //全局用户参数
    private GlobalUserVariables globalUserVariables;

    //请求头参数
    private HttpHeaderVariables httpHeaderVariables;

    //业务活动信息
    private List<BusinessActivity> businessActivities;

//    //pod 的序号
//    public String getPodNumber() {
//        return System.getProperty("pod.number");
//    }

}