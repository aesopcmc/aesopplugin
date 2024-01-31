package top.mcos.business.activity.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import top.mcos.business.activity.config.sub.AConItemConfig;
import top.mcos.business.activity.config.sub.AEventConfig;
import top.mcos.business.activity.config.sub.AGiftConfig;
import top.mcos.config.ann.ConfigFileName;
import top.mcos.config.ann.PathList;

import java.util.List;

@Setter
@Getter
@ToString
@ConfigFileName("activity.yml")
public final class ActivityConfig {
    @PathList("events")
    private List<AEventConfig> events;

    @PathList("gifts")
    private List<AGiftConfig> gifts;

    @PathList("condition.items")
    private List<AConItemConfig> conItemConfigs;
}
