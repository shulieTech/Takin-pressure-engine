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
 * 引擎并发模式能力
 *
 * @author lipeng
 * @date 2021-07-29 4:34 下午
 */
public class ConcurrencyAbility extends BaseAbility<ConcurrencyAbility> {

    //递增时长
    private Long rampUp;

    //递增步长
    private Long steps;

    //增长后持续时长 单位：秒
    private Long holdTime;

    //期望并发数
    private Long expectThroughput;

    public Long getRampUp() {
        return rampUp;
    }

    public ConcurrencyAbility setRampUp(Long rampUp) {
        this.rampUp = rampUp;
        return this;
    }

    public Long getSteps() {
        return steps;
    }

    public ConcurrencyAbility setSteps(Long steps) {
        this.steps = steps;
        return this;
    }

    public Long getHoldTime() {
        return holdTime;
    }

    public ConcurrencyAbility setHoldTime(Long holdTime) {
        this.holdTime = holdTime;
        return this;
    }

    public Long getExpectThroughput() {
        return expectThroughput;
    }

    public ConcurrencyAbility setExpectThroughput(Long expectThroughput) {
        this.expectThroughput = expectThroughput;
        return this;
    }

    public ConcurrencyAbility(String abilityName) {
        super(abilityName);
    };

    public static ConcurrencyAbility build(String abilityName) {
        return new ConcurrencyAbility(abilityName);
    }

}