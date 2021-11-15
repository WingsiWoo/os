package com.wingsiwoo.www;

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author WingsiWoo
 * @date 2021/11/10
 */
public class Application {
    public static void main(String[] args) {
        // 磁道按访问顺序初始化
        int[] diskArr = new int[10];
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            // 0-100
            diskArr[i] = random.nextInt(101);
        }
        System.out.println("----------------随机生成的磁道列表----------------");
        System.out.println(StringUtils.join(Arrays.stream(diskArr).boxed().toArray(), ","));


        DiskScheduler diskScheduler = new DiskScheduler();
        System.out.println("----------------先来先服务算法----------------");
        diskScheduler.fcfs(diskArr);

        System.out.println("----------------最短寻道时间优先算法----------------");
        diskScheduler.sstf(diskArr);

        System.out.println("----------------扫描算法----------------");
        diskScheduler.scan(diskArr);

        System.out.println("----------------循环扫描算法----------------");
        diskScheduler.cScan(diskArr);
    }
}
