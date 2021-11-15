package com.wingsiwoo.www;

import java.util.List;

import static com.wingsiwoo.www.ScheduleUtil.*;

/**
 * @author WingsiWoo
 * @date 2021/11/8
 */
public class Application {
    public static void main(String[] args) {
        List<Process> processes = randomGenerateProcess();
        sjf(processes);
        rr(processes);
        hrrn(processes);
    }
}
