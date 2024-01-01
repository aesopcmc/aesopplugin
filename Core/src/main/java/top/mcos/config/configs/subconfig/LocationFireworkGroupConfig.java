package top.mcos.config.configs.subconfig;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import top.mcos.config.ann.ConfigFileName;
import top.mcos.config.ann.PathKey;
import top.mcos.config.ann.PathValue;

import java.util.List;

@Setter
@Getter
@ToString
@ConfigFileName("firework.yml")
public class LocationFireworkGroupConfig {
    @PathKey
    private String key;
    @PathValue("location-firework-group.{key}.enable")
    private boolean enable=false;
    @PathValue("location-firework-group.{key}.firework-keys")
    private List<String> fireworkKeys;
    @PathValue("location-firework-group.{key}.location")
    private String location;
}