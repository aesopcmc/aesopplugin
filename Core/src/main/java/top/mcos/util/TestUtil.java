package top.mcos.util;

import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
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

    //使用1字节就可以表示b
    public static String numToHex8(int b) {
        return String.format("%02x", b);//2表示需要两个16进制数
    }
    //需要使用2字节表示b
    public static String numToHex16(int b) {
        return String.format("%04x", b);
    }
    //需要使用4字节表示b
    public static String numToHex32(int b) {
        return String.format("%08x", b);
    }

    public static void main(String[] args) throws IOException {
        System.out.println(new File("c:\\ff/bb/a.png").getCanonicalPath());

        //System.out.println(new Date().getTime());
        //System.out.println(numToHex16(15435844));
        //System.out.println(new Random().nextInt(4));

        //int i = Integer.parseInt("ff00ff", 16);
        //System.out.println(i);


        //// 随机获取集合下标
        //List<String> list = new ArrayList<>();
        //list.add("aa");
        //list.add("bb");
        //list.add("cc");
        //
        //for (int i=0;i<100;i++) {
        //    List<String> strings = CollectionUtils.randPickup(list);
        //    System.out.println(strings.toString());
        //}

        //ThreadLocalRandom current = ThreadLocalRandom.current();
        //int index = current.nextInt(list.size());
        //int index2 = current.nextInt(list.size());
        //int index3 = current.nextInt(list.size());
        //int index4 = current.nextInt(list.size());
        //System.out.println(index);
        //System.out.println(index2);
        //System.out.println(index3);
        //System.out.println(index4);


        //System.out.println(hasChineseChar("s圣诞节快乐！s"));

        //
        //String command = "player cd";
        //String regex = "(^\\[.+\\]) (.+)";
        ////String s = command.replaceAll(regex, "");
        //Matcher matcher = Pattern.compile(regex).matcher(command);
        //if (matcher.find()) {
        //    String prefix = matcher.group(1);
        //    System.out.println("前缀：" + prefix);
        //
        //    String cmd = matcher.group(2);
        //    System.out.println("命令：" + cmd);
        //}

    }
}
