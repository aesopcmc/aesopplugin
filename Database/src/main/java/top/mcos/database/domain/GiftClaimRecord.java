package top.mcos.database.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import top.mcos.database.dao.impl.GiftClaimRecordDaoImpl;

/**
 * 礼物领取记录
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
     * 礼物名称
     */
    @DatabaseField
    private String giftName;

    /**
     * 礼物位置
     */
    @DatabaseField
    private String location;

    /**
     * 礼物类型
     * 1圣诞节礼物 2春节礼物
     * 11雪球 22xxx 33xx
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
    @DatabaseField(canBeNull = false)
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
}
