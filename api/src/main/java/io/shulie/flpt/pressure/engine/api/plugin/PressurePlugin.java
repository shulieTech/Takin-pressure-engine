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

package io.shulie.flpt.pressure.engine.api.plugin;

import io.shulie.flpt.pressure.engine.api.ability.EnginePressureModeAbility;
import io.shulie.flpt.pressure.engine.api.ability.SupportedPressureModeAbilities;
import io.shulie.flpt.pressure.engine.api.entity.DataFile;
import io.shulie.flpt.pressure.engine.api.enums.EngineType;
import io.shulie.flpt.pressure.engine.api.plugin.response.StopResponse;

import java.util.List;
import java.util.Map;

/**
 * Create by xuyh at 2020/4/19 22:24.
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
     * @author lipeng
     * @param context
     * @return
     */
    default void initializeEnginePlugins(PressureContext context) {};

    /**
     * 解析压测数据
     *   方法用于处理压测数据，比如csv，txt的分片，路径等
     *
     *   如果压测引擎需要处理压测数据需要重写此方法
     *
     *   TODO 这里pressureData的数据结构要抽象成实体类，目前cloud数据结构未统一，只能先使用
     *
     * @param pressureData
     *
     * @author lipeng
     */
    default void doResolvePressureData(PressureContext context, List<Map<String, Object>> pressureData) {};

    /**
     * 初始化压力引擎压力模式能力
     *
     * @return
     */
    EnginePressureModeAbility initialEnginePressureModeAbility();

    /**
     * 修改压测脚本
     *
     * @param context {@link PressureContext}
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
     */
    StopResponse stopPressureTest(PressureContext context);

    /**
     * 压测结束后操作
     *
     * @param context {@link PressureContext}
     * @return true: 结束后操作成功
     */
    default boolean finish(PressureContext context) {return true;};

}