package io.shulie.flpt.pressure.engine.plugin.jmeter.consts;

import io.shulie.flpt.pressure.engine.common.Constants;

/**
 * @author 李鹏
 */
public class JmeterConstants extends Constants {

    /**
     * 并发线程组 name
     */
    public static final String CONCURRENCY_THREAD_GROUP_NAME = "com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroup";

    /**
     * TPS线程组 name
     */
    public static final String TPS_THREAD_GROUP_NAME = "com.blazemeter.jmeter.threads.arrivals.ArrivalsThreadGroup";
    /**
     * 新的TPS线程组
     */
    public static final String TPS_NEW_THREAD_GROUP_NAME = "com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroup";

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
     * REDIS TPS LIMIT KEY 过期时间（秒） 一周
     */
    public static final int REDIS_TPS_LIMIT_KEY_EXPIRES = 7 * 24 * 3600;

}
