package top.mcos.command.subcommands;

import top.mcos.util.epiclib.command.Command;
import top.mcos.util.epiclib.command.CommandRunnable;
import top.mcos.util.epiclib.command.TabCompleteRunnable;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mcos.AesopPlugin;
import top.mcos.util.MessageUtil;

/**
 * 命令测试：/xxx test
 */
public final class TestSubCommand extends Command implements Helpable {
    @Override
    public @NotNull CommandRunnable onHelp() {
        return (label, sender, args) -> AesopPlugin.logger.log(sender, "&2命令测试: &a/"+label + " "+getName());
    }

    @Override
    public @Nullable String[] getAliases() {
        return new String[]{"test"};
    }

    @Override
    public @NotNull String getName() {
        return "test";
    }

    @Override
    public int getMinArgsAmount() {
        return 2;
    }

    @Override
    public @Nullable String getPermission() {
        return "aesopplugin.admin.test";
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
            if(args.length==1) {

            }
            if(args.length==2) {
                possibleCompletions.add("bc");      // test bc <消息分类> <消息内容>  #广播消息
                possibleCompletions.add("sound");   // test sound <声音> #测试声音
                possibleCompletions.add("test");    // test test # 通用测试
            }
            if(args.length==3) {
                if("bc".equals(args[1])) {
                    possibleCompletions.add("");
                }
                if("sound".equals(args[1])) {
                    for (Sound sound : Sound.values()) {
                        possibleCompletions.add(sound.name());
                    }
                }
            }

            if (args.length == 4) {

            } else if (args.length == 3) {
            }
        };
    }

    @Override
    public void run(@NotNull String label, @NotNull CommandSender sender, @NotNull String[] args) {
        if("bc".equals(args[1])) {
            Bukkit.broadcastMessage(MessageUtil.colorize("&#2796e7测试&a你好\n可以吗"));
        } else if ("sound".equals(args[1])) {
            if (sender instanceof Player player) {
                player.playSound(player, Sound.valueOf(args[2]), 50, 1);
            }
        } else if("test".equals(args[1])){
            //double pro = Double.parseDouble(args[2]);
            //MessageHandler.sendBossBar(ConfigLoader.gbClearConfig.getPrefix(), "&e即将清理掉落物"+pro*100 +"%", pro);
        } else {
            AesopPlugin.logger.log(sender, "&c参数有误");
        }
    }
}
