package top.mcos.business.gbclear.config;

import lombok.Getter;
import lombok.Setter;
import top.mcos.config.ann.ConfigFileName;
import top.mcos.config.ann.PathValue;

import java.util.List;

@Setter
@Getter
@ConfigFileName("gbclear.yml")
public final class GbClearConfig {
    @PathValue("debug")
    private boolean debug=false;

    @PathValue("prefix")
    private String prefix;

    @PathValue("auto-clear-enable")
    private boolean autoClearEnable = false;

    @PathValue("auto-clear-period")
    private int autoClearPeriod;

    @PathValue("unload-clean-enable")
    private boolean unloadCleanEnable = false;

    @PathValue("unload-keep-time")
    private int unloadKeepTime;

    @PathValue("exclude-worlds")
    private List<String> excludeWorlds;

    @PathValue("notice.enable")
    private boolean noticeEnable=true;

    @PathValue("notice.timeleft")
    private List<Integer> noticeTimeleft;

    @PathValue("notice.countdown-in")
    private int noticeCountdownIn;

    @PathValue("notice.countdown-type")
    private int noticeCountdownType;

}
