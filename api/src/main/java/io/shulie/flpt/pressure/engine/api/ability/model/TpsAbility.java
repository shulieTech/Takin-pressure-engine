package io.shulie.flpt.pressure.engine.api.ability.model;

import lombok.Getter;
import lombok.Setter;

/**
 * TPS模式能力
 *
 * @author lipeng
 */
@Getter
@Setter
public class TpsAbility extends BaseAbility<TpsAbility> {

    /**
     * 递增时长
     */
    private Long rampUp;

    /**
     * 递增步长
     */
    private Long steps;

    /**
     * 增长后持续时长 单位：秒
     */
    private Long holdTime;

    /**
     * 目标tps
     */
    private String targetTps;

    public TpsAbility(String abilityName) {
        super(abilityName);
    }

    public static TpsAbility build(String abilityName) {
        return new TpsAbility(abilityName);
    }

}