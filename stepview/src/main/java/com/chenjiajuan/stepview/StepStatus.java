package com.chenjiajuan.stepview;

/**
 * Created by chenjiajuan on 2018/8/25.
 */

public class StepStatus {
    //未完成
    public static final  int STATUS_UNDO=-1;
    //正在进行
    public static final int STATUS_CURRENT=0;
    //已完成
    public static final int STATUS_COMPLETED=1;

    private int state;
    private int number;

    public StepStatus(int state, int number) {
        this.state = state;
        this.number = number;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
