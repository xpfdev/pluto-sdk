package com.pluto.utils;

import android.content.Context;
import android.util.Log;

import java.text.DecimalFormat;

public class ConvertUtils {

    /**
     * 数字保留小数位数
     * @param value 转换的数字
     * @param retains 保留小数点的位数
     * @param limits 最小保留小数点的位数
     * @param format2Thousandth 格式化千分位显示
     * @return 返回一个指定小数点位数的字符串
     */
    public static String double2Decimal(double value, int retains, int limits, boolean format2Thousandth) {
        if (limits > retains) {
            limits = retains;
        }
        StringBuilder pattern = new StringBuilder("#");
        if (retains > 0) {
            pattern.append(".");
            for (int i = 0; i < retains; i++) {
                pattern.append("#");
            }
        }
        DecimalFormat df = new DecimalFormat(pattern.toString());
        String temp = df.format(value);
        String[] arr = temp.split("\\.");
        String longValue = arr[0];
        if (format2Thousandth) {
            longValue = num2Thousandth(Long.parseLong(arr[0]));
        }
        if (limits <= 0) {
            return temp;
        }
        StringBuilder sb = new StringBuilder();
        if (arr.length > 1) {
            sb.insert(0, arr[1]);
        }
        int len = sb.length();
        if (limits >= len) {
            for (int i = 0; i < limits - len; i++) {
                sb.append("0");
            }
        }
        if (sb.length() > 0) {
            sb.insert(0, ".");
        }
        sb.insert(0, longValue);
        return sb.toString();
    }

    /**
     * 数字千分位转换
     * @param value 转换的值
     * @return 返回转换后的字符串
     */
    public static String num2Thousandth(int value) {
        String temp = String.valueOf(value);
        if (temp.length() <= 3) {
            return temp;
        }
        StringBuilder sb = new StringBuilder(temp);
        sb.reverse();
        int parts = (int)Math.ceil(sb.length() / 3.0);
        for (int i = 0; i < parts - 1; i++) {
            int offset = (i + 1) * 3 + i;
            sb.insert(offset, ",");
        }
        sb.reverse();
        return sb.toString();
    }

    /**
     * 数字千分位转换
     * @param value 转换的值
     * @return 返回转换后的字符串
     */
    public static String num2Thousandth(long value) {
        String temp = String.valueOf(value);
        if (temp.length() <= 3) {
            return temp;
        }
        StringBuilder sb = new StringBuilder(temp);
        sb.reverse();
        int parts = (int)Math.ceil(sb.length() / 3.0);
        for (int i = 0; i < parts - 1; i++) {
            int offset = (i + 1) * 3 + i;
            sb.insert(offset, ",");
        }
        sb.reverse();
        return sb.toString();
    }

    /**
     * dp值转换成px值
     * @param dpValue dp值
     * @return px值
     */
    public static int dp2px(Context context, final float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px值转换成dp值
     * @param pxValue px值
     * @return dp值
     */
    public static int px2dp(Context context, final float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * sp值转换成px值
     * @param spValue sp值
     * @return px值
     */
    public static int sp2px(Context context, final float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * px值转换成sp值
     * @param pxValue px值
     * @return sp值
     */
    public static int px2sp(Context context, final float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }
}
