package top.mcos.command.subcommands;

import top.mcos.util.epiclib.command.Command;
import top.mcos.util.epiclib.command.CommandRunnable;
import top.mcos.util.epiclib.command.TabCompleteRunnable;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mcos.AesopPlugin;
import top.mcos.config.ConfigLoader;
import top.mcos.scheduler.SchedulerHandler;
import top.mcos.scheduler.JobConfig;

import java.util.List;

/**
 * 消息命令：/xxx msg
 */
public final class TaskSubCommand extends Command implements Helpable {
    @Override
    public @NotNull CommandRunnable onHelp() {
        return (label, sender, args) -> AesopPlugin.logger.log(sender, "&2任务调度: &a/"+label + " "+getName());
    }

    @Override
    public @Nullable String[] getAliases() {
        return super.getAliases();
    }

    @Override
    public @NotNull String getName() {
        return "task";
    }

    @Override
    public int getMinArgsAmount() {
        return 2;
    }

    @Override
    public @Nullable String getPermission() {
        return "aesopplugin.admin.task";
    }

    @Override
    protected @Nullable CommandRunnable getNoPermissionRunnable() {
        return (label, sender, args) -> AesopPlugin.logger.log(sender, "&c您没有执行该命令的权限");
    }

    @Override
    protected @Nullable CommandRunnable getNotEnoughArgsRunnable() {
        return (label, sender, args) -> AesopPlugin.logger.log(sender, "&c参数不全");
    }

    @Override
    protected @Nullable TabCompleteRunnable getTabCompleteRunnable() {
        return (possibleCompletions, label, sender, args) -> {
            if(args.length==2) {
                possibleCompletions.add("list");         //查看任务列表
                possibleCompletions.add("run");         //执行任务 task run 任务名称
                possibleCompletions.add("disable");     //停止任务 task disable 任务名称
                possibleCompletions.add("enable");      //重新注册任务 task enable 任务名称
            }
            if(args.length==3) {
                if("run,disable,enable".contains(args[1])) {
                    List<JobConfig> allJob = SchedulerHandler.getAllJob();
                    for (JobConfig jobConfig : allJob) {
                        possibleCompletions.add(jobConfig.getKeyPrefix()+"-"+jobConfig.getKey());
                    }
                    //possibleCompletions.add("yanhua");
                }
            }
            // TODO 测试
            //if(args.length==4) {
            //    if("run".contains(args[1]) && "yanhua".contains(args[2])) {
            //        List<RunTaskPlanConfig> plans = ConfigLoader.yanHuaConfig.getPlans();
            //        for (RunTaskPlanConfig plan : plans) {
            //            possibleCompletions.add(plan.getKey());
            //        }
            //    }
            //}
        };
    }

    @Override
    public void run(@NotNull String label, @NotNull CommandSender sender, @NotNull String[] args) {
        if("list".equals(args[1])) {
            List<JobConfig> allJob = SchedulerHandler.getAllJob();
            for (JobConfig jobConfig : allJob) {
                AesopPlugin.logger.log(sender,jobConfig.getKeyPrefix()+"-"+jobConfig.getKey());
            }
        } else if("run".equals(args[1])) {
            if(args.length<3) {
                AesopPlugin.logger.log(sender, "&c参数不足");
                return;
            }
            String taskName = args[2];
            List<JobConfig> allJob = SchedulerHandler.getAllJob();
            for (JobConfig jobConfig : allJob) {
                if(taskName.equals(jobConfig.getKeyPrefix() + "-" + jobConfig.getKey())) {
                    if(!jobConfig.isEnable()) {
                        AesopPlugin.logger.log(sender, "&c任务未启用");
                        return;
                    }
                    SchedulerHandler.executeNow(jobConfig);
                    return;
                }
            }
            AesopPlugin.logger.log(sender, "&c未找到任务："+taskName);
            //config.set("tasks.refresh-map.enable", setFlag);
            //AesopPlugin.getInstance().saveConfig();
            //AesopPlugin.logger.log(player, "设为："+ setFlag);
        } else if ("disable".equals(args[1])) {
            if(args.length<3) {
                AesopPlugin.logger.log(sender, "&c参数不足");
                return;
            }
            String taskName = args[2];
            List<JobConfig> allJob = SchedulerHandler.getAllJob();
            for (JobConfig jobConfig : allJob) {
                if(taskName.equals(jobConfig.getKeyPrefix() + "-" + jobConfig.getKey())) {
                    SchedulerHandler.unRegisterJob(jobConfig);
                    jobConfig.changeEnable(false);
                    ConfigLoader.saveConfig(jobConfig);
                    AesopPlugin.logger.log(sender, "&a任务已停止");
                    return;
                }
            }
            sender.sendMessage("已停止任务："+taskName+"...");
        } else if ("enable".equals(args[1])) {
            if(args.length<3) {
                AesopPlugin.logger.log(sender, "&c参数不足");
                return;
            }
            String taskName = args[2];
            List<JobConfig> allJob = SchedulerHandler.getAllJob();
            for (JobConfig jobConfig : allJob) {
                if(taskName.equals(jobConfig.getKeyPrefix() + "-" + jobConfig.getKey())) {
                    if(jobConfig.isEnable()) {
                        AesopPlugin.logger.log(sender, "&a任务已启用，无需重复操作");
                        return;
                    }
                    jobConfig.changeEnable(true);
                    ConfigLoader.saveConfig(jobConfig);
                    SchedulerHandler.registerJob(jobConfig);
                    AesopPlugin.logger.log(sender, "&a任务已启用");
                    return;
                }
            }
            sender.sendMessage("任务已启用："+taskName+"...");
        } else if ("show".equals(args[1])) {
            // 测试显示配置
            FileConfiguration config = AesopPlugin.getInstance().getConfig();
            boolean flag = config.getBoolean("tasks.refresh-map.enable");
            AesopPlugin.logger.log(sender, "输出："+ flag);

            //ChunkyAPI chunky = Bukkit.getServer().getServicesManager().load(ChunkyAPI.class);
            //AesopPlugin.logger.log(player, "isRunning:"+chunky.isRunning("zy"));
            //AesopPlugin.logger.log(player, "out");

            //MessageHandler.sendAllOnlinePlayers("测试");
            //Bukkit.broadcastMessage("&a服务器即将&c关机: " + args[2]);

            // 测试发送center消息
            //NetworkManager networkManager = null;
            //try {
            //    networkManager = (NetworkManager) MessageHandler.networkManagerH.get(((CraftPlayer) player).getHandle().b);
            //} catch (IllegalAccessException e) {
            //    AesopPlugin.logger.log("实例化网络管理器出错", ConsoleLogger.Level.ERROR);
            //    e.printStackTrace();
            //}
            //String title = "主标题";
            //String subtitle = "子标题xxx";
            //ClientboundSetTitleTextPacket text = new ClientboundSetTitleTextPacket(CraftChatMessage.fromStringOrNull(title));
            //ClientboundSetSubtitleTextPacket subtext = new ClientboundSetSubtitleTextPacket(CraftChatMessage.fromStringOrNull(subtitle));
            //networkManager.a((Packet<?>) text);
            //networkManager.a((Packet<?>) subtext);

                /*
                多世界
                 */
            //MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
            //MVWorldManager mvWorldManager = core.getMVWorldManager();
            ////long seed = (new Random()).nextLong();
            //mvWorldManager.regenWorld("zy", true, true, null, true);
            //AesopPlugin.logger.log("世界已重置");

                /*
                区块生成
                 */
            //ChunkyAPI chunky = Bukkit.getServer().getServicesManager().load(ChunkyAPI.class);
            //AesopPlugin.logger.log("开始加载区块...");
            //chunky.startTask("zy", "square", 0, 0, 600, 600, "concentric");

                /*
                执行命令
                 */
            //String cmdline = "gamerule doDaylightCycle "+args[2];
            //ConsoleCommandSender consoleSender = Bukkit.getServer().getConsoleSender();
            //Bukkit.getServer().dispatchCommand(consoleSender, cmdline);

            /**
             * 获取插件路径
             */
            //Location location = player.getLocation();
            //AesopPlugin.getInstance().getServer().getWorld("world").playEffect(location, );

            /**
             * EffectLib 类库使用示例：
             * https://github.com/u9g/effectlib-visualizer/blob/main/src/main/java/dev/u9g/effectspreviewer/Commands.java
             */
            //EffectManager effectManager = new EffectManager(AesopPlugin.getInstance());
            ////TextEffect textEffect = new TextEffect(effectManager);
            ////textEffect.text = "我就是特效哈哈哈";
            ////textEffect.size=90;
            ////textEffect.color= Color.BLUE;
            ////textEffect.delay=10;
            ////textEffect.duration=10;
            ////textEffect.setFont(new Font("", Font.BOLD, 30));
            ////textEffect.setTargetEntity(player);
            ////textEffect.start();
            //
            //TextEffect effect = new TextEffect(effectManager);
            //World world = Bukkit.getWorld("world");
            //Location location = new Location(world, -4, 67, 61, 45, 0);
            //
            //// 设置位置
            //effect.setDynamicOrigin(new DynamicLocation(location));
            ////private final BiFunction<Player, Integer, Location> xForwardFromPlayer = (player, x) -> player.getLocation().add(0,2,0).add(player.getLocation().getDirection().multiply(x));
            ////effect.setDynamicOrigin(new DynamicLocation(xForwardFromPlayer.apply(player, 9)));
            //
            //// 设置粒子特效（暂时只能选择不需要特效数据的）
            ////effect.particle = Particle.ELECTRIC_SPARK;
            //effect.particle = Particle.SOUL_FIRE_FLAME;
            ////effect.particleData=''
            //
            //// 设置文本
            //effect.text = "圣诞节快乐hello";
            //
            //// 时间间隔，数值越小，显示越快
            //effect.period = 10;
            //try {
            //    InputStream fi = MsgSubCommand.class.getClassLoader().getResourceAsStream("font/DouyinSansBold.ttf");
            //    Font font = Font.createFont(Font.PLAIN, fi);
            //    font = font.deriveFont(Font.PLAIN, 30);
            //    effect.setFont(font);
            //    //effect.size = 16;
            //} catch (Exception e) {
            //    e.printStackTrace();
            //}
            //
            //effect.start();
            ////effect.cancel();
            //

            /**
             * 自定义NBT
             * https://www.spigotmc.org/threads/1-8-x-1-20-x-v7-19-0-maven-single-class-nbt-editor-for-items-skulls-mobs-and-tile-entities.269621/
             */
            //ItemStack itemStack = new ItemStack(Material.REDSTONE_BLOCK, 1);
            //ItemMeta itemMeta = itemStack.getItemMeta();
            //itemMeta.setDisplayName("sfee");
            //itemStack.setItemMeta(itemMeta);
            //ItemStack itemStack1 = new ItemStack(itemStack);
            ////ItemStack set = NBTEditor.set(itemStack, "any custom string key",  "item", "owner");
            //player.getInventory().addItem(itemStack1);

            //NBTItem nbtItem = new NBTItem(itemStack);
            //nbtItem.setString("PublicBukkitValues", "{'chao':'123'}");
            //ItemStack item = nbtItem.getItem();
            //item.getItemMeta().getPersistentDataContainer();
            //player.getInventory().addItem(item);
            //boolean publicBukkitValues = nbtItem.hasTag("PublicBukkitValues");
            //System.out.println("存在" +publicBukkitValues);
            //NBTEntity ne = new NBTEntity();


            //TODO 在此示例中，我们将 PI 的值存储在容器实例中，并通过将 ItemMeta 反馈回 ItemStack，即使在重新启动时我们也可以存储它。
            //NamespacedKey key = new NamespacedKey(AesopPlugin.getInstance(), "display");
            //ItemMeta itemMeta = itemStack.getItemMeta();
            //itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING,  "-》抽取礼物《-");
            //itemStack.setItemMeta(itemMeta);

            //player.getInventory().addItem(itemStack);

            AesopPlugin.logger.log("结束指令");
        } else {
            AesopPlugin.logger.log(sender, "&c参数有误");
            this.onHelp();
        }
    }
}
