package io.shulie.flpt.pressure.engine.plugin.jmeter.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.function.Function;

/**
 * 公用但不好归属的静态方法
 *
 * @author liyuanba
 */
@SuppressWarnings("unused")
public class CommonUtil {

    public static <T> boolean contains(T[] arr, T t) {
        if (null == arr || arr.length <= 0) {
            return false;
        }
        if (null == t) {
            return false;
        }
        return ArrayUtils.contains(arr, t);
    }

    /**
     * 从map中获取值
     */
    public static <T, R> R getFromMap(Map<T, R> map, T key) {
        if (null == map || null == key) {
            return null;
        }
        return map.get(key);
    }

    /**
     * 从list中取出对象某个字段的值
     */
    public static <T, R> List<R> getList(Collection<T> list, Function<T, R> func) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.stream().filter(Objects::nonNull)
            .map(func)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    /**
     * 取值选择器
     *
     * @param defValue 默认值
     * @param t        取值对象
     * @param func     取值方法，从对象中取值的方法
     * @param <T>      取值对象类型
     * @param <R>      返回值对象类型
     */
    public static <T, R> R getValue(R defValue, T t, Function<T, R> func) {
        R result = defValue;
        if (null != t) {
            R r = func.apply(t);
            if (null != r) {
                if (r instanceof String) {
                    if (StringUtils.isNotBlank((String)r)) {
                        result = r;
                    }
                } else if (r instanceof List) {
                    if (CollectionUtils.isNotEmpty((List<?>)r)) {
                        result = r;
                    }
                } else if (r instanceof Map) {
                    if (MapUtils.isNotEmpty((Map<?, ?>)r)) {
                        result = r;
                    }
                } else {
                    result = r;
                }
            }
        }
        return result;
    }
}
