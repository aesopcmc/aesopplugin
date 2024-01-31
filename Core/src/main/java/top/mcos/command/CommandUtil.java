package top.mcos.command;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import top.mcos.AesopPlugin;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandUtil {
    public static final String COMMAND_PLAYER_SENDER_TYPE = "[player]";
    public static final String COMMAND_CONSOLE_SENDER_TYPE = "[console]";

    /**
     * 执行自封装命令
     * @param commands
     * @param player
     */
    public static void executeCommand(List<String> commands, Player player) {
        if (commands != null) {
            // 执行命令需要在同步线程中执行 ，否者会报错:TODO java.lang.IllegalStateException: Asynchronous Command Dispatched Async:
            Bukkit.getScheduler().runTask(AesopPlugin.getInstance(), () -> {
                for (String command : commands) {
                    String regex = "(^\\[.+\\]) (.+)";
                    Matcher matcher = Pattern.compile(regex).matcher(command);
                    if (matcher.find()) {
                        String prefix = matcher.group(1);
                        String cmd = matcher.group(2);
                        if (COMMAND_PLAYER_SENDER_TYPE.equalsIgnoreCase(prefix)) {
                            // 以玩家身份执行命令
                            cmd = cmd.replaceAll("\\{player\\}", player.getName());
                            Bukkit.getServer().dispatchCommand(player, cmd);
                        } else if (COMMAND_CONSOLE_SENDER_TYPE.equalsIgnoreCase(prefix)) {
                            // 以控制台身份执行命令
                            cmd = cmd.replaceAll("\\{player\\}", player.getName());
                            ConsoleCommandSender consoleSender = Bukkit.getServer().getConsoleSender();
                            Bukkit.getServer().dispatchCommand(consoleSender, cmd);
                        }
                    } else {
                        // 没有匹配到前缀的情况下，默认以玩家身份执行命令
                        Bukkit.getServer().dispatchCommand(player, command);
                    }
                }
            });
        }
    }
}
