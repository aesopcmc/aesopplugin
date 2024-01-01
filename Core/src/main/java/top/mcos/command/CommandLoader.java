package top.mcos.command;

import com.epicnicity322.epicpluginlib.bukkit.command.Command;
import com.epicnicity322.epicpluginlib.bukkit.command.CommandManager;
import top.mcos.AesopPlugin;
import top.mcos.command.subcommands.ActivitySubCommand;
import top.mcos.command.subcommands.FireworkSubCommand;
import top.mcos.command.subcommands.FireSubCommand;
import top.mcos.command.subcommands.MsgSubCommand;
import top.mcos.command.subcommands.ReloadSubCommand;
import top.mcos.command.subcommands.TaskSubCommand;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import top.mcos.command.subcommands.HelpSubCommand;

import java.util.LinkedHashSet;

/**
 * 命令加载器
 */
public final class CommandLoader {
    private static final @NotNull LinkedHashSet<Command> commands = new LinkedHashSet<>();

    static {
        AesopPlugin.onInstance(() -> {
            var plugin = AesopPlugin.getInstance();

            commands.add(new HelpSubCommand());
            commands.add(new ReloadSubCommand());
            commands.add(new MsgSubCommand());
            commands.add(new TaskSubCommand());
            commands.add(new ActivitySubCommand());
            commands.add(new FireworkSubCommand());
            commands.add(new FireSubCommand());

            // 注册主命令 + 子命令
            CommandManager.registerCommand(Bukkit.getPluginCommand("aesopplugin"), commands,
                    (label, sender, args) -> {
                        /*
                        添加插件描述信息，当输入不带参数的主命令时，会打印以下消息：
                         */
                        AesopPlugin.logger.log(sender, "&2插件作者：&aAesop");
                        AesopPlugin.logger.log(sender, "&2查看所有指令帮助：&a/"+label+" help");
                    },
                    (label, sender, args) -> {
                        /*
                            输入了未知的子命令打印的消息
                         */
                        AesopPlugin.logger.log(sender, "&c未知的命令");
                    });
        });
    }

    private CommandLoader() {
    }

    /**
     * Adds a sub command to PlayMoreSounds' main command.
     *
     * @param command The command to add.
     */
    public static void addCommand(@NotNull Command command) {
        commands.add(command);
    }

    /**
     * @return An immutable set of PlayMoreSounds' registered sub commands.
     */
    public static @NotNull LinkedHashSet<Command> getCommands() {
        return new LinkedHashSet<>(commands);
    }
}
