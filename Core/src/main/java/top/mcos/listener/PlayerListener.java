package top.mcos.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import top.mcos.message.PlayerLock;

public class PlayerListener implements Listener {
    //@EventHandler(priority = EventPriority.HIGH)
    //public void onPlayerJoin(PlayerJoinEvent event) {
    //    String uniqueId = event.getPlayer().getUniqueId().toString();
    //    playLocks.put(uniqueId, new ReentrantLock());
    //}

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        PlayerLock.removeLock(event.getPlayer().getUniqueId().toString());
    }

}
