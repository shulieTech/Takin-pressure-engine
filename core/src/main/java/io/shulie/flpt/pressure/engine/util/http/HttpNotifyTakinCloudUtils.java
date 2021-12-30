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

package io.shulie.flpt.pressure.engine.util.http;

import java.util.Objects;

import io.shulie.flpt.pressure.engine.api.entity.EngineRunConfig;
import io.shulie.flpt.pressure.engine.common.Constants;
import io.shulie.flpt.pressure.engine.entity.cloud.EngineNotifyParam;
import io.shulie.flpt.pressure.engine.entity.cloud.EngineStatusEnum;
import io.shulie.flpt.pressure.engine.util.GsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 何仲奇
 * @Package io.shulie.flpt.pressure.engine.plugin.jmeter.util
 * @date 2020/9/25 4:18 下午
 */
public class HttpNotifyTakinCloudUtils {

    //压测引擎异常信息前缀
    private static final String PRESSURE_ENGINE_EXCEPTION_PREFIX = "【压测引擎】";

    private static String url;
    private static Long sceneId;
    private static Long reportId;
    private static Long customerId;
    private static Logger logger = LoggerFactory.getLogger(HttpNotifyTakinCloudUtils.class);

    public static void notifyTakinCloud(EngineStatusEnum statusEnum, String errMsg) {
        String podNumber = System.getProperty("pod.number");
        HttpUtils.doPost(url, GsonUtils.obj2Json(EngineNotifyParam.build(sceneId, reportId, customerId)
                .podNum(podNumber == null ? "" : podNumber)
                .status(
            statusEnum.getStatus()).msg(PRESSURE_ENGINE_EXCEPTION_PREFIX+errMsg).build()));
    }
    public static String getTakinCloud(EngineStatusEnum statusEnum) {
       return HttpUtils.doPost(url, GsonUtils.obj2Json(EngineNotifyParam.build(sceneId, reportId, customerId).status(
            statusEnum.getStatus()).build()));
    }

    public static void init(EngineRunConfig config) {
        if (Objects.nonNull(config)) {
            url = config.getCallbackUrl();
            sceneId = config.getSceneId();
            reportId = config.getTaskId();
            customerId = config.getCustomerId();
        } else {
            url = Constants.TAKIN_TRO_URL;
            sceneId = 0L;
            reportId = 0L;
            customerId = 0L;
        }
        logger.info("tro 交互url:{},场景:{},报告：{},客户:{}", url,sceneId,reportId,customerId);
    }
}
