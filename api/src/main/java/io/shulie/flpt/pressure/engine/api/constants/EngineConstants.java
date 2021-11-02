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

package io.shulie.flpt.pressure.engine.api.constants;

/**
 * 常量
 *
 * @author lipeng
 * @date 2021-05-07 5:00 下午
 */
public interface EngineConstants {

    // 空字符串
    String EMPTY_TEXT = "";
    /**
     * testname和md5之间的分割符
     */
    String TEST_NAME_MD5_SPLIT = "@MD5:";

    // traceId变量值
    String GENERATE_TRACE_ID_VALUE = "${pradarTraceId}";

    //全局参数 - 场景ID key
    String GLOBAL_PARAM_KEY_SCENE_ID = "__ENGINE_SCENE_ID__";

    //全局参数 - 报告ID key
    String GLOBAL_PARAM_KEY_REPORT_ID = "__ENGINE_REPORT_ID__";

    //全局参数 - 客户ID key
    String GLOBAL_PARAM_KEY_CUSTOMER_ID = "__ENGINE_CUSTOMER_ID__";

    //全局参数 - 引擎压测模式 key
    String GLOBAL_PARAM_KEY_PRESSURE_MODE = "__ENGINE_PRESSURE_MODE__";

    //全局参数 - cloud回调地址 key
    String GLOBAL_PARAM_KEY_CLOUD_CALLBACK_URL = "__TAKIN_CLOUD_CALLBACK_URL__";

    //全局参数 - csv开始位点
    String GLOBAL_PARAM_KEY_CSV_FILE_START_POSITION = "__ENGINE_CSV_FILE_STARTPOSITION__";

    //全局参数 - csv结束位点
    String GLOBAL_PARAM_KEY_CSV_FILE_STOP_POSITION = "__ENGINE_CSV_FILE_STOPPOSITION__";

    //全局参数 - 位点信息
    String GLOBAL_PARAM_KEY_POSITION_ALL = "__ENGINE_GLOBAL_VARIABLES__";

    //Http请求头参数 - traceId
    String HTTP_HEADER_PARAM_KEY_TRACE_ID = "p-pradar-traceid";

    //Http请求头参数 - userdata 存放报告id
    String HTTP_HEADER_PARAM_KEY_USERDATA = "p-pradar-userdata";

    //Http请求头参数 - rpcId
    String HTTP_HEADER_PARAM_KEY_RPC_ID = "p-pradar-rpcid";

}