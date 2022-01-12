package io.shulie.flpt.pressure.engine.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumberUtils {
    private static final Logger logger = LoggerFactory.getLogger(NumberUtils.class);

    public static double divide(long b, long d) {
        if (d == 0) {
            return 0d;
        }
        return ((double) b)/d;
    }

    public static int parseInt(Object value) {
        return parseInt(value, 0);
    }

    public static Integer parseInt(Object obj, Integer defValue) {
        String value = StringUtils.valueOf(obj);
        if (StringUtils.isBlank(value)) {
            return defValue;
        }
        if (value.contains(".")) {
            value = StringUtils.removePoint(value);
        }
        Integer v = defValue;
        try {
            v = Integer.parseInt(value);
        } catch (Exception e) {
            logger.error("parseInt failed!value="+value, e);
        }
        return v;
    }

    public static double parseDouble(Object value) {
        return parseDouble(value, 0d);
    }

    public static Double parseDouble(Object obj, Double defValue) {
        String value = StringUtils.valueOf(obj);
        if (StringUtils.isBlank(value)) {
            return defValue;
        }
        double v = defValue;
        try {
            v = Double.parseDouble(value);
        } catch (Exception e) {
            logger.error("parseDouble failed!value="+value, e);
        }
        return v;
    }
}
