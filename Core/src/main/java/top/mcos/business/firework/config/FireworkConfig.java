package top.mcos.business.firework.config;

import lombok.Getter;
import lombok.Setter;
import top.mcos.config.ann.ConfigFileName;
import top.mcos.config.ann.PathList;
import top.mcos.business.firework.config.sub.LocationFireworkGroupConfig;
import top.mcos.business.firework.config.sub.PlayerFireworkGroupConfig;
import top.mcos.business.firework.config.sub.TextFireworkConfig;
import top.mcos.config.ann.PathValue;

import java.util.List;

@Setter
@Getter
@ConfigFileName("firework.yml")
public final class FireworkConfig {
    @PathValue("player-enable")
    private boolean playerEnable;
    @PathValue("location-enable")
    private boolean locationEnable;
    @PathList("player-firework-group")
    private List<PlayerFireworkGroupConfig> playerFireworkGroups;
    @PathList("location-firework-group")
    private List<LocationFireworkGroupConfig> locationFireworkGroups;
    @PathList("firework.text-firework")
    private List<TextFireworkConfig> textFireworks;
}
