package top.mcos.database.dao;

import com.j256.ormlite.dao.Dao;
import top.mcos.database.domain.GiftClaimRecord;

import java.sql.SQLException;
import java.util.List;

public interface GiftClaimRecordDao extends Dao<GiftClaimRecord, Long> {
    @Deprecated
    long countByGift(String playerId) throws SQLException;
    @Deprecated
    long countBySnowball(String playerId) throws SQLException;
    @Deprecated
    long countBySnowball(String playerId, String location) throws SQLException;

    GiftClaimRecord saveRecord(String playerId, String playerName, String playerIpaddr, String eventKey, String eventName, int giftType) throws SQLException;

    @Deprecated
    GiftClaimRecord saveSnowball(String playerId, String playerName, String playerIpaddr, String location) throws SQLException;

    List<GiftClaimRecord> listByPlayer(String eventKey, String playerName, Boolean ascending) throws SQLException;

    /**
     * 查找玩家指定活动的记录
     * @param eventKey 活动key
     * @param playerId
     * @return
     * @throws SQLException
     */
    GiftClaimRecord queryByPlayer(String eventKey, String playerId) throws SQLException;
    GiftClaimRecord queryByPlayerName(String eventKey, String playerName) throws SQLException;

    /**
     * 查找玩家所有活动的记录
     * @param playerName 玩家名称
     * @return
     * @throws SQLException
     */
    List<GiftClaimRecord> queryByPlayerName(String playerName) throws SQLException;
}
