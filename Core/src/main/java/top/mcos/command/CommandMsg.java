package top.mcos.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.mcos.AesopPlugin;

/**
 * 简易命令注册示例
 */
public class CommandMsg implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            AesopPlugin.logger.log(player, "你好!"+player.getDisplayName()+"&a今天天气真晴朗”");
            player.sendMessage();
            return true;
        }

        return false;
    }
}
