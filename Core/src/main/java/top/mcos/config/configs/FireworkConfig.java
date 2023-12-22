package top.mcos.config.configs;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import top.mcos.config.PathEntity;
import top.mcos.config.PathKey;
import top.mcos.config.PathValue;
import top.mcos.message.PositionTypeEnum;

import java.awt.*;
import java.util.Date;

@Setter
@Getter
@ToString
@PathEntity("tasks.firework")
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
