package top.mcos.command.subcommands;

import com.epicnicity322.epicpluginlib.bukkit.command.Command;
import com.epicnicity322.epicpluginlib.bukkit.command.CommandRunnable;
import com.epicnicity322.epicpluginlib.bukkit.command.TabCompleteRunnable;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mcos.AesopPlugin;
import top.mcos.config.ConfigLoader;
import top.mcos.config.configs.subconfig.ItemBindCommandsConfig;
import top.mcos.itmebind.ItemEvent;
import top.mcos.util.MessageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ItemBindSubCommand extends Command implements Helpable {
    @Override
    public @NotNull CommandRunnable onHelp() {
        return (label, sender, args) -> AesopPlugin.logger.log(sender, "&2物品绑定事件指令: &a/"+label + " "+getName());
    }

    @Override
    public @Nullable String[] getAliases() {
        return new String[]{"itembind", "givemenu"};
    }

    @Override
    public @NotNull String getName() {
        return "itembind";
    }

    @Override
    public int getMinArgsAmount() {
        return 1;
    }

    @Override
    public @Nullable String getPermission() {
        return "aesopplugin.player.itembind";
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
            //if(args.length==2) {
            //    possibleCompletions.add("give");        // 给予玩家粒子组 give <playerName> <玩家粒子组key>
            //    possibleCompletions.add("remove");      // 移除玩家粒子组 remove <playerName> <玩家粒子组key>
            //    possibleCompletions.add("removeall");   // 移除玩家所有粒子组 removeall <playerName>
            //    possibleCompletions.add("setup");       // 设置粒子组 setup <player|location> <自定义组key> <自定义组名>
            //}
            //if(args.length==3) {
            //    if("give,remove,removeall".contains(args[1])) {
            //        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
            //        for (Player onlinePlayer : onlinePlayers) {
            //            possibleCompletions.add(onlinePlayer.getName());
            //        }
            //    }
            //    if("setup".contains(args[1])) {
            //        possibleCompletions.add("player");
            //        possibleCompletions.add("location");
            //    }
            //}
            //
        };
    }

    @Override
    public void run(@NotNull String label, @NotNull CommandSender sender, @NotNull String[] args) {
        if("givemenu".equals(args[0])) {
            if(sender instanceof Player player) {
                List<ItemBindCommandsConfig> itemBindCommandConfigs = ConfigLoader.baseConfig.getItemBindCommandConfigs();
                Map<String, ItemBindCommandsConfig> configMap = itemBindCommandConfigs.stream().collect(Collectors.toMap(ItemBindCommandsConfig::getKey, c -> c));

                String ibKey = "menu";
                ItemBindCommandsConfig config = configMap.get(ibKey);
                if(config==null) {
                    AesopPlugin.logger.log(sender, "&c物品不存在");
                    return;
                }

                NamespacedKey namespacedKey = new NamespacedKey(AesopPlugin.getInstance(), ItemEvent.persistentKeyPrefix);
                ItemStack itemStack = new ItemStack(Material.valueOf(config.getMaterial().toUpperCase()), 1);
                ItemMeta itemMeta = itemStack.getItemMeta();
                if(itemMeta!=null) {
                    // 设置显示的名称
                    itemMeta.setDisplayName(MessageUtil.colorize(config.getDisplayName()));
                    // 设置自定义持久数据
                    itemMeta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, ibKey);
                    // 设置lore
                    List<String> lines = new ArrayList<>();
                    List<String> lore = config.getLore();
                    for (String s : lore) {
                        lines.add(MessageUtil.colorize(s));
                    }
                    itemMeta.setLore(lines);
                    // 设置附魔
                    itemStack.setItemMeta(itemMeta);

                    player.getInventory().addItem(itemStack);
                }
            } else {
                AesopPlugin.logger.log("&c需要以游戏身份执行该指令");
            }
        } else {
            AesopPlugin.logger.log(sender, "&c参数有误");
        }
    }
}
