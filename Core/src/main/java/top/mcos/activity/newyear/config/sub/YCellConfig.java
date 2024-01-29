package top.mcos.activity.newyear.config.sub;

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
@ConfigFileName("yanhua.yml")
public class YCellConfig {
    @PathKey
    private String key;
    @PathValue("cells.{key}.type")
    private String type;
    @PathValue("cells.{key}.colors")
    private List<String> colors;
    @PathValue("cells.{key}.trail")
    private boolean trail=false;
    @PathValue("cells.{key}.fade-colors")
    private List<String> fadeColors;
    @PathValue("cells.{key}.flicker")
    private boolean flicker=false;
}
