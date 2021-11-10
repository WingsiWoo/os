package com.wingsiwoo.www;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author WingsiWoo
 * @date 2021/11/8
 */
public class Scheduler {
    /**
     * 时间计数器
     */
    private Integer timeCount = 0;

    /**
     * 短进程优先
     *
     * @param processes PCB队列
     */
    public void sjf(List<Process> processes) {
        // 记录已经调度的进程数
        int count = 0;
        StringBuilder scheduleSequence = new StringBuilder();

        // 对剩下的进程调度
        while (count < processes.size()) {
            // 获取队列的最短的进程
            Process nextProcess = getForSjf(processes, timeCount);
            // 没有找到已经到达调度时间的可调度进程，时间计数器+1
            if (nextProcess == null) {
                timeCount++;
                continue;
            }
            nextProcess.setStartTime(timeCount);
            timeCount += nextProcess.getNeedTime();
            printProcessInfo(nextProcess);
            schedule(nextProcess);
            count++;
            scheduleSequence.append(nextProcess.getName()).append("→");
        }
        System.out.println("进程调度顺序如下:");
        scheduleSequence.deleteCharAt(scheduleSequence.length() - 1);
        System.out.println(scheduleSequence + "\n");
    }

    /**
     * 根据执行时间获取PCB
     *
     * @param processes PCB队列
     * @param now       当前时间
     * @return PCB
     */
    private Process getForSjf(List<Process> processes, Integer now) {
        // 获取队列中的最短进程任务，需要过滤掉还未到调度时间的
        List<Process> collect = processes.stream().filter(process -> !process.getStatus().equals(StatusConstant.FINISHED) && process.getArriveTime() <= now)
                .sorted(Comparator.comparing(Process::getNeedTime)).collect(Collectors.toList());
        if (collect.isEmpty()) {
            return null;
        }
        return collect.get(0);
    }

    /**
     * 时间片轮转调度算法
     *
     * @param processes PCB队列
     */
    public void rr(List<Process> processes) {
        // 时间片大小
        int slice = 5;
        Map<String, Integer> rrProcesses = initTimeSlices(processes, slice);
        // 所需的时间片总数
        int sliceSum = (int) rrProcesses.values().stream().mapToDouble(Integer::doubleValue).sum();
        // 已用的时间片总数
        int sliceCount = 0;
        StringBuilder builder = new StringBuilder();

        while (sliceCount < sliceSum) {
            for (Process process : processes) {
                // 进程到达了才能开始运行
                if (timeCount >= process.getArriveTime()) {
                    Integer rrSlice = rrProcesses.get(process.getName());
                    // 剩余可用时间片数>0，执行
                    if (rrSlice > 0) {
                        // 每次运行消耗一个时间片
                        rrProcesses.replace(process.getName(), rrSlice - 1);
                        timeCount += slice;
                        sliceCount++;
                        printProcessInfo(process);
                        // 说明本次时间片轮转后该进程已经执行完毕
                        if (rrSlice - 1 == 0) {
                            // 无可用时间片数，说明进程已经执行完，调度其他进程
                            rrProcesses.replace(process.getName(), -1);
                            schedule(process);
                        }
                        builder.append(process.getName()).append("→");
                    }
                } else {
                    timeCount++;
                }
            }
        }
        System.out.println("进程调度顺序如下:");
        builder.deleteCharAt(builder.length() - 1);
        System.out.println(builder + "\n");
    }

    /**
     * 计算每个进程完成所需的时间片数
     *
     * @param processes PCB队列
     * @param slice     时间片大小
     */
    private Map<String, Integer> initTimeSlices(List<Process> processes, int slice) {
        return processes.stream().collect(Collectors.toMap(Process::getName, process -> (int) Math.ceil(process.getNeedTime().doubleValue() / slice)));
    }

    /**
     * 高响应比优先调度算法
     *
     * @param processes PCB队列
     */
    public void hrrn(List<Process> processes) {
        StringBuilder builder = new StringBuilder();
        List<Process> sortedProcess = responseRatio(processes);
        // 记录已经执行完毕的进程个数
        int count = 0;
        timeCount = sortedProcess.get(0).getArriveTime();
        while (count < sortedProcess.size()) {
            for (Process process : sortedProcess) {
                if (timeCount >= process.getArriveTime()) {
                    timeCount += process.getNeedTime();
                    builder.append(process.getName()).append("→");
                    count++;
                    printProcessInfo(process);
                    schedule(process);
                } else {
                    timeCount++;
                }
            }
        }
        System.out.println("进程调度顺序如下:");
        builder.deleteCharAt(builder.length() - 1);
        System.out.println(builder + "\n");
    }

    /**
     * 计算进程响应比
     *
     * @param processes PCB队列
     */
    private List<Process> responseRatio(List<Process> processes) {
        Map<Process, Double> responseMap = new HashMap<>(processes.size());
        Integer tempTimeCount = 0;
        // 根据到达时间升序排序
        List<Process> sortedProcesses = processes.stream().sorted(Comparator.comparing(Process::getArriveTime)).collect(Collectors.toList());
        // 响应比Rp = （等待时间+要求服务时间）/ 要求服务时间 = 1 +（等待时间 / 要求服务时间）
        for (Process process : sortedProcesses) {
            // 已经到达，可以计算响应比
            if (tempTimeCount >= process.getArriveTime()) {
                responseMap.put(process, 1 + (tempTimeCount.doubleValue() - process.getArriveTime().doubleValue()) / process.getNeedTime().doubleValue());
            } else {
                tempTimeCount++;
            }
        }
        // 根据响应比大小对进程进行排序，高响应比的先执行
        responseMap = responseMap.entrySet().stream()
                .sorted(Comparator.comparingDouble(Map.Entry::getValue))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
        return new ArrayList<>(responseMap.keySet());
    }

    /**
     * 处理完成的进程的相关信息
     *
     * @param process PCB队列
     */
    private void schedule(Process process) {
        // 进程完成时间
        process.setFinishTime(timeCount);
        // 进程服务所需时间
        process.setServingTime(process.getNeedTime());
        // 周转时间=完成时间-到达时间
        process.setTurnoverTime(process.getFinishTime() - process.getArriveTime());
        // 带权周转时间=周转时间/服务时间
        process.setWeightTurnoverTime(process.getTurnoverTime().doubleValue() / process.getNeedTime().doubleValue());
        // 进程完成标志
        process.setStatus(StatusConstant.FINISHED);
    }

    private void printProcessInfo(Process process) {
        System.out.println("----------------当前调度进程--------------");
        System.out.printf("%7s %10s %10s", "进程名称", "进程状态", "服务时间");
        System.out.println();
        System.out.printf("%10s %12s %10d", process.getName(), "RUNNING", process.getNeedTime());
        System.out.println();
        System.out.println("----------------------------------------\n");
    }
}
