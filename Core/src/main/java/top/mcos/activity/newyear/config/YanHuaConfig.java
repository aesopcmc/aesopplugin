package top.mcos.activity.newyear.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import top.mcos.activity.newyear.config.sub.RunTaskPlanConfig;
import top.mcos.activity.newyear.config.sub.YCellConfig;
import top.mcos.activity.newyear.config.sub.YGroupConfig;
import top.mcos.activity.newyear.config.sub.YTaskConfig;
import top.mcos.config.ann.ConfigFileName;
import top.mcos.config.ann.PathList;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
@ConfigFileName("yanhua.yml")
public final class YanHuaConfig {
    @PathList("cells")
    private List<YCellConfig> cells;

    @PathList("groups")
    private List<YGroupConfig> groups;

    @PathList("tasks")
    private List<YTaskConfig> tasks;

    @PathList("run-task-plan")
    private List<RunTaskPlanConfig> plans;
}
