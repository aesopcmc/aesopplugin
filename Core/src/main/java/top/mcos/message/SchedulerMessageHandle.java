package top.mcos.message;

import com.epicnicity322.epicpluginlib.core.logger.ConsoleLogger;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import top.mcos.AesopPlugin;

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

    public static void sendAllOnlinePlayers(String message) {
        if(isEnable()) {
            Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
            for (Player player : onlinePlayers) {
                msgPayloadQueue.offer(new MsgPayload(player, message));
            }
        }
    }

    public static void sendToPlayer(Player player, String message) {
        if(isEnable()) {
            msgPayloadQueue.offer(new MsgPayload(player, message));
        }
    }

    public static boolean isEnable() {
        return AesopPlugin.getInstance().getConfig().getBoolean("tasks.publish-anno.enable");
    }
}
