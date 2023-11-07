package top.mcos.command.subcommands;

import com.epicnicity322.epicpluginlib.bukkit.command.Command;
import com.epicnicity322.epicpluginlib.bukkit.command.CommandRunnable;
import com.epicnicity322.epicpluginlib.bukkit.command.TabCompleteRunnable;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mcos.AesopPlugin;

import java.lang.reflect.Field;

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
        return "aesopplugin.task";
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
            possibleCompletions.add("run");
            possibleCompletions.add("show");
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
            FileConfiguration config = AesopPlugin.getInstance().getConfig();
            Player player = (Player) sender;
            if("run".equals(args[1])) {
                //Boolean setFlag = Boolean.valueOf(args[2]);
                //config.set("tasks.refresh-map.enable", setFlag);
                //AesopPlugin.getInstance().saveConfig();
                //AesopPlugin.logger.log(player, "设为："+ setFlag);

                player.sendTitle("已执行run...", "", 10, 40, 10);


                try {
                    var packet = new ClientboundSetActionBarTextPacket(CraftChatMessage.fromStringOrNull("你好啊123覅方法"));

                    Field networkManagerH = PlayerConnection.class.getDeclaredField("h");
                    networkManagerH.setAccessible(true);
                    NetworkManager networkManager = (NetworkManager) networkManagerH.get(((CraftPlayer) player).getHandle().b);

                    networkManager.a((Packet<?>) packet);

                    //ComponentBuilder builder = new ComponentBuilder("");
                    //builder.append(new TranslatableComponent(""));
                    //sender.spigot().sendMessage(builder.create());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else if ("show".equals(args[1])) {
                boolean flag = config.getBoolean("tasks.refresh-map.enable");
                AesopPlugin.logger.log(player, "输出："+ flag);
            } else {
                AesopPlugin.logger.log(player, "&c参数有误");
                this.onHelp();
            }

        }
    }
}
