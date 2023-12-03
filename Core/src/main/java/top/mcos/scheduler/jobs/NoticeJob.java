package top.mcos.scheduler.jobs;

import com.epicnicity322.epicpluginlib.core.logger.ConsoleLogger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import top.mcos.AesopPlugin;
import top.mcos.config.configs.NoticeMessageConfig;
import top.mcos.message.MessageHandler;
import top.mcos.message.PositionTypeEnum;
import top.mcos.scheduler.AbstractJob;
import top.mcos.scheduler.SchedulerHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 消息通知任务
 */
//设定的时间间隔为3秒,但job执行时间是5秒,设置@DisallowConcurrentExecution以后程序会等任务执行完毕以后再去执行,否则会在3秒时再启用新的线程执行
//@DisallowConcurrentExecution
public class NoticeJob extends AbstractJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
            String message = Optional.ofNullable(jobDataMap.get("message")).map(Object::toString).orElse(null);
            String subMessage = Optional.ofNullable(jobDataMap.get("subMessage")).map(Object::toString).orElse(null);
            String positionType = Optional.ofNullable(jobDataMap.get("positionType")).map(Object::toString).orElse(null);
            if(PositionTypeEnum.actionbar.name().equals(positionType)) {
                MessageHandler.pushActionbarMessage(message);
            } else if (PositionTypeEnum.title.name().equals(positionType)) {
                MessageHandler.pushTitleMessage(message, subMessage);
            }
        }catch (Throwable e) {
            e.printStackTrace();
            log(context, "执行任务出错", ConsoleLogger.Level.ERROR);
        }
    }

    public static void registerJob(NoticeMessageConfig config) {
        String jobName = "notice-"+config.getKey()+"-task";
        String groupName = "noticeGroup";
        try {
            Map<String, Object> jobParams = new HashMap<>();
            jobParams.put("positionType", config.getPositionType());
            jobParams.put("message", config.getMessage());
            jobParams.put("subMessage", config.getSubMessage());
            SchedulerHandler.registerJob(NoticeJob.class, jobName, groupName, config.getStart(),
                    config.getEnd(), config.getCron(), jobParams);
        } catch (Exception e) {
            e.printStackTrace();
            AesopPlugin.logger.log("定时任务【"+jobName+"】激活失败，已跳过", ConsoleLogger.Level.ERROR);
        }
    }

    public static void unRegisterJob(NoticeMessageConfig config) {
        String jobName = "notice-"+config.getKey()+"-task";
        String groupName = "noticeGroup";
        SchedulerHandler.unRegisterJob(jobName, groupName);
    }

}
