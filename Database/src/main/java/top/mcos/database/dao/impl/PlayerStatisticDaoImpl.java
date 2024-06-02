package top.mcos.database.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import top.mcos.database.dao.PlayerStatisticDao;
import top.mcos.database.domain.PlayerStatistic;

import java.sql.SQLException;
public class PlayerStatisticDaoImpl extends BaseDaoImpl<PlayerStatistic, Long> implements PlayerStatisticDao {
    public PlayerStatisticDaoImpl(ConnectionSource connectionSource, Class<PlayerStatistic> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

}
