package io.shulie.flpt.pressure.engine.api.enums;

import lombok.Getter;
import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.HashMap;

/**
 * @author xuyh
 */
@Getter
@AllArgsConstructor
public enum PressureTestModeEnum {

    /**
     * 固定压力值
     */
    FIXED(1, "固定压力值"),
    /**
     * 线性递增
     */
    LINEAR(2, "线性递增"),
    /**
     * 阶梯递增
     */
    STAIR(3, "阶梯递增"),

    ;

    private final int code;
    private final String description;

    private static final Map<Integer, PressureTestModeEnum> POOL = new HashMap<>();

    static {
        for (PressureTestModeEnum e : PressureTestModeEnum.values()) {
            POOL.put(e.code, e);
        }
    }

    public static PressureTestModeEnum value(Integer code) {
        if (null == code) {
            return FIXED;
        }
        PressureTestModeEnum mode = POOL.get(code);
        if (null == mode) {
            mode = FIXED;
        }
        return mode;
    }
}