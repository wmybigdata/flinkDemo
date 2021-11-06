package com.wmy.java.arithmetic.binarySearch;

import org.junit.Test;

import java.util.Arrays;
import java.util.Scanner;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.arithmetic.binarySearch
 * @Author: wmy
 * @Date: 2021/9/19
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 二分法的查找
 * @Version: wmy-version-01
 */
public class BinarySearch {
    @Test
    public  void whileFunc() {
        int[] arr = new int[]{1, 12, 16, 18, 100, 200, 300, 500, 600};
        System.out.println("原始的数组为：");
        System.out.println(Arrays.toString(arr));

        int left = 0;
        int right = arr.length - 1;
        int middle = (left + right) / 2;
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入你的想要查找的数据？");
        int num = 12;

        boolean flag = false;

        while (true) {
            if (left > right) {
                break;
            }
            if (arr[middle] < num) {
                left = middle + 1;
            } else if (arr[middle] > num) {
                right = middle - 1;
            } else {
                flag = true;
                break;
            }
            middle = (left + right) / 2;
        }

        if (flag) {
            System.out.printf("whilefunc查找的数据位置为%d：, 数据为： %d", middle, arr[middle]);
        } else {
            System.out.println("数据没有找到");
        }
    }

    @Test
    public void forFunc() {
        int[] arr = new int[]{1, 12, 16, 18, 100, 200, 300, 500, 600};
        System.out.println("原始的数组为：");
        System.out.println(Arrays.toString(arr));

        int left = 0;
        int right = arr.length - 1;
        int middle = (left + right) / 2;
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入你的想要查找的数据？");
        int num = 12;

        boolean flag = false;

        for (int i = 0; i < arr.length / 2; i++) {
            if (left > right) {
                break;
            }
            if (arr[middle] < num) {
                left = middle + 1;
            } else if (arr[middle] > num) {
                right = middle - 1;
            } else {
                flag = true;
                break;
            }
            middle = (left + right) / 2;
        }
        if (flag) {
            System.out.printf("forfunc查找的数据位置为%d：, 数据为： %d", middle, arr[middle]);
        } else {
            System.out.println("数据没有找到");
        }

    }


    @Test
    public void Demo1() {
        int[] arr = new int[]{1, 2, 4, 5, 6, 7, 8};
        int key = 5;

        int l = 0;
        int r = arr.length - 1;

        int midle;

        while (l <= r) {
            midle = (l+r) >> 1;
            if (key == arr[midle]) {
                System.out.println(arr[midle]);
                break;
            } else if (key > arr[midle]) {
                l = midle + 1;
            } else {
                r = midle - 1;
            }
        }
        if (l > r) {
            System.out.println("否则的话没有找到数据");
        }

    }
}
