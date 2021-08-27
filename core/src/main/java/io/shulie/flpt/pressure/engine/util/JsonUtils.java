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

import com.google.gson.Gson;

/**
 * Create by xuyh at 2020/4/18 16:00.
 */
public class JsonUtils {
    private static Gson gson = new Gson();

    public static String obj2Json(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T json2Obj(String json, Class<T> tClass) {
        return gson.fromJson(json, tClass);
    }

    public static <T> T bytes2Obj(byte[] bytes, Class<T> tClass) {
        return gson.fromJson(new String(bytes), tClass);
    }
}
