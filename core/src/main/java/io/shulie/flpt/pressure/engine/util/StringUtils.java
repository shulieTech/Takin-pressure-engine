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

package io.shulie.flpt.pressure.engine.util;

/**
 * Create by xuyh at 2020/4/20 11:03.
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
}
