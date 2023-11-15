package top.mcos.util;

import java.util.Arrays;

/**
 * 全角半角转换工具
 */
public class FullHalfChangeUtil {
    /**
     * ASCII表中除空格外的可见字符与对应的全角字符的相对偏移量
     */
    private static final char CONVERT_OFFSET = 65248;

    /**
     * 全角空格的值，它没有遵从与ASCII的相对偏移，必须单独处理
     */
    private static final char FULL_SPACE = 12288;

    /**
     * 半角空格的值，单独处理
     */
    private static final char HALF_SPACE = 32;

    /**
     * 全角开始值，ASCII表的可见字符从！开始，偏移值为65281
     */
    static final char FULL_CHAR_START = 65281;

    /**
     * 全角结束值，ASCII表的可见字符到～结束
     */
    static final char FULL_CHAR_END = 65374;
    /**
     * 半角开始值，ASCII表中可见字符从!开始
     */
    static final char HALF_CHAR_START = 33;

    /**
     * 半角结束值 ASCII表中可见字符到~结束
     */
    static final char HALF_CHAR_END = 126;

    /**
     * 全角字符串转换半角字符串方法
     * @param fullStr  全角字符串
     * @return  半角字符串
     */
    public static String full2HalfChange(String fullStr){
        if (null == fullStr || fullStr.length() <= 0) {
            return "";
        }
        char[] charArray = fullStr.toCharArray();
        //对全角字符转换的char数组遍历
        for (int i = 0; i < charArray.length; ++i) {
            int charIntValue = (int) charArray[i];
            //如果符合转换关系,将对应下标之间减掉偏移量65248;如果是全角空格的话,直接做转换
            if (charIntValue >= FULL_CHAR_START && charIntValue <= FULL_CHAR_END) {
                charArray[i] = (char) (charIntValue - CONVERT_OFFSET);
            } else if (charIntValue == FULL_SPACE) {
                charArray[i] = HALF_SPACE;
            }
        }
        return new String(charArray);
    }

    /**
     * 半角字符串转换全角字符串方法
     * @param halfStr 半角字符串
     * @param isSpaceChange 是否转换空格 true转换，false不转换
     * @return  全角字符串
     */
    public static String half2FullChange(String halfStr, boolean isSpaceChange){
        if (null == halfStr || halfStr.length() <= 0) {
            return "";
        }
        char[] charArray = halfStr.toCharArray();
        //对半角字符转换的char数组遍历
        for (int i = 0; i < charArray.length; ++i) {
            int charIntValue = (int) charArray[i];
            //如果符合转换关系,将对应下标之间加上偏移量65248;如果是半角空格的话,直接做转换
            if (charIntValue >= HALF_CHAR_START && charIntValue <= HALF_CHAR_END) {
                charArray[i] = (char) (charIntValue + 65248);
            } else if (charIntValue == CONVERT_OFFSET && isSpaceChange) {
                charArray[i] = FULL_SPACE;
            }
        }
        return new String(charArray);
    }

    /**
     * 半角字符串转换全角字符串方法
     * @param char1 单个字符
     * @param isSpaceChange 是否转换空格 true转换，false不转换
     * @return  全角字符串
     */
    public static char half2FullChange(char char1, boolean isSpaceChange){
        //对半角字符转换的char数组遍历
        int charIntValue = (int) char1;
        //如果符合转换关系,将对应下标之间加上偏移量65248;如果是半角空格的话,直接做转换
        if (charIntValue >= HALF_CHAR_START && charIntValue <= HALF_CHAR_END) {
            charIntValue = (char) (charIntValue + 65248);
        } else if (charIntValue == CONVERT_OFFSET && isSpaceChange) {
            charIntValue = FULL_SPACE;
        }
        return (char)charIntValue;
    }

    public static void main(String[] args) {
        String fullStr = "１！～　９？";
        String halfStr = "1!~ 9?";
        System.out.println(full2HalfChange(fullStr));
        System.out.println(Arrays.toString(fullStr.toCharArray()));
        System.out.println(Arrays.toString(stringToIntArray(full2HalfChange(fullStr))));

        System.out.println(half2FullChange(halfStr, true));
        System.out.println(Arrays.toString(halfStr.toCharArray()));
        System.out.println(Arrays.toString(stringToIntArray(half2FullChange(halfStr,true))));
    }
    public static int[] stringToIntArray(String str){
        char[] charArray = str.toCharArray();
        int[] intArray = new int[charArray.length];
        for (int i = 0; i < charArray.length; i++) {
            intArray[i] = charArray[i]; // 自动类型转换
        }
        return intArray;
    }
}