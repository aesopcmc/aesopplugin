package top.mcos.command.subcommands;

import com.epicnicity322.epicpluginlib.bukkit.command.Command;
import com.epicnicity322.epicpluginlib.bukkit.command.CommandRunnable;
import com.epicnicity322.epicpluginlib.bukkit.command.TabCompleteRunnable;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mcos.AesopPlugin;
import top.mcos.activity.newyear.config.sub.RunTaskPlanConfig;
import top.mcos.config.ConfigLoader;
import top.mcos.scheduler.jobs.YanhuaRunTaskJob;

import java.util.List;

/**
 * 消息命令：/xxx msg
 */
public final class TaskSubCommand extends Command implements Helpable {
    @Override
    public @NotNull CommandRunnable onHelp() {
        return (label, sender, args) -> AesopPlugin.logger.log(sender, "&2任务调度: &a/"+label + " "+getName());
    }

    @Override
    public @Nullable String[] getAliases() {
        return super.getAliases();
    }

    @Override
    public @NotNull String getName() {
        return "task";
    }

    @Override
    public int getMinArgsAmount() {
        return 2;
    }

    @Override
    public @Nullable String getPermission() {
        return "aesopplugin.admin.task";
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
                possibleCompletions.add("run");         // task run 任务名称 任务key
            }
            if(args.length==3) {
                if("run".contains(args[1])) {
                    possibleCompletions.add("yanhua");
                }
            }
            if(args.length==4) {
                if("run".contains(args[1]) && "yanhua".contains(args[2])) {
                    List<RunTaskPlanConfig> plans = ConfigLoader.yanHuaConfig.getPlans();
                    for (RunTaskPlanConfig plan : plans) {
                        possibleCompletions.add(plan.getKey());
                    }
                }
            }
        };
    }

    @Override
    public void run(@NotNull String label, @NotNull CommandSender sender, @NotNull String[] args) {
        if(sender instanceof Player) {
            FileConfiguration config = AesopPlugin.getInstance().getConfig();
            Player player = (Player) sender;
            if("run".equals(args[1])) {
                if(args.length<4) {
                    AesopPlugin.logger.log(player, "&c参数不足");
                    return;
                }
                String taskName = args[2];
                String taskKey = args[3];
                if("yanhua".equals(taskName)) {
                    List<RunTaskPlanConfig> plans = ConfigLoader.yanHuaConfig.getPlans();
                    for (RunTaskPlanConfig plan : plans) {
                        if (taskKey.equals(plan.getKey())) {
                            YanhuaRunTaskJob.executeNow(plan);
                            break;
                        }
                    }
                }
                //Boolean setFlag = Boolean.valueOf(args[2]);
                //config.set("tasks.refresh-map.enable", setFlag);
                //AesopPlugin.getInstance().saveConfig();
                //AesopPlugin.logger.log(player, "设为："+ setFlag);

                player.sendMessage("已执行"+taskName+"...");
            } else if ("show".equals(args[1])) {
                // 测试显示配置
                boolean flag = config.getBoolean("tasks.refresh-map.enable");
                AesopPlugin.logger.log(player, "输出："+ flag);

            } else {
                AesopPlugin.logger.log(player, "&c参数有误");
                this.onHelp();
            }
        }
    }
}
