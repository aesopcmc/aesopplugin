package top.mcos.database.dao;

import com.j256.ormlite.dao.Dao;
import top.mcos.database.domain.GiftClaimRecord;
import top.mcos.database.enums.GiftTypeEnum;

import java.sql.SQLException;
import java.util.List;

public interface GiftClaimRecordDao extends Dao<GiftClaimRecord, Long> {
    long countByGift(String playerId) throws SQLException;
    long countBySnowball(String playerId) throws SQLException;
    long countBySnowball(String playerId, String location) throws SQLException;

    GiftClaimRecord saveGift(String playerId, String playerName, String playerIpaddr) throws SQLException;
    GiftClaimRecord saveSnowball(String playerId, String playerName, String playerIpaddr, String location) throws SQLException;

    List<GiftClaimRecord> list(GiftTypeEnum giftTypeEnum, String playerName, String orderByColumn, Boolean ascending) throws SQLException;
}
