package top.mcos.command.subcommands;

import com.epicnicity322.epicpluginlib.bukkit.command.Command;
import com.epicnicity322.epicpluginlib.bukkit.command.CommandRunnable;
import com.epicnicity322.epicpluginlib.bukkit.command.TabCompleteRunnable;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mcos.AesopPlugin;
import top.mcos.util.MessageUtil;

/**
 * 消息命令：/xxx msg
 */
public final class MsgSubCommand extends Command implements Helpable {
    @Override
    public @NotNull CommandRunnable onHelp() {
        return (label, sender, args) -> AesopPlugin.logger.log(sender, "&2发送消息: &a/"+label + " "+getName());
    }

    @Override
    public @Nullable String[] getAliases() {
        return new String[]{"msg"};
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
        return "aesopplugin.admin.msg";
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
                possibleCompletions.add("bc"); //广播消息 msg bc <消息分类> <消息内容>
                possibleCompletions.add("out");
            }
            if(args.length==3) {
                if("bc".equals(args[1])) {
                    possibleCompletions.add(""); //发 msg send
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
        if("bc".equals(args[1])) {

            Bukkit.broadcastMessage(MessageUtil.colorize("&#2796e7测试&a你好\n可以吗"));
            //  # 广播消息前缀
            //  msg-bc-prefix: ""
            //  # 广播消息声音
            //  msg-bc-sound: ""
        } else {
            AesopPlugin.logger.log(sender, "&c参数有误");
        }
    }
}
