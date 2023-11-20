package top.mcos.scheduler;

import org.quartz.*;
import top.mcos.AesopPlugin;
import top.mcos.message.MessageHandler;

import java.util.Optional;

/**
 * 发送通知任务
 */
//设定的时间间隔为3秒,但job执行时间是5秒,设置@DisallowConcurrentExecution以后程序会等任务执行完毕以后再去执行,否则会在3秒时再启用新的线程执行
@DisallowConcurrentExecution
public class DemoJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("执行任务。。");
    }
}
