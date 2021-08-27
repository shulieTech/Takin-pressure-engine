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

package io.shulie.flpt.pressure.engine.api.ability.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 能力基类
 *
 * @author lipeng
 * @date 2021-08-02 3:00 下午
 */
public abstract class BaseAbility<E> {

    public BaseAbility(String abilityName) {
        this.abilityName = abilityName;
    }

    //名称
    private String abilityName;

    //额外属性
    private Map<String, String> extraAttributes = new HashMap<>();

    public String getAbilityName() {
        return abilityName;
    }

    public void setAbilityName(String abilityName) {
        this.abilityName = abilityName;
    }

    public Map<String, String> getExtraAttributes() {
        return extraAttributes;
    }

    public E addExtraAttribute(String attributeName, String attributeValue) {
        this.extraAttributes.put(attributeName, attributeValue);
        return (E) this;
    }

    public E addAllExtraAttributes(Map<String, String> attrubutes) {
        if(attrubutes != null && attrubutes.size() > 0) {
            attrubutes.forEach((k, v) -> {
                this.extraAttributes.put(k, v);
            });
        }
        return (E) this;
    }

}