package io.shulie.flpt.pressure.engine.api.entity;

import lombok.Data;

import io.shulie.flpt.pressure.engine.api.constants.EngineConstants;
import io.shulie.flpt.pressure.engine.api.annotation.HttpHeaderParamKey;

/**
 * Http请求头参数自动添加参数
 *
 * 如果需要在全局添加http请求头参数，可以在这里添加属性
 *
 * @author 李鹏
 */
@Data
public class HttpHeaderVariables {

    @HttpHeaderParamKey(EngineConstants.HTTP_HEADER_PARAM_KEY_TRACE_ID)
    private String pradarTraceId;

    @HttpHeaderParamKey(EngineConstants.HTTP_HEADER_PARAM_KEY_USERDATA)
    private String pradarUserdata;

    @HttpHeaderParamKey(EngineConstants.HTTP_HEADER_PARAM_KEY_RPC_ID)
    private String pradarRpcId;
}