package io.shulie.flpt.pressure.engine.util;

/**
 * @author xuyh
 */
public class StringUtils {
    public static String formatStr(String format, String... args) {
        return String.format(format, args);
    }

    public static boolean isBlank(String s) {
        int strLen;
        if (null == s || (strLen = s.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(String s) {
        return !isBlank(s);
    }

    public static String removePoint(String source) {
        if (source.contains(".")) {
            return source.substring(0, source.indexOf("."));
        }
        return source;
    }

    public static String valueOf(Object o) {
        if (null == o) {
            return "";
        }
        if (o instanceof String) {
            return (String)o;
        }
        return String.valueOf(o);
    }
}
