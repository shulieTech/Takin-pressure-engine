package io.shulie.flpt.pressure.engine.api.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务活动配置
 *
 * @author 杨俊毅
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessActivityConfig extends AbstractEntry {
    /**
     * 绑定关系
     */
    private String bindRef;
    /**
     * 业务活动名称
     */
    private String activityName;
    /**
     * 业务指标，目标rt
     */
    private Integer rt;
    /**
     * 业务指标，目标tps
     */
    private Integer tps;
    /**
     * 业务目标tps占总的tps百分比
     */
    private Double rate;
}
