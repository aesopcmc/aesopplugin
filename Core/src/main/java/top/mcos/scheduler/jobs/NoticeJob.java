package top.mcos.scheduler.jobs;

import com.epicnicity322.epicpluginlib.core.logger.ConsoleLogger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import top.mcos.message.MessageHandler;
import top.mcos.message.PositionTypeEnum;
import top.mcos.scheduler.AbstractJob;

import java.util.Optional;

/**
 * 消息通知任务
 */
//设定的时间间隔为3秒,但job执行时间是5秒,设置@DisallowConcurrentExecution以后程序会等任务执行完毕以后再去执行,否则会在3秒时再启用新的线程执行
@DisallowConcurrentExecution
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
}
