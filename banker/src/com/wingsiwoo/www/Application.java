package com.wingsiwoo.www;

import java.util.Arrays;

/**
 * @author WingsiWoo
 * @date 2021/11/9
 */
public class Application {
    public static void main(String[] args) {
        Bank bank = new Bank();
        System.out.println("资源总数为" + Arrays.toString(bank.getAvailable()) + "\n");

        System.out.println("进程所需资源总数(Max)矩阵为:");
        printMatrix(bank.getMax());
        System.out.println();

        System.out.println("分配(Allocation)矩阵为:");
        printMatrix(bank.getAllocation());
        System.out.println();

        System.out.println("进程仍需资源数(Need)矩阵为:");
        printMatrix(bank.getNeed());
        System.out.println();

        bank.rrSchedule();
    }

    private static void printMatrix(int[][] matrix) {
        System.out.printf("%10s %10s %10s", "资源1", "资源2", "资源3");
        System.out.println();
        System.out.print("进程1");
        printLine(matrix[0]);
        System.out.print("进程2");
        printLine(matrix[1]);
        System.out.print("进程3");
        printLine(matrix[2]);
        System.out.print("进程4");
        printLine(matrix[3]);
        System.out.print("进程5");
        printLine(matrix[4]);
    }

    private static void printLine(int[] line) {
        System.out.printf("%7d %11d %11d", line[0], line[1], line[2]);
        System.out.println();
    }
}
