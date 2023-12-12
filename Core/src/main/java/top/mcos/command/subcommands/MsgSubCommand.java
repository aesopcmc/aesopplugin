package top.mcos.command.subcommands;

import com.epicnicity322.epicpluginlib.bukkit.command.Command;
import com.epicnicity322.epicpluginlib.bukkit.command.CommandRunnable;
import com.epicnicity322.epicpluginlib.bukkit.command.TabCompleteRunnable;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.popcraft.chunky.api.ChunkyAPI;
import top.mcos.AesopPlugin;

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

    @Override
    public void run(@NotNull String label, @NotNull CommandSender sender, @NotNull String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if("send".equals(args[1])) {
                //MessageHandler.sendAllOnlinePlayers("测试");
                //Bukkit.broadcastMessage("&a服务器即将&c关机: " + args[2]);

                // 测试发送center消息
                //NetworkManager networkManager = null;
                //try {
                //    networkManager = (NetworkManager) MessageHandler.networkManagerH.get(((CraftPlayer) player).getHandle().b);
                //} catch (IllegalAccessException e) {
                //    AesopPlugin.logger.log("实例化网络管理器出错", ConsoleLogger.Level.ERROR);
                //    e.printStackTrace();
                //}
                //String title = "主标题";
                //String subtitle = "子标题xxx";
                //ClientboundSetTitleTextPacket text = new ClientboundSetTitleTextPacket(CraftChatMessage.fromStringOrNull(title));
                //ClientboundSetSubtitleTextPacket subtext = new ClientboundSetSubtitleTextPacket(CraftChatMessage.fromStringOrNull(subtitle));
                //networkManager.a((Packet<?>) text);
                //networkManager.a((Packet<?>) subtext);

                /*
                多世界
                 */
                //MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
                //MVWorldManager mvWorldManager = core.getMVWorldManager();
                ////long seed = (new Random()).nextLong();
                //mvWorldManager.regenWorld("zy", true, true, null, true);
                //AesopPlugin.logger.log("世界已重置");

                /*
                区块生成
                 */
                //ChunkyAPI chunky = Bukkit.getServer().getServicesManager().load(ChunkyAPI.class);
                //AesopPlugin.logger.log("开始加载区块...");
                //chunky.startTask("zy", "square", 0, 0, 600, 600, "concentric");

                /*
                执行命令
                 */
                //String cmdline = "gamerule doDaylightCycle "+args[2];
                //ConsoleCommandSender consoleSender = Bukkit.getServer().getConsoleSender();
                //Bukkit.getServer().dispatchCommand(consoleSender, cmdline);

                /**
                 * 获取插件路径
                 */

                AesopPlugin.logger.log("结束指令");
            } else if ("out".equals(args[1])) {
                ChunkyAPI chunky = Bukkit.getServer().getServicesManager().load(ChunkyAPI.class);
                AesopPlugin.logger.log(player, "isRunning:"+chunky.isRunning("zy"));
                AesopPlugin.logger.log(player, "out");
            } else {
                AesopPlugin.logger.log(player, "&c参数有误");
            }
        }
    }
}
