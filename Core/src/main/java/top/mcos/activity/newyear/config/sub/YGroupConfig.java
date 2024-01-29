package top.mcos.activity.newyear.config.sub;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import top.mcos.config.ann.ConfigFileName;
import top.mcos.config.ann.PathKey;
import top.mcos.config.ann.PathValue;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
@ConfigFileName("yanhua.yml")
public class YGroupConfig {
    @PathKey
    private String key;
    @PathValue("groups.{key}.locations")
    private List<String> locations;
}
