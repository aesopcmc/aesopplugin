package top.mcos.scheduler;

import com.epicnicity322.epicpluginlib.core.logger.ConsoleLogger;
import org.quartz.JobExecutionContext;
import top.mcos.AesopPlugin;
import top.mcos.config.ConfigLoader;

public abstract class AbstractJob {
    protected void log(JobExecutionContext context, String message) {
        if(ConfigLoader.baseConfig.isDebug())
        log(context, message, ConsoleLogger.Level.INFO);
    }
    protected void log(JobExecutionContext context, String message, ConsoleLogger.Level level) {
        if(ConfigLoader.baseConfig.isDebug()) {
            message = "&9" + context.getJobDetail().getKey().getName() + " >> &7" + message;
            AesopPlugin.logger.log(message, level);
        }
    }

}
