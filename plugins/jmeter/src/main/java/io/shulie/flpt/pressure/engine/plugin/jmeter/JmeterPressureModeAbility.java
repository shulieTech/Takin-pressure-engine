package io.shulie.flpt.pressure.engine.plugin.jmeter;

import io.shulie.flpt.pressure.engine.api.ability.model.*;
import io.shulie.flpt.pressure.engine.api.plugin.PressureContext;
import io.shulie.flpt.pressure.engine.plugin.jmeter.consts.JmeterConstants;
import io.shulie.flpt.pressure.engine.api.ability.EnginePressureModeAbility;

/**
 * jmeter支持的压力模式
 *
 * @author 李鹏
 */
public class JmeterPressureModeAbility implements EnginePressureModeAbility {

    /**
     * 实现此方法可具备并发模式能力
     *
     * @param context 压测上下文
     * @return -
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
     * @param context 压测上下文
     * @return -
     */
    @Override
    public TpsAbility tpsModeAbility(PressureContext context) {
        return TpsAbility.build(JmeterConstants.TPS_THREAD_GROUP_NAME)
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
     * @param context 压测上下文
     * @return -
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
     * @param context 压测上下文
     * @return -
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
     * @param context 压测上下文
     * @return -
     */
    @Override
    public InspectionAbility inspectionModeAbility(PressureContext context) {
        return InspectionAbility.build(JmeterConstants.THREAD_GROUP_NAME)
            .addExtraAttribute("guiclass", "ThreadGroupGui")
            .addExtraAttribute("testclass", "ThreadGroup")
            .addExtraAttribute("testname", "线程组")
            .addExtraAttribute("enabled", "true")
            .setLoops(context.getLoops());
    }

}
