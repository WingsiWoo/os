package com.wingsiwoo.www;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * @author WingsiWoo
 * @date 2021/11/9
 */
public class BankInitUtil {
    /**
     * 随机生成进程
     */
    public static List<Process> randomGenerateProcess() {
        Random random = new Random();
        List<Process> processes = new LinkedList<>();
        for (int i = 0; i < 5; i++) {
            Process process = new Process();
            process.setName("P" + (i + 1));
            process.setArriveTime(0);
            // 5-105
            process.setNeedTime(random.nextInt(101) + 5);
            processes.add(process);
        }
        Process.printProcessList(processes);
        return processes;
    }

    /**
     * 初始化资源数量
     */
    public static int[] initResource() {
        int[] available = new int[3];
        available[0] = 10;
        available[1] = 15;
        available[2] = 12;
        return available;
    }

    /**
     * 随机生成请求的资源数，需要保证总和<=资源总数
     */
    public static int[][] initMax(int[] available) {
        Random random = new Random();
        int[][] max = new int[5][3];

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 3; j++) {
                // 1-最大资源数
                max[i][j] = random.nextInt(available[j]) + 1;
            }
        }
        return max;
    }

    /**
     * 随机生成已分配的资源数
     */
    public static int[][] initAllocation(int[][] max, int[] available) {
        Random random = new Random();
        int[][] allocation = new int[5][3];
        // 记录剩余的资源数量
        int[] remain = new int[available.length];
        System.arraycopy(available, 0, remain, 0, available.length);

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 3; j++) {
                // 需要保证各进程得到的资源合起来不会超过总数
                int randomNum = random.nextInt(max[i][j] + 1);
                while (remain[j] - randomNum < 0) {
                    randomNum = random.nextInt(max[i][j] + 1);
                }
                // 0-可请求的剩余资源数
                allocation[i][j] = randomNum;
                remain[j] -= randomNum;
            }
        }
        return allocation;
    }

    /**
     * 计算进程还需要的资源数
     */
    public static int[][] calculateNeed(int[][] max, int[][] allocation) {
        int[][] need = new int[5][3];
        // need=max-allocation
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 3; j++) {
                need[i][j] = max[i][j] - allocation[i][j];
            }
        }
        return need;
    }
}
