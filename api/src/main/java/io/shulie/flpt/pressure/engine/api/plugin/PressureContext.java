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

import io.shulie.flpt.pressure.engine.api.entity.BusinessActivity;
import io.shulie.flpt.pressure.engine.api.entity.GlobalUserVariables;
import io.shulie.flpt.pressure.engine.api.entity.HttpHeaderVariables;
import io.shulie.flpt.pressure.engine.api.enums.EnginePressureMode;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Create by xuyh at 2020/4/19 22:27.
 */
public class PressureContext {

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
    //运行时间
    private Long duration;
    //引擎插件路径
    private List<String> enginePluginsFilePath;
    //施压模式   固定压力值  线性递增  阶梯递增
    private String pressureMode;
    //期望目标值， 并发模式下为并发，  tps模式为tps
    private Long expectThroughput;
    //递增时长
    private Long rampUp;
    //阶梯层数
    private Long steps;
    //循环次数
    private Long loops;
    //metric上报数据url
    private String metricCollectorUrl;
    //额外参数，目前这里是记录业务活动对应的目标rt
    private Map<String, String> businessMap;
    //压测数据信息
    private List<Map<String, Object>> dataFileSets;
    //pod数量
    private int podCount;
    //引擎压测参数
    private Map<String, Object> enginePressureParams;
    //引擎压测模式
    private String enginePressureMode;
    //当前压测模式枚举
    private EnginePressureMode currentEnginePressureMode;
    //启动模式  single double
    private String startMode;
    //采样率
    private String traceSampling;
    //cloud回调地址
    private String cloudCallbackUrl;
    //jar文件地址
    private List<String> jarFilePathList;
    //脚本文件
    private File scriptFile;
    //压力引擎后端监听器对列长度
    private String pressureEngineBackendQueueCapacity;

    //pod 的序号
    public String getPodNumber() {
        return System.getProperty("pod.number");
    }

    //全局用户参数
    private GlobalUserVariables globalUserVariables;

    public GlobalUserVariables getGlobalUserVariables() {
        return globalUserVariables;
    }

    //请求头参数
    private HttpHeaderVariables httpHeaderVariables;

    //业务活动信息
    List<BusinessActivity> businessActivities;

    public List<BusinessActivity> getBusinessActivities() {
        return businessActivities;
    }

    public void setBusinessActivities(List<BusinessActivity> businessActivities) {
        this.businessActivities = businessActivities;
    }

    public String getPressureEngineBackendQueueCapacity() {
        return pressureEngineBackendQueueCapacity;
    }

    public void setPressureEngineBackendQueueCapacity(String pressureEngineBackendQueueCapacity) {
        this.pressureEngineBackendQueueCapacity = pressureEngineBackendQueueCapacity;
    }

    public HttpHeaderVariables getHttpHeaderVariables() {
        return httpHeaderVariables;
    }

    public void setHttpHeaderVariables(HttpHeaderVariables httpHeaderVariables) {
        this.httpHeaderVariables = httpHeaderVariables;
    }

    public void setGlobalUserVariables(GlobalUserVariables globalUserVariables) {
        this.globalUserVariables = globalUserVariables;
    }

    public Long getLoops() {
        return loops;
    }

    public void setLoops(Long loops) {
        this.loops = loops;
    }

    public File getScriptFile() {
        return scriptFile;
    }

    public void setScriptFile(File scriptFile) {
        this.scriptFile = scriptFile;
    }

    public List<String> getJarFilePathList() {
        return jarFilePathList;
    }

    public void setJarFilePathList(List<String> jarFilePathList) {
        this.jarFilePathList = jarFilePathList;
    }

    public String getCloudCallbackUrl() {
        return cloudCallbackUrl;
    }

    public void setCloudCallbackUrl(String cloudCallbackUrl) {
        this.cloudCallbackUrl = cloudCallbackUrl;
    }

    public String getTraceSampling() {
        return traceSampling;
    }

    public void setTraceSampling(String traceSampling) {
        this.traceSampling = traceSampling;
    }

    public String getStartMode() {
        return startMode;
    }

    public void setStartMode(String startMode) {
        this.startMode = startMode;
    }

    public EnginePressureMode getCurrentEnginePressureMode() {
        return currentEnginePressureMode;
    }

    public void setCurrentEnginePressureMode(EnginePressureMode currentEnginePressureMode) {
        this.currentEnginePressureMode = currentEnginePressureMode;
    }

    public String getEnginePressureMode() {
        return enginePressureMode;
    }

    public void setEnginePressureMode(String enginePressureMode) {
        this.enginePressureMode = enginePressureMode;
    }

    public Map<String, Object> getEnginePressureParams() {
        return enginePressureParams;
    }

    public void setEnginePressureParams(Map<String, Object> enginePressureParams) {
        this.enginePressureParams = enginePressureParams;
    }

    public int getPodCount() {
        return podCount;
    }

    public void setPodCount(int podCount) {
        this.podCount = podCount;
    }

    public List<Map<String, Object>> getDataFileSets() {
        return dataFileSets;
    }

    public void setDataFileSets(List<Map<String, Object>> dataFileSets) {
        this.dataFileSets = dataFileSets;
    }

    public String getMetricCollectorUrl() {
        return metricCollectorUrl;
    }

    public void setMetricCollectorUrl(String metricCollectorUrl) {
        this.metricCollectorUrl = metricCollectorUrl;
    }

    public Map<String, String> getBusinessMap() {
        return businessMap;
    }

    public void setBusinessMap(Map<String, String> businessMap) {
        this.businessMap = businessMap;
    }

    private Map<String, Object> taskParams;//压测任务信息

    public Map<String, Object> getTaskParams() {
        return taskParams;
    }

    public Long getExpectThroughput() {
        return expectThroughput;
    }

    public void setExpectThroughput(Long expectThroughput) {
        this.expectThroughput = expectThroughput;
    }

    public Long getRampUp() {
        return rampUp;
    }

    public void setRampUp(Long rampUp) {
        this.rampUp = rampUp;
    }

    public Long getSteps() {
        return steps;
    }

    public void setSteps(Long steps) {
        this.steps = steps;
    }

    public String getPressureMode() {
        return pressureMode;
    }

    public void setPressureMode(String pressureMode) {
        this.pressureMode = pressureMode;
    }

    public void setTaskParams(Map<String, Object> taskParams) {
        this.taskParams = taskParams;
    }

    public String getResourcesDir() {
        return resourcesDir;
    }

    public void setResourcesDir(String resourcesDir) {
        this.resourcesDir = resourcesDir;
    }

    public String getTaskDir() {
        return taskDir;
    }

    public void setTaskDir(String taskDir) {
        this.taskDir = taskDir;
    }

    public String getLogDir() {
        return logDir;
    }

    public void setLogDir(String logDir) {
        this.logDir = logDir;
    }

    public String getPtlDir() {
        return ptlDir;
    }

    public void setPtlDir(String ptlDir) {
        this.ptlDir = ptlDir;
    }

    public Long getSceneId() {
        return sceneId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public void setSceneId(Long sceneId) {
        this.sceneId = sceneId;
    }

    public String getMemSetting() {
        return memSetting;
    }

    public void setMemSetting(String memSetting) {
        this.memSetting = memSetting;
    }

    public Long getDuration() {
        return duration;
    }

    public String getScriptPath() {
        return scriptPath;
    }

    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public List<String> getEnginePluginsFilePath() {
        return enginePluginsFilePath;
    }

    public void setEnginePluginsFilePath(List<String> enginePluginsFilePath) {
        this.enginePluginsFilePath = enginePluginsFilePath;
    }

    @Override
    public String toString() {
        return "PressureContext{" +
                "sceneId='" + sceneId + '\'' +
                ", taskDir='" + taskDir + '\'' +
                ", logDir='" + logDir + '\'' +
                ", resourcesDir='" + resourcesDir + '\'' +
                ", memSetting='" + memSetting + '\'' +
                ", duration=" + duration +
                ", taskParams=" + taskParams +
                '}';
    }

}