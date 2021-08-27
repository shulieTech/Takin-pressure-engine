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

package io.shulie.flpt.pressure.engine.api.entity;

import io.shulie.flpt.pressure.engine.api.annotation.HttpHeaderParamKey;
import io.shulie.flpt.pressure.engine.api.constants.EngineConstants;

/**
 * Http请求头参数自动添加参数
 *
 * 如果需要在全局添加http请求头参数，可以在这里添加属性
 *
 * @author lipeng
 * @date 2021-05-07 4:56 下午
 */
public class HttpHeaderVariables {

    @HttpHeaderParamKey(EngineConstants.HTTP_HEADER_PARAM_KEY_TRACE_ID)
    private String pradarTraceId;

    @HttpHeaderParamKey(EngineConstants.HTTP_HEADER_PARAM_KEY_USERDATA)
    private String pradarUserdata;

    @HttpHeaderParamKey(EngineConstants.HTTP_HEADER_PARAM_KEY_RPC_ID)
    private String pradarRpcId;

    public String getPradarTraceId() {
        return pradarTraceId;
    }

    public void setPradarTraceId(String pradarTraceId) {
        this.pradarTraceId = pradarTraceId;
    }

    public String getPradarUserdata() {
        return pradarUserdata;
    }

    public void setPradarUserdata(String pradarUserdata) {
        this.pradarUserdata = pradarUserdata;
    }

    public String getPradarRpcId() {
        return pradarRpcId;
    }

    public void setPradarRpcId(String pradarRpcId) {
        this.pradarRpcId = pradarRpcId;
    }
}