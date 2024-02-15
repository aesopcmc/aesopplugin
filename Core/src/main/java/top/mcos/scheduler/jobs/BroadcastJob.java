package top.mcos.scheduler.jobs;

import com.epicnicity322.epicpluginlib.core.logger.ConsoleLogger;
import org.bukkit.Bukkit;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import top.mcos.config.configs.subconfig.BroadcastConfig;
import top.mcos.message.MessageHandler;
import top.mcos.scheduler.AbstractJob;
import top.mcos.util.BeanMapUtil;
import top.mcos.util.RandomUtil;

import java.util.Map;

/**
 * 消息广播任务
 */
//设定的时间间隔为3秒,但job执行时间是5秒,设置@DisallowConcurrentExecution以后程序会等任务执行完毕以后再去执行,否则会在3秒时再启用新的线程执行
//@DisallowConcurrentExecution
public class BroadcastJob extends AbstractJob {
    private static int nextOrder=0;
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            log(context, "执行任务");
            JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
            Map<String, Object> wrappedMap = jobDataMap.getWrappedMap();
            BroadcastConfig config = BeanMapUtil.mapToBean(wrappedMap, BroadcastConfig.class);

            // 没有玩家时不进行广播
            if(Bukkit.getOnlinePlayers().size()<1) return;

            if(config.getExecuteOrder()==0) {
                // 顺序执行
                int size = config.getMessages().size();
                if(size==0) return;
                if(nextOrder>(size-1)) nextOrder=0;
                String msg = config.getMessages().get(nextOrder);
                nextOrder++;
                MessageHandler.sendBroadcast(config.getPrefix(), msg, config.getSound());
            }else if(config.getExecuteOrder()==1) {
                // 随机执行
                int size = config.getMessages().size();
                String msg = config.getMessages().get(RandomUtil.get(0, size - 1));
                MessageHandler.sendBroadcast(config.getPrefix(), msg, config.getSound());
            }
        }catch (Throwable e) {
            e.printStackTrace();
            log(context, "执行任务出错", ConsoleLogger.Level.ERROR);
        }
    }


}
