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

package io.shulie.flpt.pressure.engine.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Create by xuyh at 2020/4/22 16:03.
 */
public class EngineConfigurations {
    private static Logger logger = LoggerFactory.getLogger(EngineConfigurations.class);

    private static Map<String, Object> propCache = new HashMap<>();

    private static String propFile = System.getProperty("work.dir") + "/conf/pressure-engine.properties";

    static {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(propFile));
            properties.forEach((k, v) -> propCache.put(String.valueOf(k), String.valueOf(v)));
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
        }
    }

    public static String getProperty(String code, String defaultValue) {
        String value = String.valueOf(propCache.get(code));
        if (value == null) {
            return defaultValue;
        }
        return value;
    }
}
