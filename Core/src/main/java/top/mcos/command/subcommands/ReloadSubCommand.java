package top.mcos.command.subcommands;

import com.epicnicity322.epicpluginlib.bukkit.command.Command;
import com.epicnicity322.epicpluginlib.bukkit.command.CommandRunnable;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mcos.AesopPlugin;
import top.mcos.message.MessageHandler;
import top.mcos.scheduler.SchedulerHandler;

/**
 * 重载插件：/xxx reload
 */
public final class ReloadSubCommand extends Command implements Helpable {
    @Override
    public @NotNull CommandRunnable onHelp() {
        return (label, sender, args) -> AesopPlugin.logger.log(sender, "&2刷新配置文件: &a/"+label + " "+getName());
    }

    @Override
    public @NotNull String getName() {
        return "reload";
    }

    //@Override
    //public @Nullable String[] getAliases() {
    //    return new String[]{"rl"};
    //}

    @Override
    public @Nullable String getPermission() {
        return "aesopplugin.reload";
    }

    @Override
    protected @Nullable CommandRunnable getNoPermissionRunnable() {
        return (label, sender, args) -> AesopPlugin.logger.log(sender, "&c您没有执行该命令的权限");
    }

    @Override
    public void run(@NotNull String label, @NotNull CommandSender sender, @NotNull String[] args) {
        try {
            AesopPlugin.getInstance().reloadConfig();
            SchedulerHandler.init();
            MessageHandler.init();
            AesopPlugin.logger.log("&a插件刷新成功");
        } catch (Throwable e) {
            e.printStackTrace();
            AesopPlugin.logger.log("&c插件重载失败");
        }
    }
}
