package top.mcos;

import com.epicnicity322.epicpluginlib.bukkit.logger.Logger;
import com.epicnicity322.epicpluginlib.core.logger.ConsoleLogger;
import de.slikey.effectlib.EffectManager;
import org.bukkit.Bukkit;
import top.mcos.config.activitiy.NSKeys;
import top.mcos.hook.HookHandler;
import top.mcos.command.CommandLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mcos.config.ConfigLoader;
import top.mcos.listener.EntityDamageListener;
import top.mcos.listener.PlayerListener;
import top.mcos.message.MessageHandler;
import top.mcos.nms.spi.NmsBuilder;
import top.mcos.nms.spi.NmsProvider;
import top.mcos.scheduler.SchedulerHandler;

import java.util.HashSet;
import java.util.ServiceLoader;

public final class AesopPlugin extends JavaPlugin {
    private static AesopPlugin instance;
    private static boolean pluginActive;
    /**
     * 粒子特效管理器
     */
    private static EffectManager effectManager;
    public static @Nullable HashSet<Runnable> onInstance;
    public static @Nullable NmsProvider nmsProvider;

    public static final @NotNull Logger logger = new Logger("&6[&b伊索插件&6]&f ");

    public AesopPlugin() {
        instance = this;

        if (onInstance == null) return;
        for (Runnable runnable : onInstance) {
            try {
                runnable.run();
            } catch (Throwable e) {
                logger.log("&c异步任务初始化失败.", ConsoleLogger.Level.ERROR);
            }
        }

    }

    @Override
    public void onEnable() {
        pluginActive = true;
        // 加载粒子特效支持库
        effectManager = new EffectManager(AesopPlugin.getInstance());

        // 简易命令注册示例
        //PluginCommand msg = this.getCommand("msg");
        //msg.setExecutor(new CommandMsg());
        //msg.setTabCompleter((sender, command, label, args) -> {
        //    List<String> list = new ArrayList<>();
        //    list.add("first");
        //    list.add("second");
        //    return list;
        //});

        // 获得版本
        final String packageName = Bukkit.getServer().getClass().getPackage().getName();
        String nmsVersion = packageName.substring(packageName.lastIndexOf('.') + 1);
        logger.log("当前服务器Minecraft版本：" + nmsVersion);
        ServiceLoader<NmsBuilder> builders = ServiceLoader.load(NmsBuilder.class, getClassLoader());
        for (NmsBuilder builder : builders) {
            if (builder.checked(nmsVersion)) {
                nmsProvider = builder.build();
                logger.log("成功加载NMS");
                break;
            }
        }

        // 若配置文件不存在，自动根据resources/config.yml创建配置文件放置数据目录（/plugin/myplugin/config.yml）
        this.saveDefaultConfig();
        // 加载配置数据
        ConfigLoader.load();
        NSKeys.init(this);
        //注册监听
        getServer().getPluginManager().registerEvents(new PlayerListener(), AesopPlugin.getInstance());
        getServer().getPluginManager().registerEvents(new EntityDamageListener(), AesopPlugin.getInstance());
        //加载命令
        CommandLoader.getCommands();
        // 启动监听消息队列，有消息，则发送
        MessageHandler.initQueue();
        // 启动任务调度器监听任务
        SchedulerHandler.init();
        // 初始化第三方插件挂钩
        HookHandler.init();
        // 注册任务
        SchedulerHandler.registerJobs();

        logger.log("&a成功加载插件");
    }

    @Override
    public void onDisable() {
        pluginActive = false;
        SchedulerHandler.shutdown();
        MessageHandler.clearQueue();
        MessageHandler.setSendBreak(true);
        logger.log("&c插件已卸载");
    }

    /**
     * 启用线程去执行一个程序。如果插件已加载，则可立即运行。
     * 参数：
     * @param runnable 可在负载上运行。
     */
    public static void onInstance(@NotNull Runnable runnable) {
        if (getInstance() != null) {
            try {
                runnable.run();
            } catch (Throwable t) {
                logger.log("&c插件实例未成功初始化！", ConsoleLogger.Level.ERROR);
            }
        } else {
            logger.log("&c当前任务已加入线程池，待插件重新初始化后加载！", ConsoleLogger.Level.ERROR);
            if (onInstance == null) onInstance = new HashSet<>();
            onInstance.add(runnable);
        }
    }


    public static AesopPlugin getInstance() {
        return instance;
    }

    public static boolean isPluginActive() {
        return pluginActive;
    }

    public static EffectManager getEffectManager() {
        return effectManager;
    }
}
