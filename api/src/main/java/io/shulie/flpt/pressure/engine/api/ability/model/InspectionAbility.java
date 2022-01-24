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

import lombok.Data;

/**
 * 巡检能力
 *
 * @author lipeng
 * @date 2021-08-02 5:00 下午
 */
public class InspectionAbility extends BaseAbility<InspectionAbility> {

    /**
     * 巡检固定定时器配置的周期
     */
    private Long fixTimer;
    /**
     * 运行时间
     */
    private Integer duration;

    public InspectionAbility(String abilityName) {
        super(abilityName);
    }
    public Long getFixTimer() {
        return fixTimer;
    }
    public InspectionAbility setFixTimer(Long fixTimer) {
        this.fixTimer = fixTimer;
        return this;
    }

    public Integer getDuration() {
        return duration;
    }

    public InspectionAbility setDuration(Integer duration) {
        this.duration = duration;
        return this;
    }

    public static InspectionAbility build(String abilityName) {
        return new InspectionAbility(abilityName);
    }

}