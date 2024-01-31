package top.mcos.database.dao;

import com.j256.ormlite.dao.Dao;
import org.jetbrains.annotations.NotNull;
import top.mcos.database.domain.GiftItem;
import top.mcos.database.enums.GiftTypeEnum;

import java.sql.SQLException;
import java.util.List;

public interface GiftItemDao extends Dao<GiftItem, Long> {
    //List<GiftItem> list(GiftTypeEnum giftType, Long recordId, String orderByColumn, Boolean ascending) throws SQLException;
    /**
     *
     * @param recordId 记录id
     * @param itemType 物品类型 1礼物 2条件物品
     * @param itemKey 物品key
     * @return
     * @throws SQLException
     */
    long countByItemKey(@NotNull Long recordId, @NotNull Integer itemType, @NotNull String itemKey) throws SQLException;
    /**
     * 查询礼物领取明细
     * @param recordIds 活动id集合
     * @param itemType 物品类型 1礼物 2条件物品
     * @param ascending true升序 false|null 降序
     * @return 礼物领取明细集合
     * @throws SQLException
     */
    List<GiftItem> queryByRecordIds(@NotNull List<Long> recordIds, Integer itemType, Boolean ascending) throws SQLException;
}
