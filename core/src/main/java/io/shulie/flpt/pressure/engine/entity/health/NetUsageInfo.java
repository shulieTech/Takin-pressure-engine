package io.shulie.flpt.pressure.engine.entity.health;

/**
 * @author angju
 */
public class NetUsageInfo {
    private String name;
    private String ip;
    private String maxSpeed;
    private float send;
    private float receive;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(String maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public float getSend() {
        return send;
    }

    public void setSend(float send) {
        this.send = send;
    }

    public float getReceive() {
        return receive;
    }

    public void setReceive(float receive) {
        this.receive = receive;
    }
}
