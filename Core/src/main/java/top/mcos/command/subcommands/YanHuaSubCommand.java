package top.mcos.command.subcommands;

import com.epicnicity322.epicpluginlib.bukkit.command.Command;
import com.epicnicity322.epicpluginlib.bukkit.command.CommandRunnable;
import com.epicnicity322.epicpluginlib.bukkit.command.TabCompleteRunnable;
import com.epicnicity322.epicpluginlib.core.logger.ConsoleLogger;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mcos.AesopPlugin;
import top.mcos.business.yanhua.YanHuaEntity;
import top.mcos.business.yanhua.YanHuaEvent;
import top.mcos.business.yanhua.config.sub.RunTaskPlanConfig;
import top.mcos.business.yanhua.config.sub.YGroupConfig;
import top.mcos.business.yanhua.config.sub.YTaskConfig;
import top.mcos.config.ConfigLoader;
import top.mcos.util.ColorUtil;
import top.mcos.util.MessageUtil;
import top.mcos.util.PlayerUtil;
import top.mcos.util.RandomUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class YanHuaSubCommand extends Command implements Helpable {
    @Override
    public @NotNull CommandRunnable onHelp() {
        return (label, sender, args) -> AesopPlugin.logger.log(sender, "&2烟花特效管理指令: &a/"+label + " "+getName());
    }

    @Override
    public @Nullable String[] getAliases() {
        return new String[]{"yanhua"};
    }

    @Override
    public @NotNull String getName() {
        return "yanhua";
    }

    @Override
    public int getMinArgsAmount() {
        return 2;
    }

    @Override
    public @Nullable String getPermission() {
        return "aesopplugin.admin.yanhua";
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
                possibleCompletions.add("giverandom");  // 给与玩家烟花，特效随机：yanhua giverandom <玩家> <数量>
                possibleCompletions.add("give");        // 给予烟花发射位置物品: yanhua give <分组KEY(不存在会创建)>
                possibleCompletions.add("clear");       // 清理所有进行中的烟花任务: yanhua clear [玩家名称（仅用于显示消息）]
                possibleCompletions.add("fire");        // 发射烟花： yanhua fire <任务Key，多个使用逗号分割>
                possibleCompletions.add("fireplan");    // 执行烟花计划： yanhua fireplan <计划Key> [玩家名称（仅用于显示消息）]
                possibleCompletions.add("preview");     // 发射预览烟花特效: yanhua preview <类型> [发射时长]
            }
            if(args.length==3) {
                if("give".contains(args[1])) {
                    possibleCompletions.add("<分组KEY(不存在会创建)>");
                }
                if("fire".contains(args[1])) {
                    possibleCompletions.add("<任务KEY，多个使用逗号分割>");
                }
                if("fireplan".contains(args[1])) {
                    possibleCompletions.addAll(ConfigLoader.yanHuaConfig.getPlans().stream().map(RunTaskPlanConfig::getKey).toList());
                }
                if("preview".contains(args[1])) {
                    possibleCompletions.add("<类型1-99>");
                }
                if("giverandom".contains(args[1])) {
                    possibleCompletions.addAll(PlayerUtil.getAllOnlinePlayerName());
                }
                if("clear".contains(args[1])) {
                    possibleCompletions.add("[玩家名称（仅用于显示消息）]");
                }
            }
            if(args.length==4) {
                if("preview".contains(args[1])) {
                    possibleCompletions.add("[发射时长1-99]");
                }
                if("giverandom".contains(args[1])) {
                    possibleCompletions.add("<数量>");
                }
                if("fireplan".contains(args[1])) {
                    possibleCompletions.add("[玩家名称（仅用于显示消息）]");
                }
            }
        };
    }

    @Override
    public void run(@NotNull String label, @NotNull CommandSender sender, @NotNull String[] args) {
        if("giverandom".equals(args[1])) {
            // 给与玩家烟花，特效随机：yanhua giverandom <玩家> <数量>
            String playerName = args[2];
            Integer amount = Integer.parseInt(args[3]);
            Player onlinePlayer = PlayerUtil.getOnlinePlayer(playerName);
            if(onlinePlayer==null) {
                AesopPlugin.logger.log("&c玩家不存在", ConsoleLogger.Level.ERROR);
                return;
            }
            ItemStack itemStack = new ItemStack(Material.FIREWORK_ROCKET, amount);
            FireworkMeta meta = (FireworkMeta) itemStack.getItemMeta();
            if(meta==null) {
                AesopPlugin.logger.log("&c不是烟花物品", ConsoleLogger.Level.ERROR);
                return;
            }
            meta.setDisplayName(MessageUtil.colorize("&d\uD83C\uDF86&#fe0000新&#ffb6b6年&#ff55ff烟&#f8aaff花 &b(随机效果)"));
            // 设置飞行高度
            meta.setPower(1);

            int typeSize = RandomUtil.get(1,3);
            for (int j=0;j<typeSize;j++) {
                var colors = new ArrayList<>();
                int colorSize = RandomUtil.get(1, 2);
                for (int i=0;i<colorSize;i++) {
                    colors.add(ColorUtil.getByInteger(RandomUtil.get(1, 44)));
                }
                var fadeOutColor = new ArrayList<>();
                if(RandomUtil.get(0, 1)==1) {
                    fadeOutColor.add(ColorUtil.getByInteger(RandomUtil.get(1, 44)));
                }
                int type = RandomUtil.get(1,5);
                FireworkEffect.Type ft = switch (type) {
                    case 1 -> FireworkEffect.Type.BALL;
                    case 2 -> FireworkEffect.Type.BALL_LARGE;
                    case 3 -> FireworkEffect.Type.STAR;
                    case 4 -> FireworkEffect.Type.BURST;
                    case 5 -> FireworkEffect.Type.CREEPER;
                    default -> FireworkEffect.Type.BALL;
                };
                boolean trail = RandomUtil.get(0, 1) == 1;
                boolean flicker = RandomUtil.get(0, 1) == 1;
                meta.addEffect(FireworkEffect.builder().withColor(colors).with(ft).trail(trail).withFade(fadeOutColor).flicker(flicker).build());
            }
            itemStack.setItemMeta(meta);
            onlinePlayer.getInventory().addItem(itemStack);
        }else if("give".equals(args[1])) {
            if(sender instanceof Player player) {
                String groupKey = args[2];

                //ConfigLoader.test();

                NamespacedKey namespacedKey = new NamespacedKey(AesopPlugin.getInstance(), YanHuaEvent.persistentKeyPrefix);
                ItemStack itemStack = new ItemStack(Material.LIGHTNING_ROD, 1);
                ItemMeta itemMeta = itemStack.getItemMeta();
                if (itemMeta != null) {
                    // 设置显示的名称
                    itemMeta.setDisplayName(MessageUtil.colorize("&b※ &c&l烟&e&l花&9发射点 | &b分组：&c"+ groupKey));
                    // 设置自定义持久数据
                    itemMeta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, groupKey);
                    // 设置lore
                    //List<String> lines = new ArrayList<>();
                    //itemMeta.setLore(lines);
                    itemStack.setItemMeta(itemMeta);
                    player.getInventory().addItem(itemStack);
                }
            } else {
                AesopPlugin.logger.log("&c需要以游戏身份执行该指令");
            }
        } else if("clear".equals(args[1])) {
            YanHuaEvent.clearQueue();
            Player onlinePlayer = null;
            if(args.length>2) {
                onlinePlayer = PlayerUtil.getOnlinePlayer(args[2]);
            }

            if(onlinePlayer!=null) {
                AesopPlugin.logger.log(onlinePlayer, "&a已停止发射。");
            } else {
                AesopPlugin.logger.log(sender, "&a已停止发射。");
            }
        } else if("fireplan".equals(args[1])) {
            Player onlinePlayer = null;
            if(args.length>3) {
                onlinePlayer = PlayerUtil.getOnlinePlayer(args[3]);
            }

            if(YanHuaEvent.hasSize()) {
                if(onlinePlayer!=null) {
                    AesopPlugin.logger.log(onlinePlayer, "&c当前烟花正在发射中...请勿重复操作!");
                } else {
                    AesopPlugin.logger.log(sender, "&c当前烟花正在发射中...请勿重复操作!");
                }
                return;
            }
            String planKey = args[2];
            List<RunTaskPlanConfig> plans = ConfigLoader.yanHuaConfig.getPlans();
            for (RunTaskPlanConfig plan : plans) {
                if(plan.getKey().equals(planKey)) {
                    YanHuaEvent.fireTaskPlan(plan);
                    if(onlinePlayer!=null) {
                        onlinePlayer.sendTitle(MessageUtil.colorize("&a&l烟火盛宴"), MessageUtil.colorize("&a&l正在启动---====>>>>>>"), 20, 50, 20);
                    } else {
                        AesopPlugin.logger.log(sender, "&a&l烟火盛宴正在启动---====>>>>>>");
                    }
                    return;
                }
            }
        } else if("fire".equals(args[1])) {
            String taskKey = args[2];
            String[] taskKeys = taskKey.split(",");

            // 任务
            List<YTaskConfig> tasks = ConfigLoader.yanHuaConfig.getTasks();
            Map<String, YTaskConfig> taskMaps = tasks.stream().collect(Collectors.toMap(YTaskConfig::getKey, c -> c));

            // 分组
            List<YGroupConfig> groups = ConfigLoader.yanHuaConfig.getGroups();
            Map<String, YGroupConfig> groupMaps = groups.stream().collect(Collectors.toMap(YGroupConfig::getKey, c -> c));

            // 烟花
            //List<YCellConfig> cells = ConfigLoader.yanHuaConfig.getCells();
            //Map<String, YCellConfig> cellsMaps = cells.stream().collect(Collectors.toMap(YCellConfig::getKey, c -> c));

            for (String key : taskKeys) {
                YTaskConfig taskConfig = taskMaps.get(key);

                String groupKey = taskConfig.getGroupKey();
                int groupLocSeq = taskConfig.getGroupLocSeq();
                List<String> cellKeys = taskConfig.getCells();

                YGroupConfig yGroupConfig = groupMaps.get(groupKey);
                List<String> locations = yGroupConfig.getLocations();

                int cellsMode = taskConfig.getCellsMode();

                if(groupLocSeq==1) { // 顺序执行

                } else if (groupLocSeq==2) { // 随机执行
                    Collections.shuffle(locations);
                }

                int delay = taskConfig.getGroupLocDelay();
                int delayTotal = delay;
                for (String location : locations) {
                    YanHuaEntity entity = new YanHuaEntity(location, taskConfig.getLocPower(), cellsMode, delayTotal, cellKeys);
                    YanHuaEvent.putQueue(entity);
                    delayTotal = delayTotal + delay;
                }
            }

        } else if("preview".equals(args[1])) {
            if(sender instanceof Player player) {
                //Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon fireworks_rocket 742 4 -391 {LifeTime:0,FireworksItem:{id:fireworks,Count:1,tag:{Fireworks:{Explosions:[{Type:4,Flicker:1,Trail:1,Colors:[16777215],FadeColors:[16755978]},{Type:4,Flicker:1,Trail:0,Colors:[16755200],FadeColors:[16755200]}]}}}}");

                if ("1".equals(args[2])) {
                    // 普通
                    Firework fw = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
                    FireworkMeta meta = fw.getFireworkMeta();
                    meta.addEffect(FireworkEffect.builder().withColor(Color.AQUA).trail(true).withFlicker().withFade(Color.fromBGR(12, 33, 120)).build());
                    fw.setFireworkMeta(meta);
                } else if ("2".equals(args[2])) {
                    // 组合
                    Firework fw = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
                    FireworkMeta meta = fw.getFireworkMeta();
                    meta.addEffect(FireworkEffect.builder().withColor(Color.AQUA).trail(true).build());
                    meta.addEffect(FireworkEffect.builder().withColor(Color.YELLOW).flicker(true).build());
                    fw.setFireworkMeta(meta);
                } else if ("3".equals(args[2])) {
                    // 类型 小型球状
                    Firework fw = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
                    FireworkMeta meta = fw.getFireworkMeta();
                    meta.addEffect(FireworkEffect.builder().withColor(Color.AQUA).with(FireworkEffect.Type.BALL).trail(true).build());
                    fw.setFireworkMeta(meta);
                } else if ("4".equals(args[2])) {
                    // 类型 大型球状
                    Firework fw = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
                    FireworkMeta meta = fw.getFireworkMeta();
                    meta.addEffect(FireworkEffect.builder().withColor(Color.AQUA).with(FireworkEffect.Type.BALL_LARGE).trail(true).build());
                    fw.setFireworkMeta(meta);
                } else if ("5".equals(args[2])) {
                    // 类型 星型
                    Firework fw = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
                    FireworkMeta meta = fw.getFireworkMeta();
                    meta.addEffect(FireworkEffect.builder().withColor(Color.AQUA).with(FireworkEffect.Type.STAR).trail(true).build());
                    fw.setFireworkMeta(meta);
                } else if ("6".equals(args[2])) {
                    // 类型 爆裂
                    Firework fw = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
                    FireworkMeta meta = fw.getFireworkMeta();
                    meta.addEffect(FireworkEffect.builder().withColor(Color.AQUA).with(FireworkEffect.Type.BURST).trail(true).build());
                    fw.setFireworkMeta(meta);
                } else if ("7".equals(args[2])) {
                    // 类型 苦力怕
                    Firework fw = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
                    FireworkMeta meta = fw.getFireworkMeta();
                    meta.addEffect(FireworkEffect.builder().withColor(Color.AQUA).with(FireworkEffect.Type.CREEPER).trail(true).build());
                    fw.setFireworkMeta(meta);
                } else if ("8".equals(args[2])) {
                    // 淡出
                    Firework fw = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
                    FireworkMeta meta = fw.getFireworkMeta();
                    meta.addEffect(FireworkEffect.builder().withColor(Color.AQUA).with(FireworkEffect.Type.STAR).trail(true).withFade().build());
                    fw.setFireworkMeta(meta);
                } else if ("9".equals(args[2])) {
                    // 多颜色
                    Firework fw = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
                    FireworkMeta meta = fw.getFireworkMeta();
                    var colors = new ArrayList<>();
                    colors.add(Color.fromBGR(255, 20, 147));
                    colors.add(Color.fromBGR(30, 144, 255));
                    colors.add(Color.fromBGR(124, 252, 0));
                    meta.addEffect(FireworkEffect.builder().withColor(colors).with(FireworkEffect.Type.STAR).trail(true).withFade().build());
                    fw.setFireworkMeta(meta);
                } else if ("10".equals(args[2])) {
                    // 飞行高度
                    int life = Integer.valueOf(args[3]);
                    Firework fw = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
                    FireworkMeta meta = fw.getFireworkMeta();
                    meta.setPower(life);
                    meta.addEffect(FireworkEffect.builder().withColor(Color.AQUA).with(FireworkEffect.Type.STAR).trail(true).withFade().build());
                    fw.setFireworkMeta(meta);
                } else if ("11".equals(args[2])) {
                    // 全部情况
                    int life = Integer.valueOf(args[3]);
                    Firework fw = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);

                    FireworkMeta meta = fw.getFireworkMeta();
                    meta.setPower(life);

                    var colors = new ArrayList<>();
                    colors.add(Color.fromBGR(255, 20, 147));
                    colors.add(Color.fromBGR(30, 144, 255));
                    colors.add(Color.fromBGR(124, 252, 0));
                    var colors2 = new ArrayList<>();
                    colors.add(Color.fromBGR(238, 232, 170));
                    colors.add(Color.fromBGR(255, 215, 0));
                    colors.add(Color.fromBGR(220, 220, 220));
                    meta.addEffect(FireworkEffect.builder().withColor(colors).with(FireworkEffect.Type.STAR).trail(true).withFade().build());
                    meta.addEffect(FireworkEffect.builder().withColor(Color.AQUA).with(FireworkEffect.Type.BURST).trail(true).withFade().build());
                    meta.addEffect(FireworkEffect.builder().withColor(Color.YELLOW).with(FireworkEffect.Type.CREEPER).trail(true).withFade().build());
                    meta.addEffect(FireworkEffect.builder().withColor(Color.GREEN).with(FireworkEffect.Type.BALL_LARGE).trail(true).withFade().build());
                    meta.addEffect(FireworkEffect.builder().withColor(Color.BLUE).with(FireworkEffect.Type.BALL).trail(true).withFade(colors2).withFlicker().build());
                    fw.setFireworkMeta(meta);
                }
            } else  {
                AesopPlugin.logger.log("&c需要以游戏身份执行该指令");

            }
        } else {
            AesopPlugin.logger.log(sender, "&c参数有误");
        }
    }
}
