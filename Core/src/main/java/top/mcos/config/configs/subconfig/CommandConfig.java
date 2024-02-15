package top.mcos.config.configs.subconfig;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import top.mcos.config.ann.ConfigFileName;
import top.mcos.config.ann.PathKey;
import top.mcos.config.ann.PathValue;
import top.mcos.scheduler.AbstractJob;
import top.mcos.scheduler.JobConfig;
import top.mcos.scheduler.jobs.CommandJob;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 执行控制台指令任务配置
 */
@Setter
@Getter
@ToString
@ConfigFileName("config.yml")
public class CommandConfig implements JobConfig {
    @PathKey
    private String key;
    @PathValue("tasks.command.{key}.enable")
    private boolean enable=false;
    @PathValue("tasks.command.{key}.cron")
    private String cron;
    @PathValue("tasks.command.{key}.start")
    private Date start;
    @PathValue("tasks.command.{key}.end")
    private Date end;
    @PathValue("tasks.command.{key}.commands")
    private List<String> commands = new ArrayList<>();

    @Override
    public String getKeyPrefix() {
        return "command";
    }

    @Override
    public Class<? extends AbstractJob> getJobClass() {
        return CommandJob.class;
    }

    @Override
    public void changeEnable(boolean isEnable) {
        this.enable = isEnable;
    }
}
