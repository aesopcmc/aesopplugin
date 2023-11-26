package top.mcos.config.configs;

import lombok.Getter;
import lombok.Setter;
import top.mcos.message.PositionTypeEnum;

import java.util.Date;

@Setter
@Getter
public class NoticeMessageConfig {
    private String key;
    private Boolean enable;
    private String cron;
    private Date start;
    private Date end;
    private PositionTypeEnum positionType;
    private String message;
    private String subMessage;
}
