package top.mcos.business.regen.config.sub;

import lombok.Getter;
import lombok.Setter;
import top.mcos.config.ann.ConfigFileName;
import top.mcos.config.ann.PathKey;
import top.mcos.config.ann.PathValue;
import top.mcos.scheduler.AbstractJob;
import top.mcos.scheduler.JobConfig;
import top.mcos.scheduler.jobs.RgWorldJob;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@ConfigFileName("regenworld.yml")
public class RgWorldConfig implements JobConfig {
    @PathKey
    private String key;
    @PathValue("worlds.{key}.enable")
    private boolean enable;

    @PathValue("worlds.{key}.alias-name")
    private String aliasName;

    @PathValue("worlds.{key}.delete-cron")
    private String cron;

    @PathValue("worlds.{key}.create-at")
    private String createAt;

    @PathValue("worlds.{key}.difficulty")
    private String difficulty;

    // 这两暂时没用
    private Date start;
    private Date end;

    @PathValue("worlds.{key}.delete-region")
    private boolean deleteRegion;

    @PathValue("worlds.{key}.gamerules")
    private List<String> gamerules;

    @PathValue("worlds.{key}.created-commands")
    private List<String> createdCommands;

    @PathValue("worlds.{key}.chunky-load-radius")
    private double chunkyLoadRadius;

    @Override
    public String getKeyPrefix() {
        return "regenworld";
    }

    @Override
    public Class<? extends AbstractJob> getJobClass() {
        return RgWorldJob.class;
    }

    @Override
    public void changeEnable(boolean isEnable) {
        this.enable = isEnable;
    }
}
