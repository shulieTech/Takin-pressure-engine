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

import java.util.HashMap;
import java.util.Map;

/**
 * 引擎压测模式
 *
 * @author lipeng
 *
 */
public enum EnginePressureMode {

    CONCURRENCY("0", "并发模式"),

    TPS("1", "TPS模式"),

    CUSTOMIZE("2", "自定义模式"),

    FLOW_DEBUG("3","流量调试"),

    INSPECTION_MODE("4","巡检模式"),

    TRY_RUN("5", "试跑模式")
    ;

    private String code;

    private String description;

    private static final Map<String, EnginePressureMode> instances = new HashMap<>();

    static {
        for(EnginePressureMode enginePressureMode : EnginePressureMode.values()) {
            instances.put(enginePressureMode.getCode(), enginePressureMode);
        }
    }

    EnginePressureMode(String code, String description) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static EnginePressureMode getMode(String code) {
        return instances.get(code);
    }

}