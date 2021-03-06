package com.wingsiwoo.www;

import java.util.*;
import java.util.stream.Collectors;

import static com.wingsiwoo.www.StatusConstant.*;

/**
 * @author WingsiWoo
 * @date 2021/11/15
 */
public class Scheduler {
    /**
     * 时间计数器
     */
    private Integer timeCount = 0;

    /**
     * 时间片大小
     */
    private final int slice = 5;

    /**
     * 当前已用的时间片数
     */
    private int sliceCount = 0;

    /**
     * 完成所有作业所需要的时间片数
     */
    private int sliceSum;

    /**
     * 内存对象
     */
    private Memory memory;

    /**
     * 作业数
     */
    private final Integer WORK_SIZE = 10;

    /**
     * 后备作业列表，初始大小为10
     */
    private List<Work> works;

    /**
     * 进入内存的进程数
     */
    private final Integer PROCESS_SIZE = 5;

    /**
     * 可工作的进程列表，最大大小为5
     */
    private List<Process> processes;

    /**
     * 记录进程与内存分区的对应关系
     */
    private Map<String, Zone> allocateMap;

    /**
     * key-作业对应进程名，value-所需时间片数
     */
    private Map<String, Integer> rrProcesses;

    /**
     * key-被阻塞的线程在processes中的下标，value-阻塞结束的时间
     */
    private Map<Integer, Integer> blockMap;

    /**
     * 最优结束时间
     */
    private Integer endTime;

    public Scheduler() {
        works = new LinkedList<>();
        processes = new LinkedList<>();
        allocateMap = new HashMap<>();
        memory = new Memory();
        blockMap = new HashMap<>();
    }

    /**
     * 入口方法
     */
    public void start() {
        Random random = new Random();
        // 随机生成10个作业
        for (int i = 1; i <= WORK_SIZE; i++) {
            Work work = new Work();
            work.setName("作业" + i);
            // 0-100
            work.setArriveTime(random.nextInt(101));
            work.setNeedTime(random.nextInt(101));
            // 1-450
            work.setNeedSize(random.nextInt(450) + 1);
            works.add(work);
        }

        // 固定的用例，方便测试
        // init();

        // 初始化每个进程所需的时间片数
        rrProcesses = initTimeSlices();
        // 所需的时间片总数
        sliceSum = rrProcesses.values().stream().mapToInt(value -> value).sum();
        // 结束时间=所需时间片总数 * 时间片大小
        endTime = sliceSum * slice;

        // 打印后备作业队列状态
        Work.printWorkList(works);

        fcfs();
        rr();
    }

    private void init() {
        Work work1 = new Work();
        work1.setName("作业1");
        work1.setArriveTime(6);
        work1.setNeedTime(21);
        work1.setNeedSize(154);
        works.add(work1);

        Work work2 = new Work();
        work2.setName("作业2");
        work2.setArriveTime(60);
        work2.setNeedTime(62);
        work2.setNeedSize(348);
        works.add(work2);

        Work work3 = new Work();
        work3.setName("作业3");
        work3.setArriveTime(37);
        work3.setNeedTime(53);
        work3.setNeedSize(293);
        works.add(work3);

        Work work4 = new Work();
        work4.setName("作业4");
        work4.setArriveTime(35);
        work4.setNeedTime(41);
        work4.setNeedSize(15);
        works.add(work4);

        Work work5 = new Work();
        work5.setName("作业5");
        work5.setArriveTime(67);
        work5.setNeedTime(8);
        work5.setNeedSize(248);
        works.add(work5);

        Work work6 = new Work();
        work6.setName("作业6");
        work6.setArriveTime(92);
        work6.setNeedTime(82);
        work6.setNeedSize(171);
        works.add(work6);

        Work work7 = new Work();
        work7.setName("作业7");
        work7.setArriveTime(54);
        work7.setNeedTime(10);
        work7.setNeedSize(79);
        works.add(work7);

        Work work8 = new Work();
        work8.setName("作业8");
        work8.setArriveTime(14);
        work8.setNeedTime(68);
        work8.setNeedSize(303);
        works.add(work8);

        Work work9 = new Work();
        work9.setName("作业9");
        work9.setArriveTime(1);
        work9.setNeedTime(77);
        work9.setNeedSize(407);
        works.add(work9);

        Work work10 = new Work();
        work10.setName("作业10");
        work10.setArriveTime(52);
        work10.setNeedTime(79);
        work10.setNeedSize(51);
        works.add(work10);
    }

    /**
     * 按照先来先服务为5个作业创建对应的进程
     */
    public void fcfs() {
        // 作业根据到达时间升序排序
        works = works.stream().sorted(Comparator.comparing(Work::getArriveTime)).collect(Collectors.toList());
        List<Work> removeWorks = new LinkedList<>();
        // 保持processes中有5个进程/作业已全部进入
        for (int i = 0; i < works.size(); i++) {
            Work work = works.get(i);
            // 说明长度为5的进程队列中有空位置
            int unfinish = (int) processes.stream().filter(process -> !FINISHED.equals(process.getStatus())).count();
            if (unfinish < PROCESS_SIZE) {
                Process process = new Process();
                process.setName("p" + work.getName().substring(2));
                process.setArriveTime(work.getArriveTime());
                process.setNeedTime(work.getNeedTime());
                process.setNeedSize(work.getNeedSize());
                // 为进程分配内存空间
                Zone zone = MemoryAllocator.bestFit(memory, process.getNeedSize());
                // 分配内存成功
                if (zone != null) {
                    System.out.println("成功为进程 " + process.getName() + " 分配 " + process.getNeedSize() + "KB 内存!");
                    allocateMap.put(process.getName(), zone);
                } else {
                    // 分配内存失败
                    System.out.println("为进程 " + process.getName() + " 分配 " + process.getNeedSize() + "KB 内存失败!");
                    // 跳过该进程和作业，之后可以分配内存了再优先调用
                    continue;
                }
                processes.add(process);
                // 把创建了进程的作业（已进入内存）从后备作业队列中移除
                removeWorks.add(work);
            } else {
                // 无空位，结束循环
                break;
            }
        }
        works.removeAll(removeWorks);
        // 打印进程状态
        Process.printProcessList(processes);
        // 打印后备作业队列状态
        Work.printWorkList(works);
        // 打印内存信息
        Zone.showZones(memory.getZones());
    }


    /**
     * 时间片轮转调度算法
     */
    public void rr() {
        while (sliceCount < sliceSum) {
            // 过滤掉已经完成的，使用暂存集合，避免ConcurrentModificationException异常
            List<Process> tempProcesses = processes.stream().filter(process -> !FINISHED.equals(process.getStatus())).collect(Collectors.toList());
            for (Process process : tempProcesses) {
                // 进程到达了才能开始运行
                if (timeCount >= process.getArriveTime() && !BLOCKING.equals(process.getStatus())) {
                    if (WAITING.equals(process.getStatus())) {
                        process.setStatus(RUNNING);
                    }
                    Integer rrSlice = rrProcesses.get(process.getName());
                    // 剩余可用时间片数>0，执行
                    if (rrSlice > 0) {
                        // 每次运行消耗一个时间片
                        rrProcesses.replace(process.getName(), rrSlice - 1);
                        timeCount += slice;
                        sliceCount++;
                        // 说明本次时间片轮转后该进程已经执行完毕
                        if (rrSlice - 1 == 0) {
                            // 无可用时间片数，说明进程已经执行完，调度其他进程
                            rrProcesses.replace(process.getName(), -1);
                            schedule(process);
                            // 把该进程占用的内存释放掉
                            Zone zone = allocateMap.get(process.getName());
                            MemoryAllocator.collection(zone, memory.getZones());
                            allocateMap.remove(process.getName());
                            // 从后备作业队列中调入最早到达的作业进入内存
                            fcfs();
                        }
                    }
                } else {
                    timeCount++;
                }
                block();
            }
        }
    }

    /**
     * 计算每个作业完成所需的时间片数
     */
    private Map<String, Integer> initTimeSlices() {
        return works.stream().collect(Collectors.toMap(work -> "p" + work.getName().substring(2), work -> (int) Math.ceil(work.getNeedTime().doubleValue() / slice)));
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
        process.setStatus(FINISHED);

        System.out.println("--------------------------------------调度完成进程------------------------------------");
        System.out.printf("%7s %10s %10s %10s %10s %10s", "进程名称", "进程状态", "完成时间", "服务所需时间", "周转时间", "带权周转时间");
        System.out.println();
        System.out.printf("%7s %16s %9d %11d %15d %16f", process.getName(), process.getStatus(), process.getFinishTime(), process.getNeedTime(), process.getTurnoverTime(), process.getWeightTurnoverTime());
        System.out.println();
        System.out.println("------------------------------------------------------------------------------------\n");
    }

    /**
     * 随机阻塞线程
     */
    private void block() {
        Random random = new Random();
        if (processes.size() > 0) {
            // 随机生成要被阻塞的进程的下标，有25%的概率会阻塞
            int index = random.nextInt(processes.size() * 4);
            if (index < processes.size()) {
                Process process = processes.get(index);
                if (RUNNING.equals(process.getStatus())) {
                    // 随机阻塞的结束时间:timeCount-endTime，需要保证进程能顺利完成
                    if (endTime > timeCount) {
                        int time = random.nextInt(endTime - timeCount + 1) + timeCount;
                        process.setStatus(BLOCKING);
                        // 放入阻塞表中
                        blockMap.put(index, time);
                        System.out.println("--------------随机阻塞进程--------------");
                        System.out.printf("%6s %6s %8s", "进程名称", "阻塞时间", "阻塞结束时间");
                        System.out.println();
                        System.out.printf("%6s %8d %12d", process.getName(), time - timeCount, time);
                        System.out.println();
                        System.out.println("--------------------------------------");
                        Process.printProcessList(processes);
                    }
                }
            }
        }

        // 恢复阻塞完成的进程
        List<Integer> indexList = blockMap.entrySet().stream().filter(entry -> timeCount >= entry.getValue()).map(Map.Entry::getKey).collect(Collectors.toList());
        for (int i = 0; i < indexList.size(); i++) {
            Integer integer = indexList.get(i);
            Process process = processes.get(integer);
            process.setStatus(RUNNING);
            blockMap.remove(integer);
        }
    }
}
