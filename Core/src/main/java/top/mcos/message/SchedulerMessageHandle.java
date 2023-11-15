package top.mcos.message;

import com.epicnicity322.epicpluginlib.core.logger.ConsoleLogger;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import top.mcos.AesopPlugin;
import top.mcos.util.MessageConvertUtil;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SchedulerMessageHandle {
    //public static Stack<MsgPayload> msgPayloadStack = new Stack<>();
    public static ConcurrentLinkedQueue<MsgPayload> msgPayloadQueue = new ConcurrentLinkedQueue<>();
    public static Field networkManagerH;

    static {
        try {
            networkManagerH = PlayerConnection.class.getDeclaredField("h");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            AesopPlugin.logger.log("实例化网络管理H出错", ConsoleLogger.Level.ERROR);
        }
        networkManagerH.setAccessible(true);
    }

    private SchedulerMessageHandle() { }

    /**
     * 监听消息队列，有消息，则发送
     */
    public static void initScheduler() {
        long delayTimes = AesopPlugin.getInstance().getConfig().getLong("tasks.publish-anno.speed");
        final long dt = delayTimes <= 0 ? 100 : delayTimes;

        Bukkit.getScheduler().runTaskAsynchronously(AesopPlugin.getInstance(), bukkitTask -> {
            while (isEnable()) {
                MsgPayload pop = msgPayloadQueue.poll();
               if(pop!=null) {
                   Bukkit.getScheduler().runTaskAsynchronously(AesopPlugin.getInstance(), () -> pop.sendPacket(dt));
               }
            }
            msgPayloadQueue.clear();
        });
    }

    /**
     * 发送消息给所有在线玩家
     * @param message 消息内容，支持颜色代码
     */
    public static void sendAllOnlinePlayers(String message) {
        if(isEnable()) {
            //组装消息
            int displayWidth = AesopPlugin.getInstance().getConfig().getInt("tasks.publish-anno.display-width");
            String[] messagePiles = MessageConvertUtil.convertMsg(message, displayWidth);

            //发送消息
            Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
            for (Player player : onlinePlayers) {
                msgPayloadQueue.offer(new MsgPayload(player, messagePiles));
            }
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
            int displayWidth = AesopPlugin.getInstance().getConfig().getInt("tasks.publish-anno.display-width");
            String[] messagePiles = MessageConvertUtil.convertMsg(message, displayWidth);

            //发送消息
            msgPayloadQueue.offer(new MsgPayload(player, messagePiles));
        }
    }

    public static boolean isEnable() {
        return AesopPlugin.getInstance().getConfig().getBoolean("tasks.publish-anno.enable");
    }
}
