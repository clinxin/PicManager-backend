package com.clinxin.picmanagerbackend.utils;

/**
 * 颜色转换
 */
public class ColorTransformUtils {

    private ColorTransformUtils() {
        // 工具类不需要实例化
    }

    /**
     * 获取标准颜色（将数据万象的 5 位色转为 6 位）
     * @param color
     * @return
     */
    public static String getStandardColor(String color) {
        if (color.length() == 7) {
            color = color.substring(0, 4) + "0" + color.substring(4, 7);
        }
        return color;
    }
}
