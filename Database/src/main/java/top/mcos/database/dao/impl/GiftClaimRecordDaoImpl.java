package top.mcos.database.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import org.apache.commons.lang3.StringUtils;
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

    @Override
    public long countByGift(String playerId) throws SQLException {
        return queryBuilder().where().eq("playerId", playerId)
                .and().eq("giftType", GiftTypeEnum.CHRISTMAS_GIFT.getIndex()).countOf();
    }

    @Override
    public long countBySnowball(String playerId, String location) throws SQLException {
        return queryBuilder().where().eq("playerId", playerId)
                .and().eq("location", location)
                .and().eq("giftType", GiftTypeEnum.SNOWBALL_ITEM.getIndex()).countOf();
    }

    @Override
    public long countBySnowball(String playerId) throws SQLException {
        return queryBuilder().where().eq("playerId", playerId)
                .and().eq("giftType", GiftTypeEnum.SNOWBALL_ITEM.getIndex()).countOf();
    }

    @Override
    public GiftClaimRecord saveGift(String playerId, String playerName, String playerIpaddr) throws SQLException {
        GiftClaimRecord record = new GiftClaimRecord();
        record.setGiftName("圣诞礼物");
        record.setPlayerId(playerId);
        record.setPlayerName(playerName);
        record.setGiftType(GiftTypeEnum.CHRISTMAS_GIFT.getIndex());
        record.setYear(DateFormatUtils.format(new Date(), "yyyy"));
        record.setCreateTime(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        record.setIpaddress(playerIpaddr);
        create(record);
        return record;
    }

    @Override
    public GiftClaimRecord saveSnowball(String playerId, String playerName, String playerIpaddr, String location) throws SQLException {
        GiftClaimRecord record = new GiftClaimRecord();
        record.setGiftName("圣诞礼物");
        record.setLocation(location);
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
    public List<GiftClaimRecord> list(GiftTypeEnum giftTypeEnum, String playerName, String orderByColumn, Boolean ascending) throws SQLException {
        //QueryBuilder<GiftClaimRecord, Long> builder = queryBuilder();
        //if(StringUtils.isNotBlank(orderByColumn)) {
        //    builder.orderBy(orderByColumn, ascending == null || ascending);
        //}
        Where<GiftClaimRecord, Long> where = queryBuilder().where();
        where.raw("1=1");
        if (giftTypeEnum!=null) {
            where.and().eq("giftType", giftTypeEnum.getIndex());
        }
        if(playerName!=null) {
            where.and().eq("playerName", playerName);
        }
        if(StringUtils.isNotBlank(orderByColumn)) {
            where.queryBuilder().orderBy(orderByColumn, ascending == null || ascending);
        }
        return where.query();
    }

}