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

package io.shulie.flpt.pressure.engine.api.plugin.response;

/**
 * 关闭后返回体
 *
 * @author lipeng
 * @date 2021-07-27 4:37 下午
 */
public class StopResponse {

    /**
     * 进程返回值
     *
     */
    private int exitValue;

    /**
     * 返回的消息
     *
     */
    private String message;

    public int getExitValue() {
        return exitValue;
    }

    public StopResponse setExitValue(int exitValue) {
        this.exitValue = exitValue;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public StopResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public StopResponse(int exitValue, String message) {
        this.exitValue = exitValue;
        this.message = message;
    }

    public static StopResponse build(int exitValue, String message) {
        return new StopResponse(exitValue, message);
    }

}