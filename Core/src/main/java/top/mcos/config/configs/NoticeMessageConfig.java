package top.mcos.config.configs;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import top.mcos.config.PathEntity;
import top.mcos.config.PathKey;
import top.mcos.config.PathValue;
import top.mcos.message.PositionTypeEnum;

import java.util.Date;

@Setter
@Getter
@ToString
@PathEntity("tasks.notice")
public class NoticeMessageConfig {
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
}
