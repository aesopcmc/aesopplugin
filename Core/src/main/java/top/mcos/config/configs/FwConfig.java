package top.mcos.config.configs;

import lombok.Getter;
import lombok.Setter;
import top.mcos.config.ann.ConfigFileName;
import top.mcos.config.ann.PathList;
import top.mcos.config.configs.subconfig.LocationFireworkGroupConfig;
import top.mcos.config.configs.subconfig.PlayerFireworkGroupConfig;
import top.mcos.config.configs.subconfig.TextFireworkConfig;

import java.util.List;

@Setter
@Getter
@ConfigFileName("firework.yml")
public final class FwConfig{
    @PathList("firework.text-firework")
    private List<TextFireworkConfig> textFireworks;
    @PathList("player-firework-group")
    private List<PlayerFireworkGroupConfig> playerFireworkGroups;
    @PathList("location-firework-group")
    private List<LocationFireworkGroupConfig> locationFireworkGroups;
}
