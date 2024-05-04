package top.mcos.scheduler.jobs;

import org.quartz.JobExecutionContext;
import top.mcos.business.BusRegister;
import top.mcos.business.regen.config.sub.RgWorldConfig;
import top.mcos.scheduler.AbstractJob;
import top.mcos.util.epiclib.logger.ConsoleLogger;

//@DisallowConcurrentExecution
public class RgWorldJob extends AbstractJob<RgWorldConfig> {

    @Override
    public void run(JobExecutionContext context, RgWorldConfig config) {
        try {
            BusRegister.regenBus.deleteWorld(config);
        } catch (Throwable e) {
            e.printStackTrace();
            log("执行任务出错", ConsoleLogger.Level.ERROR);
        }
    }

    @Override
    protected Object getSonObject() {
        return this;
    }

}
