package top.mcos.scheduler;

import java.util.Date;

public interface JobConfig {
    String getKeyPrefix();
    String getKey();
    boolean isEnable();
    String getCron();
    Date getStart();
    Date getEnd();
    Class<? extends AbstractJob> getJobClass();
    void changeEnable(boolean isEnable);

}
