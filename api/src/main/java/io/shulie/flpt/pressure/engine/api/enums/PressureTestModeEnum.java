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

package io.shulie.flpt.pressure.engine.api.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Create by xuyh at 2020/5/12 20:45.
 */
public enum PressureTestModeEnum {

    /**
     * 固定压力值
     */
    FIXED(1,"固定压力值"),
    /**
     * 线性递增
     */
    LINEAR(2,"线性递增"),
    /**
     * 阶梯递增
     */
    STAIR(3,"阶梯递增"),

    ;

    @Getter
    private int code;
    @Getter
    private String description;

    PressureTestModeEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    private static final Map<Integer, PressureTestModeEnum> pool = new HashMap<>();
    static {
        for (PressureTestModeEnum e : PressureTestModeEnum.values()) {
            pool.put(e.code, e);
        }
    }

    public static PressureTestModeEnum value(Integer code) {
        if (null == code) {
            return FIXED;
        }
        PressureTestModeEnum mode = pool.get(code);
        if (null == mode) {
            mode = FIXED;
        }
        return mode;
    }

    @Override
    public String toString() {
        return "PressureTestMode{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

}