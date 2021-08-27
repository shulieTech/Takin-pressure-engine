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

package io.shulie.flpt.pressure.engine.entity.cloud;

/**
 * @author hezhongqi
 */

public class EngineNotifyParam {

    /**
     * 任务ID
     */
    private Long resultId;

    /**
     * 场景ID
     */
    private Long sceneId;

    /**
     * 客户Id 新增
     */
    private Long customerId;

    /**
     * 状态
     */
    private String status;

    /**
     * 消息
     */
    private String msg;

    /**
     * 开启压测时间，结束压测时间
     */
    private Long time;
    /**
     * pod 序号
     */
    private String podNum;

    /**create Builder method**/
    public static EngineNotifyParam.Builder build (Long sceneId,Long resultId,Long customerId) {
        return new Builder(sceneId,resultId,customerId);
    }


    public EngineNotifyParam(Long resultId, Long sceneId, Long customerId, String status, String msg, Long time,String podNum) {
        this.resultId = resultId;
        this.sceneId = sceneId;
        this.customerId = customerId;
        this.status = status;
        this.msg = msg;
        this.time = time;
        this.podNum = podNum;
    }
    public static class Builder {
        private Long resultId;
        private Long sceneId;
        private Long customerId;
        private String status;
        private String msg;
        private Long time;
        private String podNum;

        Builder(Long sceneId,Long resultId,Long customerId){
            this.sceneId = sceneId;
            this.resultId = resultId;
            this.customerId =customerId;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }
        public Builder msg(String msg) {
            this.msg = msg;
            return this;
        }
        public Builder time(Long time) {
            this.time = time;
            return this;
        }
        public Builder podNum(String podNum) {
           this.podNum = podNum;
            return  this;
        }

        public EngineNotifyParam build() {
            return new EngineNotifyParam(resultId, sceneId, customerId, status,msg,time,podNum);
        }
    }

}
