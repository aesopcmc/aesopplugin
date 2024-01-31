package top.mcos.business.activity.config.sub;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import top.mcos.config.ann.ConfigFileName;
import top.mcos.config.ann.PathKey;
import top.mcos.config.ann.PathValue;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@ToString
@ConfigFileName("activity.yml")
public class AEventConfig {
    @PathKey
    private String key;

    @PathValue("events.{key}.enable")
    private boolean enable;

    @PathValue("events.{key}.event-name")
    private String eventName;

    @PathValue("events.{key}.gift-keys")
    private List<String> giftKeys;

    @PathValue("events.{key}.gift-type")
    private int giftType;

    @PathValue("events.{key}.condition")
    private List<String> condition;

    @PathValue("events.{key}.claimed-sound")
    private String claimedSound;

    @PathValue("events.{key}.claimed-msg")
    private String claimedMsg;

    @PathValue("events.{key}.begin-time")
    private Date beginTime;

    @PathValue("events.{key}.end-time")
    private Date endTime;

}
