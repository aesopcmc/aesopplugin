package top.mcos.business.activity.config.sub;


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
@ConfigFileName("activity.yml")
public class AConItemConfig {
    @PathKey
    private String key;

    @PathValue("condition.items.{key}.amount")
    private int amount;

    @PathValue("condition.items.{key}.display-name")
    private String displayName;

    @PathValue("condition.items.{key}.db-name")
    private String dbName;

    @PathValue("condition.items.{key}.material")
    private String material;

    @PathValue("condition.items.{key}.lore")
    private List<String> lore;

    @PathValue("condition.items.{key}.potions")
    private List<String> potions;

    @PathValue("condition.items.{key}.enchants")
    private List<String> enchants;
}
