package com.wmy.java.matercher;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.matercher
 * @Author: wmy
 * @Date: 2021/9/14
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription:
 * @Version: wmy-version-01
 */
public class KMP {
    /**
     * 求这个next数组，是最重要的
     * @param t
     * @return
     */
    public static int[] getNextArray(char[] t) {
        int[] next = new int[t.length];
        next[0] = -1;
        next[1] = 0;
        int k;
        for (int j = 2; j < t.length; j++) {
            k = next[j - 1];
            while (k!=-1) {
                if (t[j - 1] == t[k]) {
                    next[j] = k + 1;
                    break;
                } else {
                    k = next[k];
                }
                next[j] = 0;
            }
        }
        return next;
    }

    public static int kmpMatch(String s, String t) {
        char[] s_arr = s.toCharArray();
        char[] t_arr = t.toCharArray();
        int[] nextArray = getNextArray(t_arr);
        int i = 0,j = 0;
        while (i < s_arr.length && j < t_arr.length) {
            if (j == -1 || s_arr[i] == t_arr[j]) {
                i++;
                j++;
            } else {
                j = nextArray[j];
            }
        }
        if (j == t_arr.length) {
            return i - j;
        } else {
            return -1;
        }
    }

    public static void main(String[] args) {
        System.out.println(kmpMatch("abcabaabaabcacb", "abaabcac"));
    }
}
