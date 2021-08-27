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

package io.shulie.flpt.pressure.engine.api.entity;

import io.shulie.flpt.pressure.engine.api.annotation.GlobalParamKey;
import io.shulie.flpt.pressure.engine.api.constants.EngineConstants;

/**
 * 全局用户参数
 * 使用jmeter插件的此类的属性均可在jmeter使用JMeterContextService.getContext().getVariables()获取到
 * 非jmeter可以自行在插件实现属性获取
 * 如果需要额外的参数 可以添加属性
 *
 * ps. 只有标注了GlobalParamKey注解的属性才会被写入jmeter脚本
 *
 * @author lipeng
 * @date 2021-01-26 11:34 上午
 */
public final class GlobalUserVariables {

    @GlobalParamKey(EngineConstants.GLOBAL_PARAM_KEY_SCENE_ID)
    private String sceneId;

    @GlobalParamKey(EngineConstants.GLOBAL_PARAM_KEY_REPORT_ID)
    private String reportId;

    @GlobalParamKey(EngineConstants.GLOBAL_PARAM_KEY_CUSTOMER_ID)
    private String customerId;

    //引擎压测模式 0并发模式， 1TPS模式， 9自定义模式
    @GlobalParamKey(EngineConstants.GLOBAL_PARAM_KEY_PRESSURE_MODE)
    private String enginePressureMode;

    //cloud 回调地址
    @GlobalParamKey(EngineConstants.GLOBAL_PARAM_KEY_CLOUD_CALLBACK_URL)
    private String takinCloudCallbackUrl;

    //引擎读取csv文件开始位置
    @GlobalParamKey(EngineConstants.GLOBAL_PARAM_KEY_CSV_FILE_START_POSITION)
    private String engineCsvFilePositionStart;

    //引擎读取csv文件结束位置
    @GlobalParamKey(EngineConstants.GLOBAL_PARAM_KEY_CSV_FILE_STOP_POSITION)
    private String engineCsvFilePositionEnd;

    //引擎读取csv文件位点信息
    @GlobalParamKey(EngineConstants.GLOBAL_PARAM_KEY_POSITION_ALL)
    private String globalVariablesMap;

    public String getGlobalVariablesMap() {
        return globalVariablesMap;
    }

    public void setGlobalVariablesMap(String globalVariablesMap) {
        this.globalVariablesMap = globalVariablesMap;
    }

    public String getEngineCsvFilePositionStart() {
        return engineCsvFilePositionStart;
    }

    public void setEngineCsvFilePositionStart(String engineCsvFilePositionStart) {
        this.engineCsvFilePositionStart = engineCsvFilePositionStart;
    }

    public String getEngineCsvFilePositionEnd() {
        return engineCsvFilePositionEnd;
    }

    public void setEngineCsvFilePositionEnd(String engineCsvFilePositionEnd) {
        this.engineCsvFilePositionEnd = engineCsvFilePositionEnd;
    }

    public String getSceneId() {
        return sceneId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getEnginePressureMode() {
        return enginePressureMode;
    }

    public void setEnginePressureMode(String enginePressureMode) {
        this.enginePressureMode = enginePressureMode;
    }

    public String getTakinCloudCallbackUrl() {
        return takinCloudCallbackUrl;
    }

    public void setTakinCloudCallbackUrl(String takinCloudCallbackUrl) {
        this.takinCloudCallbackUrl = takinCloudCallbackUrl;
    }
}