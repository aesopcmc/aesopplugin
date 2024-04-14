package top.mcos.business.gbclear.config;

import lombok.Getter;
import lombok.Setter;
import top.mcos.business.firework.config.sub.LocationFireworkGroupConfig;
import top.mcos.business.firework.config.sub.PlayerFireworkGroupConfig;
import top.mcos.business.firework.config.sub.TextFireworkConfig;
import top.mcos.config.ann.ConfigFileName;
import top.mcos.config.ann.PathList;
import top.mcos.config.ann.PathValue;

import java.util.List;

@Setter
@Getter
@ConfigFileName("gbclear.yml")
public final class GbClearConfig {
    @PathValue("enable")
    private boolean enable=false;

    @PathValue("debug")
    private boolean debug=false;

    @PathValue("prefix")
    private String prefix;

    @PathValue("clear-period")
    private int clearPeriod;

    @PathValue("notice.enable")
    private boolean noticeEnable=true;

    @PathValue("notice.timeleft")
    private List<Integer> noticeTimeleft;

    @PathValue("notice.countdown-in")
    private int noticeCountdownIn;

    @PathValue("notice.countdown-type")
    private int noticeCountdownType;

}
