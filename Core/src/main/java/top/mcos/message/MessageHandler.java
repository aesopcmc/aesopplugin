package top.mcos.message;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import top.mcos.AesopPlugin;
import top.mcos.config.ConfigLoader;
import top.mcos.message.payload.ActionbarMessagePayload;
import top.mcos.message.payload.TitleMessagePayload;
import top.mcos.util.MessageUtil;

import java.util.*;
import java.util.concurrent.*;

/**
 * 消息发送处理器
 */
public final class MessageHandler {
    /**
     * 中断消息发送。用于当插件重载时，让正在发送消息的线程尽快退出
     */
    private static boolean sendBreak = false;
    /**
     * 消息队列
     */
    private static ConcurrentLinkedQueue<List<MessagePayload>> msgPayloadQueue = new ConcurrentLinkedQueue<>();

    private MessageHandler() { }

    public static void initQueue() {
        // 启动消息监听线程
        Bukkit.getScheduler().runTaskAsynchronously(AesopPlugin.getInstance(), bukkitTask -> {
            while (AesopPlugin.getInstance().isPluginActive()) {
                // 获取一组消息。一组消息由一个消息单元和多个在线用户组成
                List<MessagePayload> poll = msgPayloadQueue.poll();
                if(poll!=null) {
                    for (MessagePayload messagePayload : poll) {
                        //Bukkit.getScheduler().callSyncMethod(AesopPlugin.getInstance(), () -> msgPayload.sendPacket(dt));
                        Bukkit.getScheduler().runTaskAsynchronously(AesopPlugin.getInstance(), messagePayload::sendPacket);
                    }
                }
            }
            msgPayloadQueue.clear();
        });
        AesopPlugin.logger.log("已启动消息监听");
    }

    /**
     * 推送滚动消息进队列
     * @param message 消息内容，支持颜色代码
     */
    public static void pushActionbarMessage(String message) {
        if(ConfigLoader.baseConfig.getSettingConfig().isNoticeActionbarEnabled() && StringUtils.isNotBlank(message)) {
            // 获取玩家
            Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
            if(onlinePlayers.size()<1) return;

            //组装消息
            String[] messagePiles = MessageUtil.convertMsg(message, ConfigLoader.baseConfig.getSettingConfig().getDisplayWidth());

            //发送消息
            List<MessagePayload> list = new ArrayList<>();
            for (Player player : onlinePlayers) {
                list.add(new ActionbarMessagePayload(player, messagePiles));
            }
            msgPayloadQueue.offer(list);
        }
    }

    /**
     * 推送标题消息进队列
     * @param message 消息内容，支持颜色代码
     * @param subMessage 消息内容，支持颜色代码
     */
    public static void pushTitleMessage(String message, String subMessage) {
        if(ConfigLoader.baseConfig.getSettingConfig().isNoticeTitleEnabled() && StringUtils.isNotBlank(message)) {
            // 获取玩家
            Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
            if(onlinePlayers.size()<1) return;

            List<MessagePayload> list = new ArrayList<>();
            for (Player onlinePlayer : onlinePlayers) {
                list.add(new TitleMessagePayload(onlinePlayer, message, subMessage));
            }

            msgPayloadQueue.offer(list);
        }
    }


    /**
     * 发送消息给指定用户
     * @param player 用户
     * @param message 消息内容，支持颜色代码
     */
    public static void sendToPlayer(Player player, String message) {
        if(ConfigLoader.baseConfig.getSettingConfig().isNoticeActionbarEnabled()) {
            //组装消息
            String[] messagePiles = MessageUtil.convertMsg(message, ConfigLoader.baseConfig.getSettingConfig().getDisplayWidth());

            //发送消息
            List<MessagePayload> list = new ArrayList<>();
            list.add(new ActionbarMessagePayload(player, messagePiles));
            msgPayloadQueue.offer(list);
        }
    }

    /**
     * 清理消息队列
     */
    public static void clearQueue() {
        msgPayloadQueue.clear();
    }

    public static boolean isSendBreak() {
        return sendBreak;
    }

    /**
     * 中断发送消息
     * @param sendBreak true中断 false正常
     */
    public static synchronized void setSendBreak(boolean sendBreak) {
        MessageHandler.sendBreak = sendBreak;
    }

}
