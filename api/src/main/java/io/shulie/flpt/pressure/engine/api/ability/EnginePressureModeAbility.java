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
import io.shulie.flpt.pressure.engine.api.plugin.PressureContext;

/**
 * 压力引擎压力模式能力接口
 *
 * 需要支持各种压力模式需要实现此接口
 *
 * @author lipeng
 * @date 2021-07-29 4:33 下午
 */
public interface EnginePressureModeAbility {

    /**
     * 实现此方法可具备并发模式能力
     *
     * @param context
     * @return
     */
    default ConcurrencyAbility concurrencyModeAbility(PressureContext context) {return null;};

    /**
     * 实现此方法可具备并发模式能力
     *
     * @param context
     * @return
     */
    default TPSAbility tpsModeAbility(PressureContext context) {return null;};

    /**
     * 实现此方法可具备流量调试能力
     *
     * @param context
     * @return
     */
    default FlowDebugAbility flowDebugModeAbility(PressureContext context) {return null;};

    /**
     * 实现此方法可具备脚本调试能力
     *
     * @param context
     * @return
     */
    default TryRunAbility tryRunModeAbility(PressureContext context) {return null;};

    /**
     * 实现此方法可具备巡检能力
     *
     * @param context
     * @return
     */
    default InspectionAbility inspectionModeAbility(PressureContext context) {return null;};

}