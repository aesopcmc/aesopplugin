package top.mcos.message.payload;

import top.mcos.util.epiclib.logger.ConsoleLogger;
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
        if(ConfigLoader.baseConfig.getSettingConfig().isNoticeActionbarEnabled()) {
            Lock lock = PlayerLock.getPlayerLock(player.getUniqueId().toString());
            boolean locked = false;
            try {
                //将消息排队发送，防止一个玩家同时收到多个消息
                if (lock.tryLock(ConfigLoader.baseConfig.getSettingConfig().getTrylockTimes(), TimeUnit.MILLISECONDS)) {
                    locked = true;
                    if(ConfigLoader.baseConfig.getSettingConfig().isDebug()) {
                        AesopPlugin.logger.log("当前执行的线程：【" + Thread.currentThread().getName() + "】");
                    }
                    AesopPlugin.nmsProvider.sendActionbar(player, messagePiles, ConfigLoader.baseConfig.getSettingConfig().getDelayTimes());
                }
            } catch (InterruptedException e) {
                if(ConfigLoader.baseConfig.getSettingConfig().isDebug()) {
                    AesopPlugin.logger.log(player.getName() + ":消息发送已被中断", ConsoleLogger.Level.WARN);
                }
            } finally {
                if (locked) {
                    lock.unlock();
                } else {
                    if(ConfigLoader.baseConfig.getSettingConfig().isDebug()) {
                        AesopPlugin.logger.log("线程【" + Thread.currentThread().getName() + "】放弃锁");
                    }
                }
            }
        }
        return true;
    }
}
