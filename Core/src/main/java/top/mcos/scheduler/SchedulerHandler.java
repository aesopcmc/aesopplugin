package top.mcos.scheduler;

import com.epicnicity322.epicpluginlib.core.logger.ConsoleLogger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import top.mcos.AesopPlugin;
import top.mcos.config.ConfigLoader;
import top.mcos.config.configs.subconfig.CommandConfig;
import top.mcos.config.configs.subconfig.NoticeConfig;
import top.mcos.config.configs.subconfig.RegenWorldConfig;
import top.mcos.scheduler.jobs.CommandJob;
import top.mcos.scheduler.jobs.DemoJob;
import top.mcos.scheduler.jobs.NoticeJob;
import top.mcos.scheduler.jobs.RegenWorldJob;

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

    private SchedulerHandler() {
    }

    public static synchronized void init() {
        // 1、创建调度器Scheduler
        try {
            SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            scheduler = schedulerFactory.getScheduler();
            scheduler.start();
            AesopPlugin.logger.log("已启动定时任务调度器");
        } catch (SchedulerException e) {
            e.printStackTrace();
            AesopPlugin.logger.log("任务调度器创建失败", ConsoleLogger.Level.ERROR);
        }
    }

    // todo 注册任务、激活所有任务、激活指定任务、暂停指定任务、暂停所有任务、移除所有任务

    /**
     *  注册任务
     */
    public static void registerJobs() {
        // 注册世界生成任务
        for (RegenWorldConfig config : ConfigLoader.baseConfig.getRegenWorldConfigs()) {
            if (config.isEnable()) RegenWorldJob.registerJob(config);
        }

        // 注册消息广播任务
        for (NoticeConfig config : ConfigLoader.baseConfig.getNoticeConfigs()) {
            if(config.isEnable()) NoticeJob.registerJob(config);
        }

        // 注册指令任务
        for (CommandConfig config : ConfigLoader.baseConfig.getCommandConfigs()) {
            if(config.isEnable()) CommandJob.registerJob(config);
        }
    }

    public static void registerJob(Class<? extends Job> clazz, String jobName, String groupName, Date startAt, Date endAt, String cron, Map<String, Object> jobParams) {
        try {
            JobDetail jobDetail = JobBuilder.newJob(clazz)
                .withIdentity(jobName, groupName).build();
            jobDetail.getJobDataMap().putAll(jobParams);
            // 3、构建Trigger实例,每隔1s执行一次
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName+"trigger", groupName+"triggerGroup")
                    .startNow()//立即生效
                    .endAt(endAt) //表示触发器结束触发的时间;
                .startAt(startAt==null ? new Date() : startAt) //表示触发器首次被触发的时间;
                .withSchedule(CronScheduleBuilder.cronSchedule(cron).withMisfireHandlingInstructionFireAndProceed()).build();
            scheduler.scheduleJob(jobDetail, trigger);
            AesopPlugin.logger.log("定时任务【"+jobName+"】已激活");
        } catch (SchedulerException e) {
            e.printStackTrace();
            AesopPlugin.logger.log("任务加载失败！", ConsoleLogger.Level.ERROR);
        }
    }

    public static void unRegisterJob(String jobName, String groupName) {
        try {
            scheduler.deleteJob(JobKey.jobKey(jobName, groupName));
            AesopPlugin.logger.log("成功移除任务【"+jobName+"】");
        } catch (SchedulerException e) {
            e.printStackTrace();
            AesopPlugin.logger.log("移除任务【"+jobName+"】失败!", ConsoleLogger.Level.ERROR);
        }
    }

    public static synchronized void clear() {
        shutdown();
        init();
    }

    public static synchronized void start() {
        try {
            scheduler.start();
            AesopPlugin.logger.log("成功启动任务调度器。");
        } catch (SchedulerException e) {
            e.printStackTrace();
            AesopPlugin.logger.log("任务调度器启动失败!", ConsoleLogger.Level.ERROR);
        }
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
