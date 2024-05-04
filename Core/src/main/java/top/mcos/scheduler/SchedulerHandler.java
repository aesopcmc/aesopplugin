package top.mcos.scheduler;

import top.mcos.business.regen.config.RgConfig;
import top.mcos.business.regen.config.sub.RgWorldConfig;
import top.mcos.util.epiclib.logger.ConsoleLogger;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import top.mcos.AesopPlugin;
import top.mcos.business.yanhua.config.sub.RunTaskPlanConfig;
import top.mcos.config.ConfigLoader;
import top.mcos.config.configs.subconfig.BroadcastConfig;
import top.mcos.config.configs.subconfig.CommandConfig;
import top.mcos.config.configs.subconfig.NoticeConfig;
import top.mcos.config.configs.subconfig.RegenWorldConfig;
import top.mcos.scheduler.jobs.DemoJob;
import top.mcos.util.BeanMapUtil;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 任务调度：
 * 教程：https://blog.csdn.net/noaman_wgs/article/details/80984873
 * 工具生成cron表达式: https://cron.qqe2.com/
 *
 * Misfire失火策略说明：
 *  withMisfireHandlingInstructionFireAndProceed   [MISFIRE_INSTRUCTION_FIRE_ONCE_NOW]（默认）
 *  ——以当前时间为触发频率立刻触发一次执行
 *  ——然后按照Cron频率依次执行
 *
 *  withMisfireHandlingInstructionDoNothing   [MISFIRE_INSTRUCTION_DO_NOTHING ]
 *  ——不触发立即执行
 *  ——等待下次Cron触发频率到达时刻开始按照Cron频率依次执行
 *
 *  withMisfireHandlingInstructionIgnoreMisfires    [MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY]
 *  ——以错过的第一个频率时间立刻开始执行
 *  ——重做错过的所有频率周期后
 *  ——当下一次触发频率发生时间大于当前时间后，再按照正常的Cron频率依次执行
 *原文链接：https://blog.csdn.net/zhanglong_4444/article/details/104322354
 */
public final class SchedulerHandler {
    public static Scheduler scheduler;

    private static List<JobConfig> allJob = new ArrayList<>();

    private SchedulerHandler() {}

    public static synchronized void init() {
        // 1、创建调度器Scheduler
        try {
            SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            scheduler = schedulerFactory.getScheduler();
            scheduler.start();
            AesopPlugin.logger.log("&a已启动定时任务调度器");
        } catch (SchedulerException e) {
            e.printStackTrace();
            AesopPlugin.logger.log("任务调度器创建失败", ConsoleLogger.Level.ERROR);
        }
    }

    /**
     *  注册任务
     */
    public static void registerAllJobs() {
        // 注册世界生成任务
        for (RegenWorldConfig config : ConfigLoader.baseConfig.getRegenWorldConfigs()) {
            allJob.add(config);
            registerJob(config);
        }

        // 注册消息通知任务
        for (NoticeConfig config : ConfigLoader.baseConfig.getNoticeConfigs()) {
            allJob.add(config);
            registerJob(config);
        }

        // 注册消息广播任务
        for (BroadcastConfig config : ConfigLoader.baseConfig.getBroadcastConfigs()) {
            allJob.add(config);
            registerJob(config);
        }

        // 注册指令任务
        for (CommandConfig config : ConfigLoader.baseConfig.getCommandConfigs()) {
            allJob.add(config);
            registerJob(config);
        }

        // 注册烟花执行任务
        for (RunTaskPlanConfig config : ConfigLoader.yanHuaConfig.getPlans()) {
            allJob.add(config);
            registerJob(config);
        }
        //
        //// 注册世界重置执行任务
        //for (RgWorldConfig config : ConfigLoader.rgConfig.getRgWorldConfigs()) {
        //    allJob.add(config);
        //    registerJob(config);
        //}
    }

    public static synchronized void shutdown() {
        try {
            scheduler.shutdown();
            AesopPlugin.logger.log("任务调度器已停止。");
        } catch (SchedulerException e) {
            e.printStackTrace();
            AesopPlugin.logger.log("任务调度器停止失败", ConsoleLogger.Level.ERROR);
        }
    }

    public static synchronized void clear() {
        allJob.clear();
        shutdown();
        init();
    }


    public static List<JobConfig> getAllJob() {
        return allJob;
    }

    //public static synchronized void start() {
    //    try {
    //        scheduler.start();
    //        AesopPlugin.logger.log("成功启动任务调度器。");
    //    } catch (SchedulerException e) {
    //        e.printStackTrace();
    //        AesopPlugin.logger.log("任务调度器启动失败!", ConsoleLogger.Level.ERROR);
    //    }
    //}

    public static boolean registerJob(JobConfig config) {
        if(!config.isEnable()) return false;
        String keyPrefix = config.getKeyPrefix() + "-task";
        String jobName = keyPrefix+"-"+config.getKey();
        String groupName = keyPrefix + "-group";
        try {
            Map<String, Object> jobParams = BeanMapUtil.beanToMap(config);
            SchedulerHandler.registerJob(config.getJobClass(), jobName, groupName, config.getStart(),
                    config.getEnd(), config.getCron(), jobParams);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            AesopPlugin.logger.log("&c定时任务【"+jobName+"】激活失败，已跳过", ConsoleLogger.Level.ERROR);
        }
        return false;
    }

    public static void unRegisterJob(JobConfig config) {
        String keyPrefix = config.getKeyPrefix() + "-task";
        String jobName = keyPrefix+"-"+config.getKey();
        String groupName = keyPrefix + "-group";
        SchedulerHandler.unRegisterJob(jobName, groupName);
    }

    public static void executeNow(JobConfig config) {
        String keyPrefix = config.getKeyPrefix() + "-task";
        String jobName = keyPrefix+"-"+config.getKey();
        String groupName = keyPrefix + "-group";
        SchedulerHandler.executeNow(jobName, groupName);
    }

    private static void registerJob(Class<? extends Job> clazz, String jobName, String groupName, Date startAt, Date endAt, String cron, Map<String, Object> jobParams) throws SchedulerException, ParseException {
        JobDetail jobDetail = JobBuilder.newJob(clazz)
            .withIdentity(jobName, groupName).build();
        jobDetail.getJobDataMap().putAll(jobParams);
        // 3、构建Trigger实例,每隔1s执行一次
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName+"trigger", groupName+"triggerGroup")
                .startNow()//立即生效
                .startAt(startAt==null ? DateUtils.parseDate("1999-01-01", "yyyy-MM-dd") : startAt) //表示触发器首次被触发的时间;
                .endAt(endAt) //表示触发器结束触发的时间;
            //.withSchedule(CronScheduleBuilder.cronSchedule(cron).withMisfireHandlingInstructionFireAndProceed()).build();
            .withSchedule(CronScheduleBuilder.cronSchedule(cron).withMisfireHandlingInstructionDoNothing()).build();
        scheduler.scheduleJob(jobDetail, trigger);
        AesopPlugin.logger.log("&b定时任务【"+jobName+"】已激活");
    }

    private static void unRegisterJob(String jobName, String groupName) {
        try {
            scheduler.deleteJob(JobKey.jobKey(jobName, groupName));
            AesopPlugin.logger.log("&e成功移除任务【"+jobName+"】");
        } catch (SchedulerException e) {
            e.printStackTrace();
            AesopPlugin.logger.log("移除任务【"+jobName+"】失败!", ConsoleLogger.Level.ERROR);
        }
    }

    private static void executeNow(String jobName, String groupName) {
        try {
            scheduler.triggerJob(JobKey.jobKey(jobName, groupName));
            AesopPlugin.logger.log("&e已手动触发任务job【jobname:"+jobName+",groupname:"+groupName+"】");
        } catch (SchedulerException e) {
            e.printStackTrace();
            AesopPlugin.logger.log("手动执行任务失败！", ConsoleLogger.Level.ERROR);
        }
    }

    public static void main(String[] args) throws SchedulerException, InterruptedException {
        // 1、创建调度器Scheduler
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();
        // 2、创建JobDetail实例，并与PrintWordsJob类绑定(Job执行内容)
        JobDetail jobDetail = JobBuilder.newJob(DemoJob.class)
                .withIdentity("job1", "group1").build();
        // 3、构建Trigger实例,每隔1s执行一次
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger1", "triggerGroup1")
                .startNow()//立即生效
                //.startAt() 表示触发器首次被触发的时间;
                //.endAt() 表示触发器结束触发的时间;
                .withSchedule(CronScheduleBuilder.cronSchedule("0/1 * * * * ?")
                        .withMisfireHandlingInstructionDoNothing()).build();
                //.withSchedule(SimpleScheduleBuilder.simpleSchedule()
                //        .withIntervalInSeconds(1)//每隔1s执行一次
                //        .repeatForever()).build();//一直执行

        //4、执行
        System.out.println("--------scheduler start ! ------------");
        scheduler.start();
        // 注册任务
        scheduler.scheduleJob(jobDetail, trigger);
        TimeUnit.SECONDS.sleep(5);

        // 暂停任务
        //scheduler.pauseJob(JobKey.jobKey("job1", "group1"));// 暂停
        scheduler.deleteJob(JobKey.jobKey("job1", "group1"));

        TimeUnit.SECONDS.sleep(16);

        // 恢复任务
        System.out.println("恢复--");
        //scheduler.resumeJob(JobKey.jobKey("job1", "group1"));
        scheduler.scheduleJob(jobDetail, trigger);

        //睡眠
        TimeUnit.MINUTES.sleep(2);
        scheduler.shutdown();
        System.out.println("--------scheduler shutdown ! ------------");
    }

}
