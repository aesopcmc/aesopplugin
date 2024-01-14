package top.mcos.util;

import org.bukkit.Bukkit;

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
        //System.out.println(hasChineseChar("s圣诞节快乐！s"));


        String command = "player cd";
        String regex = "(^\\[.+\\]) (.+)";
        //String s = command.replaceAll(regex, "");
        Matcher matcher = Pattern.compile(regex).matcher(command);
        if (matcher.find()) {
            String prefix = matcher.group(1);
            System.out.println("前缀：" + prefix);

            String cmd = matcher.group(2);
            System.out.println("命令：" + cmd);
        }

    }
}
