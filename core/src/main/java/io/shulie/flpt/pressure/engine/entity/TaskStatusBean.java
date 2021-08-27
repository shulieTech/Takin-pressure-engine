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

package io.shulie.flpt.pressure.engine.entity;

/**
 * Create by xuyh at 2020/4/18 19:16.
 */
public class TaskStatusBean {
    private String pid;//压测任务进程号
    private Long startTime;//任务开始压测时间（时间戳）
    private Long currentTime;//上报点时间戳
    private String status;//压测任务状态
    private String message;//消息

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(Long currentTime) {
        this.currentTime = currentTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "TaskStatusBean{" +
                "pid='" + pid + '\'' +
                ", startTime=" + startTime +
                ", currentTime=" + currentTime +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
