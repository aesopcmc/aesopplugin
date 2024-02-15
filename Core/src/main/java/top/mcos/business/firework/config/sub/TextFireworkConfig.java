package top.mcos.business.firework.config.sub;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import top.mcos.config.ann.ConfigFileName;
import top.mcos.config.ann.PathKey;
import top.mcos.config.ann.PathValue;

@Setter
@Getter
@ToString
@ConfigFileName("firework.yml")
public class TextFireworkConfig {
    @PathKey
    private String key;
    @PathValue("firework.text-firework.{key}.enable")
    private boolean enable=false;
    @PathValue("firework.text-firework.{key}.text")
    private String text;
    @PathValue("firework.text-firework.{key}.text-size")
    private float textSize;
    /**
     * 枚举值：{@link org.bukkit.Particle}
     */
    @PathValue("firework.text-firework.{key}.particle")
    private String particle;
    @PathValue("firework.text-firework.{key}.period")
    private int period;
    @PathValue("firework.text-firework.{key}.duration")
    private long duration;
    @PathValue("firework.text-firework.{key}.inverted")
    private boolean inverted;
    @PathValue("firework.text-firework.{key}.offsetY")
    private double offsetY;
}
