package top.mcos.database.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import org.apache.commons.lang3.StringUtils;
import top.mcos.database.dao.GiftItemDao;
import top.mcos.database.domain.GiftItem;
import top.mcos.database.enums.GiftTypeEnum;

import java.sql.SQLException;
import java.util.List;

public class GiftItemDaoImpl extends BaseDaoImpl<GiftItem, Long> implements GiftItemDao {
    public GiftItemDaoImpl(ConnectionSource connectionSource, Class<GiftItem> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    @Override
    public List<GiftItem> list(GiftTypeEnum giftType, Long recordId, String orderByColumn, Boolean ascending) throws SQLException {
        Where<GiftItem, Long> where = queryBuilder().where();
        where.raw("1=1");
        if(giftType!=null) {
            where.and().eq("giftType", giftType.getIndex());
        }
        if(recordId!=null) {
            where.and().eq("recordId", recordId);
        }
        if(StringUtils.isNotBlank(orderByColumn)) {
            where.queryBuilder().orderBy(orderByColumn, ascending == null || ascending);
        }
        return where.query();
    }
}
