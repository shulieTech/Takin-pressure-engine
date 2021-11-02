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
 * 压测场景枚举
 *
 * @author lipeng
 *
 */
public enum PressureSceneEnum {

//    CONCURRENCY(0, "并发模式"),
//
//    TPS(1, "TPS模式"),
//
//    CUSTOMIZE(2, "自定义模式"),

    DEFAULT(0, "常规模式"),

    FLOW_DEBUG(3,"流量调试"),

    INSPECTION_MODE(4,"巡检模式"),

    TRY_RUN(5, "试跑模式")
    ;

    @Getter
    private int code;

    @Getter
    private String description;

    private static final Map<Integer, PressureSceneEnum> instances = new HashMap<>();

    static {
        for(PressureSceneEnum enginePressureMode : PressureSceneEnum.values()) {
            instances.put(enginePressureMode.getCode(), enginePressureMode);
        }
        //为了兼容老版本数据，将1，2转化为常规模式
        instances.put(1, DEFAULT);
        instances.put(2, DEFAULT);
    }

    PressureSceneEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static PressureSceneEnum value(Integer code) {
        if (null == code) {
            return null;
        }
        return instances.get(code);
    }

}