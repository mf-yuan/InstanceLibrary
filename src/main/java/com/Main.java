package com;

import java.util.concurrent.TimeUnit;

/**
 * @author EDY
 * @date 2023/8/15 14:52
 * @description
 */
public class Main {

    static boolean flag = true;

    public static void main(String[] args) {

        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "\t -----come in");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            flag = false;
            // System.out.println(flag);
        }, "t1").start();

        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "\t -----come in");

            while (flag) {
                // System.out.println(1);
            }
            System.out.println(Thread.currentThread().getName() + "\t -----flag被设置为false，程序停止");
        }, "t2").start();
    }
}
