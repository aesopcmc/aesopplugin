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
public class CommonConfig {
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

    private List<RegenWorldConfig> regenWorldConfigs = new ArrayList<>();

    private List<NoticeMessageConfig> noticeMessageConfigs = new ArrayList<>();
}
