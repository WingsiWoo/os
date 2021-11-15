package com.wingsiwoo.www;

import lombok.Data;

/**
 * @author WingsiWoo
 * @date 2021/11/10
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
}
