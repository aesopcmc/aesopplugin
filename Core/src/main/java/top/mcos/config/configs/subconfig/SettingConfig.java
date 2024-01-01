package top.mcos.config.configs.subconfig;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import top.mcos.config.ann.ConfigFileName;
import top.mcos.config.ann.PathValue;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
@ConfigFileName("config.yml")
public class SettingConfig {
    @PathValue("config.debug")
    private boolean debug;
    @PathValue("config.regen-world.chunky-loading-notice.enable")
    private boolean chunkyLoadingNoticeEnable;
    @PathValue("config.regen-world.chunky-loading-notice.message")
    private String chunkyLoadingNoticeMessage;
    @PathValue("config.regen-world.chunky-loading-notice.delay")
    private long chunkyLoadingNoticeDelay;
    @PathValue("config.notice.actionbar.enable")
    private boolean noticeActionbarEnabled;
    @PathValue("config.notice.actionbar.delay-times")
    private long delayTimes;
    @PathValue("config.notice.actionbar.trylock-times")
    private long trylockTimes;
    @PathValue("config.notice.actionbar.display-width")
    private int displayWidth;
    @PathValue("config.notice.title.enable")
    private boolean noticeTitleEnabled;
    @PathValue("config.notice.title.fadein")
    private int noticeTitleFadein;
    @PathValue("config.notice.title.keep")
    private int noticeTitleKeep;
    @PathValue("config.notice.title.fadeout")
    private int noticeTitleFadeout;
    @PathValue("config.activity.claimed-sound")
    private String claimedSound;
    @PathValue("config.activity.handle-block-locations")
    private List<String> actHandleBlockLocations = new ArrayList<>();
    @PathValue("config.activity.snowball-count")
    private int actSnowballCount;
    @PathValue("config.activity.handle-snowball-button-locations")
    private List<String> actHandleSnowballButtonLocations = new ArrayList<>();
}