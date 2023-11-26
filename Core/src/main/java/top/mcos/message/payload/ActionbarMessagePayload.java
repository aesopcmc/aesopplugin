package top.mcos.message.payload;

import com.epicnicity322.epicpluginlib.core.logger.ConsoleLogger;
import org.bukkit.entity.Player;
import top.mcos.AesopPlugin;
import top.mcos.config.ConfigLoader;
import top.mcos.listener.PlayerLock;
import top.mcos.message.MessagePayload;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * 滚动消息载体
 */
public class ActionbarMessagePayload implements MessagePayload {
    private Player player;
    private String[] messagePiles;
    public ActionbarMessagePayload(Player player, String[] messagePiles) {
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
     */
    public boolean sendPacket() {
        if(ConfigLoader.commonConfig.isNoticeActionbarEnabled()) {
            Lock lock = PlayerLock.getPlayerLock(player.getUniqueId().toString());
            boolean locked = false;
            try {
                //将消息排队发送，防止一个玩家同时收到多个消息
                if (lock.tryLock(ConfigLoader.commonConfig.getTrylockTimes(), TimeUnit.MILLISECONDS)) {
                    locked = true;
                    AesopPlugin.logger.log("当前执行的线程：【" + Thread.currentThread().getName() + "】");
                    AesopPlugin.nmsProvider.sendActionbar(player, messagePiles, ConfigLoader.commonConfig.getDelayTimes());
                }
            } catch (InterruptedException e) {
                AesopPlugin.logger.log(player.getName() + ":消息发送已被中断", ConsoleLogger.Level.WARN);
            } finally {
                if (locked) {
                    lock.unlock();
                } else {
                    AesopPlugin.logger.log("线程【" + Thread.currentThread().getName() + "】放弃锁");
                }
            }
        }
        return true;
    }
}
