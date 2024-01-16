package top.mcos.itmebind;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import top.mcos.AesopPlugin;
import top.mcos.config.ConfigLoader;
import top.mcos.config.configs.subconfig.ItemBindCommandsConfig;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ItemEvent {
    public static final String persistentKeyPrefix = "item-bind-event";
    public static final String COMMAND_PLAYER_SENDER_TYPE = "[player]";
    public static final String COMMAND_CONSOLE_SENDER_TYPE = "[console]";

    public static void triggerEvent(Player player, ItemStack itemStack){
        if (itemStack == null) return;
        if (itemStack.getItemMeta()==null) return;

        try {
            Bukkit.getScheduler().runTask(AesopPlugin.getInstance(), ()-> {
                PersistentDataContainer pdc = itemStack.getItemMeta().getPersistentDataContainer();
                Set<NamespacedKey> keys = pdc.getKeys();

                for (NamespacedKey key : keys) {
                    if (persistentKeyPrefix.equalsIgnoreCase(key.getKey())) {
                        String ibKey = pdc.get(key, PersistentDataType.STRING);

                        // 处理命令事件
                        ItemBindCommandsConfig config = getCommandsConfig(ibKey);
                        if (config != null && config.isEnable()) {
                            List<String> commands = config.getCommands();
                            for (String command : commands) {
                                String regex = "(^\\[.+\\]) (.+)";
                                Matcher matcher = Pattern.compile(regex).matcher(command);
                                if (matcher.find()) {
                                    String prefix = matcher.group(1);
                                    String cmd = matcher.group(2);
                                    if(COMMAND_PLAYER_SENDER_TYPE.equalsIgnoreCase(prefix)) {
                                        // 以玩家身份执行命令
                                        Bukkit.getServer().dispatchCommand(player, cmd);
                                    } else if(COMMAND_CONSOLE_SENDER_TYPE.equalsIgnoreCase(prefix)) {
                                        // 以控制台身份执行命令
                                        ConsoleCommandSender consoleSender = Bukkit.getServer().getConsoleSender();
                                        Bukkit.getServer().dispatchCommand(consoleSender, cmd);
                                    }
                                } else {
                                    // 没有匹配到前缀的情况下，默认以玩家身份执行命令
                                    Bukkit.getServer().dispatchCommand(player, command);
                                }
                            }
                        }

                        // 处理其它事件
                        // ...
                    }
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
        //player.sendMessage("手持物品："+item.getItemMeta().getDisplayName());
    }

    public static ItemBindCommandsConfig getCommandsConfig(String ibKey) {
        List<ItemBindCommandsConfig> configs = ConfigLoader.baseConfig.getItemBindCommandConfigs();
        Map<String, ItemBindCommandsConfig> configMap = configs.stream().collect(Collectors.toMap(ItemBindCommandsConfig::getKey, c -> c));
        return configMap.get(ibKey);
    }

}
