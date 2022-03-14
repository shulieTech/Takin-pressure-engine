package io.shulie.flpt.pressure.engine.plugin.jmeter.enums;

import io.shulie.flpt.pressure.engine.util.StringUtils;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liyuanba
 */
public enum NodeTypeEnum {
    /**
     * TestPlan
     */
    TEST_PLAN("TestPlan"),
    /**
     * 线程组
     */
    THREAD_GROUP("ThreadGroup"),
    /**
     * 逻辑控制器
     */
    CONTROLLER("Controller"),
    /**
     * 取样器
     */
    SAMPLER("SamplerProxy", "Sampler", "Sample"),
    ;

    @Getter
    private final String[] suffixes;

    NodeTypeEnum(String... suffixes) {this.suffixes = suffixes;}

    public static final Map<String, NodeTypeEnum> POOL = new HashMap<>();

    static {
        for (NodeTypeEnum e : NodeTypeEnum.values()) {
            for (String suffix : e.getSuffixes()) {
                if (POOL.containsKey(suffix)) {
                    continue;
                }
                POOL.put(suffix, e);
            }
        }
    }

    public static NodeTypeEnum value(String nodeName) {
        if (StringUtils.isBlank(nodeName)) {
            return null;
        }
        for (String suffix : POOL.keySet()) {
            if (nodeName.endsWith(suffix)) {
                return POOL.get(suffix);
            }
        }
        return null;
    }

    public boolean equals(String name) {
        NodeTypeEnum type = NodeTypeEnum.value(name);
        if (null == type) {
            return false;
        }
        return type == this;
    }
}
