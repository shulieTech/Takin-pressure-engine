package io.shulie.flpt.pressure.engine.api.ability.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 引擎并发模式能力
 *
 * @author 李鹏
 */
@Getter
@Setter
public class ConcurrencyAbility extends BaseAbility<ConcurrencyAbility> {

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
     * 期望并发数
     */
    private Long expectThroughput;

    public ConcurrencyAbility(String abilityName) {
        super(abilityName);
    }

    public static ConcurrencyAbility build(String abilityName) {
        return new ConcurrencyAbility(abilityName);
    }

}