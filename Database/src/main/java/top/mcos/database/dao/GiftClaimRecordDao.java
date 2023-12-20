package top.mcos.database.dao;

import com.j256.ormlite.dao.Dao;
import top.mcos.database.domain.GiftClaimRecord;

import java.sql.SQLException;

public interface GiftClaimRecordDao extends Dao<GiftClaimRecord, Long> {
    long countByGift(String playerId) throws SQLException;
    long countBySnowball(String playerId) throws SQLException;
    long countBySnowball(String playerId, String location) throws SQLException;

    GiftClaimRecord saveGift(String playerId, String playerName, String playerIpaddr) throws SQLException;
    GiftClaimRecord saveSnowball(String playerId, String playerName, String playerIpaddr, String location) throws SQLException;
}
