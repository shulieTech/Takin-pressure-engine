package io.shulie.flpt.pressure.engine.api.ability.model;

import java.util.Map;
import java.util.HashMap;

import lombok.Data;

/**
 * 能力基类
 *
 * @author 李鹏
 */
@Data
public abstract class BaseAbility<E> {

    public BaseAbility(String abilityName) {
        this.abilityName = abilityName;
    }

    /**
     * 名称
     */
    private String abilityName;

    /**
     * 额外属性
     */
    private final Map<String, String> extraAttributes = new HashMap<>();

    public Map<String, String> getExtraAttributes() {
        return extraAttributes;
    }

    public E addExtraAttribute(String attributeName, String attributeValue) {
        this.extraAttributes.put(attributeName, attributeValue);
        return (E)this;
    }

    public E addAllExtraAttributes(Map<String, String> attributes) {
        if (attributes != null && attributes.size() > 0) {
            this.extraAttributes.putAll(attributes);
        }
        return (E)this;
    }

}