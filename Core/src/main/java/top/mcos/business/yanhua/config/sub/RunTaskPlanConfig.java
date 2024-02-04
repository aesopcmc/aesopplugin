package top.mcos.business.yanhua.config.sub;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import top.mcos.config.ann.ConfigFileName;
import top.mcos.config.ann.PathKey;
import top.mcos.config.ann.PathValue;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@ToString
@ConfigFileName("yanhua.yml")
public class RunTaskPlanConfig {
    @PathKey
    private String key;

    @PathValue("run-task-plan.{key}.enable")
    private boolean enable=false;

    @PathValue("run-task-plan.{key}.cron")
    private String cron;

    @PathValue("run-task-plan.{key}.start")
    private Date start;

    @PathValue("run-task-plan.{key}.end")
    private Date end;

    @PathValue("run-task-plan.{key}.plans")
    private List<String> plans;

}