package io.shulie.flpt.pressure.engine.api.entity;

import lombok.Data;

/**
 * 业务活动信息  数据绑定了业务活动对应的tps信息
 *
 * @author 李鹏
 */
@Data
public class BusinessActivity {

    /**
     * 业务活动名称，即元素名称
     */
    private String elementTestName;

    /**
     * 目标TPS
     */
    private String throughputPercent;

    public BusinessActivity(String elementTestName, String throughputPercent) {
        this.elementTestName = elementTestName;
        this.throughputPercent = throughputPercent;
    }

    public static BusinessActivity build(String elementTestName, String throughputPercent) {
        return new BusinessActivity(elementTestName, throughputPercent);
    }
}