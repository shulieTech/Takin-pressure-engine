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

package io.shulie.flpt.pressure.engine.plugin.jmeter.script;

/**
 * 节点属性
 *
 * @author lipeng
 * @date 2021-05-07 3:53 下午
 */
public class ElementProp {

    private String propName;

    private String name;

    private String value;

    public String getPropName() {
        return propName;
    }

    public void setPropName(String propName) {
        this.propName = propName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private ElementProp(String propName, String name, String value) {
        this.propName = propName;
        this.name = name;
        this.value = value;
    }

    public static ElementProp create(String propName, String name, String value) {
        return new ElementProp(propName, name, value);
    }

}
