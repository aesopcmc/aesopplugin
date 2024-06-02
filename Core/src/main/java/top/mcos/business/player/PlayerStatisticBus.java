package top.mcos.business.player;

import com.j256.ormlite.stmt.QueryBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import top.mcos.AesopPlugin;
import top.mcos.business.Bus;
import top.mcos.config.ConfigLoader;
import top.mcos.config.configs.subconfig.SettingConfig;
import top.mcos.database.dao.PlayerStatisticDao;
import top.mcos.database.domain.PlayerStatistic;
import top.mcos.hook.placeholder.statistic.StatisticsUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 玩家信息统计业务
 */
public class PlayerStatisticBus implements Bus {
    /**
     * 玩家在线时长统计
     */
    public static Map<String, String> playedTimeList = new HashMap<>();
    private PlayerStatisticDao playerStatisticDao;
    private BukkitTask periodRunTask;

    private boolean firstCache = true;

    @Override
    public boolean load() {
        playerStatisticDao = AesopPlugin.getInstance().getDatabase().getPlayerStatisticDao();

        // 缓存玩家在线时长统计
        SettingConfig settingConfig = ConfigLoader.baseConfig.getSettingConfig();
        boolean enable = settingConfig.isTimeplayedStatisticsEnable();
        int cachePeriod = settingConfig.getTimeplayedStatisticsCachePeriod();
        if(enable) {
            this.periodRunTask = Bukkit.getScheduler().runTaskTimer(AesopPlugin.getInstance(), () -> {
                cacheTimePlayedStatistic();
            }, 110, (long) cachePeriod *60*20);
        }
        // ...

        return true;
    }

    @Override
    public boolean unload() {
        if(this.periodRunTask!=null) this.periodRunTask.cancel();
        playedTimeList.clear();
        return true;
    }

    @Override
    public boolean reload() {
        unload();
        return load();
    }

    /**
     * 缓存玩家在线时长统计
     * @return true 缓存成功，false 缓存失败
     */
    private boolean cacheTimePlayedStatistic() {
        playedTimeList.clear();
        SettingConfig settingConfig = ConfigLoader.baseConfig.getSettingConfig();
        boolean enable = settingConfig.isTimeplayedStatisticsEnable();
        if(!enable) return false;
        int topCount = settingConfig.getTimeplayedStatisticsTopCount();
        if(insertTimePlayed()) {
            List<PlayerStatistic> topList = getTimePlayedList(topCount);
            if(topList!=null) {
                for (int i=0;i<topList.size();i++) {
                    PlayerStatistic playerStatistic = topList.get(i);
                    playedTimeList.put("time_played_top_"+(i+1), StatisticsUtils.formatTime(playerStatistic.getTimePlayed()));
                    playedTimeList.put("time_played_name_top_"+(i+1), playerStatistic.getPlayerName());
                }
            }
        }
        if(ConfigLoader.baseConfig.getSettingConfig().isDebug()) {
            AesopPlugin.logger.log("更新玩家时长完毕。");
        }
        return true;
    }

    /**
     * 查询数据库玩家时长统计
     * @param topCount
     */
    private List<PlayerStatistic> getTimePlayedList(int topCount) {
        try {
            QueryBuilder<PlayerStatistic, Long> queryBuilder = playerStatisticDao.queryBuilder()
                    .orderBy("timePlayed", false).limit((long) topCount);
            return queryBuilder.query();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 更新玩家在线时长统计，若玩家数据不存在则创建
     * @return true更新成功，false更新失败
     */
    private boolean insertTimePlayed() {
        if (playerStatisticDao != null) {
            if(firstCache) {
                // 获取所有离线玩家(包含在线玩家）
                OfflinePlayer[] offlinePlayers = Bukkit.getServer().getOfflinePlayers();
                // 第一次加载，重新更新一次所有玩家的数据
                for (OfflinePlayer offlinePlayer : offlinePlayers) {
                    // 获取玩家的游玩时长，单位：秒
                    int secondPlayed = offlinePlayer.getStatistic(Statistic.PLAY_ONE_MINUTE)/20;
                    PlayerStatistic ps = new PlayerStatistic();
                    ps.setPlayerId(offlinePlayer.getUniqueId().toString());
                    ps.setPlayerName(offlinePlayer.getName());
                    ps.setTimePlayed((long) secondPlayed);
                    try {
                        playerStatisticDao.createOrUpdate(ps);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                firstCache = false;
            } else {
                // 服务器启动后，就只需要更新在线玩家的数据即可
                Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
                for (Player onlinePlayer : onlinePlayers) {
                    // 获取玩家的游玩时长，单位：秒
                    int secondPlayed = onlinePlayer.getStatistic(Statistic.PLAY_ONE_MINUTE)/20;
                    PlayerStatistic ps = new PlayerStatistic();
                    ps.setPlayerId(onlinePlayer.getUniqueId().toString());
                    ps.setPlayerName(onlinePlayer.getName());
                    ps.setTimePlayed((long) secondPlayed);
                    try {
                        playerStatisticDao.createOrUpdate(ps);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }

            }

            return true;
        }
        return false;
    }
}
