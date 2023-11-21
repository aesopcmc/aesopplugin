package top.mcos.command.subcommands;

import com.epicnicity322.epicpluginlib.bukkit.command.Command;
import com.epicnicity322.epicpluginlib.bukkit.command.CommandRunnable;
import com.epicnicity322.epicpluginlib.core.logger.ConsoleLogger;
import top.mcos.AesopPlugin;
import top.mcos.command.CommandLoader;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * 帮助命令
 * /xxx help
 */
public final class HelpSubCommand extends Command implements Helpable {
    @Override
    public @NotNull String getName() {
        return "help";
    }

    @Override
    public @NotNull String getPermission() {
        return "aesopplugin.help";
    }

    @Override
    public @NotNull CommandRunnable onHelp() {
        return (label, sender, args) -> AesopPlugin.logger.log(sender, "&2指令帮助: &a/"+label + " "+getName());
    }

    @Override
    protected @NotNull CommandRunnable getNoPermissionRunnable() {
        return (label, sender, args) -> AesopPlugin.logger.log(sender, "&c您没有执行该命令的权限");
    }

    @Override
    public void run(@NotNull String label, @NotNull CommandSender sender, @NotNull String[] args) {
        for (var command : CommandLoader.getCommands()) {
            if (command instanceof Helpable helpable && (command.getPermission() == null || sender.hasPermission(command.getPermission()))) {
                try {
                    //TODO 分页显示多个命令
                    AesopPlugin.logger.log("&a指令帮助：");
                    helpable.onHelp().run(label, sender, args);
                } catch (Throwable t) {
                    AesopPlugin.logger.log(sender.getName() + " 在尝试发送命令时发生错误: " + command.getName(), ConsoleLogger.Level.WARN);
                }
            }
        }
    }
}
