package io.shulie.flpt.pressure.engine.api.plugin.response;

/**
 * 关闭后返回体
 *
 * @author 李鹏
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