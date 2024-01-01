package top.mcos.database.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import top.mcos.database.dao.PlayerFireworkDao;
import top.mcos.database.domain.PlayerFirework;

import java.sql.SQLException;
import java.util.List;

public class PlayerFireworkDaoImpl extends BaseDaoImpl<PlayerFirework, Long> implements PlayerFireworkDao {
    public PlayerFireworkDaoImpl(ConnectionSource connectionSource, Class<PlayerFirework> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    @Override
    public List<PlayerFirework> queryGroupKeys(@NotNull String playerId, Boolean enable) {
        try {
            QueryBuilder<PlayerFirework, Long> builder = queryBuilder();
            Where<PlayerFirework, Long> where = builder.where();
            where.eq("playerId", playerId);
            if(enable!=null) {
                where.and().eq("enable", enable ? 1 : 0);
            }
            return builder.query();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean isExist(@NotNull String playerId, @NotNull String playerFireworkGroupKey) {
        try {
            long count = queryBuilder().where().eq("playerId", playerId).and().eq("playerFireworkGroupKey", playerFireworkGroupKey).countOf();
            return count>0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean insertGroupKeys(@NotNull String playerId, @NotNull String playerName, @NotNull String playerFireworkGroupKey) {
        PlayerFirework po = new PlayerFirework();
        po.setPlayerFireworkGroupKey(playerFireworkGroupKey);
        po.setPlayerId(playerId);
        po.setPlayerName(playerName);
        try {
            create(po);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteGroupKey(@NotNull String playerId, String playerFireworkGroupKey) {
        try {
            DeleteBuilder<PlayerFirework, Long> builder = deleteBuilder();
            Where<PlayerFirework, Long> where = builder.where();
            where.eq("playerId", playerId);
            if(StringUtils.isNotBlank(playerFireworkGroupKey)) {
                where.and().eq("playerFireworkGroupKey", playerFireworkGroupKey);
            }
            int delete = builder.delete();
            return delete > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateGroupKey(@NotNull String playerId, @NotNull Boolean enable, String playerFireworkGroupKey) {
        try {
            UpdateBuilder<PlayerFirework, Long> builder = updateBuilder();
            builder.updateColumnValue("enable", enable ? 1 : 0);
            Where<PlayerFirework, Long> where = builder.where();
            where.eq("playerId", playerId);
            if(StringUtils.isNotBlank(playerFireworkGroupKey)) {
                where.and().eq("playerFireworkGroupKey", playerFireworkGroupKey);
            }
            return builder.update() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
