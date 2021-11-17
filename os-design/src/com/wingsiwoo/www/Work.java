package com.wingsiwoo.www;

import lombok.Data;

import java.util.List;

/**
 * @author WingsiWoo
 * @date 2021/11/15
 */
@Data
public class Work {
    /**
     * 作业名称
     */
    private String name;

    /**
     * 到达时间
     */
    private Integer arriveTime;

    /**
     * 完成所需时间
     */
    private Integer needTime;

    /**
     * 所需内存空间
     */
    private Integer needSize;

    public static void printWorkList(List<Work> works) {
        System.out.println("----------------------------后备作业队列--------------------------");
        System.out.printf("%10s %10s %10s %10s", "作业名称", "到达时间", "完成所需时间", "所需内存空间");
        System.out.println();
        works.forEach(work -> {
            System.out.printf("%10s %11d %11d %14d", work.getName(), work.getArriveTime(), work.getNeedTime(), work.getNeedSize());
            System.out.println();
        });
        System.out.println("----------------------------------------------------------------\n");
    }
}
