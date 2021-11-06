package com.wmy.java.arithmetic.binarySearch;

import java.util.BitSet;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.arithmetic.binarySearch
 * @Author: wmy
 * @Date: 2021/10/30
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: BitSetTestDemo
 * @Version: wmy-version-01
 */
public class BitSetTest {
    public static void main(String[] args) {
        // 创建一个数组
        int[] arr = {1, 2, 3, 4, 5, 6};

        // 创建一个BitSet
        BitSet bitSet = new BitSet(6);

        // 添加元素
        for (int i = 0; i < arr.length; i++) {
            bitSet.set(arr[i], true);
        }

        // 打印测试
        System.out.println(bitSet.size()); // 64
        System.out.println(bitSet.get(7)); // false
        System.out.println(bitSet.get(3)); // true
    }
}
