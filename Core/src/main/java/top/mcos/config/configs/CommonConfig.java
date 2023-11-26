package top.mcos.config.configs;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommonConfig {
    private boolean chunkyLoadingNoticeEnable;
    private String chunkyLoadingNoticeMessage;
    private long chunkyLoadingNoticeDelay;
    private boolean noticeActionbarEnabled;
    private long delayTimes;
    private long trylockTimes;
    private int displayWidth;
    private boolean noticeTitleEnabled;
    private int noticeTitleFadein;
    private int noticeTitleKeep;
    private int noticeTitleFadeout;
}
