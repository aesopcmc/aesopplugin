package top.mcos.config;

import top.mcos.message.PositionTypeEnum;

import java.util.Date;

public class NoticeMessageConfig {
    private String taskKey;
    private Boolean enable;
    private String cron;
    private Date start;
    private Date end;
    private PositionTypeEnum positionType;
    private String message;
    private String subMessage;

    public String getTaskKey() {
        return taskKey;
    }

    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public PositionTypeEnum getPositionType() {
        return positionType;
    }

    public void setPositionType(PositionTypeEnum positionType) {
        this.positionType = positionType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSubMessage() {
        return subMessage;
    }

    public void setSubMessage(String subMessage) {
        this.subMessage = subMessage;
    }
}
