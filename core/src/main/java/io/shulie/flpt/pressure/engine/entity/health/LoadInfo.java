package io.shulie.flpt.pressure.engine.entity.health;

/**
 * @author 昂驹
 */
@SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
public class LoadInfo {
    private int cpuNum;
    private String load_1;
    private String load_2;
    private String load_3;

    public int getCpuNum() {
        return cpuNum;
    }

    public void setCpuNum(int cpuNum) {
        this.cpuNum = cpuNum;
    }

    public String getLoad_1() {
        return load_1;
    }

    public void setLoad_1(String load_1) {
        this.load_1 = load_1;
    }

    public String getLoad_2() {
        return load_2;
    }

    public void setLoad_2(String load_2) {
        this.load_2 = load_2;
    }

    public String getLoad_3() {
        return load_3;
    }

    public void setLoad_3(String load_3) {
        this.load_3 = load_3;
    }
}
