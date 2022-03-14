package io.shulie.flpt.pressure.engine.util;

import com.google.gson.Gson;

/**
 * @author xuyh
 */
public class GsonUtils {
    private static final Gson GSON = new Gson();

    public static String obj2Json(Object obj) {
        return GSON.toJson(obj);
    }

    public static <T> T json2Obj(String json, Class<T> tClass) {
        return GSON.fromJson(json, tClass);
    }

    public static <T> T bytes2Obj(byte[] bytes, Class<T> tClass) {
        return GSON.fromJson(new String(bytes), tClass);
    }
}
