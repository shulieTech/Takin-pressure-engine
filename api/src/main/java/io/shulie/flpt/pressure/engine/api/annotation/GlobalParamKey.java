package io.shulie.flpt.pressure.engine.api.annotation;

import io.shulie.flpt.pressure.engine.api.enums.PressureSceneEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注此注解 可以为GlobalUserVariables对应的属性设置为全局的参数key
 *
 * @author 李鹏
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GlobalParamKey {

    /**
     * 参数key
     *
     * @return value
     */
    String value() default "";

    /**
     * 专为某些引擎压测模式的指定参数  不指定则认为是所有模式均可使用
     *
     * @return -
     */
    PressureSceneEnum[] assignForMode() default {};

}