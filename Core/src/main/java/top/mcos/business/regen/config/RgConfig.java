package top.mcos.business.regen.config;

import lombok.Getter;
import lombok.Setter;
import top.mcos.business.regen.config.sub.RgWorldConfig;
import top.mcos.config.ann.ConfigFileName;
import top.mcos.config.ann.PathList;
import top.mcos.config.ann.PathValue;

import java.util.List;

@Setter
@Getter
@ConfigFileName("regenworld.yml")
public final class RgConfig {

    @PathValue("chunky-loading-notice.enable")
    private boolean chunkyLoadingNoticeEnable;

    @PathValue("chunky-loading-notice.message")
    private String chunkyLoadingNoticeMessage;

    @PathValue("chunky-loading-notice.delay")
    private long chunkyLoadingNoticeDelay;

    @PathList("worlds")
    private List<RgWorldConfig> rgWorldConfigs;

}
