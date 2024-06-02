package top.mcos.scheduler.jobs;

import top.mcos.util.epiclib.logger.ConsoleLogger;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.popcraft.chunky.api.ChunkyAPI;
import org.quartz.*;
import top.mcos.AesopPlugin;
import top.mcos.config.ConfigLoader;
import top.mcos.config.configs.subconfig.NoticeConfig;
import top.mcos.config.configs.subconfig.RegenWorldConfig;
import top.mcos.hook.HookHandler;
import top.mcos.hook.providers.ChunkyProvider;
import top.mcos.hook.providers.MultiverseProvider;
import top.mcos.scheduler.AbstractJob;
import top.mcos.scheduler.SchedulerHandler;
import top.mcos.util.BeanMapUtil;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//@DisallowConcurrentExecution
public class RegenWorldJob extends AbstractJob<RegenWorldConfig> {
    @Override
    protected void run(JobExecutionContext context, RegenWorldConfig config) {
        try {
            List<String> afterRunCommands = config.getLoadedRunCommands();
            /*
            重置世界
             */
            MultiverseProvider multiverseProvider = HookHandler.multiverseProvider;
            if(!multiverseProvider.isLoaded()) {
                // 未检测到多世界插件，已跳过世界重置
                log("未检测到MultiverseCore插件，已跳过世界重置");
                return;
            }
            // mvWorldManager.regenWorld();需要在同步环境中执行，故此使用bukkit的同步任务方法runTask执行
            Bukkit.getScheduler().runTask(AesopPlugin.getInstance(), ()->{
                /*
                重新生成世界
                 */
                MultiverseCore core = multiverseProvider.getAPI();
                MVWorldManager mvWorldManager = core.getMVWorldManager();
                String aliasWorldName = mvWorldManager.getMVWorld(config.getKey()).getAlias();
                //long seed = (new Random()).nextLong();
                log("正在重置世界【"+aliasWorldName+"】");
                mvWorldManager.regenWorld(config.getKey(), config.isNewSeed(), config.isRandomSeed(), config.getSeed(), config.isKeepGameRules());
                log("世界【"+aliasWorldName+"】已重置完成");

                /*
                发送滚动消息提醒
                 */
                String afterNoticeKey = config.getLoadedNoticeKey();
                if(StringUtils.isNotBlank(afterNoticeKey)) {
                    Map<String, NoticeConfig> noticeConfigMap = ConfigLoader.baseConfig.getNoticeConfigs()
                            .stream().collect(Collectors.toMap(NoticeConfig::getKey, c -> c));
                    // 获取消息通知配置
                    NoticeConfig noticeConfig = noticeConfigMap.get(afterNoticeKey);
                    // 取消注册消息
                    SchedulerHandler.unRegisterJob(noticeConfig);
                    // 更改配置
                    noticeConfig.setEnable(true);
                    String message = config.getLoadedNoticeMessage().replace("{world-name}", aliasWorldName);
                    noticeConfig.setMessage(message);
                    noticeConfig.setStart(DateUtils.addHours(new Date(), config.getLoadedNoticeDelayHours()));
                    noticeConfig.setEnd(DateUtils.addHours(noticeConfig.getStart(), config.getLoadedNoticeKeepHours()));
                    // 保存配置
                    ConfigLoader.saveConfig(noticeConfig);
                    // 重新注册消息
                    SchedulerHandler.registerJob(noticeConfig);
                }

                /*
                执行区块加载
                 */
                if(config.isLoadedChunky()) {
                    ChunkyProvider chunkyProvider = HookHandler.chunkyProvider;
                    if(chunkyProvider.isLoaded()) {
                        ChunkyAPI chunky = chunkyProvider.getAPI();
                        chunky.cancelTask(config.getKey());
                        // 调用异步任务开启区块预加载
                        chunky.startTask(config.getKey(),
                                "square",
                                0, 0,
                                config.getLoadedChunkyRadius(),
                                config.getLoadedChunkyRadius(),
                                "concentric");
                        log("开始执行区块加载【"+config.getKey()+"】");
                    }else {
                        // 未检测到区块加载插件，已跳过区块加载
                        log("未检测到Chunky插件，已跳过区块加载");
                    }
                }

                /*
                执行后续命令
                 */
                if(afterRunCommands!=null && afterRunCommands.size()>0) {
                    for (String cmdline : afterRunCommands) {
                        log("执行指令：" + cmdline);
                        // 获取控制台身份，以控制台身份执行指令
                        ConsoleCommandSender consoleSender = Bukkit.getServer().getConsoleSender();
                        Bukkit.getServer().dispatchCommand(consoleSender, cmdline);
                    }
                }
            });

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
