package top.mcos.business.regen;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.RemovalStrategy;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.command.ConsoleCommandSender;
import org.popcraft.chunky.api.ChunkyAPI;
import top.mcos.AesopPlugin;
import top.mcos.business.Bus;
import top.mcos.business.regen.config.sub.RgWorldConfig;
import top.mcos.config.ConfigLoader;
import top.mcos.hook.HookHandler;
import top.mcos.hook.providers.ChunkyProvider;
import top.mcos.hook.providers.MultiverseProvider;
import top.mcos.hook.providers.WorldGuardProvider;
import top.mcos.message.MessageHandler;
import top.mcos.scheduler.SchedulerHandler;

import java.util.List;
import java.util.Map;

/**
 * 世界重置业务
 */
public class RegenBus implements Bus {
    private MVWorldManager mvWorldManager;
    private WorldGuard worldGuard;
    public RegenBus() {
        // TODO 服务器启动完毕后，会执行该任务一次
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(AesopPlugin.getInstance(), ()->{
            List<RgWorldConfig> rgWorldConfigs = ConfigLoader.rgConfig.getRgWorldConfigs();
            for (RgWorldConfig rgwConfig : rgWorldConfigs) {
                if(rgwConfig.isEnable()) {
                    String createAt = rgwConfig.getCreateAt();
                    String[] split = createAt.split(":");
                    if("RESTART".equals(split[0]) && Bukkit.getServer().getWorld(rgwConfig.getKey())==null) {
                        this.createWorld(rgwConfig);
                    }
                }
            }
        }, 100);
    }

    @Override
    public boolean load() {
        MultiverseProvider multiverseProvider = HookHandler.getMultiverseProvider();
        if(!multiverseProvider.isLoaded()) {
            AesopPlugin.logger.log("&e未检测到MultiverseCore插件，已跳过世界重置");
            return false;
        }

        MultiverseCore core = multiverseProvider.getAPI();
        this.mvWorldManager = core.getMVWorldManager();

        WorldGuardProvider worldGuardProvider = HookHandler.getWorldGuardProvider();
        if(worldGuardProvider.isLoaded()) {
            worldGuard = worldGuardProvider.getAPI();
        }

        // 注册定时任务
        List<RgWorldConfig> rgWorldConfigs = ConfigLoader.rgConfig.getRgWorldConfigs();
        for (RgWorldConfig rgwConfig : rgWorldConfigs) {
            SchedulerHandler.getAllJob().add(rgwConfig);
            SchedulerHandler.registerJob(rgwConfig);
        }

        return true;
    }

    @Override
    public boolean unload() {
        // 取消注册定时任务
        //List<RgWorldConfig> rgWorldConfigs = ConfigLoader.rgConfig.getRgWorldConfigs();
        //for (RgWorldConfig rgwConfig : rgWorldConfigs) {
        //    SchedulerHandler.unRegisterJob(rgwConfig);
        //}
        return true;
    }

    @Override
    public boolean reload() {
        unload();
        load();
        return true;
    }

    /**
     * 删除世界
     * @param config 世界配置
     */
    public void deleteWorld(RgWorldConfig config) {
        if(mvWorldManager==null) {
            AesopPlugin.logger.log("&e未检测到MultiverseCore插件，已跳过世界重置");
            return;
        }
        String createAt = config.getCreateAt();
        String[] split = createAt.split(":");

        if(Bukkit.getWorld(config.getKey())==null) {
            if("NOW".equals(split[0])) {
                createWorld(config);
            } else if ("RESTART".equals(split[0])) {
                AesopPlugin.logger.log("&a世界【" + config.getKey() + "】将在服务器重启后开始创建");
            } else {
                ///
            }
            return;
        }

        String aliasWorldName = mvWorldManager.getMVWorld(config.getKey()).getAlias();

        // 准备删除世界消息
        Bukkit.getScheduler().runTask(AesopPlugin.getInstance(), () -> {
            // 执行删除
            MessageHandler.sendBroadcast(AesopPlugin.logger.getPrefix(), "&e>>>>>开始删除世界【"+config.getKey()+"】.", null);

            World world = Bukkit.getWorld(config.getKey());
            removeRegion(world, config.isDeleteRegion());
            mvWorldManager.deleteWorld(config.getKey(), true, true);

            // 世界已删除消息
            MessageHandler.sendBroadcast(AesopPlugin.logger.getPrefix(), "&e世界【"+config.getKey()+"】已删除.", null);

            if("NOW".equals(split[0])) {
                createWorld(config);
            } else if ("RESTART".equals(split[0])) {
                AesopPlugin.logger.log("&c世界【" + aliasWorldName + "】将在服务器重启后开始创建");
            } else {
                ///
            }
        });

    }

    /**
     * 创建世界
     * @param config 世界配置
     */
    public void createWorld(RgWorldConfig config) {
        if(mvWorldManager==null) {
            AesopPlugin.logger.log("&e未检测到MultiverseCore插件，已跳过世界重置");
            return;
        }
        Bukkit.getScheduler().runTask(AesopPlugin.getInstance(), () -> {
            String createAt = config.getCreateAt();
            String[] split = createAt.split(":");

            World.Environment env = World.Environment.valueOf(split[1]);
            String seedString = "null".equalsIgnoreCase(split[2]) ? null : split[2];
            String generator = "null".equalsIgnoreCase(split[3]) ? null : split[3];
            MessageHandler.sendBroadcast(AesopPlugin.logger.getPrefix(), "&a>>>>>开始创建世界【"+config.getKey()+"】.", null);
            // mvWorldManager.regenWorld();需要在同步环境中执行，故此使用bukkit的同步任务方法runTask执行
            mvWorldManager.addWorld(config.getKey(), env, seedString, WorldType.NORMAL, true, generator, true);
            World world = Bukkit.getServer().getWorld(config.getKey());
            setGameRues(world, config.getCreatedGamerules());
            runCommand(config.getCreatedCommands());
            chunkLoad(config.getKey(), config.getChunkyLoadRadius());

            String aliasWorldName = mvWorldManager.getMVWorld(config.getKey()).getAlias();
            // 世界已完成创建
            MessageHandler.sendBroadcast(AesopPlugin.logger.getPrefix(), "&a世界【"+aliasWorldName+"】 已重置完成，快去探索新天地吧~", null);

            ///*
            //发送滚动消息提醒
            // */
            //String afterNoticeKey = config.getLoadedNoticeKey();
            //if(StringUtils.isNotBlank(afterNoticeKey)) {
            //    Map<String, NoticeConfig> noticeConfigMap = ConfigLoader.baseConfig.getNoticeConfigs()
            //            .stream().collect(Collectors.toMap(NoticeConfig::getKey, c -> c));
            //    // 获取消息通知配置
            //    NoticeConfig noticeConfig = noticeConfigMap.get(afterNoticeKey);
            //    // 取消注册消息
            //    SchedulerHandler.unRegisterJob(noticeConfig);
            //    // 更改配置
            //    noticeConfig.setEnable(true);
            //    String message = config.getLoadedNoticeMessage().replace("{world-name}", aliasWorldName);
            //    noticeConfig.setMessage(message);
            //    noticeConfig.setStart(DateUtils.addHours(new Date(), config.getLoadedNoticeDelayHours()));
            //    noticeConfig.setEnd(DateUtils.addHours(noticeConfig.getStart(), config.getLoadedNoticeKeepHours()));
            //    // 保存配置
            //    ConfigLoader.saveConfig(noticeConfig);
            //    // 重新注册消息
            //    SchedulerHandler.registerJob(noticeConfig);
            //}

        });

    }

    /**
     * 删除世界守卫保护区域 WorldGuard
     *
     * @param world        世界
     * @param deleteRegion 是否删除保护区域
     */
    private void removeRegion(World world, boolean deleteRegion) {
        if(worldGuard!=null && deleteRegion) {
            RegionContainer regionContainer = worldGuard.getPlatform().getRegionContainer();
            RegionManager regionManager = regionContainer.get(new BukkitWorld(world));
            if (regionManager != null) {
                Map<String, ProtectedRegion> regions = regionManager.getRegions();
                regions.forEach((key, value) -> {
                    regionManager.removeRegion(key, RemovalStrategy.REMOVE_CHILDREN);
                });
                try {
                    regionManager.save();
                    AesopPlugin.logger.log("&9世界【" + world.getName() + "】守卫保护区域已清理");
                } catch (StorageException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 设置游戏规则
     * @param gameRues 世界游戏规则
     */
    private void setGameRues(World world, List<String> gameRues) {
        if(gameRues!=null && gameRues.size()>0) {
            if(world==null) return;
            for (String ruleLine : gameRues) {
                try {
                    String[] split = ruleLine.split(":");
                    if ("true,false".contains(split[1])) {
                        Boolean value = Boolean.parseBoolean(split[1]);
                        GameRule<Boolean> rule = (GameRule<Boolean>) GameRule.getByName(split[0]);
                        world.setGameRule(rule, value);
                    } else {
                        // 其它转换为INTEGER
                        Integer value = Integer.valueOf(split[1]);
                        GameRule<Integer> rule = (GameRule<Integer>) GameRule.getByName(split[0]);
                        world.setGameRule(rule, value);
                    }
                    AesopPlugin.logger.log("&a世界【" + world.getName() + "】已设置规则："+ruleLine);
                } catch (Exception e) {
                    e.printStackTrace();
                    AesopPlugin.logger.log("&c设置世界规则【"+ruleLine+"】出错，已跳过");
                }
            }
        }
    }

    /**
     * 执行命令
     * @param afterRunCommands 命令列表
     */
    private void runCommand(List<String> afterRunCommands) {
        if(afterRunCommands!=null && afterRunCommands.size()>0) {
            for (String cmdline : afterRunCommands) {
                AesopPlugin.logger.log("执行指令：" + cmdline);
                // 获取控制台身份，以控制台身份执行指令
                ConsoleCommandSender consoleSender = Bukkit.getServer().getConsoleSender();
                Bukkit.getServer().dispatchCommand(consoleSender, cmdline);
            }
        }
    }

    /**
     * 执行区块加载
     */
    private void chunkLoad(String worldKey, double radius) {
        if(radius>0) {
            ChunkyProvider chunkyProvider = HookHandler.getChunkyProvider();
            if (chunkyProvider.isLoaded()) {
                ChunkyAPI chunky = chunkyProvider.getAPI();
                chunky.cancelTask(worldKey);
                // 调用异步，不阻塞 任务执行区块预加载，并由ChunkyProvider监听区块加载进度
                chunky.startTask(worldKey, "square", 0, 0, radius, radius, "concentric");
                AesopPlugin.logger.log("&e[chunk] &b世界【" + worldKey + "】开始执行区块加载 >");
            } else {
                // 未检测到区块加载插件，已跳过区块加载
                AesopPlugin.logger.log("&e未检测到Chunky插件，已跳过区块加载");
            }
        } else {
            AesopPlugin.logger.log("&e区块加载未启用，已跳过区块加载");
        }
    }

}
