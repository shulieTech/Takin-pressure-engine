package io.shulie.flpt.pressure.engine.api.enums;

import lombok.Getter;

import java.util.Map;
import java.util.HashMap;

/**
 * 压测场景枚举
 *
 * @author 李鹏
 */
public enum PressureSceneEnum {
    /**
     * 常规模式
     */
    DEFAULT(0, "常规模式"),
    /**
     * 流量调试
     */
    FLOW_DEBUG(3, "流量调试"),
    /**
     * 巡检模式
     */
    INSPECTION_MODE(4, "巡检模式"),
    /**
     * 试跑模式
     */
    TRY_RUN(5, "试跑模式");

    @Getter
    private final int code;

    @Getter
    private final String description;

    private static final Map<Integer, PressureSceneEnum> INSTANCES = new HashMap<>();

    static {
        for (PressureSceneEnum enginePressureMode : PressureSceneEnum.values()) {
            INSTANCES.put(enginePressureMode.getCode(), enginePressureMode);
        }
        //为了兼容老版本数据，将1，2转化为常规模式
        INSTANCES.put(1, DEFAULT);
        INSTANCES.put(2, DEFAULT);
    }

    PressureSceneEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static PressureSceneEnum value(Integer code) {
        if (null == code) {
            return null;
        }
        return INSTANCES.get(code);
    }

}