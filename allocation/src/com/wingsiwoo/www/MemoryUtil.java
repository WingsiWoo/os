package com.wingsiwoo.www;

import java.util.List;
import java.util.Scanner;

import static com.wingsiwoo.www.Memory.MIN_SIZE;

/**
 * @author WingsiWoo
 * @date 2021/11/10
 */
public class MemoryUtil {
    /**
     * 内存分配
     */
    public static void allocation(Memory memory, int size) {
        System.out.print("请选择分配算法:");
        System.out.println("1.首次适应算法 2.最佳适应算法");
        Scanner in = new Scanner(System.in);
        int algorithm = in.nextInt();
        switch (algorithm) {
            case 1:
                firstFit(memory, size);
                break;
            case 2:
                bestFit(memory, size);
                break;
            default:
                System.out.println("请重新选择!");
        }
    }

    /**
     * 首次适应算法
     */
    private static void firstFit(Memory memory, int size) {
        //遍历分区链表
        int pointer = 0;
        List<Zone> zones = memory.getZones();
        for (; pointer < zones.size(); pointer++) {
            Zone zone = zones.get(pointer);
            //找到可用分区（空闲且大小足够）
            if (zone.getFree() && (zone.getSize() > size)) {
                doAllocation(size, pointer, zone, zones);
                return;
            }
        }
        memory.setPointer(pointer);
        // 遍历结束后未找到可用分区, 则内存分配失败
        System.out.println("无可用内存空间!");
    }

    /**
     * 最佳适应算法
     */
    private static void bestFit(Memory memory, int size) {
        int flag = -1;
        int min = memory.getSize();
        int pointer = 0;
        List<Zone> zones = memory.getZones();
        for (; pointer < zones.size(); pointer++) {
            Zone zone = zones.get(pointer);
            if (zone.getFree() && (zone.getSize() > size)) {
                if (min > zone.getSize() - size) {
                    min = zone.getSize() - size;
                    flag = pointer;
                }
            }
        }
        memory.setPointer(pointer);
        if (flag == -1) {
            System.out.println("无可用内存空间!");
        } else {
            doAllocation(size, flag, zones.get(flag), zones);
        }
    }

    /**
     * 执行分配
     *
     * @param size     申请大小
     * @param location 当前可用分区位置
     * @param zone     可用空闲区
     * @param zones    内存分区列表
     */
    private static void doAllocation(int size, int location, Zone zone, List<Zone> zones) {
        //如果分割后分区剩余大小过小（MIN_SIZE）则将分区全部分配，否则分割为两个分区
        if (zone.getSize() - size > MIN_SIZE) {
            Zone split = new Zone(zone.getHead() + size, zone.getSize() - size);
            zones.add(location + 1, split);
            zone.setSize(size);
        }
        zone.setFree(false);
        System.out.println("成功分配 " + size + "KB 内存!");
    }

    /**
     * 内存回收
     *
     * @param id 指定要回收的分区好号
     */
    public static void collection(int id, List<Zone> zones) {
        if (id >= zones.size()) {
            System.out.println("无此分区编号!");
            return;
        }
        Zone zone = zones.get(id);
        int size = zone.getSize();
        if (zone.getFree()) {
            System.out.println("指定分区未被分配, 无需回收");
            return;
        }
        //如果回收分区不是尾分区且后一个分区为空闲, 则与后一个分区合并
        if (id < zones.size() - 1 && zones.get(id + 1).getFree()) {
            Zone next = zones.get(id + 1);
            zone.setSize(zone.getSize() + next.getSize());
            zones.remove(next);
        }
        //如果回收分区不是首分区且前一个分区为空闲, 则与前一个分区合并
        if (id > 0 && zones.get(id - 1).getFree()) {
            Zone previous = zones.get(id - 1);
            previous.setSize(previous.getSize() + zone.getSize());
            zones.remove(id);
            id--;
        }
        zones.get(id).setFree(true);
        System.out.println("内存回收成功!, 本次回收了 " + size + "KB 空间!");
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
            String state = zone.getFree() ? "free" : "busy";
            System.out.printf("%4d %10d %10d %10s", i, zone.getHead(), zone.getSize(), state);
            System.out.println();
        }
        System.out.println("---------------------------------------------------");
    }
}
