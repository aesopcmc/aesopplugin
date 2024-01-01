package top.mcos.command.subcommands;

import com.epicnicity322.epicpluginlib.bukkit.command.Command;
import com.epicnicity322.epicpluginlib.bukkit.command.CommandRunnable;
import com.epicnicity322.epicpluginlib.bukkit.command.TabCompleteRunnable;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mcos.AesopPlugin;
import top.mcos.config.ConfigLoader;
import top.mcos.config.configs.subconfig.PlayerFireworkGroupConfig;
import top.mcos.database.dao.PlayerFireworkDao;
import top.mcos.hook.firework.FireWorkManage;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public final class FireworkSubCommand extends Command implements Helpable {
    @Override
    public @NotNull CommandRunnable onHelp() {
        return (label, sender, args) -> AesopPlugin.logger.log(sender, "&2粒子特效管理指令: &a/"+label + " "+getName());
    }

    @Override
    public @Nullable String[] getAliases() {
        return new String[]{"firework"};
    }

    @Override
    public @NotNull String getName() {
        return "firework";
    }

    @Override
    public int getMinArgsAmount() {
        return 2;
    }

    @Override
    public @Nullable String getPermission() {
        return "aesopplugin.admin.firework";
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
                possibleCompletions.add("give"); //     设置粒子组 give <playerName> <玩家粒子组key>
                possibleCompletions.add("remove"); //   移除玩家粒子组 remove <playerName> <玩家粒子组key>
                possibleCompletions.add("removeall"); //   移除玩家粒子组 remove <playerName>
            }
            if(args.length==3) {
                Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
                for (Player onlinePlayer : onlinePlayers) {
                    possibleCompletions.add(onlinePlayer.getName());
                }
            }
            if(args.length==4 && "give,remove".contains(args[1])) {
                List<PlayerFireworkGroupConfig> playerFireworkGroups = ConfigLoader.fwConfig.getPlayerFireworkGroups();
                for (PlayerFireworkGroupConfig playerFireworkGroup : playerFireworkGroups) {
                    possibleCompletions.add(playerFireworkGroup.getKey());
                }
            }
            if(args.length==4 && "removeall".contains(args[1])) {

            }
        };
    }

    @Override
    public void run(@NotNull String label, @NotNull CommandSender sender, @NotNull String[] args) {
        if("give".equals(args[1])) {
            String playerName = args[2];

            String groupKey = args[3];

            PlayerFireworkGroupConfig currCconfig = null;

            List<PlayerFireworkGroupConfig> configs = ConfigLoader.fwConfig.getPlayerFireworkGroups();
            for (PlayerFireworkGroupConfig config : configs) {
                if(groupKey.equals(config.getKey())) {
                    currCconfig = config;
                }
            }
            if(currCconfig==null) {
                AesopPlugin.logger.log(sender, "&c'"+groupKey+"' 粒子组不存在！");
                return;
            }

            // TODO 获取不了离线玩家, 只能给在线玩家赋予粒子特效
            Player targetPlayer = Bukkit.getPlayer(playerName);
            if(targetPlayer==null) {
                AesopPlugin.logger.log(sender, "&c玩家 '"+playerName+"' 不存在，或已经离线，无法给予！");
                return;
            }
            String playerId = targetPlayer.getUniqueId().toString();
            String groupName = currCconfig.getName();
            PlayerFireworkDao playerFireworkDao = AesopPlugin.getInstance().getDatabase().getPlayerFireworkDao();

            try {
                AesopPlugin.callInTransaction(()->{
                    boolean exist = playerFireworkDao.isExist(playerId, groupKey);
                    if(exist) {
                        AesopPlugin.logger.log(sender, "&c该玩家已经拥有过这个粒子组了");
                    } else {
                        playerFireworkDao.insertGroupKeys(playerId, playerName, groupKey);
                        AesopPlugin.logger.log(sender, "&a已将 '"+groupName+"' 授予给玩家 '"+playerName+"'");
                        AesopPlugin.logger.log(targetPlayer, "&a恭喜您，获得一个文字粒子特效 '"+groupName+"' .");
                    }
                    return null;
                });
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            // 更新缓存
            FireWorkManage.getInstance().putPlayerFireworkToCache(targetPlayer.getUniqueId().toString());
        } else if("remove,removeall".contains(args[1])) {
            try {
                if(args.length<3) {
                    AesopPlugin.logger.log(sender, "&c参数数量有误！");
                    return;
                }
                // 清理玩家数据
                String playerName = args[2];
                String groupKey = args.length==4 && StringUtils.isNotBlank(args[3]) ? args[3] : "";

                PlayerFireworkGroupConfig currCconfig = null;

                if(StringUtils.isNotBlank(groupKey)) {
                    List<PlayerFireworkGroupConfig> configs = ConfigLoader.fwConfig.getPlayerFireworkGroups();
                    for (PlayerFireworkGroupConfig config : configs) {
                        if (groupKey.equals(config.getKey())) {
                            currCconfig = config;
                        }
                    }
                }
                if("remove".equals(args[1]) && currCconfig==null) {
                    AesopPlugin.logger.log(sender, "&c '"+groupKey+"' 粒子组不存在！");
                    return;
                }

                Player targetPlayer = Bukkit.getPlayer(playerName);
                String playerId = targetPlayer.getUniqueId().toString();
                String groupName = currCconfig==null ? "" : currCconfig.getName();

                PlayerFireworkDao playerFireworkDao = AesopPlugin.getInstance().getDatabase().getPlayerFireworkDao();

                AesopPlugin.callInTransaction(()-> {
                    if(StringUtils.isNotBlank(groupKey)) {
                        if (playerFireworkDao.deleteGroupKey(playerId, groupKey)) {
                            AesopPlugin.logger.log(sender, "&a 成功删除玩家 '" + playerName + "' 粒子组 '" + groupName + "'。");
                        }
                    } else {
                        if (playerFireworkDao.deleteGroupKey(playerId, null)) {
                            AesopPlugin.logger.log(sender, "&a 成功删除玩家 '" + playerName + "' 所有粒子组。");
                        }
                    }
                    return null;
                });

                // 更新缓存
                FireWorkManage.getInstance().putPlayerFireworkToCache(playerId);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            AesopPlugin.logger.log(sender, "&c参数有误");
        }
    }
}
