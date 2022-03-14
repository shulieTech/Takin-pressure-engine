package io.shulie.flpt.pressure.engine.api.ability.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 流量调试能力
 *
 * @author 李鹏
 */
@Getter
@Setter
public class FlowDebugAbility extends BaseAbility<FlowDebugAbility> {

    /**
     * 调试次数  默认1000
     */
    private Long loops = 1000L;

    public FlowDebugAbility(String abilityName) {
        super(abilityName);
    }

    public static FlowDebugAbility build(String abilityName) {
        return new FlowDebugAbility(abilityName);
    }

}