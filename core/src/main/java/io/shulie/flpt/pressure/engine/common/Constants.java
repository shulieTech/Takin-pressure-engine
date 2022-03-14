package io.shulie.flpt.pressure.engine.common;

/**
 * @author xuyh
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

}
