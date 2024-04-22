package top.mcos.business.gbclear;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.scheduler.BukkitTask;
import top.mcos.AesopPlugin;
import top.mcos.business.Bus;
import top.mcos.config.ConfigLoader;
import top.mcos.message.MessageHandler;
import top.mcos.util.MessageUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GbClearBus implements Bus {
    private BukkitTask periodRunTask;
    private boolean debug;

    // 被排除的世界
    private List<String> excludeWorld;

    // 1. 是否自动清理掉落物
    private boolean autoClearEnable;
    // 自动清理周期。单位：毫秒 （默认10分钟）
    private int coolingTime = 600000;
    // 计算时间。当计算时间超过冷却时间时，触发一次垃圾清理
    private int calcTime = 0;

    // 已提醒的时间记录
    private List<Integer> timeLeftRun;
    // 倒计时进度条
    private static BossBar bossBar;


    // 2. 是否清理未加载的掉落物
    private boolean unloadCleanEnable;
    // 存储未加载的掉落物 TODO 处理可能出现的内存溢出问题
    private Map<String, Long> unloadItemStore = new HashMap<>();
    // 未加载的掉落物保留时长。单位：毫秒（默认10分钟）
    private int unloadKeepTime=600000;

    @Override
    public boolean load() {
        debug = ConfigLoader.gbClearConfig.isDebug();

        autoClearEnable = ConfigLoader.gbClearConfig.isAutoClearEnable();
        coolingTime = ConfigLoader.gbClearConfig.getAutoClearPeriod();
        calcTime = 0;
        timeLeftRun = new ArrayList<>();

        unloadCleanEnable = ConfigLoader.gbClearConfig.isUnloadCleanEnable();
        unloadKeepTime = ConfigLoader.gbClearConfig.getUnloadKeepTime();

        excludeWorld = ConfigLoader.gbClearConfig.getExcludeWorlds();

        this.startAutoClearListen();
        return true;
    }

    @Override
    public boolean unload() {
        if (periodRunTask != null) periodRunTask.cancel();
        this.sendBossBar(ConfigLoader.gbClearConfig.getPrefix(), "", 0);
        return true;
    }

    @Override
    public boolean reload() {
        unload();
        load();
        return true;
    }

    /**
     * 【未加载的掉落物清理】
     * 区块卸载时保存掉落物
     *
     * @param unloadEvent 区块卸载事件
     */
    public void chunkUnloadEvent(ChunkUnloadEvent unloadEvent) {
        if(!unloadCleanEnable) return;
        if(excludeWorld.contains(unloadEvent.getWorld().getName())) return;
        //Entity[] entities = unloadEvent.getChunk().getEntities();
        List<Entity> entities = unloadEvent.getWorld().getEntities();
        long nowTime = new Date().getTime();
        for (Entity entity : entities) {
            if (AesopPlugin.nmsProvider.isCraftItem(entity, null)) {
                String uid = entity.getUniqueId().toString();
                if(!unloadItemStore.containsKey(uid)) {
                    Location location = entity.getLocation();
                    String lstr = location.getX() + " " + location.getY() + " " + location.getZ();
                    String name = entity.getName();
                    unloadItemStore.put(uid, nowTime);
                    if(debug) {
                        AesopPlugin.logger.log("&e卸载并加入缓存：" + uid + "(" + name + "),location:" + lstr);
                    }
                }
            }
        }
    }

    /**
     * 【未加载的掉落物清理】
     * 区块加载时，若超时，立即移除掉落物，清除缓存
     *
     * TODO 未加载的区块，清理掉落
     * TODO Listen for ChunkLoadEvent, store the chunk in a Map<Chunk, Long>, the long being the current system when it was loaded. Make an task to loop through all entries in the map and check the current time based on when it was entered, if more than two mins then get all entities in the chunk and clear
     * https://www.spigotmc.org/threads/clear-items-in-unloaded-chunk.438289/
     *
     * @param loadEvent 区块加载事件
     */
    public void chunkLoadEvent(ChunkLoadEvent loadEvent) {
        if(!unloadCleanEnable) return;
        if(excludeWorld.contains(loadEvent.getWorld().getName())) return;

        Entity[] entities = loadEvent.getChunk().getEntities();
        long time = new Date().getTime();
        int cleanedCount = 0;
        for (Entity entity : entities) {
            //AesopPlugin.logger.log("&e加载实体："+uid+"("+name+"),location:"+location.getX()+" "+location.getY()+" "+location.getZ());

            if (unloadItemStore.containsKey(entity.getUniqueId().toString()) && AesopPlugin.nmsProvider.isCraftItem(entity, null)) {
                Location location = entity.getLocation();
                String lstr = location.getX()+" "+location.getY()+" "+location.getZ();
                String name = entity.getName();
                String uid = entity.getUniqueId().toString();
                // 移除实体
                Long recordTime = unloadItemStore.get(uid);
                if((time - recordTime) >= unloadKeepTime ) {
                    entity.remove();
                    unloadItemStore.remove(uid);
                    cleanedCount++;
                    if(debug) {
                        AesopPlugin.logger.log("&e移除实体：" + uid + "(" + name + "),location:" + lstr);
                    }
                }
            }
        }
        if(cleanedCount>0) {
            AesopPlugin.logger.log("清理未加载掉落物：" + cleanedCount);
        }
    }

    /**
     * 【未加载的掉落物清理】
     * 物品自然消失事件（倒计时结束），清除缓存
     *
     * @param itemDespawnEvent 事件
     */
    public void itemDespawnEvent(ItemDespawnEvent itemDespawnEvent) {
        if(!unloadCleanEnable) return;
        if(excludeWorld.contains(itemDespawnEvent.getEntity().getWorld().getName())) return;

        Item entity = itemDespawnEvent.getEntity();
        unloadItemStore.remove(entity.getUniqueId().toString());
        if(debug) {
            AesopPlugin.logger.log("&e消失，移除缓存：" + entity.getName() + " " + entity.getLocation());
        }
    }

    /**
     * 【未加载的掉落物清理】
     * 捡起物品，清除缓存
     *
     * @param pickupItemEvent 捡起物品事件
     */
    public void pickupItemEvent(EntityPickupItemEvent pickupItemEvent) {
        if(!unloadCleanEnable) return;
        if(excludeWorld.contains(pickupItemEvent.getItem().getWorld().getName())) return;

        Item entity = pickupItemEvent.getItem();
        unloadItemStore.remove(entity.getUniqueId().toString());
        if(debug) {
            AesopPlugin.logger.log("&e捡起，移除缓存：" + entity.getName() + " " + entity.getLocation());
        }
    }

    /**
     * 清理已加载的掉落物
     */
    public void clearLoadedItems() {
        MessageHandler.sendBroadcast(ConfigLoader.gbClearConfig.getPrefix(), "&a开始清理掉落物&#88e588>&#c3f2c3>&#ffffff>", null);
        List<World> worlds = Bukkit.getWorlds();
        int removedTotalCount = 0;
        for (World world : worlds) {
            if(excludeWorld.contains(world.getName())) continue;

            int removedCount = 0;
            List<Entity> entities = world.getEntities();
            //Entity[] entities = world.getChunkAt(location).getEntities();

            //"slimefun", "slimefun_item"
            for (Entity entity : entities) {
                //AesopPlugin.logger.log("查看实体：" + entity.getName() + "， 类名：" + entity.getClass().getName());

                if (AesopPlugin.nmsProvider.isCraftItem(entity, null)) {
                    entity.remove();
                    unloadItemStore.remove(entity.getUniqueId().toString());
                    removedCount++;
                    if (debug) {
                        AesopPlugin.logger.log("移除已加载的实体：" + entity.getName() + "， 类名：" + entity.getClass().getName());
                    }
                }
            }
            if (debug) {
                AesopPlugin.logger.log("&a已移除了世界“" + world.getName() + "”中的" + removedCount + "个物品");
            }

            removedTotalCount += removedCount;
        }

        MessageHandler.sendBroadcast(ConfigLoader.gbClearConfig.getPrefix(), "&a清理完成，本次共清理了 &e" + removedTotalCount + " &a个掉落物。", null);
    }

    /**
     * 自动清理监听
     */
    private void startAutoClearListen() {
        if (!autoClearEnable) return;

        this.periodRunTask = Bukkit.getScheduler().runTaskTimer(AesopPlugin.getInstance(), () -> {
            if (calcTime >= coolingTime) {
                calcTime = 0;
                timeLeftRun.clear();
                clearLoadedItems();
            } else {
                calcTime += 1000;
                this.notice();
            }
        }, 100, 20);

        AesopPlugin.logger.log("&a已开启掉落物定时清理");
    }

    /**
     * 消息通知
     * 文字 + bossBar
     */
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

    public Map<String, Long> getUnloadItemStore() {
        return unloadItemStore;
    }


}
