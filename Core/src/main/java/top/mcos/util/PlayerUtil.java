package top.mcos.util;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PlayerUtil {

    /**
     * 获取所有在线玩家的名字
     * @return 在线玩家名称集合
     */
    public static List<String> getAllOnlinePlayerName() {
        List<String> players = new ArrayList<>();
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player onlinePlayer : onlinePlayers) {
            players.add(onlinePlayer.getName());
        }
        return players;
    }

    /**
     * 获取所有在线玩家的ID
     * @return 在线玩家ID集合
     */
    public static List<String> getAllOnlinePlayerId() {
        List<String> players = new ArrayList<>();
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player onlinePlayer : onlinePlayers) {
            players.add(onlinePlayer.getUniqueId().toString());
        }
        return players;
    }

    /**
     * 获取所有在线玩家的ID
     * @return 在线玩家ID集合
     */
    public static String getOnlinePlayerId(String playerName) {
        if(StringUtils.isBlank(playerName)) return null;
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player onlinePlayer : onlinePlayers) {
            if(playerName.equals(onlinePlayer.getName())) {
                return onlinePlayer.getUniqueId().toString();
            }
        }
        return null;
    }

    /**
     * 获取所有在线玩家的ID
     * @return 在线玩家ID集合
     */
    public static @Nullable Player getOnlinePlayer(String playerName) {
        if(StringUtils.isBlank(playerName)) return null;
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player onlinePlayer : onlinePlayers) {
            if(playerName.equals(onlinePlayer.getName())) {
                return onlinePlayer;
            }
        }
        return null;
    }


}
