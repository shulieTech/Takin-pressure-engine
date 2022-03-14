package io.shulie.flpt.pressure.engine.plugin.jmeter.script;

import lombok.Data;

/**
 * 节点属性
 *
 * @author 李鹏
 */
@Data
public class ElementProp {

    private String propName;

    private String name;

    private String value;

    private ElementProp(String propName, String name, String value) {
        this.propName = propName;
        this.name = name;
        this.value = value;
    }

    public static ElementProp create(String propName, String name, String value) {
        return new ElementProp(propName, name, value);
    }

}
