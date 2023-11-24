package top.mcos.scheduler;

import org.quartz.*;
import top.mcos.AesopPlugin;
import top.mcos.message.MessageHandler;
import top.mcos.message.PositionTypeEnum;

import java.util.Optional;

/**
 * 消息通知任务
 */
//设定的时间间隔为3秒,但job执行时间是5秒,设置@DisallowConcurrentExecution以后程序会等任务执行完毕以后再去执行,否则会在3秒时再启用新的线程执行
@DisallowConcurrentExecution
public class NoticeJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        String message = Optional.ofNullable(jobDataMap.get("message")).map(Object::toString).orElse(null);
        String subMessage = Optional.ofNullable(jobDataMap.get("subMessage")).map(Object::toString).orElse(null);
        String positionType = Optional.ofNullable(jobDataMap.get("positionType")).map(Object::toString).orElse(null);
        if(PositionTypeEnum.actionbar.name().equals(positionType)) {
            MessageHandler.pushActionbarMessage(message);
        } else if (PositionTypeEnum.title.name().equals(positionType)) {
            MessageHandler.pushTitleMessage(message, subMessage);
        }
        AesopPlugin.logger.log("执行NoticeJob");
    }
}
