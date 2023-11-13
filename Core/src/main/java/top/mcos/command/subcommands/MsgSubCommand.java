package top.mcos.command.subcommands;

import com.epicnicity322.epicpluginlib.bukkit.command.Command;
import com.epicnicity322.epicpluginlib.bukkit.command.CommandRunnable;
import com.epicnicity322.epicpluginlib.bukkit.command.TabCompleteRunnable;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mcos.AesopPlugin;
import top.mcos.message.SchedulerMessageHandle;

/**
 * 消息命令：/xxx msg
 */
public final class MsgSubCommand extends Command implements Helpable {
    @Override
    public @NotNull CommandRunnable onHelp() {
        return (label, sender, args) -> AesopPlugin.logger.log(sender, "&2定时消息: &a/"+label + " "+getName());
    }

    @Override
    public @Nullable String[] getAliases() {
        return new String[]{"sendmessage"};
    }

    @Override
    public @NotNull String getName() {
        return "msg";
    }

    @Override
    public int getMinArgsAmount() {
        return 2;
    }

    @Override
    public @Nullable String getPermission() {
        return "aesopplugin.msg";
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
            possibleCompletions.add("send");
            possibleCompletions.add("out");
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

    //private String getInvalidArgsMessage(String label, CommandSender sender, String[] args) {
    //    var lang = PlayMoreSounds.getLanguage();
    //    return lang.get("General.Invalid Arguments").replace("<label>", label)
    //            .replace("<label2>", args[0]).replace("<args>", "<" +
    //                    lang.get("Play.Sound") + "> " + (sender instanceof Player ? "[" + lang.get("General.Player")
    //                    + "]" : "<" + lang.get("General.Player") + ">") + " [" + lang.get("Play.Volume") + "] [" +
    //                    lang.get("Play.Pitch") + "]");
    //}

    @Override
    public void run(@NotNull String label, @NotNull CommandSender sender, @NotNull String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if("send".equals(args[1])) {
                SchedulerMessageHandle.sendAllOnlinePlayers(args[2]);
                AesopPlugin.logger.log("已发送");
            } else if ("out".equals(args[1])) {
                AesopPlugin.logger.log(player, "out");
            } else {
                AesopPlugin.logger.log(player, "&c参数有误");
            }
        }
    }
}
