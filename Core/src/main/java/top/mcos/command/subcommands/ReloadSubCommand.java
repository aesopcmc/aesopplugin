package top.mcos.command.subcommands;

import com.epicnicity322.epicpluginlib.bukkit.command.Command;
import com.epicnicity322.epicpluginlib.bukkit.command.CommandRunnable;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mcos.AesopPlugin;
import top.mcos.config.ConfigLoader;
import top.mcos.message.MessageHandler;
import top.mcos.scheduler.SchedulerHandler;

import java.util.concurrent.TimeUnit;

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
            MessageHandler.setSendBreak(true);
            // 清理定时任务
            SchedulerHandler.clear();
            // 清理消息队列
            MessageHandler.clearQueue();
            // 稍作延迟，等待所有消息线程推出
            TimeUnit.SECONDS.sleep(1);
            MessageHandler.setSendBreak(false);
            // 重新读取配置
            ConfigLoader.load();
            // 重新注册消息通知任务
            SchedulerHandler.registerJobs();

            AesopPlugin.logger.log("&a插件刷新成功");
        } catch (Throwable e) {
            e.printStackTrace();
            AesopPlugin.logger.log("&c插件重载失败");
        }
    }
}
