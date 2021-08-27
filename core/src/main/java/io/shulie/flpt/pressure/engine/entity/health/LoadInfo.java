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

package io.shulie.flpt.pressure.engine.entity.health;

/**
 * @author angju
 * @date 2018/9/28 15:37
 */
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
