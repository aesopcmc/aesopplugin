package top.mcos.business.itmebind;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import top.mcos.AesopPlugin;
import top.mcos.command.CommandUtil;
import top.mcos.config.ConfigLoader;
import top.mcos.config.configs.subconfig.ItemBindCommandsConfig;
import top.mcos.util.MessageUtil;

import javax.annotation.CheckForNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class ItemEvent {
    public static final String persistentKeyPrefix = "item-bind-event";


    public static void triggerEvent(Player player, PlayerInteractEvent event){
        try {
            // 右键按钮 (方块类型)
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Block clicked = event.getClickedBlock();
                if (clicked.getType().name().endsWith("_BUTTON")) {
                    List<ItemBindCommandsConfig> configs = ConfigLoader.baseConfig.getItemBindCommandConfigs();
                    configs = configs.stream().filter(c -> c.isEnable() && c.getExecuteType() == 1 && c.getLocations()!=null && c.getLocations().size()>0).toList();
                    for (ItemBindCommandsConfig config : configs) {
                        System.out.println(config.getLocations());
                        if (config.getLocations().contains(formatLocation(clicked.getLocation()))) {
                            // 处理命令事件
                            //System.out.println("处理方块命令事件。。。");
                            CommandUtil.executeCommand(config.getCommands(), player);
                            // 多个位置只执行一次
                            return;
                        }
                    }
                }
            }

            // 右键物品 （物品类型）
            ItemStack itemStack = event.getItem();
            if (itemStack == null) return;
            if (itemStack.getItemMeta()==null) return;

            PersistentDataContainer pdc = itemStack.getItemMeta().getPersistentDataContainer();
            Set<NamespacedKey> keys = pdc.getKeys();

            for (NamespacedKey key : keys) {
                if (persistentKeyPrefix.equalsIgnoreCase(key.getKey())) {
                    String ibKey = pdc.get(key, PersistentDataType.STRING);
                    // 处理命令事件
                    ItemBindCommandsConfig config = getConfigByKey(ibKey);
                    if(config!=null && config.getExecuteType()==0) {
                        //System.out.println("处理物品命令事件。。。");
                        CommandUtil.executeCommand(config.getCommands(), player);
                    }

                    // 处理其它事件 (例如数据库交互、缓存等待)
                    // ...
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        //player.sendMessage("手持物品："+item.getItemMeta().getDisplayName());
    }



    public static @CheckForNull ItemBindCommandsConfig getConfigByKey(String ibKey) {
        List<ItemBindCommandsConfig> configs = ConfigLoader.baseConfig.getItemBindCommandConfigs();
        for (ItemBindCommandsConfig config : configs) {
            if(config.isEnable() && config.getKey().equals(ibKey)) return config;
        }
        return null;
    }

    public static String formatLocation(Location loc) {
        return loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw() + "," + loc.getPitch();
    }

    /**
     * 监听放置方块事件
     *
     * @param placeBlock 放置的方块
     * @param itemInHand 手持放置的物品
     * @param namespace
     */
    public static void onBlockPlace(Block placeBlock, ItemStack itemInHand, NamespacedKey namespace) {
        try {
            //System.out.println("放置事件。。。");

            ItemMeta itemMeta = itemInHand.getItemMeta();
            if (itemMeta != null) {
                PersistentDataContainer container = itemMeta.getPersistentDataContainer();
                String itemKey = container.get(namespace, PersistentDataType.STRING);
                //String itemKey = container.get(new NamespacedKey(AesopPlugin.getInstance(), persistentKeyPrefix), PersistentDataType.STRING);
                if (StringUtils.isNotBlank(itemKey)) {
                    // 添加坐标，并更新配置
                    String loc = formatLocation(placeBlock.getLocation());
                    ItemBindCommandsConfig config = getConfigByKey(itemKey);
                    if(config!=null){
                        config.getLocations().add(loc);
                        ConfigLoader.saveConfig(config);
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 破坏方块事件。破坏后执行
     * @param block 被破坏的方块
     */
    public static void onBlockBreakEvent(Block block) {
        try {
            String loc = formatLocation(block.getLocation());
            List<ItemBindCommandsConfig> configs = ConfigLoader.baseConfig.getItemBindCommandConfigs();
            for (ItemBindCommandsConfig config : configs) {
                List<String> locations = config.getLocations();
                if(locations!=null) {
                    if(locations.remove(loc)) {
                        ConfigLoader.saveConfig(config);
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 给与玩家物品
     * @param config 当前物品配置
     * @param player 玩家
     */
    public static void giveItem(ItemBindCommandsConfig config, Player player) {
        NamespacedKey namespacedKey = new NamespacedKey(AesopPlugin.getInstance(), ItemEvent.persistentKeyPrefix);
        ItemStack itemStack = new ItemStack(Material.valueOf(config.getMaterial().toUpperCase()), 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            // 设置显示的名称
            itemMeta.setDisplayName(MessageUtil.colorize(config.getDisplayName()));
            // 设置自定义持久数据
            itemMeta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, config.getKey());
            // 设置lore
            List<String> lines = new ArrayList<>();
            List<String> lore = config.getLore();
            for (String s : lore) {
                lines.add(MessageUtil.colorize(s));
            }
            itemMeta.setLore(lines);
            itemStack.setItemMeta(itemMeta);

            player.getInventory().addItem(itemStack);
        }
    }
}
