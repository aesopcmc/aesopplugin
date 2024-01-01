package top.mcos.database.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;
import top.mcos.database.dao.impl.PlayerFireworkDaoImpl;

@Setter
@Getter
@DatabaseTable(tableName = "fw_player_firework", daoClass = PlayerFireworkDaoImpl.class)
public class PlayerFirework {
    /**
     * 主键
     */
    @DatabaseField(generatedId = true)
    private Long id;

    /**
     * 玩家唯一ID
     */
    @DatabaseField(canBeNull = false, index = true)
    private String playerId;

    /**
     * 玩家名称
     */
    @DatabaseField
    private String playerName;

    /**
     * 粒子特效
     */
    @DatabaseField(canBeNull = false, index = true)
    private String playerFireworkGroupKey;

    /**
     * 是否显示 0显示 1关闭
     */
    @DatabaseField(canBeNull = false, defaultValue = "0")
    private Integer enable;


}
