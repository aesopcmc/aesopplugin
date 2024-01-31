package top.mcos.database.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;
import top.mcos.database.dao.impl.GiftItemDaoImpl;

/**
 * 礼物领取明细
 */
@Setter
@Getter
@DatabaseTable(tableName = "act_gif_item", daoClass = GiftItemDaoImpl.class)
public class GiftItem {
    /**
     * 主键
     */
    @DatabaseField(generatedId = true)
    private Long id;
    /**
     * 礼物名称
     */
    @DatabaseField
    private String giftName;
    /**
     * 礼物key或条件物品key
     */
    @DatabaseField(canBeNull = false)
    private String itemKey;
    /**
     * 物品类型 1礼物 2条件物品
     */
    @DatabaseField(canBeNull = false, defaultValue = "1")
    private Integer itemType;
    /**
     * 领取数量
     */
    @DatabaseField(canBeNull = false, defaultValue = "1")
    private Integer amount;
    /**
     * 获得概率，单位百分比
     */
    @DatabaseField
    private Integer percent;
    /**
     * 领取记录id
     */
    @DatabaseField(canBeNull = false, index = true)
    private Long recordId;
}
