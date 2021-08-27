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
 * Create by xuyh at 2020/4/19 22:29.
 */
public enum EngineType {

    JMETER("jmeter"),

    HTTP_RUNNER("httpRunner"),

    LOAD_RUNNER("loadRunner"),

    APACHE_AB("apacheAb");

    private String type;

    EngineType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "EngineType{" +
                "type='" + type + '\'' +
                '}';
    }

    public static EngineType getByType(String type) {
        for (EngineType engineType : EngineType.values()) {
            if (engineType.getType().equals(type)) {
                return engineType;
            }
        }
        return JMETER;
    }

}