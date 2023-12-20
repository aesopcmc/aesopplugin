package top.mcos.database.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;
import top.mcos.database.dao.impl.GiftItemDaoImpl;
import top.mcos.database.enums.GiftTypeEnum;

/**
 * 礼物
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
     * 礼物名称key
     */
    @DatabaseField(canBeNull = false)
    private String itemKey;
    /**
     * 礼物类型，枚举值：{@link GiftTypeEnum}
     */
    @DatabaseField(canBeNull = false, defaultValue = "1")
    private Integer giftType;
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
