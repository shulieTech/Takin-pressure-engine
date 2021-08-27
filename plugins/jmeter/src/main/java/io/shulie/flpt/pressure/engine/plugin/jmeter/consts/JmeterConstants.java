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

package io.shulie.flpt.pressure.engine.plugin.jmeter.consts;

import io.shulie.flpt.pressure.engine.common.Constants;

/**
 * @author lipeng
 * @date 2021-08-05 2:59 下午
 */
public class JmeterConstants extends Constants {

    /**
     * 并发线程组 name
     *
     */
    public static final String CONCURRENCY_THREAD_GROUP_NAME = "com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroup";

    /**
     * TPS线程组 name
     *
     */
    public static final String TPS_THREAD_GROUP_NAME = "com.blazemeter.jmeter.threads.arrivals.ArrivalsThreadGroup";

    /**
     * 线程组
     */
    public static final String THREAD_GROUP_NAME = "ThreadGroup";

    /**
     * REDIS 压测实例 格式化串
     */
    public static final String PRESSURE_ENGINE_INSTANCE_REDIS_KEY_FORMAT = "PRESSURE:ENGINE:INSTANCE:%s:%s:%s";

    /**
     * TPS限制数field
     */
    public static final String REDIS_TPS_LIMIT_FIELD = "REDIS_TPS_LIMIT";

    /**
     * REDIS 业务活动吞吐量百分比 KEY 格式化串
     */
    public static final String REDIS_ACTIVITY_PERCENTAGE_KEY_FORMAT = "__REDIS_TPS_LIMIT_KEY_%s_%s_%s_%s_";

    /**
     * REDIS TPS LIMIE KEY 过期时间（秒） 一周
     */
    public static final int REDIS_TPS_LIMIT_KEY_EXPIRES = 7*24*3600;

}
