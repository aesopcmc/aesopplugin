package top.mcos.scheduler.jobs;

import com.epicnicity322.epicpluginlib.core.logger.ConsoleLogger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import top.mcos.AesopPlugin;
import top.mcos.config.configs.subconfig.NoticeConfig;
import top.mcos.message.MessageHandler;
import top.mcos.message.PositionTypeEnum;
import top.mcos.scheduler.AbstractJob;
import top.mcos.scheduler.SchedulerHandler;
import top.mcos.util.BeanMapUtil;

import java.util.Map;

/**
 * 消息通知任务
 */
//设定的时间间隔为3秒,但job执行时间是5秒,设置@DisallowConcurrentExecution以后程序会等任务执行完毕以后再去执行,否则会在3秒时再启用新的线程执行
//@DisallowConcurrentExecution
public class NoticeJob extends AbstractJob {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            log(context, "执行任务");
            JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
            Map<String, Object> wrappedMap = jobDataMap.getWrappedMap();
            NoticeConfig config = BeanMapUtil.mapToBean(wrappedMap, NoticeConfig.class);

            if(PositionTypeEnum.actionbar.name().equals(config.getPositionType())) {
                MessageHandler.pushActionbarMessage(config.getMessage());
            } else if (PositionTypeEnum.title.name().equals(config.getPositionType())) {
                MessageHandler.pushTitleMessage(config.getMessage(), config.getSubMessage());
            }
        }catch (Throwable e) {
            e.printStackTrace();
            log(context, "执行任务出错", ConsoleLogger.Level.ERROR);
        }
    }

}
