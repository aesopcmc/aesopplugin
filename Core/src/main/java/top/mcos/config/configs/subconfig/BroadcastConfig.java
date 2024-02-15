package top.mcos.config.configs.subconfig;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import top.mcos.config.ann.ConfigFileName;
import top.mcos.config.ann.PathKey;
import top.mcos.config.ann.PathValue;
import top.mcos.scheduler.AbstractJob;
import top.mcos.scheduler.JobConfig;
import top.mcos.scheduler.jobs.BroadcastJob;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@ToString
@ConfigFileName("config.yml")
public class BroadcastConfig implements JobConfig {
    @PathKey
    private String key;

    @PathValue("tasks.broadcast.{key}.enable")
    private boolean enable;

    @PathValue("tasks.broadcast.{key}.cron")
    private String cron;

    @PathValue("tasks.broadcast.{key}.start")
    private Date start;

    @PathValue("tasks.broadcast.{key}.end")
    private Date end;

    @PathValue("tasks.broadcast.{key}.prefix")
    private String prefix;

    @PathValue("tasks.broadcast.{key}.sound")
    private String sound;

    @PathValue("tasks.broadcast.{key}.execute-order")
    private int executeOrder;

    @PathValue("tasks.broadcast.{key}.messages")
    private List<String> messages;

    @Override
    public String getKeyPrefix() {
        return "broadcast";
    }

    @Override
    public Class<? extends AbstractJob> getJobClass() {
        return BroadcastJob.class;
    }

    @Override
    public void changeEnable(boolean isEnable) {
        this.enable = isEnable;
    }
}
