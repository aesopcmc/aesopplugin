package top.mcos.command.subcommands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mcos.AesopPlugin;
import top.mcos.business.BusRegister;
import top.mcos.util.epiclib.command.Command;
import top.mcos.util.epiclib.command.CommandRunnable;
import top.mcos.util.epiclib.command.TabCompleteRunnable;

import java.util.Map;

/**
 * 掉落物清理：/xxx gbclear
 */
public final class GbclearSubCommand extends Command implements Helpable {
    @Override
    public @NotNull CommandRunnable onHelp() {
        return (label, sender, args) -> AesopPlugin.logger.log(sender, "&2清理掉落物: &a/"+label + " "+getName());
    }

    @Override
    public @Nullable String[] getAliases() {
        return new String[]{"gbclear"};
    }

    @Override
    public @NotNull String getName() {
        return "gbclear";
    }

    @Override
    public int getMinArgsAmount() {
        return 1;
    }

    @Override
    public @Nullable String getPermission() {
        return "aesopplugin.admin.gbclear";
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
            //gbclear 手动清理掉落物（已加载的掉落物）
            //gbclear showStore  显示缓存的掉落物集合信息
            //gbclear clearStore 清理掉落物缓存
            if(args.length==2) {
                possibleCompletions.add("showStore");
                possibleCompletions.add("clearStore");
            }
        };
    }

    @Override
    public void run(@NotNull String label, @NotNull CommandSender sender, @NotNull String[] args) {
        if("gbclear".equals(args[0]) && args.length==1){
            BusRegister.clearBus.clearLoadedItems();
        } else if("gbclear".equals(args[0]) && args.length==2){
            if("showStore".equals(args[1])) {
                Map<String, Long> itemStore = BusRegister.clearBus.getUnloadItemStore();
                itemStore.forEach((key, value)-> {
                    System.out.println("缓存uid: "+key);
                });
                System.out.println("缓存总量: "+itemStore.size());
            } else if("clearStore".equals(args[1])) {
                Map<String, Long> itemStore = BusRegister.clearBus.getUnloadItemStore();
                itemStore.clear();
                System.out.println("已清理缓存的掉落物");
            }
        } else {
            AesopPlugin.logger.log(sender, "&c参数有误");
        }
    }
}
