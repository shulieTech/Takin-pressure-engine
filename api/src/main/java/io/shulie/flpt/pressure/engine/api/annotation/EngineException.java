package io.shulie.flpt.pressure.engine.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注此注解 解析方法具体异常信息&上报异常信息
 *
 * @author 李鹏
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EngineException {

    /**
     * 参数具体异常信息描述
     *
     * @return
     */
    String value() default "压测引擎异常";
}