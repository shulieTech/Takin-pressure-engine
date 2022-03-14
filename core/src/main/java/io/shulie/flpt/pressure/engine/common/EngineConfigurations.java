package io.shulie.flpt.pressure.engine.common;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.util.Properties;
import java.io.FileInputStream;

/**
 * @author xuyh
 */
@Slf4j
public class EngineConfigurations {
    private static final Map<String, Object> PROP_CACHE = new HashMap<>();

    private static final String PROP_FILE = System.getProperty("work.dir") + "/conf/pressure-engine.properties";

    static {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(PROP_FILE));
            properties.forEach((k, v) -> PROP_CACHE.put(String.valueOf(k), String.valueOf(v)));
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
    }

    public static String getProperty(String code, String defaultValue) {
        String value = String.valueOf(PROP_CACHE.get(code));
        if (value == null) {
            return defaultValue;
        }
        return value;
    }
}
