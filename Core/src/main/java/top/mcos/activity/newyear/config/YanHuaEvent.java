package top.mcos.activity.newyear.config;

import com.epicnicity322.epicpluginlib.core.logger.ConsoleLogger;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import top.mcos.AesopPlugin;
import top.mcos.activity.newyear.config.sub.RunTaskPlanConfig;
import top.mcos.activity.newyear.config.sub.YCellConfig;
import top.mcos.activity.newyear.config.sub.YGroupConfig;
import top.mcos.activity.newyear.config.sub.YTaskConfig;
import top.mcos.config.ConfigLoader;
import top.mcos.util.CollectionUtils;
import top.mcos.util.ColorUtil;
import top.mcos.util.RandomUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.DelayQueue;
import java.util.stream.Collectors;


public class YanHuaEvent {
    public static final String persistentKeyPrefix = "yanhua-nkey";
    private static final DelayQueue<YanHuaEntity> yanhuaQueue = new DelayQueue<>();

    public static void onFireListen() {
        // todo 全局开关配置

        Bukkit.getScheduler().runTaskTimer(AesopPlugin.getInstance(), () -> {
            YanHuaEntity poll = yanhuaQueue.poll();
            if(poll!=null) {
                //System.out.println("成功消费...");
                spawnFirework(poll.getLocation(), poll.getPower(), poll.getCellsMode(), poll.getCellKeys());
            }

        }, 1, 1);
    }

    public static boolean hasSize() {
        return yanhuaQueue.size()>0;
    }

    public static void putQueue(YanHuaEntity entity) {
        yanhuaQueue.add(entity);
    }

    public static void clearQueue() {
        yanhuaQueue.clear();
    }

    /**
     * 放置方块，这只坐标到配置文件
     *
     * @param placeBlock 被放置的方块
     * @param itemInHand 被放置的物品（物品放置后 变成方块placeBlock）
     */
    public static void onBlockPlace(Block placeBlock, ItemStack itemInHand) {
        try {
            ItemMeta itemMeta = itemInHand.getItemMeta();
            if (itemMeta != null) {
                PersistentDataContainer container = itemMeta.getPersistentDataContainer();
                String groupKey = container.get(new NamespacedKey(AesopPlugin.getInstance(), persistentKeyPrefix), PersistentDataType.STRING);
                if (StringUtils.isNotBlank(groupKey)) {

                    String loc = formatLocation(placeBlock.getLocation());

                    List<YGroupConfig> groups = ConfigLoader.yanHuaConfig.getGroups();
                    for (YGroupConfig group : groups) {
                        if (group.getKey().equals(groupKey)) {
                            // 更新分组坐标
                            List<String> locations = group.getLocations();

                            if (!locations.contains(loc)) {
                                locations.add(loc);
                                ConfigLoader.saveConfig(group);
                                return;
                            }
                        }
                    }
                    // 新增分组坐标
                    YGroupConfig yGroupConfig = new YGroupConfig();
                    List<String> locations = new ArrayList<>();
                    locations.add(loc);
                    yGroupConfig.setKey(groupKey);
                    yGroupConfig.setLocations(locations);
                    groups.add(yGroupConfig);
                    ConfigLoader.saveConfig(ConfigLoader.yanHuaConfig);
                    // TODO 新增分组的情况下，有时候不生效，再保存一次稳妥
                    //ConfigLoader.saveConfig(ConfigLoader.yanHuaConfig);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 破坏方块，移除配置坐标
     * @param block 被破坏的方块
     */
    public static void onBlockBreakEvent(Block block) {
        try {
            String location = formatLocation(block.getLocation());
            List<YGroupConfig> groups = ConfigLoader.yanHuaConfig.getGroups();
            for (YGroupConfig group : groups) {
                List<String> locations = group.getLocations();
                if (locations.remove(location)) {
                    ConfigLoader.saveConfig(ConfigLoader.yanHuaConfig);
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    public static String formatLocation(Location loc) {
        return loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw() + "," + loc.getPitch();
    }

    /**
     * 发射烟花计划
     * @param config
     */
    public static void fireTaskPlan(RunTaskPlanConfig config) {
        List<String> plans = config.getPlans();
        if(plans!=null && plans.size()>0) {
            for (String plan : plans) {
                String[] split = plan.split(":");
                String taskKey = split[0];
                int delay = Integer.parseInt(split[1]);
                int repeatDelay = Integer.parseInt(split[2]);
                int repeat = Integer.parseInt(split[3]);
                int repeatDelayTotal = repeatDelay;
                for (int i=1;i<=repeat;i++) {
                    putFireworkQueue(taskKey, delay + repeatDelayTotal);
                    repeatDelayTotal = repeatDelayTotal + repeatDelay;
                }
            }
        }
    }

    /**
     * 推送烟花到队列
     * @param taskKey 任务key
     * @param addDelay 延迟
     */
    private static void putFireworkQueue(String taskKey, int addDelay) {
        // 任务
        List<YTaskConfig> tasks = ConfigLoader.yanHuaConfig.getTasks();
        Map<String, YTaskConfig> taskMaps = tasks.stream().collect(Collectors.toMap(YTaskConfig::getKey, c -> c));

        // 分组
        List<YGroupConfig> groups = ConfigLoader.yanHuaConfig.getGroups();
        Map<String, YGroupConfig> groupMaps = groups.stream().collect(Collectors.toMap(YGroupConfig::getKey, c -> c));

        YTaskConfig taskConfig = taskMaps.get(taskKey);
        if(taskConfig==null) {
            AesopPlugin.logger.log("taskKey不存在:"+taskKey, ConsoleLogger.Level.ERROR);
        }

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

        int delayTotal = taskConfig.getGroupLocDelay() + addDelay;
        for (String location : locations) {
            YanHuaEntity entity = new YanHuaEntity(location, taskConfig.getLocPower(), cellsMode, delayTotal, cellKeys);
            // 推送到队列
            YanHuaEvent.putQueue(entity);
            delayTotal = delayTotal + taskConfig.getGroupLocDelay();
        }
    }

    private static void spawnFirework(String location, int power, int cellsMode, List<String> cellKeys) {
        // ======== 设置一个烟花属性 ==========
        // 烟花
        List<YCellConfig> cells = ConfigLoader.yanHuaConfig.getCells();
        Map<String, YCellConfig> cellsMaps = cells.stream().collect(Collectors.toMap(YCellConfig::getKey, c -> c));

        // location = worldName, x, y, z, yaw, pitch
        String[] locArrays = location.split(",");
        String worldName = locArrays[0];
        World world = Bukkit.getWorld(worldName);
        if(world==null) {
            return;
        }
        Location locationObj = new Location(world,
                Double.parseDouble(locArrays[1]),
                Double.parseDouble(locArrays[2]),
                Double.parseDouble(locArrays[3]),
                Float.parseFloat(locArrays[4]),
                Float.parseFloat(locArrays[5]));
        Firework fw = (Firework) world.spawnEntity(locationObj, EntityType.FIREWORK);
        //fw.setFireTicks(500);
        FireworkMeta meta = fw.getFireworkMeta();
        meta.setPower(power);

        // 1 设置一个烟花的类型组合
        if(cellsMode==1) {
            // 全部组合
            for (String cellKey : cellKeys) {
                String[] split = cellKey.split(",");
                for (String key : split) {
                    meta.addEffect(createEffects(cellsMaps.get(key)));
                }
            }
        } else if(cellsMode==2) {
            // 随机组合
            List<String> allKeys = new ArrayList<>();
            for (String cellKey : cellKeys) {
                allKeys.addAll(Arrays.asList(cellKey.split(",")));
            }
            List<String> randList = CollectionUtils.randPickup(allKeys);
            for (String cellKey : randList) {
                meta.addEffect(createEffects(cellsMaps.get(cellKey)));
            }
        } else if(cellsMode==3) {
            // 随机一个
            //List<String> randList = CollectionUtils.randPickup(cellKeys);
            int index = RandomUtil.get(0, cellKeys.size()-1);
            String keySplit = cellKeys.get(index);
            String[] split = keySplit.split(",");
            for (String key : split) {
                meta.addEffect(createEffects(cellsMaps.get(key)));
            }
        }
        fw.setFireworkMeta(meta);
        // ======== 设置一个烟花属性 end ==========
    }

    private static FireworkEffect createEffects(YCellConfig yCellConfig) {
        FireworkEffect.Builder builder = FireworkEffect.builder();
        builder.with(FireworkEffect.Type.valueOf(yCellConfig.getType()));
        if(yCellConfig.getColors()!=null && yCellConfig.getColors().size()>0) {
            List<String> colors = yCellConfig.getColors();
            // 设置颜色
            for (String color : colors) {
                if(color.contains("#")) {
                    String s = color.split("#")[1];
                    builder.withColor(Color.fromRGB(Integer.parseInt(s, 16)));
                } else {
                    builder.withColor(ColorUtil.getByCode(color));
                }
            }
            // 设置踪迹
            if(yCellConfig.isTrail()) {
                builder.withTrail();
            }
            // 设置淡出颜色
            if(yCellConfig.getFadeColors()!=null && yCellConfig.getFadeColors().size()>0) {
                List<String> fcolors = yCellConfig.getFadeColors();
                // 设置颜色
                for (String color : fcolors) {
                    if(color.contains("#")) {
                        String s = color.split("#")[1];
                        builder.withFade(Color.fromRGB(Integer.parseInt(s, 16)));
                    } else {
                        builder.withFade(ColorUtil.getByCode(color));
                    }
                }
            }
            // 设置闪烁
            if(yCellConfig.isFlicker()) {
                builder.withFlicker();
            }
        }
        // 添加到一个烟花
        return builder.build();
    }
}
