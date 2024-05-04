package top.mcos.scheduler;

import lombok.SneakyThrows;
import org.quartz.JobDataMap;
import top.mcos.util.BeanMapUtil;
import top.mcos.util.ReflectUtil;
import top.mcos.util.epiclib.logger.ConsoleLogger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import top.mcos.AesopPlugin;
import java.util.Map;

public abstract class AbstractJob<C> implements Job {
    private JobExecutionContext context;
    @SneakyThrows
    @Override
    public void execute(JobExecutionContext context){
        this.context = context;
        log("执行任务");
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        Map<String, Object> wrappedMap = jobDataMap.getWrappedMap();
        Class<?> classC = ReflectUtil.getClassT(getSonObject(), 0);
        C config = (C) BeanMapUtil.mapToBean(wrappedMap, classC);

        this.run(context, config);
    }

    /**
     * 执行具体任务
     * @param context
     * @param config
     */
    protected abstract void run(JobExecutionContext context, C config);

    protected abstract Object getSonObject();

    protected void log(String message) {
        log(message, ConsoleLogger.Level.INFO);
    }
    protected void log(String message, ConsoleLogger.Level level) {
        message = "&9" + context.getJobDetail().getKey().getName() + " >> &7" + message;
        AesopPlugin.logger.log(message, level);
    }

}
