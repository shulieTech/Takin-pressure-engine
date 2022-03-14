package io.shulie.flpt.pressure.engine.api.ability.model;

/**
 * 脚本调试结果集
 *
 * @author 李鹏
 */
public class TryRunAbility extends BaseAbility<TryRunAbility> {

    /**
     * 试跑支持并发数量
     */
    public static final int[] TRY_RUN_SUPPORTED_CONCURRENT_NUM = {1, 5, 10, 20, 50, 100};

    /**
     * 试跑支持次数
     */
    public static final int[] TRY_RUN_SUPPORTED_TIMES = {1, 10, 100, 1000, 10000};

    /**
     * 调试条数
     */
    private Long loops;

    /**
     * 并发数量
     */
    private Long expectThroughput;

    public Long getLoops() {
        return loops;
    }

    public TryRunAbility setLoops(Long loops) {
        this.loops = loops;
        return this;
    }

    public Long getExpectThroughput() {
        return expectThroughput;
    }

    public TryRunAbility setExpectThroughput(Long expectThroughput) {
        this.expectThroughput = expectThroughput;
        return this;
    }

    public TryRunAbility(String abilityName) {
        super(abilityName);
    }

    public static TryRunAbility build(String abilityName) {
        return new TryRunAbility(abilityName);
    }

}