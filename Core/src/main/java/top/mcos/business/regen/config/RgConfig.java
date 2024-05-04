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

    @PathList("worlds")
    private List<RgWorldConfig> rgWorldConfigs;

}
