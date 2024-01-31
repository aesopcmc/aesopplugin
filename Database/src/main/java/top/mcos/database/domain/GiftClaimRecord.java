package top.mcos.database.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import top.mcos.database.dao.impl.GiftClaimRecordDaoImpl;
import top.mcos.database.enums.GiftTypeEnum;

/**
 * 活动物品领取记录
 * 唯一：活动key+玩家ID
 */
@Setter
@Getter
@ToString
@DatabaseTable(tableName = "act_gif_claim_record", daoClass = GiftClaimRecordDaoImpl.class)
public class GiftClaimRecord {
    /**
     * 主键
     */
    @DatabaseField(generatedId = true)
    private Long id;

    /**
     * 活动名称
     */
    @DatabaseField
    private String eventName;

    /**
     * 活动key
     */
    @DatabaseField(canBeNull = false)
    private String eventKey;

    ///**
    // * 礼物位置
    // */
    //@DatabaseField
    //private String location;

    /**
     * 礼物类型
     * 枚举值：{@link GiftTypeEnum}
     * 1圣诞节礼物 2春节礼物
     */
    @DatabaseField(canBeNull = false, defaultValue = "1")
    private Integer giftType;

    /**
     * 玩家唯一ID
     */
    @DatabaseField(canBeNull = false, width = 100, index = true)
    private String playerId;
    /**
     * 玩家名称
     */
    @DatabaseField(width = 200)
    private String playerName;

    /**
     * 领取年份
     */
    @DatabaseField
    private String year;

    /**
     * 领取时间
     */
    @DatabaseField(width = 20)
    private String createTime;

    /**
     * 记录玩家ip地址
     */
    @DatabaseField(width = 100)
    private String ipaddress;

    /**
     * 是否领取：0未领取，1已领取
     */
    @DatabaseField(canBeNull = false, defaultValue = "0")
    private Integer claimed;
}
