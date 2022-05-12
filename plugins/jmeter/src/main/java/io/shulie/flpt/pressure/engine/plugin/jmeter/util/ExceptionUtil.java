package io.shulie.flpt.pressure.engine.plugin.jmeter.util;

import com.alibaba.fastjson.JSON;
import io.shulie.flpt.pressure.engine.api.annotation.EngineException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

/**
 * ClassName:    ExceptionUtil
 * Package:    io.shulie.takin.drilling.schedule.util
 * Description:
 * Datetime:    2022/5/12   11:53
 * Author:   chenhongqiao@shulie.com
 */
@Slf4j
public class ExceptionUtil {

    @Data
    public static class ExceptionInfo {
        private String clazz;
        private String method;
        private Integer line;
        private String msg;
    }

    public static ExceptionInfo resolvingException(Exception e) {
        ExceptionInfo info = new ExceptionInfo() {{
            setClazz(e.getStackTrace()[0].getClassName());
            setLine(e.getStackTrace()[0].getLineNumber());
            setMethod(e.getStackTrace()[0].getMethodName());
            setMsg(e.getMessage());
        }};
        for (StackTraceElement element : e.getStackTrace()) {
            int line = element.getLineNumber();
            String className = element.getClassName();
            String methodName = element.getMethodName();
            Method method = null;
            try {
                Method[] methods = Class.forName(className).getMethods();
                method = Arrays.stream(methods).filter(m -> Objects.equals(methodName, m.getName())).findFirst().get();
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
            String exceptionMsg = "发生未识别的异常";
            if (Objects.nonNull(method)) {
                EngineException annotation = method.getAnnotation(EngineException.class);
                if (Objects.isNull(annotation)) {
                    continue;
                }
                exceptionMsg = String.format("%s:[%s:%s]", annotation.value(), e.getClass().getName(), e.getMessage());
            }

            String finalExceptionMsg = exceptionMsg;
            info = new ExceptionInfo() {{
                setClazz(className);
                setLine(line);
                setMethod(methodName);
                setMsg(finalExceptionMsg);
            }};
        }
        log.info("new exception info: {}", JSON.toJSONString(info));
        return info;
    }

}
