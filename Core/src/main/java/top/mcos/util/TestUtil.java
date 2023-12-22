package top.mcos.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestUtil {

    /**
     * 检查是否有中文汉字
     * @param message 字符串
     * @return true:有 false:否
     */
    public static boolean hasChineseChar(String message) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(message);
        return m.find();
    }

    public static void main(String[] args) {
        System.out.println(hasChineseChar("s圣诞节快乐！s"));
    }
}
