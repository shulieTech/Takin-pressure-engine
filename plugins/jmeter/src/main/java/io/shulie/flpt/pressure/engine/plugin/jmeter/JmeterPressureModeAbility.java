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

package io.shulie.flpt.pressure.engine.plugin.jmeter;

import io.shulie.flpt.pressure.engine.api.ability.EnginePressureModeAbility;
import io.shulie.flpt.pressure.engine.api.ability.model.*;
import io.shulie.flpt.pressure.engine.api.entity.EnginePressureConfig;
import io.shulie.flpt.pressure.engine.api.plugin.PressureContext;
import io.shulie.flpt.pressure.engine.plugin.jmeter.consts.JmeterConstants;
import io.shulie.flpt.pressure.engine.plugin.jmeter.util.CommonUtil;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * jmeter支持的压力模式
 *
 * @author lipeng
 * @date 2021-08-03 2:37 下午
 */
public class JmeterPressureModeAbility implements EnginePressureModeAbility {

    /**
     * 实现此方法可具备并发模式能力
     *
     * @param context
     * @return
     */
    @Override
    public ConcurrencyAbility concurrencyModeAbility(PressureContext context) {
        return ConcurrencyAbility.build(JmeterConstants.CONCURRENCY_THREAD_GROUP_NAME)
                .addExtraAttribute("guiclass",
                        "com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroupGui")
                .addExtraAttribute("testclass",
                        "com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroup")
                .addExtraAttribute("testname",
                        "bzm - Concurrency Thread Group")
                .addExtraAttribute("enabled", "true");
    }

    /**
     * 实现此方法可具备并发模式能力
     *
     * @param context
     * @return
     */
    @Override
    public TPSAbility tpsModeAbility(PressureContext context) {
        return TPSAbility.build(JmeterConstants.TPS_THREAD_GROUP_NAME)
                .addExtraAttribute("guiclass",
                        "com.blazemeter.jmeter.threads.arrivals.ArrivalsThreadGroupGui")
                .addExtraAttribute("testclass",
                        "com.blazemeter.jmeter.threads.arrivals.ArrivalsThreadGroup")
                .addExtraAttribute("testname",
                        "bzm - Arrivals Thread Group")
                .addExtraAttribute("enabled", "true");
    }

    /**
     * 实现此方法可具备流量调试能力
     *
     * @param context
     * @return
     */
    @Override
    public FlowDebugAbility flowDebugModeAbility(PressureContext context) {
        return FlowDebugAbility.build(JmeterConstants.THREAD_GROUP_NAME)
                .addExtraAttribute("guiclass", "ThreadGroupGui")
                .addExtraAttribute("testclass", "ThreadGroup")
                .addExtraAttribute("testname", "线程组")
                .addExtraAttribute("enabled", "true");
    }

    /**
     * 实现此方法可具备脚本调试能力
     *
     * @param context
     * @return
     */
    @Override
    public TryRunAbility tryRunModeAbility(PressureContext context) {
        return TryRunAbility.build(JmeterConstants.THREAD_GROUP_NAME)
                .addExtraAttribute("guiclass", "ThreadGroupGui")
                .addExtraAttribute("testclass", "ThreadGroup")
                .addExtraAttribute("testname", "线程组")
                .addExtraAttribute("enabled", "true")
                .setExpectThroughput(context.getExpectThroughput())
                .setLoops(context.getLoops());
    }

    /**
     * 实现此方法可具备巡检能力
     *
     * @param context
     * @return
     */
    @Override
    public InspectionAbility inspectionModeAbility(PressureContext context) {
        EnginePressureConfig config = context.getPressureConfig();
        return InspectionAbility.build(JmeterConstants.THREAD_GROUP_NAME)
                .addExtraAttribute("guiclass", "ThreadGroupGui")
                .addExtraAttribute("testclass", "ThreadGroup")
                .addExtraAttribute("testname", "线程组")
                .addExtraAttribute("enabled", "true")
                //设置巡检间隔，默认是1秒
                .setFixTimer(CommonUtil.getValue(1000L, config, EnginePressureConfig::getFixedTimer))
                //设置运行时间，默认是100年
                .setDuration(CommonUtil.getValue(3600*24*365*100L, context, PressureContext::getDuration));
    }

}
