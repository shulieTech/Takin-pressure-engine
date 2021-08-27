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

package io.shulie.flpt.pressure.engine.api.ability;

import io.shulie.flpt.pressure.engine.api.ability.model.*;
import io.shulie.flpt.pressure.engine.api.enums.EnginePressureMode;
import io.shulie.flpt.pressure.engine.api.plugin.PressureContext;

import java.util.HashMap;
import java.util.Map;

/**
 * 支持的压力模式能力
 *
 * @author lipeng
 * @date 2021-08-03 3:09 下午
 */
public final class SupportedPressureModeAbilities {

    private PressureContext context;

    //支持的压力模式
    private Map<EnginePressureMode, BaseAbility> mapAbilities = new HashMap<>();

    private SupportedPressureModeAbilities(PressureContext context) {
        this.context = context;
    }

    /**
     * 获取压力模式对应能力
     *
     * @param enginePressureMode
     * @param <T>
     * @return
     */
    public <T> T getPressureModeAbility(EnginePressureMode enginePressureMode) {
        return (T) mapAbilities.get(enginePressureMode);
    }

    public static SupportedPressureModeAbilities build(PressureContext context) {
        return new SupportedPressureModeAbilities(context);
    }

    /**
     * 初始化
     *
     * @param enginePressureModeAbility
     */
    public SupportedPressureModeAbilities initialize(EnginePressureModeAbility enginePressureModeAbility) {
        //获取压测引擎支持的压力模式
        if(enginePressureModeAbility != null) {
            //获取并发模式能力
            ConcurrencyAbility concurrencyAbility = enginePressureModeAbility.concurrencyModeAbility(context);
            if(concurrencyAbility != null) {
                mapAbilities.put(EnginePressureMode.CONCURRENCY, concurrencyAbility);
            }
            //tps模式
            TPSAbility tpsAbility = enginePressureModeAbility.tpsModeAbility(context);
            if(tpsAbility != null) {
                mapAbilities.put(EnginePressureMode.TPS, tpsAbility);
            }
            //流量调试
            FlowDebugAbility flowDebugAbility = enginePressureModeAbility.flowDebugModeAbility(context);
            if(flowDebugAbility != null) {
                mapAbilities.put(EnginePressureMode.FLOW_DEBUG, flowDebugAbility);
            }
            //脚本调试
            TryRunAbility tryRunAbility = enginePressureModeAbility.tryRunModeAbility(context);
            if(tryRunAbility != null) {
                mapAbilities.put(EnginePressureMode.TRY_RUN, tryRunAbility);
            }
            //巡检
            InspectionAbility inspectionAbility = enginePressureModeAbility.inspectionModeAbility(context);
            if(inspectionAbility != null) {
                mapAbilities.put(EnginePressureMode.INSPECTION_MODE, inspectionAbility);
            }
        }
        return this;
    }

}