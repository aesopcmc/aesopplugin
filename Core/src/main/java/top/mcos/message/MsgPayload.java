package top.mcos.message;

import com.epicnicity322.epicpluginlib.core.logger.ConsoleLogger;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;
import top.mcos.AesopPlugin;

public class MsgPayload {
    private Player player;
    private String[] messagePiles;
    public MsgPayload(Player player, String[] messagePiles) {
        this.player = player;
        this.messagePiles = messagePiles;
    }


    public String[] getMessagePiles() {
        return messagePiles;
    }

    public void setMessagePiles(String[] messagePiles) {
        this.messagePiles = messagePiles;
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
            // TODO 添加消息前缀
            String prefix="";
            String s = ChatColor.translateAlternateColorCodes('&', prefix + messagePile);

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
