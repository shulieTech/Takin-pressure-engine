package io.shulie.flpt.pressure.engine.entity.cloud;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * @author 何仲奇
 */
@Getter
@AllArgsConstructor
public enum EngineStatusEnum {
    /**
     * 准备就绪
     */
    READIED("准备就绪", "readied"),
    /**
     * 启动成功
     */
    STARTED("启动成功", "started"),
    /**
     * 启动失败
     */
    START_FAILED("启动失败", "startFail"),
    /**
     * 开始压测
     */
    PRESSURE("开始压测","pressure"),
    /**
     * 中断
     */
    INTERRUPT("中断", "interrupt"),
    /**
     * 中断成功
     */
    INTERRUPT_SUCCEED("中断成功", "interruptSuccess"),
    /**
     * 中断失败
     */
    INTERRUPT_FAILED("中断失败", "interruptFail");

    private final String message;
    private final String status;
}
