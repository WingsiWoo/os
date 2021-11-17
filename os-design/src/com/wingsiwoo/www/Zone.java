package com.wingsiwoo.www;

import lombok.Data;

import java.util.List;

/**
 * @author WingsiWoo
 * @date 2021/11/16
 * 分区节点类
 */
@Data
public class Zone {
    /**
     * 分区大小
     */
    private int size;
    /**
     * 分区始址
     */
    private int head;
    /**
     * 空闲状态
     */
    private Boolean free;

    public Zone(int head, int size) {
        this.head = head;
        this.size = size;
        this.free = true;
    }

    /**
     * 展示内存分区状况
     */
    public static void showZones(List<Zone> zones) {
        System.out.println("---------------------------------------------------");
        System.out.printf("%5s | %5s | %5s | %5s", "分区编号", "分区始址", "分区大小", "空闲状态");
        System.out.println();
        for (int i = 0; i < zones.size(); i++) {
            Zone zone = zones.get(i);
            String state = zone.getFree() ? "FREE" : "BUSY";
            System.out.printf("%4d %10d %10d %10s", i, zone.getHead(), zone.getSize(), state);
            System.out.println();
        }
        System.out.println("---------------------------------------------------");
    }
}
