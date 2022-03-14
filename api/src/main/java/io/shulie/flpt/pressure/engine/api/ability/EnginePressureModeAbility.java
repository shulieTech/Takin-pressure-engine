package io.shulie.flpt.pressure.engine.api.ability;

import io.shulie.flpt.pressure.engine.api.ability.model.*;
import io.shulie.flpt.pressure.engine.api.plugin.PressureContext;

/**
 * 压力引擎压力模式能力接口
 *
 * 需要支持各种压力模式需要实现此接口
 *
 * @author 李鹏
 */
public interface EnginePressureModeAbility {

    /**
     * 实现此方法可具备并发模式能力
     *
     * @param context 压测上下文
     * @return -
     */
    default ConcurrencyAbility concurrencyModeAbility(PressureContext context) {return null;}

    /**
     * 实现此方法可具备并发模式能力
     *
     * @param context 压测上下文
     * @return -
     */
    default TpsAbility tpsModeAbility(PressureContext context) {return null;}

    /**
     * 实现此方法可具备流量调试能力
     *
     * @param context 压测上下文
     * @return -
     */
    default FlowDebugAbility flowDebugModeAbility(PressureContext context) {return null;}

    /**
     * 实现此方法可具备脚本调试能力
     *
     * @param context 压测上下文
     * @return -
     */
    default TryRunAbility tryRunModeAbility(PressureContext context) {return null;}

    /**
     * 实现此方法可具备巡检能力
     *
     * @param context 压测上下文
     * @return -
     */
    default InspectionAbility inspectionModeAbility(PressureContext context) {return null;}
}