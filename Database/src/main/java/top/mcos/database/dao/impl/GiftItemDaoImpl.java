package top.mcos.database.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import top.mcos.database.dao.GiftItemDao;
import top.mcos.database.domain.GiftItem;

import java.sql.SQLException;

public class GiftItemDaoImpl extends BaseDaoImpl<GiftItem, Long> implements GiftItemDao {
    public GiftItemDaoImpl(ConnectionSource connectionSource, Class<GiftItem> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }
}
