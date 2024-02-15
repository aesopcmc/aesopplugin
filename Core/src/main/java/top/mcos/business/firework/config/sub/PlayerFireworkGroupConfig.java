package top.mcos.business.firework.config.sub;

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
public class PlayerFireworkGroupConfig {
    @PathKey
    private String key;
    @PathValue("player-firework-group.{key}.enable")
    private boolean enable=false;
    @PathValue("player-firework-group.{key}.name")
    private String name;
    @PathValue("player-firework-group.{key}.offsetY")
    private double offsetY;
    @PathValue("player-firework-group.{key}.firework-keys")
    private List<String> fireworkKeys;
}
