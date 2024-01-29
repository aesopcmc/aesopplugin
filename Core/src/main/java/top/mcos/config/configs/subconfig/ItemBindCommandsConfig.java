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
@ConfigFileName("config.yml")
public class ItemBindCommandsConfig {
    @PathKey
    private String key;

    @PathValue("item-bind-event.commands.{key}.enable")
    private boolean enable;

    @PathValue("item-bind-event.commands.{key}.material")
    private String material;

    @PathValue("item-bind-event.commands.{key}.display-name")
    private String displayName;

    @PathValue("item-bind-event.commands.{key}.lore")
    private List<String> lore;

    @PathValue("item-bind-event.commands.{key}.commands")
    private List<String> commands;

    @PathValue("item-bind-event.commands.{key}.execute-delay")
    private long executeDelay;

    @PathValue("item-bind-event.commands.{key}.execute-type")
    private int executeType;

    @PathValue("item-bind-event.commands.{key}.locations")
    private List<String> locations;
}
