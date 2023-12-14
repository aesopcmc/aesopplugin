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
    @PathValue("tasks.firework.{key}.cron")
    private String cron;
    @PathValue("tasks.firework.{key}.start")
    private Date start;
    @PathValue("tasks.firework.{key}.end")
    private Date end;
    /**
     * 枚举值：{@link org.bukkit.Particle}
     */
    @PathValue("tasks.firework.{key}.particle")
    private String particle;
    @PathValue("tasks.firework.{key}.text")
    private String text;
    @PathValue("tasks.firework.{key}.text-size")
    private float textSize;
    @PathValue("tasks.firework.{key}.text-location")
    private String textLocation;
    @PathValue("tasks.firework.{key}.subtext")
    private String subtext;
    @PathValue("tasks.firework.{key}.subtext-size")
    private float subtextSize;
    @PathValue("tasks.firework.{key}.subtext-location")
    private String subtextLocation;

}
