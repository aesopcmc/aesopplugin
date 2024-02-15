package top.mcos.command.subcommands;

import com.epicnicity322.epicpluginlib.bukkit.command.Command;
import com.epicnicity322.epicpluginlib.bukkit.command.CommandRunnable;
import com.epicnicity322.epicpluginlib.bukkit.command.TabCompleteRunnable;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mcos.AesopPlugin;
import top.mcos.config.ConfigLoader;
import top.mcos.business.firework.config.sub.PlayerFireworkGroupConfig;
import top.mcos.database.dao.PlayerFireworkDao;
import top.mcos.database.domain.PlayerFirework;
import top.mcos.business.firework.FireWorkManage;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class FireSubCommand extends Command implements Helpable {
    @Override
    public @NotNull CommandRunnable onHelp() {
        return (label, sender, args) -> AesopPlugin.logger.log(sender, "&2粒子特效使用指令: &a/"+label + " "+getName());
    }

    @Override
    public @Nullable String[] getAliases() {
        return new String[]{"fire"};
    }

    @Override
    public @NotNull String getName() {
        return "fire";
    }

    @Override
    public int getMinArgsAmount() {
        return 2;
    }

    @Override
    public @Nullable String getPermission() {
        return "aesopplugin.player.fire";
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
                possibleCompletions.add("list"); // 查看我的所有粒子特效组 list
                possibleCompletions.add("toggle"); // 开关粒子组特效 toggle <on|off> <玩家粒子组key>
            }
            if(args.length==3) {
                if("list".equals(args[1])) {

                }
                if("toggle".equals(args[1])) {
                    possibleCompletions.add("on");
                    possibleCompletions.add("off");
                }
            }
            if(args.length==4) {
                // 获取玩家粒子
                Map<String, List<PlayerFirework>> playerFireworkCache = FireWorkManage.getInstance().getPlayerFireworkCache();
                if(sender instanceof Player player) {
                    List<PlayerFirework> playerFireworkList = playerFireworkCache.get(player.getUniqueId().toString());
                    if(playerFireworkList!=null) {
                        for (PlayerFirework playerFirework : playerFireworkList) {
                            possibleCompletions.add(playerFirework.getPlayerFireworkGroupKey());
                        }
                    }
                }
            }
        };
    }

    @Override
    public void run(@NotNull String label, @NotNull CommandSender sender, @NotNull String[] args) {
        if("list".equals(args[1])) {
            if(sender instanceof Player player) {
                PlayerFireworkDao playerFireworkDao = AesopPlugin.getInstance().getDatabase().getPlayerFireworkDao();
                List<PlayerFirework> playerFireworkList = playerFireworkDao.queryGroupKeys(player.getUniqueId().toString(), null);

                List<PlayerFireworkGroupConfig> configs = ConfigLoader.fireworkConfig.getPlayerFireworkGroups();
                Map<String, PlayerFireworkGroupConfig> fireworkGroupKeys = configs.stream().collect(Collectors.toMap(PlayerFireworkGroupConfig::getKey, c -> c));

                AesopPlugin.logger.log(player, "&2 你当前拥有的粒子组列表：");
                AesopPlugin.logger.log(player, "&2 组key | 组名 | 显示状态");
                for (PlayerFirework playerFirework : playerFireworkList) {
                    PlayerFireworkGroupConfig config = fireworkGroupKeys.get(playerFirework.getPlayerFireworkGroupKey());
                    if(config!=null) AesopPlugin.logger.log(player,
                            "&a "+config.getKey()+" | "+config.getName()+" | "+ (playerFirework.getEnable()==1 ? "显示" : "&7隐藏"));
                }
                AesopPlugin.logger.log(player, "&2 ---------------end.");

            } else {
                AesopPlugin.logger.log(sender, "&c请在游戏中使用该指令");
            }
        }else if("toggle".equals(args[1])) {
            if(sender instanceof Player player) {
                if(args.length!=4) {
                    AesopPlugin.logger.log(player, "&c参数数量有误！");
                    return;
                }
                String enableArg = args[2];

                Boolean enable = "on".equalsIgnoreCase(enableArg);

                String groupKey = args[3];

                PlayerFireworkGroupConfig currCconfig = null;

                if (StringUtils.isNotBlank(groupKey)) {
                    List<PlayerFireworkGroupConfig> configs = ConfigLoader.fireworkConfig.getPlayerFireworkGroups();
                    for (PlayerFireworkGroupConfig config : configs) {
                        if (groupKey.equals(config.getKey())) {
                            currCconfig = config;
                        }
                    }
                }
                if (currCconfig == null) {
                    AesopPlugin.logger.log(player, "&c '" + groupKey + "' 粒子组不存在！");
                    return;
                }

                String playerId = player.getUniqueId().toString();
                String groupName = currCconfig.getName();
                PlayerFireworkDao playerFireworkDao = AesopPlugin.getInstance().getDatabase().getPlayerFireworkDao();

                try {
                    AesopPlugin.callInTransaction(() -> {
                        // 先关闭所有
                        playerFireworkDao.updateGroupKey(playerId, false, null);
                        // 针对当前做开启
                        if (enable && playerFireworkDao.updateGroupKey(playerId, enable, groupKey)) {
                                AesopPlugin.logger.log(player, enable ? "&a'" + groupName + "' 粒子特效已开启" : "&e'" + groupName + "' 粒子特效已关闭");
                        }

                        // 更新缓存
                        FireWorkManage.getInstance().putPlayerFireworkToCache(playerId);
                        return null;
                    });
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else {
                AesopPlugin.logger.log(sender, "&c请在游戏中使用该指令");
            }
        } else {
            AesopPlugin.logger.log(sender, "&c参数有误");
        }
    }
}
