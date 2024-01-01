package top.mcos.database.dao;

import com.j256.ormlite.dao.Dao;
import org.jetbrains.annotations.NotNull;
import top.mcos.database.domain.PlayerFirework;

import java.util.List;

public interface PlayerFireworkDao extends Dao<PlayerFirework, Long> {
    List<PlayerFirework> queryGroupKeys(@NotNull String playerId, Boolean enable);

    boolean isExist(@NotNull String playerId, @NotNull String playerFireworkGroupKey);
    boolean insertGroupKeys(@NotNull String playerId, @NotNull String playerName, @NotNull String playerFireworkGroupKey);
    boolean deleteGroupKey(@NotNull String playerId, String playerFireworkGroupKey);

    boolean updateGroupKey(@NotNull String playerId, @NotNull Boolean enable, String playerFireworkGroupKey);

}
