package io.shulie.flpt.pressure.engine.util.http;

import java.util.Objects;

import lombok.extern.slf4j.Slf4j;

import io.shulie.flpt.pressure.engine.util.GsonUtils;
import io.shulie.flpt.pressure.engine.common.Constants;
import io.shulie.flpt.pressure.engine.api.entity.EngineRunConfig;
import io.shulie.flpt.pressure.engine.entity.cloud.EngineStatusEnum;
import io.shulie.flpt.pressure.engine.entity.cloud.EngineNotifyParam;

/**
 * 利用Http接口通知Takin-Cloud服务的工具类
 *
 * @author 何仲奇
 */
@Slf4j
public class HttpNotifyTakinCloudUtils {

    /**
     * 压测引擎异常信息前缀
     */
    private static final String PRESSURE_ENGINE_EXCEPTION_PREFIX = "【压测引擎】";

    private static String url;
    private static String resourceId;
    private static Long reportId;
    private static Long customerId;

    /**
     * 通知Takin-Cloud
     *
     * @param statusEnum 状态枚举
     * @param errMsg     错误信息
     */
    public static void notifyTakinCloud(EngineStatusEnum statusEnum, String errMsg) {
        String podNumber = System.getProperty("pod.number");
        HttpUtils.doPost(url, GsonUtils.obj2Json(EngineNotifyParam.build(resourceId, reportId, customerId)
            .podNum(podNumber == null ? "" : podNumber)
            .status(
                statusEnum.getStatus()).msg(PRESSURE_ENGINE_EXCEPTION_PREFIX + errMsg).build()));
    }

    public static String getTakinCloud(EngineStatusEnum statusEnum) {
        String podNumber = System.getProperty("pod.number");
        return HttpUtils.doPost(url,
            GsonUtils.obj2Json(EngineNotifyParam.build(resourceId, reportId, customerId)
                .podNum(podNumber == null ? "" : podNumber)
                .status(statusEnum.getStatus()).build()));
    }

    /**
     * 初始化
     *
     * @param config 配置信息
     */
    public static void init(EngineRunConfig config) {
        if (Objects.nonNull(config)) {
            url = config.getCallbackUrl();
            resourceId = config.getResourceId();
            reportId = config.getTaskId();
            customerId = config.getCustomerId();
        } else {
            url = Constants.TAKIN_TRO_URL;
            resourceId = "";
            reportId = 0L;
            customerId = 0L;
        }
        log.info("接口路径:{}\n资源主键:{}\n报告主键:{}\n租户主键\n{}", url, resourceId, reportId, customerId);
    }
}
