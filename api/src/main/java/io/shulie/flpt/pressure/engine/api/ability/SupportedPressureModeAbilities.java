package io.shulie.flpt.pressure.engine.api.ability;

import java.util.Map;
import java.util.HashMap;

import io.shulie.flpt.pressure.engine.api.ability.model.*;
import io.shulie.flpt.pressure.engine.api.plugin.PressureContext;
import io.shulie.flpt.pressure.engine.api.enums.PressureSceneEnum;

/**
 * 支持的压力模式能力
 *
 * @author 李鹏
 */
public final class SupportedPressureModeAbilities {

    private final PressureContext context;

    /**
     * 支持的压力模式
     */
    private final Map<PressureSceneEnum, BaseAbility<?>> mapAbilities = new HashMap<>();

    private SupportedPressureModeAbilities(PressureContext context) {
        this.context = context;
    }

    /**
     * 获取压力模式对应能力
     *
     * @param enginePressureMode 施压模式
     * @param <T>                期待的类型
     * @return 施压能力
     */
    public <T> T getPressureModeAbility(PressureSceneEnum enginePressureMode) {
        return (T)mapAbilities.get(enginePressureMode);
    }

    public static SupportedPressureModeAbilities build(PressureContext context) {
        return new SupportedPressureModeAbilities(context);
    }

    /**
     * 初始化
     *
     * @param enginePressureModeAbility 施压能力
     */
    public SupportedPressureModeAbilities initialize(EnginePressureModeAbility enginePressureModeAbility) {
        //获取压测引擎支持的压力模式
        if (enginePressureModeAbility != null) {
            //获取并发模式能力
            ConcurrencyAbility concurrencyAbility = enginePressureModeAbility.concurrencyModeAbility(context);
            if (concurrencyAbility != null) {
                mapAbilities.put(PressureSceneEnum.DEFAULT, concurrencyAbility);
            }
            // 流量调试
            FlowDebugAbility flowDebugAbility = enginePressureModeAbility.flowDebugModeAbility(context);
            if (flowDebugAbility != null) {
                mapAbilities.put(PressureSceneEnum.FLOW_DEBUG, flowDebugAbility);
            }
            //脚本调试
            TryRunAbility tryRunAbility = enginePressureModeAbility.tryRunModeAbility(context);
            if (tryRunAbility != null) {
                mapAbilities.put(PressureSceneEnum.TRY_RUN, tryRunAbility);
            }
            //巡检
            InspectionAbility inspectionAbility = enginePressureModeAbility.inspectionModeAbility(context);
            if (inspectionAbility != null) {
                mapAbilities.put(PressureSceneEnum.INSPECTION_MODE, inspectionAbility);
            }
        }
        return this;
    }

}