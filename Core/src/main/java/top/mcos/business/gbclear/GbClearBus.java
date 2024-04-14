package top.mcos.business.gbclear;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import top.mcos.AesopPlugin;
import top.mcos.business.Bus;
import top.mcos.config.ConfigLoader;
import top.mcos.message.MessageHandler;
import top.mcos.util.MessageUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GbClearBus implements Bus {
    private BukkitTask periodRunTask;

    // 冷却时间。单位毫秒 （默认10分钟）
    private int coolingTime = 600000;
    // 计算时间。当计算时间超过冷却时间时，触发一次垃圾清理
    private int calcTime = 0;

    // 已提醒的时间记录
    private List<Integer> timeLeftRun;
    // 倒计时进度条
    private static BossBar bossBar;

    @Override
    public boolean load() {
        if (ConfigLoader.gbClearConfig.isEnable()) {
            coolingTime = ConfigLoader.gbClearConfig.getClearPeriod();
            calcTime = 0;
            timeLeftRun = new ArrayList<>();
            this.startListen();
        }
        return false;
    }

    @Override
    public boolean unload() {
        if (periodRunTask != null) periodRunTask.cancel();
        return true;
    }

    @Override
    public boolean reload() {
        unload();
        load();
        return true;
    }

    /**
     * 启动时间监听
     */
    private void startListen() {
        this.periodRunTask = Bukkit.getScheduler().runTaskTimer(AesopPlugin.getInstance(), () -> {
            if (calcTime >= coolingTime) {
                calcTime = 0;
                timeLeftRun.clear();
                clear();
            } else {
                calcTime += 1000;
                this.notice();
            }
        }, 100, 20);

        AesopPlugin.logger.log("&a已开启掉落物定时清理");
    }

    private void notice() {
        if (ConfigLoader.gbClearConfig.isNoticeEnable()) {
            List<Integer> noticeTimeleft = ConfigLoader.gbClearConfig.getNoticeTimeleft();
            int timeleft = coolingTime - calcTime;
            for (Integer tl : noticeTimeleft) {
                if (timeleft <= tl && !timeLeftRun.contains(tl)) {
                    timeLeftRun.add(tl);
                    MessageHandler.sendBroadcast(ConfigLoader.gbClearConfig.getPrefix(), "&e将在 &b" + tl / 1000 + " &e秒后清理所有掉落物！", null);
                }
            }
            // 倒计时提醒
            int noticeCountdownIn = ConfigLoader.gbClearConfig.getNoticeCountdownIn();
            if (timeleft <= noticeCountdownIn && timeleft >= 0) {
                if (ConfigLoader.gbClearConfig.getNoticeCountdownType() == 1) {
                    double p = timeleft * 1.0 / noticeCountdownIn;
                    this.sendBossBar(ConfigLoader.gbClearConfig.getPrefix(), "&e清理倒计时 &b" + timeleft / 1000, p);
                } else if (ConfigLoader.gbClearConfig.getNoticeCountdownType() == 2) {
                    MessageHandler.sendBroadcast(ConfigLoader.gbClearConfig.getPrefix(), "&e清理倒计时 &b" + timeleft / 1000, null);
                }
            }
        }
    }

    private void sendBossBar(String prefix, String message, double progress) {
        if (bossBar == null) {
            bossBar = Bukkit.createBossBar("", BarColor.GREEN, BarStyle.SOLID);
        }
        Collection<? extends Player> receivers = Bukkit.getOnlinePlayers();
        for (Player p : receivers) {
            bossBar.addPlayer(p);
        }
        bossBar.setTitle(MessageUtil.colorize(prefix + message));
        bossBar.setProgress(progress);
        if (progress > 0) {
            bossBar.setVisible(true);
        } else {
            bossBar.setVisible(false);
        }
    }

    /**
     * 执行掉落物清理
     */
    public void clear() {
        MessageHandler.sendBroadcast(ConfigLoader.gbClearConfig.getPrefix(), "&a开始清理掉落物&#88e588>&#c3f2c3>&#ffffff>", null);
        List<World> worlds = Bukkit.getWorlds();
        int removedTotalCount = 0;
        for (World world : worlds) {
            //String x = args[2];
            //String z = args[3];
            int removedCount = 0;
            //Location location = new Location(world, Double.parseDouble(x), 0, Double.parseDouble(z));
            List<Entity> entities = world.getEntities();
            //Entity[] entities = world.getChunkAt(location).getEntities();

            //"slimefun", "slimefun_item"
            for (Entity entity : entities) {

                // TODO 未加载的区块，清理掉落
                // TODO Listen for ChunkLoadEvent, store the chunk in a Map<Chunk, Long>, the long being the current system when it was loaded. Make an task to loop through all entries in the map and check the current time based on when it was entered, if more than two mins then get all entities in the chunk and clear
                //https://www.spigotmc.org/threads/clear-items-in-unloaded-chunk.438289/
                //AesopPlugin.logger.log("查看实体：" + entity.getName() + "， 类名：" + entity.getClass().getName());

                if (AesopPlugin.nmsProvider.isCraftItem(entity, null)) {
                    entity.remove();
                    removedCount++;
                    if (ConfigLoader.gbClearConfig.isDebug()) {
                        AesopPlugin.logger.log("移除实体：" + entity.getName() + "， 类名：" + entity.getClass().getName());
                    }
                }
            }
            if (ConfigLoader.gbClearConfig.isDebug()) {
                AesopPlugin.logger.log("&a已移除了世界“" + world.getName() + "”中的" + removedCount + "个物品");
            }

            removedTotalCount += removedCount;
        }

        MessageHandler.sendBroadcast(ConfigLoader.gbClearConfig.getPrefix(), "&a本次共清理了 &e" + removedTotalCount + " &a个垃圾。", null);
    }


}
