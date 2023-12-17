package top.mcos.scheduler.jobs;

import com.epicnicity322.epicpluginlib.core.logger.ConsoleLogger;
import de.slikey.effectlib.effect.TextEffect;
import de.slikey.effectlib.util.DynamicLocation;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import top.mcos.AesopPlugin;
import top.mcos.command.subcommands.MsgSubCommand;
import top.mcos.config.configs.CommandConfig;
import top.mcos.config.configs.FireworkConfig;
import top.mcos.scheduler.AbstractJob;
import top.mcos.scheduler.SchedulerHandler;
import top.mcos.util.BeanMapUtil;

import java.awt.*;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 执行控制台指令任务
 */
//设定的时间间隔为3秒,但job执行时间是5秒,设置@DisallowConcurrentExecution以后程序会等任务执行完毕以后再去执行,否则会在3秒时再启用新的线程执行
//@DisallowConcurrentExecution
public class FireworkJob extends AbstractJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            log(context, "执行任务");
            JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
            Map<String, Object> wrappedMap = jobDataMap.getWrappedMap();
            FireworkConfig config = BeanMapUtil.mapToBean(wrappedMap, FireworkConfig.class);
            if(!config.isEnable()) return;

            Particle particle = Particle.valueOf(config.getParticle());

            String textLocation = config.getTextLocation();
            String[] textArray = textLocation.split(",");
            String worldName = textArray[0];
            double x = Double.parseDouble(textArray[1]);
            double y = Double.parseDouble(textArray[2]);
            double z = Double.parseDouble(textArray[3]);
            float xr = Float.parseFloat(textArray[4]);
            float yr = Float.parseFloat(textArray[5]);

            String subtextLocation = config.getSubtextLocation();
            String[] subtextArray = subtextLocation.split(",");
            String worldName2 = subtextArray[0];
            double x2 = Double.parseDouble(subtextArray[1]);
            double y2 = Double.parseDouble(subtextArray[2]);
            double z2 = Double.parseDouble(subtextArray[3]);
            float xr2 = Float.parseFloat(subtextArray[4]);
            float yr2 = Float.parseFloat(subtextArray[5]);

            InputStream fi = MsgSubCommand.class.getClassLoader().getResourceAsStream("font/DouyinSansBold.ttf");
            InputStream fi2 = MsgSubCommand.class.getClassLoader().getResourceAsStream("font/zool-addict-Italic-02.ttf");
            //Font font = Font.createFont(Font.PLAIN, fi);
            //font = font.deriveFont(Font.PLAIN, 25);

            TextEffect effect1 = new TextEffect(AesopPlugin.getEffectManager());
            // 设置位置
            effect1.setDynamicOrigin(new DynamicLocation(new Location(Bukkit.getWorld(worldName), x, y, z, xr, yr)));
            // 设置粒子特效（暂时只能选择不需要特效数据的）
            effect1.particle = particle;
            // 设置文本
            effect1.text = config.getText();
            effect1.color = Color.GREEN;
            // 时间间隔，数值越小，显示越快
            effect1.period = 10;
            effect1.setFont(Font.createFont(Font.PLAIN, fi).deriveFont(Font.PLAIN, config.getTextSize()));
            effect1.start();

            TextEffect effect2 = new TextEffect(AesopPlugin.getEffectManager());
            // 设置位置
            effect2.setDynamicOrigin(new DynamicLocation(new Location(Bukkit.getWorld(worldName2), x2, y2, z2, xr2, yr2)));
            // 设置粒子特效（暂时只能选择不需要特效数据的）
            effect2.particle = particle;
            // 设置文本
            effect2.text = config.getSubtext();
            effect2.color = Color.RED;
            // 时间间隔，数值越小，显示越快
            effect2.period = 10;
            //effect2.setFont(new Font("DejaVu Sans", Font.PLAIN, (int) subtextSize));
            effect2.setFont(Font.createFont(Font.PLAIN, fi2).deriveFont(Font.PLAIN, config.getSubtextSize()));
            effect2.start();
        }catch (Throwable e) {
            e.printStackTrace();
            log(context, "执行任务出错", ConsoleLogger.Level.ERROR);
        }
    }

    public static void registerJob(FireworkConfig config) {
        String jobName = "firework-"+config.getKey()+"-task";
        String groupName = "fireworkGroup";
        try {
            Map<String, Object> jobParams = BeanMapUtil.beanToMap(config);
            SchedulerHandler.registerJob(FireworkJob.class, jobName, groupName, config.getStart(),
                    config.getEnd(), config.getCron(), jobParams);
        } catch (Exception e) {
            e.printStackTrace();
            AesopPlugin.logger.log("定时任务【"+jobName+"】激活失败，已跳过", ConsoleLogger.Level.ERROR);
        }
    }

    public static void unRegisterJob(FireworkConfig config) {
        String jobName = "firework-"+config.getKey()+"-task";
        String groupName = "fireworkGroup";
        SchedulerHandler.unRegisterJob(jobName, groupName);
    }

}
