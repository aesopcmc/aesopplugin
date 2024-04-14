package top.mcos.command.subcommands;

import top.mcos.business.BusRegister;
import top.mcos.util.epiclib.command.Command;
import top.mcos.util.epiclib.command.CommandRunnable;
import top.mcos.util.epiclib.command.TabCompleteRunnable;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mcos.AesopPlugin;
import top.mcos.business.yanhua.YanHuaEvent;
import top.mcos.config.ConfigLoader;
import top.mcos.business.firework.FireWorkManage;
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
        return "aesopplugin.admin.reload";
    }

    @Override
    protected @Nullable CommandRunnable getNoPermissionRunnable() {
        return (label, sender, args) -> AesopPlugin.logger.log(sender, "&c您没有执行该命令的权限");
    }

    @Override
    protected @Nullable TabCompleteRunnable getTabCompleteRunnable() {
        return (possibleCompletions, label, sender, args) -> {
            if(args.length==2) {
                possibleCompletions.add("yanhua");
            }
        };
    }

    @Override
    public void run(@NotNull String label, @NotNull CommandSender sender, @NotNull String[] args) {
        try {
            if(args.length==1) {
                MessageHandler.setSendBreak(true);
                // 清理定时任务
                SchedulerHandler.clear();
                // 清理消息队列
                MessageHandler.clearQueue();
                // 清理烟花消息队列
                YanHuaEvent.clearQueue();
                // 稍作延迟，等待所有消息线程推出
                TimeUnit.SECONDS.sleep(1);
                MessageHandler.setSendBreak(false);
                // 重新读取配置
                ConfigLoader.load(null);
                // 重新注册消息通知任务
                SchedulerHandler.registerAllJobs();
                // 重新注册粒子特效
                FireWorkManage.getInstance().reload();
                // 重载业务
                BusRegister.reload();
            } else if (args.length==2) {
                ConfigLoader.load(args[1]);
            } else {
                return;
            }
            AesopPlugin.logger.log(sender, "&a插件刷新成功");
        } catch (Throwable e) {
            e.printStackTrace();
            AesopPlugin.logger.log(sender, "&c插件重载失败");
        }
    }
}
