package top.mcos.scheduler.jobs;

import com.epicnicity322.epicpluginlib.core.logger.ConsoleLogger;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.checkerframework.checker.units.qual.A;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import top.mcos.AesopPlugin;
import top.mcos.activity.newyear.config.YanHuaEntity;
import top.mcos.activity.newyear.config.YanHuaEvent;
import top.mcos.activity.newyear.config.sub.RunTaskPlanConfig;
import top.mcos.activity.newyear.config.sub.YGroupConfig;
import top.mcos.activity.newyear.config.sub.YTaskConfig;
import top.mcos.config.ConfigLoader;
import top.mcos.config.configs.subconfig.CommandConfig;
import top.mcos.scheduler.AbstractJob;
import top.mcos.scheduler.SchedulerHandler;
import top.mcos.util.BeanMapUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 执行控制台指令任务
 */
//设定的时间间隔为3秒,但job执行时间是5秒,设置@DisallowConcurrentExecution以后程序会等任务执行完毕以后再去执行,否则会在3秒时再启用新的线程执行
//@DisallowConcurrentExecution
public class YanhuaRunTaskJob extends AbstractJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            log(context, "执行任务");
            JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
            Map<String, Object> wrappedMap = jobDataMap.getWrappedMap();
            RunTaskPlanConfig config = BeanMapUtil.mapToBean(wrappedMap, RunTaskPlanConfig.class);
            List<String> plans = config.getPlans();
            if(plans!=null && plans.size()>0) {
                for (String plan : plans) {
                    String[] split = plan.split(":");
                    String taskKey = split[0];
                    int delay = Integer.parseInt(split[1]);
                    int repeatDelay = Integer.parseInt(split[2]);
                    int repeat = Integer.parseInt(split[3]);
                    int repeatDelayTotal = repeatDelay;
                    for (int i=1;i<=repeat;i++) {
                        putFireworkQueue(taskKey, delay + repeatDelayTotal);
                        repeatDelayTotal = repeatDelayTotal + repeatDelay;
                    }
                }
            }
        }catch (Throwable e) {
            e.printStackTrace();
            log(context, "执行任务出错", ConsoleLogger.Level.ERROR);
        }
    }

    public static void registerJob(RunTaskPlanConfig config) {
        jobSuffer = "yanhua-task";

        String jobName = config.getKey()+"-"+jobSuffer;
        String groupName = jobSuffer + "-group";
        try {
            Map<String, Object> jobParams = BeanMapUtil.beanToMap(config);
            SchedulerHandler.registerJob(YanhuaRunTaskJob.class, jobName, groupName, config.getStart(),
                    config.getEnd(), config.getCron(), jobParams);
        } catch (Exception e) {
            e.printStackTrace();
            AesopPlugin.logger.log("定时任务【"+jobName+"】激活失败，已跳过", ConsoleLogger.Level.ERROR);
        }
    }

    public static void unRegisterJob(RunTaskPlanConfig config) {
        SchedulerHandler.unRegisterJob( config.getKey()+"-"+jobSuffer, jobSuffer + "-group");
    }

    public static void executeNow(RunTaskPlanConfig config) {
        SchedulerHandler.executeNow(config.getKey()+"-"+jobSuffer, jobSuffer + "-group");
    }

    /**
     * 推送烟花到队列
     * @param taskKey 任务key
     * @param addDelay 延迟
     */
    private void putFireworkQueue(String taskKey, int addDelay) {
        // 任务
        List<YTaskConfig> tasks = ConfigLoader.yanHuaConfig.getTasks();
        Map<String, YTaskConfig> taskMaps = tasks.stream().collect(Collectors.toMap(YTaskConfig::getKey, c -> c));

        // 分组
        List<YGroupConfig> groups = ConfigLoader.yanHuaConfig.getGroups();
        Map<String, YGroupConfig> groupMaps = groups.stream().collect(Collectors.toMap(YGroupConfig::getKey, c -> c));

        YTaskConfig taskConfig = taskMaps.get(taskKey);
        if(taskConfig==null) {
            AesopPlugin.logger.log("taskKey不存在:"+taskKey, ConsoleLogger.Level.ERROR);
        }

        String groupKey = taskConfig.getGroupKey();
        int groupLocSeq = taskConfig.getGroupLocSeq();
        List<String> cellKeys = taskConfig.getCells();

        YGroupConfig yGroupConfig = groupMaps.get(groupKey);
        List<String> locations = yGroupConfig.getLocations();

        int cellsMode = taskConfig.getCellsMode();

        if(groupLocSeq==1) { // 顺序执行

        } else if (groupLocSeq==2) { // 随机执行
            Collections.shuffle(locations);
        }

        int delayTotal = taskConfig.getGroupLocDelay() + addDelay;
        for (String location : locations) {
            YanHuaEntity entity = new YanHuaEntity(location, taskConfig.getLocPower(), cellsMode, delayTotal, cellKeys);
            // 推送到队列
            YanHuaEvent.putQueue(entity);
            delayTotal = delayTotal + taskConfig.getGroupLocDelay();
        }
    }
}
