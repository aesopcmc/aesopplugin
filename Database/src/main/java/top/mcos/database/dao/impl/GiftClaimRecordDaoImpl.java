package top.mcos.database.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import org.apache.commons.lang3.time.DateFormatUtils;
import top.mcos.database.dao.GiftClaimRecordDao;
import top.mcos.database.domain.GiftClaimRecord;
import top.mcos.database.enums.GiftTypeEnum;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class GiftClaimRecordDaoImpl extends BaseDaoImpl<GiftClaimRecord, Long> implements GiftClaimRecordDao {
    public GiftClaimRecordDaoImpl(ConnectionSource connectionSource, Class<GiftClaimRecord> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    @Deprecated
    @Override
    public long countByGift(String playerId) throws SQLException {
        return queryBuilder().where().eq("playerId", playerId)
                .and().eq("giftType", GiftTypeEnum.CHRISTMAS_GIFT.getIndex()).countOf();
    }

    @Deprecated
    @Override
    public long countBySnowball(String playerId, String location) throws SQLException {
        return queryBuilder().where().eq("playerId", playerId)
                .and().eq("location", location)
                .and().eq("giftType", GiftTypeEnum.SNOWBALL_ITEM.getIndex()).countOf();
    }

    @Deprecated
    @Override
    public long countBySnowball(String playerId) throws SQLException {
        return queryBuilder().where().eq("playerId", playerId)
                .and().eq("giftType", GiftTypeEnum.SNOWBALL_ITEM.getIndex()).countOf();
    }

    @Override
    public GiftClaimRecord saveRecord(String playerId, String playerName, String playerIpaddr, String eventKey, String eventName, int giftType) throws SQLException {
        GiftClaimRecord record = new GiftClaimRecord();
        record.setEventKey(eventKey);
        record.setEventName(eventName);
        record.setPlayerId(playerId);
        record.setPlayerName(playerName);
        record.setGiftType(giftType);
        record.setYear(DateFormatUtils.format(new Date(), "yyyy"));
        record.setCreateTime(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        record.setIpaddress(playerIpaddr);
        record.setClaimed(0);// 默认未领取
        create(record);
        return record;
    }

    @Deprecated
    @Override
    public GiftClaimRecord saveSnowball(String playerId, String playerName, String playerIpaddr, String location) throws SQLException {
        GiftClaimRecord record = new GiftClaimRecord();
        record.setEventName("圣诞雪球");
        //record.setLocation(location);
        record.setPlayerId(playerId);
        record.setPlayerName(playerName);
        record.setGiftType(GiftTypeEnum.SNOWBALL_ITEM.getIndex());
        record.setYear(DateFormatUtils.format(new Date(), "yyyy"));
        record.setCreateTime(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        record.setIpaddress(playerIpaddr);
        create(record);
        return record;
    }

    @Override
    public List<GiftClaimRecord> listByPlayer(String eventKey, String playerName, Boolean ascending) throws SQLException {
        //QueryBuilder<GiftClaimRecord, Long> builder = queryBuilder();
        //if(StringUtils.isNotBlank(orderByColumn)) {
        //    builder.orderBy(orderByColumn, ascending == null || ascending);
        //}
        Where<GiftClaimRecord, Long> where = queryBuilder().where();
        where.raw("1=1");
        if(playerName!=null) {
            where.and().eq("playerName", playerName);
        }
        if(eventKey!=null) {
            where.and().eq("eventKey", eventKey);
        }
        where.queryBuilder().orderBy("createTime", ascending == null || ascending);
        return where.query();
    }

    @Override
    public GiftClaimRecord queryByPlayer(String eventKey, String playerId) throws SQLException {
        return queryBuilder().where().eq("eventKey", eventKey).and().eq("playerId", playerId).queryForFirst();
    }

    @Override
    public GiftClaimRecord queryByPlayerName(String eventKey, String playerName) throws SQLException {
        return queryBuilder().where().eq("eventKey", eventKey).and().eq("playerName", playerName).queryForFirst();
    }

    @Override
    public List<GiftClaimRecord> queryByPlayerName(String playerName) throws SQLException {
        return queryBuilder().where().eq("playerName", playerName).query();
    }

}
