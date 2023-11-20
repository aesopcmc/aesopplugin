package top.mcos.message;

import com.epicnicity322.epicpluginlib.core.logger.ConsoleLogger;
import net.minecraft.server.network.PlayerConnection;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import top.mcos.AesopPlugin;
import top.mcos.scheduler.SchedulerHandler;
import top.mcos.scheduler.NoticeJob;
import top.mcos.util.MessageConvertUtil;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.*;

public final class MessageHandler {
    //public static Stack<MsgPayload> msgPayloadStack = new Stack<>();
    private static ConcurrentLinkedQueue<List<MsgPayload>> msgPayloadQueue = new ConcurrentLinkedQueue<>();
    public static Field networkManagerH;

    static {
        try {
            networkManagerH = PlayerConnection.class.getDeclaredField("h");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            AesopPlugin.logger.log("实例化网络管理H出错", ConsoleLogger.Level.ERROR);
        }
        networkManagerH.setAccessible(true);

        // 启动消息监听线程
        Bukkit.getScheduler().runTaskAsynchronously(AesopPlugin.getInstance(), bukkitTask -> {
            while (isEnable()) {
                // 获取一组消息。一组消息由一个消息单元和多个在线用户组成
                List<MsgPayload> poll = msgPayloadQueue.poll();
                if(poll!=null) {
                    // 消息滚动延迟，单位毫秒
                    long delayTimes = AesopPlugin.getInstance().getConfig().getLong("config.notice.bottom.delay-times");
                    final long dt = delayTimes <= 0 ? 100 : delayTimes;
                    long tryLockTimes = AesopPlugin.getInstance().getConfig().getLong("config.notice.bottom.trylock-times");
                    final long tt = tryLockTimes <= 0 ? 60000 : tryLockTimes;

                    for (MsgPayload msgPayload : poll) {
                        //Bukkit.getScheduler().callSyncMethod(AesopPlugin.getInstance(), () -> msgPayload.sendPacket(dt));
                        Bukkit.getScheduler().runTaskAsynchronously(AesopPlugin.getInstance(), () -> msgPayload.sendPacket(dt,tt));
                    }
                }
            }
            msgPayloadQueue.clear();
        });
    }

    private MessageHandler() { }

    /**
     * 监听消息队列，有消息，则发送
     */
    public static void init() {
        if(!isEnable()) return;
        msgPayloadQueue.clear();
        // 加载配置，注册定时任务，注入数据
        Map<String, Object> msgMap = AesopPlugin.getInstance().getConfig().getConfigurationSection("tasks.notice").getValues(false);
        msgMap.forEach((key,v)->{
            ConfigurationSection section = (ConfigurationSection)v;
            boolean enable = section.getBoolean("enable");
            if(enable) {
                String cron = section.getString("cron");
                String startStr = section.getString("start");
                Date start = null;
                if (StringUtils.isNotBlank(startStr)) {
                    try {
                        start = DateUtils.parseDate(startStr, "yyyy-MM-dd HH:mm:ss");
                    } catch (ParseException e) {
                        AesopPlugin.logger.log("start日期【" + startStr + "】转换出错，格式有误！", ConsoleLogger.Level.ERROR);
                    }
                }
                String endStr = section.getString("end");
                Date end = null;
                if (StringUtils.isNotBlank(endStr)) {
                    try {
                        end = DateUtils.parseDate(endStr, "yyyy-MM-dd HH:mm:ss");
                    } catch (ParseException e) {
                        AesopPlugin.logger.log("end日期【" + endStr + "】转换出错，格式有误！", ConsoleLogger.Level.ERROR);
                    }
                }
                String position = section.getString("position");
                String message = section.getString("message");
                Map<String, Object> jobParams = new HashMap<>();
                jobParams.put("position", position);
                jobParams.put("message", message);
                SchedulerHandler.registerJob(NoticeJob.class, key, "noticeGroup", start, end, cron, jobParams);
            }
        });
    }

    public static void clear() {
        msgPayloadQueue.clear();
    }

    /**
     * 发送消息给所有在线玩家
     * @param message 消息内容，支持颜色代码
     */
    public static void sendAllOnlinePlayers(String message) {
        if(isEnable() && !StringUtils.isBlank(message)) {
            // 获取玩家
            Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
            if(onlinePlayers.size()<1) return;

            //组装消息
            int displayWidth = AesopPlugin.getInstance().getConfig().getInt("config.notice.bottom.display-width");
            String[] messagePiles = MessageConvertUtil.convertMsg(message, displayWidth);

            //发送消息
            List<MsgPayload> list = new ArrayList<>();
            for (Player player : onlinePlayers) {
                list.add(new MsgPayload(player, messagePiles));
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
        if(isEnable()) {
            //组装消息
            int displayWidth = AesopPlugin.getInstance().getConfig().getInt("config.notice.bottom.display-width");
            String[] messagePiles = MessageConvertUtil.convertMsg(message, displayWidth);

            //发送消息
            List<MsgPayload> list = new ArrayList<>();
            list.add(new MsgPayload(player, messagePiles));
            msgPayloadQueue.offer(list);

        }
    }

    public static boolean isEnable() {
        return AesopPlugin.getInstance().getConfig().getBoolean("config.notice.bottom.enable");
    }
}
