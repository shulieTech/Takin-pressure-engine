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
 * 脚本调试结果集
 *
 * @author lipeng
 * @date 2021-08-02 10:23 上午
 */
public class TryRunAbility extends BaseAbility<TryRunAbility> {

    //试跑支持并发数量
    public static final int[] TRY_RUN_SUPPORTED_CONURRENT_NUM = {1, 5, 10, 20, 50, 100};

    //试跑支持次数
    public static final int[] TRY_RUN_SUPPORTED_TIMES = {1, 10, 100, 1000, 10000};

    //调试条数
    private Long loops;

    //并发数量
    private Long expectThroughput;

    public Long getLoops() {
        return loops;
    }

    public TryRunAbility setLoops(Long loops) {
        this.loops = loops;
        return this;
    }

    public Long getExpectThroughput() {
        return expectThroughput;
    }

    public TryRunAbility setExpectThroughput(Long expectThroughput) {
        this.expectThroughput = expectThroughput;
        return this;
    }

    public TryRunAbility(String abilityName) {
        super(abilityName);
    }

    public static TryRunAbility build(String abilityName) {
        return new TryRunAbility(abilityName);
    }

}