package top.mcos.database.dao;

import com.j256.ormlite.dao.Dao;
import top.mcos.database.domain.GiftItem;
import top.mcos.database.enums.GiftTypeEnum;

import java.sql.SQLException;
import java.util.List;

public interface GiftItemDao extends Dao<GiftItem, Long> {
    List<GiftItem> list(GiftTypeEnum giftType, Long recordId, String orderByColumn, Boolean ascending) throws SQLException;
}
