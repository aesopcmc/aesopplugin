package top.mcos.scheduler.jobs;

import com.epicnicity322.epicpluginlib.core.logger.ConsoleLogger;
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
public class CommandJob extends AbstractJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            log(context, "执行任务");
            JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
            Map<String, Object> wrappedMap = jobDataMap.getWrappedMap();
            CommandConfig config = BeanMapUtil.mapToBean(wrappedMap, CommandConfig.class);
            List<String> commands = config.getCommands();
            if(commands!=null) {
                Bukkit.getScheduler().runTask(AesopPlugin.getInstance(), ()->{
                    for (String cmdline : commands) {
                        if(StringUtils.isNotBlank(cmdline)) {
                            log(context, "执行指令：" + cmdline);
                            // 获取控制台身份，以控制台身份执行指令
                            ConsoleCommandSender consoleSender = Bukkit.getServer().getConsoleSender();
                            Bukkit.getServer().dispatchCommand(consoleSender, cmdline);
                        }
                    }
                });
            }
        }catch (Throwable e) {
            e.printStackTrace();
            log(context, "执行任务出错", ConsoleLogger.Level.ERROR);
        }
    }

    public static void registerJob(CommandConfig config) {
        String jobName = "command-"+config.getKey()+"-task";
        String groupName = "commandGroup";
        try {
            Map<String, Object> jobParams = BeanMapUtil.beanToMap(config);
            SchedulerHandler.registerJob(CommandJob.class, jobName, groupName, config.getStart(),
                    config.getEnd(), config.getCron(), jobParams);
        } catch (Exception e) {
            e.printStackTrace();
            AesopPlugin.logger.log("定时任务【"+jobName+"】激活失败，已跳过", ConsoleLogger.Level.ERROR);
        }
    }

    public static void unRegisterJob(CommandConfig config) {
        String jobName = "command-"+config.getKey()+"-task";
        String groupName = "commandGroup";
        SchedulerHandler.unRegisterJob(jobName, groupName);
    }

}
