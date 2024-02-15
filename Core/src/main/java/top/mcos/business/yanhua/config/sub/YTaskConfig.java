package top.mcos.business.yanhua.config.sub;

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
public class YTaskConfig{
    @PathKey
    private String key;
    @PathValue("tasks.{key}.group-key")
    private String groupKey;
    @PathValue("tasks.{key}.cells")
    private List<String> cells;
    @PathValue("tasks.{key}.group-loc-delay")
    private int groupLocDelay;
    @PathValue("tasks.{key}.group-loc-seq")
    private int groupLocSeq;
    @PathValue("tasks.{key}.cells-mode")
    private int cellsMode;
    @PathValue("tasks.{key}.loc-power")
    private int locPower;
}
