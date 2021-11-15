package com.wingsiwoo.www;

import lombok.Data;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author WingsiWoo
 * @date 2021/11/9
 */
@Data
public class Bank {
    /**
     * 可用资源，记录系统中各类资源当前的可利用数
     */
    private int[] available;

    /**
     * 进程
     */
    private List<Process> processes;

    /**
     * 最大需求，记录每个进程对各类资源的最大需求量
     * max[i][j]表示进程i对资源j的最大需求量
     */
    private int[][] max;

    /**
     * 分配矩阵，记录每个进程对各类资源当前的占有量
     * allocation[i][j]表示进程i对资源j当前的占有量
     */
    private int[][] allocation;

    /**
     * 需求矩阵，记录每个进程对各类资源尚需要的量，等于max-allocation
     * 进程i对资源j还需要的数量为need[i][j]
     */
    private int[][] need;

    /**
     * 安全状态进程
     */
    private List<Process> securityProcesses;

    public Bank() {
        securityProcesses = new LinkedList<>();
        available = BankInitUtil.initResource();
        processes = BankInitUtil.randomGenerateProcess();
        max = BankInitUtil.initMax(available);
        allocation = BankInitUtil.initAllocation(max, available);
        need = BankInitUtil.calculateNeed(max, allocation);
        // 寻找安全的分配方式
        while (!securityCheck()) {
            allocation = BankInitUtil.initAllocation(max, available);
            need = BankInitUtil.calculateNeed(max, allocation);
        }
    }

    private boolean securityCheck() {
        // 工作向量Work：它表示系统可提供给进程继续运行所需的各类资源数量的多少
        int[] work = new int[available.length];
        System.arraycopy(available, 0, work, 0, available.length);
        // 减去已经分配的资源数量
        for (int i = 0; i < available.length; i++) {
            for (int j = 0; j < processes.size(); j++) {
                work[i] -= allocation[j][i];
            }
        }
        // finish向量：表示系统是否有足够的资源分配给进程，使之运行完成，初始为false
        boolean[] finish = new boolean[processes.size()];

        for (int i = 0; i < processes.size(); i++) {
            if (!finish[i]) {
                // 该进程尚未分配
                boolean temp = true;
                // 检查该进程要求的各类资源是否充足
                for (int j = 0; j < work.length; j++) {
                    if (need[i][j] > work[j]) {
                        temp = false;
                        break;
                    }
                }
                // 该进程可以分配
                if (temp) {
                    // 回收进程i占用的资源
                    for (int j = 0; j < work.length; j++) {
                        work[j] += allocation[i][j];
                    }
                    // 修改进程i的分配状态位
                    finish[i] = true;
                    // 把进程放入安全序列中
                    securityProcesses.add(processes.get(i));
                    System.out.println(processes.get(i).getName() + "分配成功！当前资源存储状态为[" + Arrays.toString(work) + "]");
                    // 从头开始查找是否可以分配
                    i = 0;
                }
            }
        }
        // 说明全部分配成功
        if (securityProcesses.size() == processes.size()) {
            StringBuilder builder = new StringBuilder();
            securityProcesses.forEach(p -> {
                builder.append(p.getName()).append("→");
            });
            builder.deleteCharAt(builder.length() - 1);
            System.out.println("安全序列为：" + builder);
            return true;
        }
        // 清空之前加入的序列
        securityProcesses.clear();
        return false;
    }

    public void rrSchedule() {
        // 时间片大小
        int slice = 5;
        Map<String, Integer> rrProcesses = initTimeSlices(securityProcesses, slice);
        // 所需的时间片总数
        int sliceSum = (int) rrProcesses.values().stream().mapToDouble(Integer::doubleValue).sum();
        // 已用的时间片总数
        int sliceCount = 0;
        StringBuilder builder = new StringBuilder();
        // 记录当前时间
        Integer timeCount = 0;

        while (sliceCount < sliceSum) {
            for (Process process : securityProcesses) {
                // 进程到达了才能开始运行
                if (timeCount >= process.getArriveTime()) {
                    Integer rrSlice = rrProcesses.get(process.getName());
                    // 剩余可用时间片数>0，执行
                    if (rrSlice > 0) {
                        // 每次运行消耗一个时间片
                        rrProcesses.replace(process.getName(), rrSlice - 1);
                        timeCount += slice;
                        sliceCount++;
                        System.out.println("----------------当前调度进程--------------");
                        System.out.printf("%7s %10s %10s", "进程名称", "进程状态", "服务时间");
                        System.out.println();
                        System.out.printf("%10s %12s %10d", process.getName(), "RUNNING", process.getNeedTime());
                        System.out.println();
                        System.out.println("----------------------------------------\n");
                        // 说明本次时间片轮转后该进程已经执行完毕
                        if (rrSlice - 1 == 0) {
                            // 无可用时间片数，说明进程已经执行完，调度其他进程
                            rrProcesses.replace(process.getName(), -1);
                            schedule(process, timeCount);
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
     * 处理完成的进程的相关信息
     *
     * @param process PCB队列
     */
    private void schedule(Process process, Integer timeCount) {
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
}
