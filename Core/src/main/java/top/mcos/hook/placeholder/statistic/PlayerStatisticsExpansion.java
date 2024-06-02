package top.mcos.hook.placeholder.statistic;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import top.mcos.business.player.PlayerStatisticBus;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * papi集成参考 https://github.com/PlaceholderAPI/PlaceholderAPI/wiki/PlaceholderExpansion#with-a-plugin-internal-class
 * Expansion扩展参考：https://github.com/PlaceholderAPI/PlaceholderAPI/blob/master/src/main/java/me/clip/placeholderapi/util/TimeUtil.java
 */

/**
 * 玩家信息统计papi变量扩展
 */
public class PlayerStatisticsExpansion extends PlaceholderExpansion{
    /**
     * 定义变量唯一标识符。
     * 使用变量以 %aep_ 作为开头
     */
    @Override
    public @NotNull String getIdentifier() {
        return "aep";
    }
    /**
     * 版本信息，一搬用在ecloud的检测更新中
     */
    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public @NotNull String getAuthor() {
        return "aesop";
    }
    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    /**
     * 当使用时触发
     * 当使用在全息显示时，一般一秒触发一次，部分插件可以配置刷新周期。TODO 所以此处代码需要充分考虑性能问题。
     * @param player 输入玩家，可能为null
     * @param params 变量。是除掉前缀"%identifier_"后的剩余部分
     * @return 展示值。如果返回null，将展示为变量
     */
    @Override
    public String onRequest(OfflinePlayer player, String params) {
        // 在线时长统计排行
        // 格式：%aep_time_played_top_1% （时长排行）
        // 格式：%aep_time_played_name_top_1% （时长玩家排行）
        if (params.startsWith("time_played_top_") || params.startsWith("time_played_name_top_")) {
            return PlayerStatisticBus.playedTimeList.get(params);
        }

        // 在线时长统计
        // 格式：%aep_time_played%
        // 格式：%aep_time_played:seconds%
        switch (params.toLowerCase()) {
            /*
             * Time played
             */
            case "time_played": {
                return StatisticsUtils.formatTime(StatisticsUtils.getSecondsPlayed(player));
            }

            case "time_played:seconds": {
                return Integer.toString(StatisticsUtils.getSecondsPlayed(player) % 60);
            }

            case "time_played:minutes": {
                return Long.toString(TimeUnit.SECONDS.toMinutes(StatisticsUtils.getSecondsPlayed(player)) % 60);
            }

            case "time_played:hours": {
                return Long.toString(TimeUnit.SECONDS.toHours(StatisticsUtils.getSecondsPlayed(player)) % 24);
            }

            case "seconds_played": {
                return Integer.toString(StatisticsUtils.getSecondsPlayed(player));
            }

            case "minutes_played": {
                return Long.toString(TimeUnit.SECONDS.toMinutes(StatisticsUtils.getSecondsPlayed(player)));
            }

            case "hours_played": {
                return Long.toString(TimeUnit.SECONDS.toHours(StatisticsUtils.getSecondsPlayed(player)));
            }

            case "time_played:days":
            case "days_played": {
                return Long.toString(TimeUnit.SECONDS.toDays(StatisticsUtils.getSecondsPlayed(player)));
            }
        }
        return "未知变量"; // Placeholder is unknown by the Expansion
    }
}
