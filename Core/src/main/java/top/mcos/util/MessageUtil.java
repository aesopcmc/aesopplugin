package top.mcos.util;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Horse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class MessageUtil {
    private static final Pattern HEX_PATTERN = Pattern.compile("&(#\\w{6})");

    public static String[] convertMsg(String message, int displayWidth) {
        String[] messagePiles;

        String padMessage = StringUtils.center(message, message.length()+displayWidth*2+1, " ");
        int colorPrefixCount = StringUtils.countMatches(padMessage, "&") * 2;//统计颜色占位符个数
        int total = padMessage.length();
        messagePiles = new String[total - displayWidth - colorPrefixCount +1];
        int p=0;
        StringBuilder firstSym = new StringBuilder();//置顶颜色字符
        for(int i = 0; i<total;) {
            if(padMessage.charAt(i)=='&') {
                //https://minecraft.fandom.com/zh/wiki/%E6%A0%BC%E5%BC%8F%E5%8C%96%E4%BB%A3%E7%A0%81
                // 不是颜色”格式化代码“，则重置
                if(!"k,l,m,n,o,r".contains(padMessage.charAt(i+1)+"")) {
                    firstSym.setLength(0);
                }
                firstSym.append(padMessage.charAt(i)).append(padMessage.charAt(i+1));
                i=i+2;
                if(i>=total) {
                    break;
                }
                continue;
            }
            StringBuilder tmp = new StringBuilder();
            int loop = 0;
            for(int j=i;j<total;) {
                if(padMessage.charAt(j)=='&'){
                    tmp.append(padMessage.charAt(j)).append(padMessage.charAt(j+1));
                    j=j+2;
                } else {
                    // 取得有效字符进行拼接，并转为全角字符
                    tmp.append(FullHalfChangeUtil.half2FullChange(padMessage.charAt(j), false));
                    loop++;
                    j++;
                }
                if(loop==displayWidth){
                    String res = firstSym + tmp.toString().replaceAll("(^\\s+)", "$1$1").replaceAll("(\\s+$)", "$1$1");
                    messagePiles[p++] = res;//FullHalfChangeUtil.half2FullChange(res, false);
                    break;
                }
            }
            i++;
        }
        return messagePiles;
    }

    /**
     * TODO 方法过期，使用 colorize 代替
     * 支持&x颜色
     * @param message
     * @return
     */
    @Deprecated
    public static String symbol(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * 支持&x 颜色，支持16禁止颜色
     * 颜色使用示例：
     * String colorize = MessageUtil.colorize("测试颜色：#fbd267圣#8fe964诞#23ff61节&a固定颜色&a&l加粗，#ff0000十六进制");
     * @param message
     * @return
     */
    public static String colorize(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find())
            matcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of(matcher.group(1)).toString());
        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

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

    //public static void main(String[] args) {
    //    //MsgPayload msgPayload = new MsgPayload(null, "&a钟鼓馔玉不足贵，&b&l但愿长醉不愿醒。陈王昔时宴平乐，&c斗酒十千恣欢谑。");
    //    String message = "&a钟鼓馔玉不足贵，&b&l但愿长醉不愿醒。陈王昔时宴平乐，&c斗酒十千恣欢谑。";
    //    String[] messagePiles = MessageUtil.convertMsg(message, 20);
    //    for (String s : messagePiles) {
    //        System.out.println(s);
    //    }
    //    //String msg = "人生呐  -";
    //    //System.out.println(msg);
    //    //System.out.println(msg.replaceAll("(^\\s+)", "$1$1").replaceAll("(\\s+$)", "$1$1"));
    //}
    //
}
