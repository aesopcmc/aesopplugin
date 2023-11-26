package top.mcos.scheduler.jobs;

import com.epicnicity322.epicpluginlib.core.logger.ConsoleLogger;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.popcraft.chunky.api.ChunkyAPI;
import org.quartz.*;
import top.mcos.AesopPlugin;
import top.mcos.config.configs.RegenWorldConfig;
import top.mcos.hook.HookHandler;
import top.mcos.scheduler.AbstractJob;
import top.mcos.util.BeanMapUtil;

import java.util.List;
import java.util.Map;

@DisallowConcurrentExecution
public class RegenWorldJob extends AbstractJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        Map<String, Object> wrappedMap = jobDataMap.getWrappedMap();
        try {
            RegenWorldConfig config = BeanMapUtil.mapToBean(wrappedMap, RegenWorldConfig.class);
            List<String> afterRunCommands = config.getAfterRunCommands();
            /*
            重置世界
             */
            // mvWorldManager.regenWorld();需要在同步环境中执行，故此使用bukkit的同步任务方法runTask执行
            Bukkit.getScheduler().runTask(AesopPlugin.getInstance(), ()->{
                MultiverseCore core = HookHandler.getMultiverseProvider().getAPI();
                MVWorldManager mvWorldManager = core.getMVWorldManager();
                //long seed = (new Random()).nextLong();
                log(context, "正在重置世界【"+config.getWorld()+"】");
                mvWorldManager.regenWorld(config.getWorld(), config.getNewSeed(), config.getRandomSeed(), config.getSeed(), config.getKeepGameRules());
                log(context, "世界【"+config.getWorld()+"】已重置完成");


                /*
                执行区块加载
                 */
                if(config.getAfterLoadChunky()) {
                    log(context, "开始执行区块加载【"+config.getWorld()+"】");
                    ChunkyAPI chunky = HookHandler.getChunkyProvider().getAPI();
                    chunky.startTask(config.getWorld(),
                            "square",
                            0, 0,
                            config.getAfterLoadChunkyRadius(),
                            config.getAfterLoadChunkyRadius(),
                            "concentric");
                }

                /*
                执行后续命令
                 */
                if(afterRunCommands!=null && afterRunCommands.size()>0) {
                    for (String cmdline : afterRunCommands) {
                        log(context, "执行指令：" + cmdline);
                        // 获取控制台身份，以控制台身份执行指令
                        ConsoleCommandSender consoleSender = Bukkit.getServer().getConsoleSender();
                        Bukkit.getServer().dispatchCommand(consoleSender, cmdline);
                    }
                }
            });

        } catch (Throwable e) {
            e.printStackTrace();
            log(context, "执行任务出错", ConsoleLogger.Level.ERROR);
        }

    }
}
