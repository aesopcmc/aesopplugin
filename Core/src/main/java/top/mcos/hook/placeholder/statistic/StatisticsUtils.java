package top.mcos.hook.placeholder.statistic;

import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;

import java.util.StringJoiner;

public class StatisticsUtils {
    public static int getSecondsPlayed(final OfflinePlayer player) {
        return player.getPlayer().getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;
    }
    /**
     * @author Sxtanna
     */
    public static String formatTime(final long time) {
        if (time < 1) {
            return "";
        }

        if (time < 60) {
            return time + "s";
        }

        long seconds = time;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        //long weeks = days / 7;

        seconds %= 60;
        minutes %= 60;
        hours %= 24;
        //days %= 7;

        final StringJoiner joiner = new StringJoiner(" ");
        //appendTime(joiner, weeks, "周");
        appendTime(joiner, days, "天");
        appendTime(joiner, hours, "时");
        appendTime(joiner, minutes, "分");
        appendTime(joiner, seconds, "秒");
        return joiner.toString();
    }

    private static void appendTime(final StringJoiner joiner, final long value, final String unit) {
        if (value > 0) {
            joiner.add(value + unit);
        }
    }
}
