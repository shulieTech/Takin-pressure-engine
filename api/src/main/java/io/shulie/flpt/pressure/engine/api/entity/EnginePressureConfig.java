package io.shulie.flpt.pressure.engine.api.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * @Author: liyuanba
 * @Date: 2021/10/29 3:11 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class EnginePressureConfig extends AbstractEntry {
    /**
     * 后端监听器对列长度
     */
    private String pressureEngineBackendQueueCapacity;
    /**
     * redis库
     */
    private Integer engineRedisDatabase;
    /**
     * redis地址
     */
    private String engineRedisAddress;
    /**
     * redis端口
     */
    private String engineRedisPort;
    /**
     * redis限流节点
     */
    private String engineRedisSentinelNodes;
    /**
     * redis限流主机
     */
    private String engineRedisSentinelMaster;
    /**
     * redis密码
     */
    private String engineRedisPassword;
    /**
     * 固定定时器配置的周期
     */
    private Long fixedTimer;
    /**
     * 循环次数
     */
    private Long loopsNum;
    /**
     * 采样率
     */
    private Integer traceSampling;
    /**
     * jtl日志配置
     */
    private EnginePtlLogConfig ptlLogConfig;
    /**
     * zk地址
     */
    private String zkServers;
    /**
     * 日志队列大小
     */
    private Integer logQueueSize;
    /**
     * 各线程组施压配置
     */
    private Map<String, ThreadGroupConfig> threadGroupConfigMap;
    /**
     * 各业务活动施压目标配置
     */
    private List<Map<String, String>> businessActivities;
    /**
     * 总的施压tps目标
     */
    private Double tpsTargetLevel;
    /**
     * tps类型下，采用哪种具体的tps方式
     */
    private Integer tpsThreadMode;
    /**
     * tps上浮因子
     */
    private Double tpsTargetLevelFactor;
    /**
     * 最大线程数
     */
    private Integer maxThreadNum;

}
