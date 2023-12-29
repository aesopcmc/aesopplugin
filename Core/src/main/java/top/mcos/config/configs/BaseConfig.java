package top.mcos.config.configs;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import top.mcos.config.PathEntity;
import top.mcos.config.PathValue;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置类
 * 声明配置类要求：
 * 1.字段使用基本数据类型，而不是封装类
 * 2.对象集合类，要添加类注解{@link PathEntity}
 */
@Setter
@Getter
@ToString
public class BaseConfig {
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

    private List<RegenWorldConfig> regenWorldConfigs = new ArrayList<>();

    private List<NoticeConfig> noticeConfigs = new ArrayList<>();

    private List<CommandConfig> commandConfigs = new ArrayList<>();

    private List<FireworkConfig> fireworkConfigs = new ArrayList<>();

    private List<PlayerFireworkConfig> playerFireworkConfigs = new ArrayList<>();
}
