package io.shulie.flpt.pressure.engine.entity;

/**
 * @author xuyh
 */
public class TaskStatusBean {
    /**
     * 压测任务进程号
     */
    private String pid;
    /**
     * 任务开始压测时间（时间戳）
     */
    private Long startTime;
    /**
     * 上报点时间戳
     */
    private Long currentTime;
    /**
     * 压测任务状态
     */
    private String status;
    /**
     * 消息
     */
    private String message;

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
