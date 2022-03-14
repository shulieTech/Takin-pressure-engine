package io.shulie.flpt.pressure.engine.api.plugin;

import java.util.Map;
import java.util.List;

import io.shulie.flpt.pressure.engine.api.enums.EngineType;
import io.shulie.flpt.pressure.engine.api.plugin.response.StopResponse;
import io.shulie.flpt.pressure.engine.api.ability.EnginePressureModeAbility;
import io.shulie.flpt.pressure.engine.api.ability.SupportedPressureModeAbilities;

/**
 * @author xuyh
 */
public interface PressurePlugin {

    /**
     * 压测引擎类型
     *
     * @return {@link EngineType}
     */
    EngineType engineType();

    /**
     * 初始化引擎插件  比如支持dubbo或者kafka的插件
     *
     * @param context 压测上下文
     * @author 李鹏
     */
    default void initializeEnginePlugins(PressureContext context) {}

    /**
     * 解析压测数据
     * 方法用于处理压测数据，比如csv，txt的分片，路径等
     *
     * 如果压测引擎需要处理压测数据需要重写此方法
     *
     *   TODO 这里pressureData的数据结构要抽象成实体类，目前cloud数据结构未统一，只能先使用
     *
     * @param context      压测上下文
     * @param pressureData 压测数据
     * @author 李鹏
     */
    default void doResolvePressureData(PressureContext context, List<Map<String, Object>> pressureData) {}

    /**
     * 初始化压力引擎压力模式能力
     *
     * @return 压力模式能力
     */
    EnginePressureModeAbility initialEnginePressureModeAbility();

    /**
     * 修改压测脚本
     *
     * @param context                        {@link PressureContext}
     * @param supportedPressureModeAbilities 支持的压力模式
     * @return true: 准备成功 false: 失败，停止进程
     */
    boolean doModifyScript(PressureContext context, SupportedPressureModeAbilities supportedPressureModeAbilities);

    /**
     * 执行压测
     *
     * @param context {@link PressureContext}
     */
    void doPressureTest(PressureContext context);

    /**
     * 关闭引擎
     *
     * @param context 压测上下文
     * @return 停止响应
     */
    StopResponse stopPressureTest(PressureContext context);

    /**
     * 压测结束后操作
     *
     * @param context {@link PressureContext}
     * @return true: 结束后操作成功
     */
    default boolean finish(PressureContext context) {
        return context != null;
    }

}