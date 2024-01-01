package top.mcos.config.configs;

import lombok.Getter;
import lombok.Setter;
import top.mcos.config.ann.ConfigFileName;
import top.mcos.config.ann.PathEntity;
import top.mcos.config.ann.PathList;
import top.mcos.config.configs.subconfig.CommandConfig;
import top.mcos.config.configs.subconfig.FireworkConfig;
import top.mcos.config.configs.subconfig.NoticeConfig;
import top.mcos.config.configs.subconfig.RegenWorldConfig;
import top.mcos.config.configs.subconfig.SettingConfig;

import java.util.List;

/**
 * 配置类
 * 声明配置类要求：
 * 1.字段使用基本数据类型，而不是封装类
 * 2.对象集合类，要添加类注解 {@link PathEntity} 、{@link PathList}
 */
@Setter
@Getter
@ConfigFileName("config.yml")
public final class BaseConfig {
    @PathEntity("config")
    private SettingConfig settingConfig;

    @PathList("tasks.regen-world")
    private List<RegenWorldConfig> regenWorldConfigs;

    @PathList("tasks.notice")
    private List<NoticeConfig> noticeConfigs;

    @PathList("tasks.command")
    private List<CommandConfig> commandConfigs;

    @PathList("tasks.firework")
    private List<FireworkConfig> fireworkConfigs;


}
