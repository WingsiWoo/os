package com.wingsiwoo.www;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author WingsiWoo
 * @date 2021/11/15
 */
@Data
@NoArgsConstructor
public class Process {
    /**
     * 进程名称
     */
    private String name;

    /**
     * 到达时间
     */
    private Integer arriveTime;

    /**
     * 需要的服务时间
     */
    private Integer needTime;

    /**
     * 需要占用的内存
     */
    private Integer needSize;

    /**
     * 开始时间
     */
    private Integer startTime;

    /**
     * 结束时间
     */
    private Integer finishTime;

    /**
     * 已经执行的时间
     */
    private Integer servingTime = 0;

    /**
     * 作业状态：等待W，运行R，完成F
     * 默认为就绪状态
     */
    private String status = StatusConstant.WAITING;

    /**
     * 周转时间
     */
    private Integer turnoverTime;

    /**
     * 带权周转时间
     */
    private Double weightTurnoverTime;

    public static void printProcessList(List<Process> processes) {
        System.out.println("-----------------------------------------------------");
        System.out.printf("%7s %10s %10s %10s", "进程名称", "到达时间", "所需CPU时间", "所需内存");
        System.out.println();
        processes.forEach(process -> {
            System.out.printf("%7s %12d %11d %15d", process.getName(), process.getArriveTime(), process.getNeedTime(), process.getNeedSize());
            System.out.println();
        });
        System.out.println("------------------------------------------------------");
    }


}
