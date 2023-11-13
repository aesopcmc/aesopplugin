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

        int parts = padMessage.length() - displayWidth;
        this.messagePiles = new String[parts];

        for(int i = 0; i<parts; i++) {
            String msg = padMessage.substring(i, displayWidth + i);
            msg = FullHalfChangeUtil.half2FullChange(msg, false);
            this.messagePiles[i] = msg.replaceAll("(^\\s+)", "$1$1").replaceAll("(\\s+$)", "$1$1");
        }
    }



    public static void main(String[] args) {
        //MsgPayload msgPayload = new MsgPayload(null, "人生得意须尽欢发及覅方法");
        //String[] messagePiles1 = msgPayload.getMessagePiles();
        //for (String s : messagePiles1) {
        //    System.out.println(s);
        //}

        String msg = "人生呐  -";
        System.out.println(msg);
        System.out.println(msg.replaceAll("(^\\s+)", "$1$1").replaceAll("(\\s+$)", "$1$1"));
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
            // 颜色
            String s = ChatColor.translateAlternateColorCodes('&', "&c" + messagePile);
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
