package top.mcos.config;

import com.epicnicity322.epicpluginlib.core.logger.ConsoleLogger;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.bukkit.configuration.ConfigurationSection;
import top.mcos.AesopPlugin;
import top.mcos.config.configs.CommonConfig;
import top.mcos.config.configs.NoticeMessageConfig;
import top.mcos.config.configs.RegenWorldConfig;
import top.mcos.message.PositionTypeEnum;

import java.text.ParseException;
import java.util.*;

public class ConfigLoader {
    public static CommonConfig commonConfig = new CommonConfig();
    public static List<NoticeMessageConfig> noticeMessageConfigs = new ArrayList<>();
    public static List<RegenWorldConfig> regenWorldConfigs = new ArrayList<>();

    public static synchronized void reload() {
        //重新读取配置
        AesopPlugin.getInstance().reloadConfig();
        loadCommonConfigs();
        loadRegenWorlds();
        loadNoticeMessages();
    }

    public static synchronized void loadCommonConfigs() {
        commonConfig.setChunkyLoadingNoticeEnable(AesopPlugin.getInstance().getConfig().getBoolean("config.regen-world.chunky-loading-notice.enable"));
        commonConfig.setChunkyLoadingNoticeMessage(AesopPlugin.getInstance().getConfig().getString("config.regen-world.chunky-loading-notice.message"));
        commonConfig.setChunkyLoadingNoticeDelay(AesopPlugin.getInstance().getConfig().getLong("config.regen-world.chunky-loading-notice.delay"));
        commonConfig.setDelayTimes(AesopPlugin.getInstance().getConfig().getLong("config.notice.actionbar.delay-times"));
        commonConfig.setTrylockTimes(AesopPlugin.getInstance().getConfig().getLong("config.notice.actionbar.trylock-times"));
        commonConfig.setNoticeActionbarEnabled(AesopPlugin.getInstance().getConfig().getBoolean("config.notice.actionbar.enable"));
        commonConfig.setDisplayWidth(AesopPlugin.getInstance().getConfig().getInt("config.notice.actionbar.display-width"));
        commonConfig.setNoticeTitleEnabled(AesopPlugin.getInstance().getConfig().getBoolean("config.notice.title.enable"));
        commonConfig.setNoticeTitleFadein(AesopPlugin.getInstance().getConfig().getInt("config.notice.title.fadein"));
        commonConfig.setNoticeTitleKeep(AesopPlugin.getInstance().getConfig().getInt("config.notice.title.keep"));
        commonConfig.setNoticeTitleFadeout(AesopPlugin.getInstance().getConfig().getInt("config.notice.title.fadeout"));
    }

    private static synchronized void loadRegenWorlds() {
        regenWorldConfigs.clear();
        Map<String, Object> worlds = AesopPlugin.getInstance().getConfig().getConfigurationSection("tasks.regen-world").getValues(false);
        worlds.forEach((key,value)->{
            try {
                ConfigurationSection section = (ConfigurationSection)value;
                boolean enable = section.getBoolean("enable");
                String cron = section.getString("cron");
                boolean newSeed = section.getBoolean("new-seed");
                boolean randomSeed = section.getBoolean("random-seed");
                String seed = section.getString("seed");
                boolean keepGameRules = section.getBoolean("keep-game-rules");
                boolean afterLoadChunky = section.getBoolean("after-load-chunky");
                double afterLoadChunkyRadius = section.getDouble("after-load-chunky-radius");
                String afterNoticeKey = section.getString("after-notice-key");
                List<String> afterRunCommands = section.getStringList("after-run-commands");
                RegenWorldConfig config = new RegenWorldConfig();
                config.setWorld(key);
                config.setEnable(enable);
                config.setCron(cron);
                config.setNewSeed(newSeed);
                config.setRandomSeed(randomSeed);
                config.setSeed(seed);
                config.setKeepGameRules(keepGameRules);
                config.setAfterLoadChunky(afterLoadChunky);
                config.setAfterLoadChunkyRadius(afterLoadChunkyRadius);
                config.setAfterNoticeKey(afterNoticeKey);
                config.setAfterRunCommands(afterRunCommands);
                regenWorldConfigs.add(config);
            }catch (Exception e) {
                e.printStackTrace();
                AesopPlugin.logger.log(key + "配置加载出错，已跳过");
            }
        });
    }

    /**
     * 加载消息配置
     */
    public static synchronized void loadNoticeMessages() {
        noticeMessageConfigs.clear();
        // 加载配置，注册定时任务，注入数据
        Map<String, Object> msgMap = AesopPlugin.getInstance().getConfig().getConfigurationSection("tasks.notice").getValues(false);
        msgMap.forEach((key,value)->{
            try {
                ConfigurationSection section = (ConfigurationSection) value;
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
                String subMessage = section.getString("sub-message");
                NoticeMessageConfig msg = new NoticeMessageConfig();
                msg.setKey(key);
                msg.setEnable(enable);
                msg.setCron(cron);
                msg.setStart(start);
                msg.setEnd(end);
                msg.setPositionType(PositionTypeEnum.valueOf(position));
                msg.setMessage(message);
                msg.setSubMessage(subMessage);
                noticeMessageConfigs.add(msg);
            }catch (Exception e) {
                e.printStackTrace();
                AesopPlugin.logger.log(key + "消息配置加载出错，已跳过");
            }
        });
    }
}
