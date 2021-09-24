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

package io.shulie.flpt.pressure.engine.common;

/**
 * Create by xuyh at 2020/4/20 10:55.
 */
public class Constants {
    /**
     * work.dir
     */
    public static final String WORK_DIR_KEY = "work.dir";
    /**
     * current.pid
     */
    public static final String CURRENT_PID_KEY = "current.pid";
    /**
     * task.log.dir
     */
    public static final String TASK_LOG_DIR_KEY = "task.log.dir";

    /**
     * task.jtl.dir
     */
    public static final String TASK_JTL_DIR_KEY = "task.jtl.dir";
    /**
     * 任务目录
     * <pre>
     *     "/home/opt/flpt/pressure-task"
     * </pre>
     */
    public static String PRESSURE_TASK_DIR = EngineConfigurations
            .getProperty("pressure.task.dir", "/home/opt/flpt/pressure-task");

    /**
     * 日志目录
     */
    public static String PRESSURE_LOG_DIR = EngineConfigurations
            .getProperty("pressure.log.dir", "/home/opt/flpt/pressure-task");
    /**
     * 压测引擎目录
     * <pre>
     *     "/home/opt/flpt/pressure-engine"
     * </pre>
     */
    public static String PRESSURE_ENGINE_INSTALL_DIR = EngineConfigurations
            .getProperty("pressure.engine.install.dir", "/home/opt/flpt/pressure-engine");

    /**
     * 没有获取配置信息中的tro交互地址，取这个本地配置地址
     */
    public static String TAKIN_TRO_URL = EngineConfigurations.getProperty("tro.url", "http://asas");

    /**
     * 引擎NFS挂载路径
     */
    public static final String ENGINE_NFS_MOUNTED_PATH = "/etc/engine/script";

    /**
     * TPS模式并发数限制 500  TODO 改为根据cloud计算结果传过来
     */
    public static final String TPS_MODE_CONCURRENCY_LIMIT = "500";

    /**
     * 并发线程组 name
     *
     */
    public static final String CONCURRENCY_THREAD_GROUP_NAME = "com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroup";


}
