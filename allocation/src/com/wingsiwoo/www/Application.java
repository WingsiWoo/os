package com.wingsiwoo.www;

import java.util.Scanner;

import static com.wingsiwoo.www.MemoryUtil.*;

/**
 * @author WingsiWoo
 * @date 2021/11/10
 */
public class Application {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("请初始化内存大小:");
        int size = in.nextInt();
        Memory memory = new Memory(size);
        showZones(memory.getZones());
        while (true) {
            System.out.println("1.申请空间  2.回收空间 3.退出");
            System.out.print("请选择指令:");
            int chose = in.nextInt();
            if (chose == 3) {
                break;
            }
            switch (chose) {
                case 1:
                    System.out.print("请输入需要申请的空间大小:");
                    size = in.nextInt();
                    allocation(memory, size);
                    showZones(memory.getZones());
                    break;
                case 2:
                    System.out.print("请输入需要回收的分区号:");
                    int id = in.nextInt();
                    collection(id, memory.getZones());
                    showZones(memory.getZones());
                    break;
                default:
                    System.out.println("请重新选择!");
            }
        }
    }
}
