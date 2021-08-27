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

/**
 * Create by xuyh at 2020/5/12 20:45.
 */
public enum PressureTestMode {

    FIXED("fixed", "固定压力值"),
    LINEAR("linear", "线性递增"),
    STAIR("stair", "阶梯递增");

    private String code;
    private String description;

    PressureTestMode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static PressureTestMode getMode(String code) {
        for (PressureTestMode pressureTestMode : PressureTestMode.values()) {
            if (pressureTestMode.getCode().equalsIgnoreCase(code)) {
                return pressureTestMode;
            }
        }
        return PressureTestMode.FIXED;
    }

    @Override
    public String toString() {
        return "PressureTestMode{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

}