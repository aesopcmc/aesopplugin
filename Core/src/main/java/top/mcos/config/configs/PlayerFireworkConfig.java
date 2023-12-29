package top.mcos.config.configs;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import top.mcos.config.PathEntity;
import top.mcos.config.PathKey;
import top.mcos.config.PathValue;

@Setter
@Getter
@ToString
@PathEntity("player-firework")
public class PlayerFireworkConfig {
    @PathKey
    private String key;
    @PathValue("player-firework.{key}.enable")
    private boolean enable=false;
    @PathValue("player-firework.{key}.text")
    private String text;
    @PathValue("player-firework.{key}.text-size")
    private float textSize;
    /**
     * 枚举值：{@link org.bukkit.Particle}
     */
    @PathValue("player-firework.{key}.particle")
    private String particle;
    @PathValue("player-firework.{key}.period")
    private int period;
    @PathValue("player-firework.{key}.duration")
    private long duration;
    @PathValue("player-firework.{key}.inverted")
    private boolean inverted;
    @PathValue("player-firework.{key}.offset")
    private String offset;
}
