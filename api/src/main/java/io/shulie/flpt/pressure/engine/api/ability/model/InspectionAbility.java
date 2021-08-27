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

package io.shulie.flpt.pressure.engine.api.ability.model;

/**
 * 巡检能力
 *
 * @author lipeng
 * @date 2021-08-02 5:00 下午
 */
public class InspectionAbility extends BaseAbility<InspectionAbility> {

    //调试次数  默认1
    private Long loops = 1L;

    public InspectionAbility(String abilityName) {
        super(abilityName);
    };

    public Long getLoops() {
        return loops;
    }

    public InspectionAbility setLoops(Long loops) {
        this.loops = loops;
        return this;
    }

    public static InspectionAbility build(String abilityName) {
        return new InspectionAbility(abilityName);
    }

}