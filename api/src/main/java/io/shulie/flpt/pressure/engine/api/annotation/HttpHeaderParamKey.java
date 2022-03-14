package io.shulie.flpt.pressure.engine.api.annotation;

import io.shulie.flpt.pressure.engine.api.constants.EngineConstants;
import io.shulie.flpt.pressure.engine.api.enums.PressureSceneEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注此注解 可以为HttpHeaderVariables对应的属性设置为HttpHeader参数
 *
 * @author 李鹏
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpHeaderParamKey {

    /**
     * key值  不设置为属性名
     *
     * @return -
     */
    String value() default EngineConstants.EMPTY_TEXT;

    /**
     * 专为某些引擎压测模式的指定参数  不指定则认为是所有模式均可使用
     *
     * @return -
     */
    PressureSceneEnum[] assignForMode() default {};
}