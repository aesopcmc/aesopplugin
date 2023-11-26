package top.mcos.config.configs;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class RegenWorldConfig {
    private String world;
    private Boolean enable;
    private String cron;
    private Boolean newSeed;
    private Boolean randomSeed;
    private String seed;
    private Boolean keepGameRules;
    private Boolean afterLoadChunky;
    private Double afterLoadChunkyRadius;
    private String afterNoticeKey;
    private List<String> afterRunCommands;
}
