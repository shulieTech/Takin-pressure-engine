package io.shulie.flpt.pressure.engine.api.enums;

import lombok.Getter;

import java.util.Map;
import java.util.HashMap;

/**
 * 线程组类型
 *
 * @author liyuanba
 */
public enum ThreadGroupTypeEnum {
    /**
     * 并发模式
     */
    CONCURRENCY(0, "并发模式"),
    /**
     * TPS模式
     */
    TPS(1, "TPS模式"),
    /**
     * 自定义模式
     */
    CUSTOMIZE(2, "自定义模式"),
    ;
    @Getter
    private final int code;
    @Getter
    private final String description;

    ThreadGroupTypeEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    private static final Map<Integer, ThreadGroupTypeEnum> POOL = new HashMap<>();

    static {
        for (ThreadGroupTypeEnum e : ThreadGroupTypeEnum.values()) {
            POOL.put(e.getCode(), e);
        }
    }

    public static ThreadGroupTypeEnum value(Integer code) {
        if (null == code) {return null;}
        return POOL.get(code);
    }
}
