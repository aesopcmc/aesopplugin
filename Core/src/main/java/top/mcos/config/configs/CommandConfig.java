package top.mcos.config.configs;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import top.mcos.config.PathEntity;
import top.mcos.config.PathKey;
import top.mcos.config.PathValue;
import top.mcos.message.PositionTypeEnum;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 执行控制台指令任务配置
 */
@Setter
@Getter
@ToString
@PathEntity("tasks.command")
public class CommandConfig {
    @PathKey
    private String key;
    @PathValue("tasks.command.{key}.enable")
    private boolean enable=false;
    @PathValue("tasks.command.{key}.cron")
    private String cron;
    @PathValue("tasks.command.{key}.start")
    private Date start;
    @PathValue("tasks.command.{key}.end")
    private Date end;
    @PathValue("tasks.command.{key}.commands")
    private List<String> commands = new ArrayList<>();
}
