package com.wingsiwoo.www;

import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author WingsiWoo
 * @date 2021/11/10
 */
public class DiskScheduler {
    /**
     * 假设磁头刚从80磁道移到100磁道
     */
    private final int start = 100;

    /**
     * 先来先服务算法
     */
    public void fcfs(int[] diskArr) {
        // 记录移动距离
        int moveDistance = 0;
        // 记录磁头当前所在位置
        int readWriteHead = start;
        // 记录磁头位置
        List<Integer> headPosition = new LinkedList<>();
        headPosition.add(readWriteHead);

        for (int j : diskArr) {
            // 位置的差值为移动的距离
            moveDistance += Math.abs(j - readWriteHead);
            // 更新磁头位置
            readWriteHead = j;
            headPosition.add(readWriteHead);
        }
        System.out.println("磁盘访问顺序：" + StringUtils.join(headPosition.toArray(), ","));
        System.out.println("平均移动磁道数：" + (float)moveDistance / diskArr.length);
    }

    /**
     * 最短寻道时间优先算法
     */
    public void sstf(int[] diskArr) {
        // 记录移动距离
        int moveDistance = 0;
        // 记录磁头当前所在位置
        int readWriteHead = start;
        // 记录磁头位置
        List<Integer> headPosition = new LinkedList<>();
        headPosition.add(readWriteHead);
        // 记录剩下需要走的磁道
        List<Integer> diskList = Arrays.stream(diskArr).boxed().collect(Collectors.toList());

        for (int i = 0; i < diskArr.length; i++) {
            int index = getNextForSSTF(diskList, readWriteHead);
            Integer next = diskList.get(index);
            moveDistance += Math.abs(next - readWriteHead);
            readWriteHead = next;
            headPosition.add(readWriteHead);
            // 从剩下需要走的磁道列表中移除
            diskList.remove(index);
        }

        System.out.println("磁盘访问顺序：" + StringUtils.join(headPosition.toArray(), ","));
        System.out.println("平均移动磁道数：" + (float)moveDistance / diskArr.length);
    }

    /**
     * 获取diskArr中距离readWriteHead最近的磁道
     * @return 磁道在diskArr中对应的下标
     */
    private int getNextForSSTF(List<Integer> diskArr, int readWriteHead) {
        // key为磁道对应下标，value为差值
        Map<Integer, Integer> differenceMap = new HashMap<>();
        for (int i = 0; i < diskArr.size(); i++) {
            differenceMap.put(i, Math.abs(diskArr.get(i) - readWriteHead));
        }

        // 根据value升序排序
        List<Map.Entry<Integer, Integer>> entries = differenceMap.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());
        return entries.get(0).getKey();
    }

    /**
     * 扫描算法
     */
    public void scan(int diskArr[]) {
        // 记录移动距离
        int moveDistance = 0;
        // 记录磁头当前所在位置
        int readWriteHead = start;
        // 记录磁头位置
        List<Integer> headPosition = new LinkedList<>();
        headPosition.add(readWriteHead);
        // 升序排序的磁道列表
        List<Integer> diskList = Arrays.stream(diskArr).boxed().sorted().collect(Collectors.toList());

        // 寻找第一个比磁头当前所在位置大的磁道数下标
        int i = 0;
        for (; i < diskList.size(); i++) {
            if(diskList.get(i) >= readWriteHead) {
                break;
            }
        }

        // 先向磁道数增加的方向移动
        for (int j = i; j < diskList.size(); j++) {
            moveDistance += diskList.get(j) - readWriteHead;
            readWriteHead = diskList.get(j);
            headPosition.add(readWriteHead);
        }

        // 再向减少的方向移动
        for(int k = i - 1; k >= 0; k--) {
            moveDistance += readWriteHead - diskList.get(k);
            readWriteHead = diskList.get(k);
            headPosition.add(readWriteHead);
        }

        System.out.println("磁盘访问顺序：" + StringUtils.join(headPosition.toArray(), ","));
        System.out.println("平均移动磁道数：" + (float)moveDistance / diskArr.length);
    }

    /**
     * 循环扫描算法
     */
    public void cScan(int diskArr[]) {
        // 记录移动距离
        int moveDistance = 0;
        // 记录磁头当前所在位置
        int readWriteHead = start;
        // 记录磁头位置
        List<Integer> headPosition = new LinkedList<>();
        headPosition.add(readWriteHead);
        // 升序排序的磁道列表
        List<Integer> diskList = Arrays.stream(diskArr).boxed().sorted().collect(Collectors.toList());

        // 寻找第一个比磁头当前所在位置大的磁道数下标
        int i = 0;
        for (; i < diskList.size(); i++) {
            if(diskList.get(i) >= readWriteHead) {
                break;
            }
        }

        // 从磁道数低向磁道数高的方向循环扫描
        for (int j = i; j < diskList.size(); j++) {
            moveDistance += diskList.get(j) - readWriteHead;
            readWriteHead = diskList.get(j);
            headPosition.add(readWriteHead);
        }
        for (int j = 0; j < i; j++) {
            moveDistance += readWriteHead - diskList.get(j);
            readWriteHead = diskList.get(j);
            headPosition.add(readWriteHead);
        }

        System.out.println("磁盘访问顺序：" + StringUtils.join(headPosition.toArray(), ","));
        System.out.println("平均移动磁道数：" + (float)moveDistance / diskArr.length);
    }

}
