package io.shulie.flpt.pressure.engine.api.ability.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 巡检能力
 *
 * @author 李鹏
 */
@Getter
@Setter
@Accessors(chain = true)
public class InspectionAbility extends BaseAbility<InspectionAbility> {
    /**
     * 调试次数  默认1
     */
    private Long loops = 1L;

    public InspectionAbility(String abilityName) {
        super(abilityName);
    }

    public static InspectionAbility build(String abilityName) {
        return new InspectionAbility(abilityName);
    }

}