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
 * @author 李鹏
 */
public final class GlobalUserVariables {

    @GlobalParamKey(EngineConstants.GLOBAL_PARAM_KEY_SCENE_ID)
    private String sceneId;

    @GlobalParamKey(EngineConstants.GLOBAL_PARAM_KEY_REPORT_ID)
    private String reportId;

    @GlobalParamKey(EngineConstants.GLOBAL_PARAM_KEY_CUSTOMER_ID)
    private String customerId;

    /**
     * cloud 回调地址
     */
    @GlobalParamKey(EngineConstants.GLOBAL_PARAM_KEY_CLOUD_CALLBACK_URL)
    private String takinCloudCallbackUrl;

    /**
     * 引擎读取csv文件位点信息
     */
    @GlobalParamKey(EngineConstants.GLOBAL_PARAM_KEY_POSITION_ALL)
    private String globalVariablesMap;

    public String getGlobalVariablesMap() {
        return globalVariablesMap;
    }

    public void setGlobalVariablesMap(String globalVariablesMap) {
        this.globalVariablesMap = globalVariablesMap;
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

    public String getTakinCloudCallbackUrl() {
        return takinCloudCallbackUrl;
    }

    public void setTakinCloudCallbackUrl(String takinCloudCallbackUrl) {
        this.takinCloudCallbackUrl = takinCloudCallbackUrl;
    }
}