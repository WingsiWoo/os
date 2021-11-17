package com.wingsiwoo.www;

import java.util.List;

import static com.wingsiwoo.www.Memory.MIN_SIZE;

/**
 * @author WingsiWoo
 * @date 2021/11/16
 */
public class MemoryAllocator {

    /**
     * 最佳适应算法
     *
     * @param memory 内存对象
     * @param size   需要分配的内存空间大小
     * @return 分配好的分区
     */
    public static Zone bestFit(Memory memory, int size) {
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
            return null;
        } else {
            return doAllocation(size, flag, zones.get(flag), zones);
        }
    }

    /**
     * 执行分配
     *
     * @param size     申请大小
     * @param location 当前可用分区位置
     * @param zone     可用空闲区
     * @param zones    内存分区列表
     * @return 分配好的分区
     */
    private static Zone doAllocation(int size, int location, Zone zone, List<Zone> zones) {
        //如果分割后分区剩余大小过小（MIN_SIZE）则将分区全部分配，否则分割为两个分区
        if (zone.getSize() - size > MIN_SIZE) {
            Zone split = new Zone(zone.getHead() + size, zone.getSize() - size);
            zones.add(location + 1, split);
            zone.setSize(size);
        }
        zone.setFree(false);
        return zone;
    }

    /**
     * 内存回收
     *
     * @param zone  指定要回收的分区
     * @param zones 分区列表
     */
    public static void collection(Zone zone, List<Zone> zones) {
        if (!zones.contains(zone)) {
            System.out.println("无此分区!");
            return;
        }
        int id = zones.indexOf(zone);
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
}
