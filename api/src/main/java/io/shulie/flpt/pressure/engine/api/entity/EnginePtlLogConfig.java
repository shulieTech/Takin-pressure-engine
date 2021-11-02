package io.shulie.flpt.pressure.engine.api.entity;

import lombok.Data;

/**
 * @Author: liyuanba
 * @Date: 2021/10/29 5:19 下午
 */
@Data
public class EnginePtlLogConfig extends AbstractEntry {
    /**
     * 是否输出ptl日志文件
     */
    private Boolean ptlFileEnable;

    /**
     * ptl日志文件是否只输出错误信息
     */
    private Boolean ptlFileErrorOnly;

    /**
     * ptl日志是否只输出接口调用时间较长信息
     */
    private Boolean ptlFileTimeoutOnly;

    /**
     * ptl日志接口超时阈值
     */
    private Long timeoutThreshold;

    /**
     * ptl日志是否截断
     */
    private Boolean logCutOff;
}