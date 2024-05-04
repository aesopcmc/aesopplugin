package top.mcos.scheduler;

import java.util.Date;

public interface JobConfig {
    /**
     * 任务key前缀，必须保证全局唯一
     */
    String getKeyPrefix();
    String getKey();
    boolean isEnable();
    String getCron();
    Date getStart();
    Date getEnd();

    /**
     * 定义需要执行该任务的类
     */
    Class<? extends AbstractJob> getJobClass();

    /**
     * 用于激活任务
     */
    void changeEnable(boolean isEnable);

}
