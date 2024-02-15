package top.mcos.config.configs.subconfig;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import top.mcos.config.ann.ConfigFileName;
import top.mcos.config.ann.PathKey;
import top.mcos.config.ann.PathValue;
import top.mcos.message.PositionTypeEnum;
import top.mcos.scheduler.AbstractJob;
import top.mcos.scheduler.JobConfig;
import top.mcos.scheduler.jobs.NoticeJob;

import java.util.Date;

@Setter
@Getter
@ToString
@ConfigFileName("config.yml")
public class NoticeConfig implements JobConfig {
    @PathKey
    private String key;
    @PathValue("tasks.notice.{key}.enable")
    private boolean enable=false;
    @PathValue("tasks.notice.{key}.cron")
    private String cron;
    @PathValue("tasks.notice.{key}.start")
    private Date start;
    @PathValue("tasks.notice.{key}.end")
    private Date end;
    /**
     * 枚举值：{@link PositionTypeEnum}
     */
    @PathValue("tasks.notice.{key}.position")
    private String positionType;
    @PathValue("tasks.notice.{key}.message")
    private String message;
    @PathValue("tasks.notice.{key}.sub-message")
    private String subMessage;

    @Override
    public String getKeyPrefix() {
        return "notice";
    }

    @Override
    public Class<? extends AbstractJob> getJobClass() {
        return NoticeJob.class;
    }

    @Override
    public void changeEnable(boolean isEnable) {
        this.enable = isEnable;
    }
}
