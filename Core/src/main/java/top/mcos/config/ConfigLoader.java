package top.mcos.config;

import com.epicnicity322.epicpluginlib.core.logger.ConsoleLogger;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.bukkit.configuration.ConfigurationSection;
import top.mcos.AesopPlugin;
import top.mcos.message.PositionTypeEnum;

import java.text.ParseException;
import java.util.*;

public class ConfigLoader {
    public static boolean notice_actionbar_enabled = true;
    public static long delay_times = 280;
    public static long trylock_times = 60000;
    public static int display_width = 20;
    public static List<NoticeMessageConfig> noticeMessageConfigs = new ArrayList<>();

    public static synchronized void reload() {
        //重新读取配置
        AesopPlugin.getInstance().reloadConfig();
        loadNoticeMessages();
        loadNoticeConfig();
    }

    public static synchronized void loadNoticeConfig() {
        long delayTimes = AesopPlugin.getInstance().getConfig().getLong("config.notice.actionbar.delay-times");
        delay_times = delayTimes <= 0 ? 100 : delayTimes;

        long tryLockTimes = AesopPlugin.getInstance().getConfig().getLong("config.notice.actionbar.trylock-times");
        trylock_times = tryLockTimes <= 0 ? 60000 : tryLockTimes;

        notice_actionbar_enabled = AesopPlugin.getInstance().getConfig().getBoolean("config.notice.actionbar.enable");

        display_width = AesopPlugin.getInstance().getConfig().getInt("config.notice.actionbar.display-width");
    }

    /**
     * 加载消息配置
     */
    public static synchronized void loadNoticeMessages() {
        if(!notice_actionbar_enabled) return;
        noticeMessageConfigs.clear();
        // 加载配置，注册定时任务，注入数据
        Map<String, Object> msgMap = AesopPlugin.getInstance().getConfig().getConfigurationSection("tasks.notice").getValues(false);
        msgMap.forEach((key,v)->{
            ConfigurationSection section = (ConfigurationSection)v;
            boolean enable = section.getBoolean("enable");
            String cron = section.getString("cron");
            String startStr = section.getString("start");
            Date start = null;
            if (StringUtils.isNotBlank(startStr)) {
                try {
                    start = DateUtils.parseDate(startStr, "yyyy-MM-dd HH:mm:ss");
                } catch (ParseException e) {
                    AesopPlugin.logger.log("start日期【" + startStr + "】转换出错，格式有误！", ConsoleLogger.Level.ERROR);
                }
            }
            String endStr = section.getString("end");
            Date end = null;
            if (StringUtils.isNotBlank(endStr)) {
                try {
                    end = DateUtils.parseDate(endStr, "yyyy-MM-dd HH:mm:ss");
                } catch (ParseException e) {
                    AesopPlugin.logger.log("end日期【" + endStr + "】转换出错，格式有误！", ConsoleLogger.Level.ERROR);
                }
            }
            String position = section.getString("position");
            String message = section.getString("message");
            String subMessage = section.getString("subMessage");
            Map<String, Object> jobParams = new HashMap<>();
            jobParams.put("position", position);
            jobParams.put("message", message);
            jobParams.put("subMessage", subMessage);

            NoticeMessageConfig msg = new NoticeMessageConfig();
            msg.setTaskKey(key);
            msg.setEnable(enable);
            msg.setCron(cron);
            msg.setStart(start);
            msg.setEnd(end);
            msg.setPositionType(PositionTypeEnum.valueOf(position));
            msg.setMessage(message);
            msg.setSubMessage(subMessage);
            noticeMessageConfigs.add(msg);
        });
    }

    //public static boolean noticeActionbarEnabled() {
    //    return AesopPlugin.getInstance().getConfig().getBoolean("config.notice.actionbar.enable");
    //}
}
