package top.mcos.scheduler.jobs;

import top.mcos.util.epiclib.logger.ConsoleLogger;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import top.mcos.AesopPlugin;
import top.mcos.config.configs.subconfig.CommandConfig;
import top.mcos.scheduler.AbstractJob;
import top.mcos.scheduler.SchedulerHandler;
import top.mcos.util.BeanMapUtil;

import java.util.List;
import java.util.Map;

/**
 * 执行控制台指令任务
 */
//设定的时间间隔为3秒,但job执行时间是5秒,设置@DisallowConcurrentExecution以后程序会等任务执行完毕以后再去执行,否则会在3秒时再启用新的线程执行
//@DisallowConcurrentExecution
public class CommandJob extends AbstractJob<CommandConfig> {
    @Override
    protected void run(JobExecutionContext context, CommandConfig config) {
        try {
            List<String> commands = config.getCommands();
            if(commands!=null) {
                Bukkit.getScheduler().runTask(AesopPlugin.getInstance(), ()->{
                    for (String cmdline : commands) {
                        if(StringUtils.isNotBlank(cmdline)) {
                            log("执行指令：" + cmdline);
                            // 获取控制台身份，以控制台身份执行指令
                            ConsoleCommandSender consoleSender = Bukkit.getServer().getConsoleSender();
                            Bukkit.getServer().dispatchCommand(consoleSender, cmdline);
                        }
                    }
                });
            }
        }catch (Throwable e) {
            e.printStackTrace();
            log("执行任务出错", ConsoleLogger.Level.ERROR);
        }
    }

    @Override
    protected Object getSonObject() {
        return this;
    }

}
