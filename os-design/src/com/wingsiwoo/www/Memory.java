package com.wingsiwoo.www;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * @author WingsiWoo
 * @date 2021/11/16
 */
@Data
public class Memory {
    /**
     * 内存总大小-640KB
     */
    public static final Integer MEMORY_SIZE = 640;

    /**
     * 内存大小
     */
    private int size;
    /**
     * 最小剩余分区大小
     */
    public static final int MIN_SIZE = 5;
    /**
     * 内存分区
     */
    private List<Zone> zones;
    /**
     * 上次分配的空闲区位置
     */
    private int pointer;

    /**
     * 默认内存大小为 640 KB
     */
    public Memory() {
        this.size = MEMORY_SIZE;
        this.pointer = 0;
        this.zones = new LinkedList<>();
        zones.add(new Zone(0, size));
    }

    public Memory(int size) {
        this.size = size;
        this.pointer = 0;
        this.zones = new LinkedList<>();
        zones.add(new Zone(0, size));
    }
}
