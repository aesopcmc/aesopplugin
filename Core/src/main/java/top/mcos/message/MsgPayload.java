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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

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
     *
     * @param afterDelay 消息延迟
     * @param tt
     */
    public boolean sendPacket(long afterDelay, long tt) {
        NetworkManager networkManager;
        try {
            networkManager = (NetworkManager) MessageHandler.networkManagerH.get(((CraftPlayer) player).getHandle().b);
        } catch (IllegalAccessException e) {
            AesopPlugin.logger.log("实例化网络管理器出错", ConsoleLogger.Level.ERROR);
            e.printStackTrace();
            return false;
        }
        Lock lock = PlayerLock.getPlayerLock(player.getUniqueId().toString());
        boolean locked = false;
        try {
            //将消息排队发送，防止一个玩家同时收到多个消息
            if(lock.tryLock(tt, TimeUnit.MILLISECONDS)) {
                locked = true;
                AesopPlugin.logger.log("当前执行的线程：【"+Thread.currentThread().getName()+"】");
                for (String messagePile : messagePiles) {
                    // 颜色格式转换 & -> §
                    // TODO 添加消息前缀
                    String prefix = "";
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
        } catch (InterruptedException e) {
            AesopPlugin.logger.log(player.getName() + ":消息发送已被中断", ConsoleLogger.Level.WARN);
        } finally {
            if(locked) {
                lock.unlock();
            } else {
                AesopPlugin.logger.log("线程【"+Thread.currentThread().getName()+"】放弃锁");
            }
        }

        return true;
    }
}
