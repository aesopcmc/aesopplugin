package top.mcos.database.dao;

import com.j256.ormlite.dao.Dao;
import top.mcos.database.domain.GiftClaimRecord;
import top.mcos.database.domain.GiftItem;
import top.mcos.database.enums.GiftTypeEnum;

import java.sql.SQLException;

public interface GiftItemDao extends Dao<GiftItem, Long> {
}
