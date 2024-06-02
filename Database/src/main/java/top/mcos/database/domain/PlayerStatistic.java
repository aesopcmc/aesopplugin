package top.mcos.database.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import top.mcos.database.dao.impl.PlayerStatisticDaoImpl;

/**
 * 玩家信息统计
 */
@Setter
@Getter
@ToString
@DatabaseTable(tableName = "py_statistic", daoClass = PlayerStatisticDaoImpl.class)
public class PlayerStatistic {
    /**
     * 主键
     */
    //@DatabaseField(generatedId = true)
    //private Long playerId;

    /**
     * 玩家唯一ID (主键)
     */
    @DatabaseField(id=true, canBeNull = false)
    private String playerId;

    /**
     * 玩家名称
     */
    @DatabaseField(width = 200)
    private String playerName;

    /**
     * 在线时长统计(单位:秒)
     */
    @DatabaseField(canBeNull = false, defaultValue = "0")
    private Long timePlayed;

}
