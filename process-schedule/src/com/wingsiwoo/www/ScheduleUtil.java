package com.wingsiwoo.www;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * @author WingsiWoo
 * @date 2021/11/8
 */
public class ScheduleUtil {
    /**
     * 随机生成进程
     */
    public static List<Process> randomGenerateProcess() {
        Random random = new Random();
        List<Process> processes = new LinkedList<>();
        for (int i = 0; i < 5; i++) {
            Process process = new Process();
            process.setName("P" + (i + 1));
            process.setArriveTime(0);
            // 5-105
            process.setNeedTime(random.nextInt(101) + 5);
            processes.add(process);
        }
        Process.printProcessList(processes);
        return processes;
    }

    /**
     * 短进程优先调度算法
     */
    public static void sjf(List<Process> processes) {
        System.out.println("\n\n开始调度。短进程优先调度过程如下\n\n");

        // 模拟调度过程
        Scheduler scheduler = new Scheduler();
        scheduler.sjf(processes);

        printScheduleResult(processes, "短进程优先调度算法");
    }

    /**
     * 时间片轮转调度算法
     */
    public static void rr(List<Process> processes) {
        System.out.println("\n\n开始调度。时间片轮转调度过程如下\n");

        // 模拟调度过程
        Scheduler scheduler = new Scheduler();
        scheduler.rr(processes);

        printScheduleResult(processes, "时间片轮转调度算法");
    }

    /**
     * 高响应比优先调度算法
     */
    public static void hrrn(List<Process> processes) {
        System.out.println("\n\n开始调度。高响应比优先调度过程如下\n\n");

        // 模拟调度过程
        Scheduler scheduler = new Scheduler();
        scheduler.hrrn(processes);

        printScheduleResult(processes, "高响应比优先调度算法");
    }

    /**
     * 打印进程调度结果
     *
     * @param processes 进程调度结果
     * @param schedule  调度算法
     */
    private static void printScheduleResult(List<Process> processes, String schedule) {
        Integer turnoverTime = 0;
        double weightTurnoverTime = 0;
        System.out.printf("%7s %10s %10s %10s", "进程名称", "完成时间", "周转时间", "带权周转时间");
        System.out.println();
        for (Process process : processes) {
            System.out.printf("%10s %10d %12d %15f", process.getName(), process.getFinishTime(), process.getTurnoverTime(), process.getWeightTurnoverTime());
            System.out.println();
            turnoverTime += process.getTurnoverTime();
            weightTurnoverTime += process.getWeightTurnoverTime();
        }
        System.out.println("\n" + schedule + "的平均周转时间为：" + turnoverTime.doubleValue() / processes.size());
        System.out.println(schedule + "的平均带权周转时间为：" + weightTurnoverTime / processes.size());
    }
}
