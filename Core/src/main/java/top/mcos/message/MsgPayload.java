package top.mcos.message;

import com.epicnicity322.epicpluginlib.core.logger.ConsoleLogger;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;
import top.mcos.AesopPlugin;
import top.mcos.util.FullHalfChangeUtil;

public class MsgPayload {
    private Player player;
    private String message;
    private String[] messagePiles;
    private int displayWidth;

    public MsgPayload(Player player, String message) {
        this.player = player;
        this.message = StringUtils.trim(message);
        int displayWidth = AesopPlugin.getInstance().getConfig().getInt("tasks.publish-anno.display-width");
        this.displayWidth = displayWidth == 0 ? 10 : displayWidth;
        this.initMsg();
    }

    private void initMsg() {
        String padMessage = StringUtils.center(this.message, this.message.length()+displayWidth*2+1, " ");
        int colorPrefixCount = StringUtils.countMatches(padMessage, "&") * 2;//统计颜色占位符个数
        int total = padMessage.length();
        this.messagePiles = new String[total - displayWidth - colorPrefixCount +1];
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
                    this.messagePiles[p++] = res;//FullHalfChangeUtil.half2FullChange(res, false);
                    break;
                }
            }
            i++;
        }
    }

    /**
     * 旧的写法
     */
    //private void initMsg() {
    //    String padMessage = StringUtils.center(this.message, this.message.length()+displayWidth*2+1, " ");
    //    int parts = padMessage.length() - displayWidth;
    //    this.messagePiles = new String[parts];
    //    padMessage = FullHalfChangeUtil.half2FullChange(padMessage, false);
    //    for(int i = 0; i<parts; i++) {
    //        String msg = padMessage.substring(i, displayWidth + i);
    //        this.messagePiles[i] = msg.replaceAll("(^\\s+)", "$1$1").replaceAll("(\\s+$)", "$1$1");
    //    }
    //}

    public static void main(String[] args) {
        //MsgPayload msgPayload = new MsgPayload(null, "&a钟鼓馔玉不足贵，&b&l但愿长醉不愿醒。陈王昔时宴平乐，&c斗酒十千恣欢谑。");
        MsgPayload msgPayload = new MsgPayload(null, "&a钟鼓馔玉不足贵，&b&l但愿长醉不愿醒。陈王昔时宴平乐，&c斗酒十千恣欢谑。");
        String[] messagePiles1 = msgPayload.getMessagePiles();
        for (String s : messagePiles1) {
            System.out.println(s);
        }
        //String msg = "人生呐  -";
        //System.out.println(msg);
        //System.out.println(msg.replaceAll("(^\\s+)", "$1$1").replaceAll("(\\s+$)", "$1$1"));
    }

    public String getMessage() {
        return message;
    }

    public String[] getMessagePiles() {
        return messagePiles;
    }

    public void setMessagePiles(String[] messagePiles) {
        this.messagePiles = messagePiles;
    }

    public int getDisplayWidth() {
        return displayWidth;
    }

    public void setDisplayWidth(int displayWidth) {
        this.displayWidth = displayWidth;
    }

    /**
     * 发送数据包
     * @param afterDelay
     */
    public void sendPacket(long afterDelay) {
        NetworkManager networkManager;
        try {
            networkManager = (NetworkManager) SchedulerMessageHandle.networkManagerH.get(((CraftPlayer) player).getHandle().b);
        } catch (IllegalAccessException e) {
            AesopPlugin.logger.log("实例化网络管理器出错", ConsoleLogger.Level.ERROR);
            e.printStackTrace();
            return;
        }

        for (String messagePile : messagePiles) {
            // 颜色格式转换 & -> §
            String s = ChatColor.translateAlternateColorCodes('&', messagePile);

            var packet = new ClientboundSetActionBarTextPacket(CraftChatMessage.fromStringOrNull(s));
            networkManager.a((Packet<?>) packet);
            try {
                Thread.sleep(afterDelay);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
