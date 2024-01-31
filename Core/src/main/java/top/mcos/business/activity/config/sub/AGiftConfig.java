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
public class AGiftConfig {
    @PathKey
    private String key;

    @PathValue("gifts.{key}.type")
    private int type;

    @PathValue("gifts.{key}.display-name")
    private String displayName;

    @PathValue("gifts.{key}.db-name")
    private String dbName;

    @PathValue("gifts.{key}.commands")
    private List<String> commands;

    @PathValue("gifts.{key}.material")
    private String material;

    @PathValue("gifts.{key}.lore")
    private List<String> lore;

    @PathValue("gifts.{key}.potions")
    private List<String> potions;

    @PathValue("gifts.{key}.enchants")
    private List<String> enchants;

    @PathValue("gifts.{key}.ext-namespace")
    private String extNamespace;

    @PathValue("gifts.{key}.ext-namespace-key")
    private String extNamespaceKey;

    @PathValue("gifts.{key}.broadcast")
    private boolean broadcast;

}
