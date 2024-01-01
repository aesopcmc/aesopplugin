package top.mcos.config.configs.subconfig;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import top.mcos.config.ann.ConfigFileName;
import top.mcos.config.ann.PathKey;
import top.mcos.config.ann.PathValue;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
@ConfigFileName("config.yml")
public class RegenWorldConfig {
    @PathKey
    private String key;
    @PathValue("tasks.regen-world.{key}.enable")
    private boolean enable=false;
    @PathValue("tasks.regen-world.{key}.cron")
    private String cron;
    @PathValue("tasks.regen-world.{key}.new-seed")
    private boolean newSeed;
    @PathValue("tasks.regen-world.{key}.random-seed")
    private boolean randomSeed;
    @PathValue("tasks.regen-world.{key}.seed")
    private String seed;
    @PathValue("tasks.regen-world.{key}.keep-game-rules")
    private boolean keepGameRules;
    @PathValue("tasks.regen-world.{key}.loaded-chunky")
    private boolean loadedChunky;
    @PathValue("tasks.regen-world.{key}.loaded-chunky-radius")
    private double loadedChunkyRadius = 500d;
    @PathValue("tasks.regen-world.{key}.loaded-notice-key")
    private String loadedNoticeKey;
    @PathValue("tasks.regen-world.{key}.loaded-notice-message")
    private String loadedNoticeMessage;
    @PathValue("tasks.regen-world.{key}.loaded-notice-delay-hours")
    private int loadedNoticeDelayHours;
    @PathValue("tasks.regen-world.{key}.loaded-notice-keep-hours")
    private int loadedNoticeKeepHours;
    @PathValue("tasks.regen-world.{key}.loaded-run-commands")
    private List<String> loadedRunCommands = new ArrayList<>();
}
