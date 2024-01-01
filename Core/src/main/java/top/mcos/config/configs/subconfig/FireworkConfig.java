package top.mcos.config.configs.subconfig;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import top.mcos.config.ann.ConfigFileName;
import top.mcos.config.ann.PathKey;
import top.mcos.config.ann.PathValue;

@Setter
@Getter
@ToString
@ConfigFileName("config.yml")
public class FireworkConfig {
    @PathKey
    private String key;
    @PathValue("tasks.firework.{key}.enable")
    private boolean enable=false;
    @PathValue("tasks.firework.{key}.text")
    private String text;
    @PathValue("tasks.firework.{key}.text-size")
    private float textSize;
    /**
     * 枚举值：{@link org.bukkit.Particle}
     */
    @PathValue("tasks.firework.{key}.particle")
    private String particle;
    @PathValue("tasks.firework.{key}.period")
    private int period;
    @PathValue("tasks.firework.{key}.duration")
    private int duration;
    @PathValue("tasks.firework.{key}.text-location")
    private String textLocation;
}
