package top.mcos.command.subcommands;

import com.epicnicity322.epicpluginlib.bukkit.command.Command;
import com.epicnicity322.epicpluginlib.bukkit.command.CommandRunnable;
import com.epicnicity322.epicpluginlib.bukkit.command.TabCompleteRunnable;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mcos.AesopPlugin;
import top.mcos.config.ConfigLoader;
import top.mcos.config.configs.subconfig.ItemBindCommandsConfig;
import top.mcos.business.itmebind.ItemEvent;

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
            if(args.length==2) {
                if("itembind".contains(args[0]) && sender.isOp()) { // TODO 暂且固定op才能有该权限
                    possibleCompletions.add("give");        // itembind give <命令key>
                }
            }
            if(args.length==3) {
                if("itembind".contains(args[0]) && "give".contains(args[1]) && sender.isOp()) { // TODO 暂且固定op才能有该权限
                    List<ItemBindCommandsConfig> itemBindCommandConfigs = ConfigLoader.baseConfig.getItemBindCommandConfigs();
                    List<String> keys = itemBindCommandConfigs.stream().filter(ItemBindCommandsConfig::isEnable).map(ItemBindCommandsConfig::getKey).toList();
                    possibleCompletions.addAll(keys);
                }
            }
        };
    }

    @Override
    public void run(@NotNull String label, @NotNull CommandSender sender, @NotNull String[] args) {
        if("givemenu".equals(args[0])) {
            if (sender instanceof Player player) {
                List<ItemBindCommandsConfig> itemBindCommandConfigs = ConfigLoader.baseConfig.getItemBindCommandConfigs();
                Map<String, ItemBindCommandsConfig> configMap = itemBindCommandConfigs.stream().collect(Collectors.toMap(ItemBindCommandsConfig::getKey, c -> c));

                String ibKey = "menu"; //特殊key: menu：菜单命令，普通玩家可通过命令（ /aep givemenu）执行
                ItemBindCommandsConfig config = configMap.get(ibKey);
                if (config == null) {
                    AesopPlugin.logger.log(sender, "&c物品不存在");
                    return;
                }
                // 给与物品
                ItemEvent.giveItem(config, player);
            } else {
                AesopPlugin.logger.log("&c需要以游戏身份执行该指令");
            }
        }else if("itembind".equals(args[0])) {
            if("give".equals(args[1]) && sender instanceof Player player) {
                String key = args[2];
                List<ItemBindCommandsConfig> configs = ConfigLoader.baseConfig.getItemBindCommandConfigs();
                for (ItemBindCommandsConfig config : configs) {
                    if(config.getKey().equals(key)) {
                        // 给与物品
                        ItemEvent.giveItem(config, player);
                        return;
                    }
                }
            }
        } else {
            AesopPlugin.logger.log(sender, "&c参数有误");
        }
    }
}
