package top.mcos.command.subcommands;

import top.mcos.util.epiclib.logger.ConsoleLogger;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mcos.AesopPlugin;
import top.mcos.util.epiclib.command.Command;
import top.mcos.util.epiclib.command.CommandRunnable;
import top.mcos.util.epiclib.command.TabCompleteRunnable;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * 消息命令：/xxx msg
 */
public final class CleanCommand extends Command implements Helpable {
    @Override
    public @NotNull CommandRunnable onHelp() {
        return (label, sender, args) -> AesopPlugin.logger.log(sender, "&2发送消息: &a/"+label + " "+getName());
    }

    @Override
    public @Nullable String[] getAliases() {
        return new String[]{"clean"};
    }

    @Override
    public @NotNull String getName() {
        return "clean";
    }

    @Override
    public int getMinArgsAmount() {
        return 2;
    }

    @Override
    public @Nullable String getPermission() {
        return "aesopplugin.admin.clean";
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
                possibleCompletions.add("logs"); //清理日志文件 clean logs <1d|1w|1mo>(保留时长)
            }
            if(args.length==3) {
                if("logs".equals(args[1])) {
                    possibleCompletions.add("1d");
                    possibleCompletions.add("1w");
                    possibleCompletions.add("1m");
                    possibleCompletions.add("1y");
                }
            }

            //if (args.length == 2) {
            //    for (String soundType : SoundType.getPresentSoundNames()) {
            //        if (soundType.startsWith(args[1].toUpperCase(Locale.ROOT))) {
            //            possibleCompletions.add(soundType);
            //        }
            //    }
            //} else if (args.length == 3) {
            //    CommandUtils.addTargetTabCompletion(possibleCompletions, args[2], sender, "playmoresounds.play.others");
            //}
        };
    }

    @Override
    public void run(@NotNull String label, @NotNull CommandSender sender, @NotNull String[] args) {
        if("logs".equals(args[1])) {
            try {
                //String basePath = AesopPlugin.getInstance().getDataFolder().getCanonicalPath();
                String basePath = AesopPlugin.getInstance().getDataFolder().getCanonicalPath();
                basePath = new File(basePath).getParentFile().getParentFile().getCanonicalPath();
                if(StringUtils.isBlank(basePath)) return;
                String targetDir = basePath + "/logs";
                String timeInput = args[2];
                long keepTime;

                String unit = timeInput.replaceAll("^[1-9]+", "");
                long addNumber = Long.parseLong(timeInput.replaceAll("[a-z]+$", ""));
                switch (unit) {
                    case "d":
                        keepTime = LocalDateTime.now().minusDays(addNumber).toInstant(ZoneOffset.of("+8")).toEpochMilli();
                        break;
                    case "w":
                        keepTime = LocalDateTime.now().minusWeeks(addNumber).toInstant(ZoneOffset.of("+8")).toEpochMilli();
                        break;
                    case "m":
                        keepTime = LocalDateTime.now().minusMonths(addNumber).toInstant(ZoneOffset.of("+8")).toEpochMilli();
                        break;
                    case "y":
                        keepTime = LocalDateTime.now().minusYears(addNumber).toInstant(ZoneOffset.of("+8")).toEpochMilli();
                        break;
                    default:
                        return;
                }

                int index = 0;
                AesopPlugin.logger.log("&2&l========开始清理日志文件========>>>");
                File[] files = new File(targetDir).listFiles();
                if(files!=null) {
                    for (File file : files) {
                        long fileTime = file.lastModified();
                        if (fileTime < keepTime) {
                            if (file.exists() && file.isFile()) {
                                //删除
                                String canonicalPath = file.getCanonicalPath();
                                if (new File(canonicalPath).delete()) {
                                    AesopPlugin.logger.log("&a" + canonicalPath + " 已清理。");
                                    index++;
                                }
                            }
                        }
                    }
                }
                AesopPlugin.logger.log("&2&l日志文件清理完毕，共清理了 &e"+index+" &a&l个文件");
                AesopPlugin.logger.log("&2&l<<<==========================");
            } catch (IOException e) {
                AesopPlugin.logger.log("&c清理日志文件出错", ConsoleLogger.Level.ERROR);
            }
        } else {
            AesopPlugin.logger.log(sender, "&c参数有误");
        }
    }
}
