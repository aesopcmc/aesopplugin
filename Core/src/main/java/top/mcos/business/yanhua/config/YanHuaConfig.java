package top.mcos.business.yanhua.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import top.mcos.business.yanhua.config.sub.RunTaskPlanConfig;
import top.mcos.business.yanhua.config.sub.YCellConfig;
import top.mcos.business.yanhua.config.sub.YGroupConfig;
import top.mcos.business.yanhua.config.sub.YTaskConfig;
import top.mcos.config.ann.ConfigFileName;
import top.mcos.config.ann.PathList;
import top.mcos.config.ann.PathValue;

import java.util.List;

@Setter
@Getter
@ToString
@ConfigFileName("yanhua.yml")
public final class YanHuaConfig {

    @PathValue("task-enable")
    private boolean taskEnable;

    @PathList("cells")
    private List<YCellConfig> cells;

    @PathList("groups")
    private List<YGroupConfig> groups;

    @PathList("tasks")
    private List<YTaskConfig> tasks;

    @PathList("run-task-plan")
    private List<RunTaskPlanConfig> plans;
}
