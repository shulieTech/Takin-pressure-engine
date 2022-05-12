package io.shulie.flpt.pressure.engine.plugin.jmeter.util;

import io.shulie.flpt.pressure.engine.api.annotation.EngineException;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * ClassName:    ExceptionUtil
 * Package:    io.shulie.takin.drilling.schedule.util
 * Description:
 * Datetime:    2022/5/12   11:53
 * Author:   chenhongqiao@shulie.com
 */
public class ExceptionUtil {

    @Data
    public static class ExceptionInfo {
        private String clazz;
        private String method;
        private Integer line;
        private String msg;
    }

    public static ExceptionInfo resolvingException(Exception e) {
        for (StackTraceElement element : e.getStackTrace()) {
            int line = element.getLineNumber();
            String className = element.getClassName();
            String methodName = element.getMethodName();
            Method method = null;
            try {
                method = Class.forName(className).getMethod(methodName);
            } catch (ClassNotFoundException | NoSuchMethodException ex) {
                ex.printStackTrace();
            }
            String exceptionMsg = "发生未识别的异常";
            if (Objects.nonNull(method)) {
                EngineException annotation = method.getAnnotation(EngineException.class);
                if (Objects.isNull(annotation)) {
                    continue;
                }
                exceptionMsg = String.format("%s:[%s:%s]", annotation.value(), e.getClass().toString(), e.getMessage());
            }

            String finalExceptionMsg = exceptionMsg;
            return new ExceptionInfo() {{
                setClazz(className);
                setLine(line);
                setMethod(methodName);
                setMsg(finalExceptionMsg);
            }};
        }
        return new ExceptionInfo() {{
            setClazz(e.getStackTrace()[0].getClassName());
            setLine(e.getStackTrace()[0].getLineNumber());
            setMethod(e.getStackTrace()[0].getMethodName());
            setMsg(e.getMessage());
        }};
    }

}
