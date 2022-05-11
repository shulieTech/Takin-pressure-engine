package io.shulie.flpt.pressure.engine.api.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 压测引擎启动配置
 * @Author: liyuanba
 * @Date: 2021/10/29 2:47 下午
 */
@Data
public class EngineRunConfig extends AbstractEntry {
    private String resourceId;
    private Long taskId;
    private Long customerId;
    /**
     * 数据上报地址
     */
    private String consoleUrl;
    /**
     * cloud回调地址
     */
    private String callbackUrl;
    /**
     * 动态tps地址
     */
    private String callDynamicTpsUrl;
    /**
     * csv文件位置回调地址
     */
    private String csvPositionUrl;
    /**
     * 启动的pod数量
     */
    private Integer podCount;
    /**
     * 脚本文件完整路径和文件名
     */
    private String scriptFile;
    /**
     * 脚本文件所在目录
     */
    private String scriptFileDir;
    /**
     * 是否是在本地启动
     */
    private Boolean isLocal;
    /**
     * 调度任务路径
     */
    private String taskDir;
    /**
     * 压测场景：常规，试跑，巡检
     */
    private Integer pressureScene;
    /**
     * 是否是新版，新版标识是压测场景有脚本解析的结果
     */
    private Boolean bindByXpathMd5;
    /**
     * 压测时长
     */
    private Integer continuedTime;
    /**
     * 并发线程数
     */
    private Long expectThroughput;
    /**
     * 压测引擎插件文件位置  一个压测场景可能有多个插件 一个插件也有可能有多个文件
     */
    private List<String> enginePluginsFiles;
    /**
     * 压测配置信息
     */
    private EnginePressureConfig pressureConfig;
    /**
     * 文件
     */
    private List<Map<String, Object>> fileSets;
    /**
     * 业务活动配置的目标信息
     */
    private Map<String, BusinessActivityConfig> businessMap;
    /**
     * 压测引起java虚拟机内存等参数配置
     */
    private String memSetting;

}
