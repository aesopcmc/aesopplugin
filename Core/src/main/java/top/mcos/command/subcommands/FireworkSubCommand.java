package top.mcos.command.subcommands;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mcos.AesopPlugin;
import top.mcos.config.ConfigLoader;
import top.mcos.business.firework.config.sub.LocationFireworkGroupConfig;
import top.mcos.business.firework.config.sub.PlayerFireworkGroupConfig;
import top.mcos.database.dao.PlayerFireworkDao;
import top.mcos.business.firework.FireWorkManage;
import top.mcos.util.epiclib.command.Command;
import top.mcos.util.epiclib.command.CommandRunnable;
import top.mcos.util.epiclib.command.TabCompleteRunnable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                possibleCompletions.add("location");    // 显示|隐藏位置粒子 firework location <show|hide> <位置粒子组key>
                possibleCompletions.add("give");        // 给予玩家粒子组 firework give <playerName> <玩家粒子组key>
                possibleCompletions.add("remove");      // 移除玩家粒子组 firework  remove <playerName> <玩家粒子组key>
                possibleCompletions.add("removeall");   // 移除玩家所有粒子组 firework  removeall <playerName>
                possibleCompletions.add("setup");       // 设置粒子组 firework setup <player|location> <自定义组key> <自定义组名>
            }
            if(args.length==3) {
                if("give,remove,removeall".contains(args[1])) {
                    Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
                    for (Player onlinePlayer : onlinePlayers) {
                        possibleCompletions.add(onlinePlayer.getName());
                    }
                }
                if("setup".contains(args[1])) {
                    possibleCompletions.add("player");
                    possibleCompletions.add("location");
                }
                if("location".contains(args[1])) {
                    possibleCompletions.add("show");
                    possibleCompletions.add("hide");
                }
            }
            if(args.length==4) {
                if("give,remove".contains(args[1])) {
                    List<PlayerFireworkGroupConfig> playerFireworkGroups = ConfigLoader.fireworkConfig.getPlayerFireworkGroups();
                    for (PlayerFireworkGroupConfig playerFireworkGroup : playerFireworkGroups) {
                        possibleCompletions.add(playerFireworkGroup.getKey());
                    }
                }
                if("setup".contains(args[1])) {
                    possibleCompletions.add("<自定义组key>");
                }
                if("location".contains(args[1])) {
                    List<LocationFireworkGroupConfig> locationFireworkGroups = ConfigLoader.fireworkConfig.getLocationFireworkGroups();
                    List<String> keys = locationFireworkGroups.stream().map(LocationFireworkGroupConfig::getKey).toList();
                    possibleCompletions.addAll(keys);
                }
            }
            if(args.length==5) {
                if("setup".contains(args[1])) {
                    possibleCompletions.add("<自定义组名>");
                }
            }
        };
    }

    @Override
    public void run(@NotNull String label, @NotNull CommandSender sender, @NotNull String[] args) {
        if("location".equals(args[1])) {
            String flag = args[2];
            String groupKey = args[3];

            List<LocationFireworkGroupConfig> fgList = ConfigLoader.fireworkConfig.getLocationFireworkGroups();
            Map<String, LocationFireworkGroupConfig> fgMaps = fgList.stream().collect(Collectors.toMap(LocationFireworkGroupConfig::getKey, c -> c));
            LocationFireworkGroupConfig groupConfig = fgMaps.get(groupKey);
            if(groupConfig==null) {
                AesopPlugin.logger.log(sender, "&c'"+groupKey+"' 粒子组不存在！");
                return;
            }
            if("show".equals(flag)) {
                groupConfig.setEnable(true);
                ConfigLoader.saveConfig(groupConfig);
                AesopPlugin.logger.log(sender, "&a已显示");
            } else if("hide".equals(flag)) {
                groupConfig.setEnable(false);
                ConfigLoader.saveConfig(groupConfig);
                AesopPlugin.logger.log(sender, "&c已隐藏");
            }
        }else if("give".equals(args[1])) {
            String playerName = args[2];

            String groupKey = args[3];

            PlayerFireworkGroupConfig currCconfig = null;

            List<PlayerFireworkGroupConfig> configs = ConfigLoader.fireworkConfig.getPlayerFireworkGroups();
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
                    List<PlayerFireworkGroupConfig> configs = ConfigLoader.fireworkConfig.getPlayerFireworkGroups();
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
        } else if("setup".contains(args[1])) {
            if(sender instanceof Player player) {
                // 简单生成粒子组配置，具体特效要编辑
                //firework setup <player|location> <自定义组key> <自定义组名>
                if (args.length < 5) {
                    AesopPlugin.logger.log(player, "&c参数不全");
                    return;
                }

                String type = args[2];
                String groupKey = args[3];
                String groupName = args[4];

                if ("location".equals(type)) {
                    List<LocationFireworkGroupConfig> locationFireworkGroups = ConfigLoader.fireworkConfig.getLocationFireworkGroups();
                    for (LocationFireworkGroupConfig groupConfig : locationFireworkGroups) {
                        if(groupConfig.getKey().equals(groupKey)) {
                            AesopPlugin.logger.log(player, "&c组key '"+groupKey+"' 已存在，请另起一个名称");
                            return;
                        }
                    }

                    LocationFireworkGroupConfig groupConfig = new LocationFireworkGroupConfig();
                    groupConfig.setKey(groupKey);
                    groupConfig.setEnable(true);
                    groupConfig.setName(groupName);
                    groupConfig.setFireworkKeys(new ArrayList<>());
                    Location location = player.getLocation();
                    groupConfig.setLocation(location.getWorld().getName()+ ","+location.getX()+","+location.getY()+","+location.getZ() + "," + location.getYaw() +","+location.getPitch());

                    ConfigLoader.saveConfig(groupConfig);
                    locationFireworkGroups.add(groupConfig);

                    AesopPlugin.logger.log(player, "&a设置成功.");
                } else if("player".equals(type)) {
                    List<PlayerFireworkGroupConfig> playerFireworkGroups = ConfigLoader.fireworkConfig.getPlayerFireworkGroups();
                    for (PlayerFireworkGroupConfig groupConfig : playerFireworkGroups) {
                        if(groupConfig.getKey().equals(groupKey)) {
                            AesopPlugin.logger.log(player, "&c组key '"+groupKey+"' 已存在，请重新换一个");
                            return;
                        }
                    }

                    PlayerFireworkGroupConfig groupConfig = new PlayerFireworkGroupConfig();
                    groupConfig.setKey(groupKey);
                    groupConfig.setEnable(true);
                    groupConfig.setName(groupName);
                    groupConfig.setFireworkKeys(new ArrayList<>());
                    groupConfig.setOffsetY(0);

                    ConfigLoader.saveConfig(groupConfig);
                    playerFireworkGroups.add(groupConfig);

                    AesopPlugin.logger.log(player, "&a设置成功.");
                }
            } else {
                AesopPlugin.logger.log("&c需要以游戏身份执行该指令");
            }

        } else {
            AesopPlugin.logger.log(sender, "&c参数有误");
        }
    }
}
