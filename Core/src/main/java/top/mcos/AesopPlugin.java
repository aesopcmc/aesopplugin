package top.mcos;

import com.epicnicity322.epicpluginlib.bukkit.logger.Logger;
import com.epicnicity322.epicpluginlib.core.logger.ConsoleLogger;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;
import org.bukkit.Bukkit;
import top.mcos.business.activity.christmas.NSKeys;
import top.mcos.business.yanhua.YanHuaEvent;
import top.mcos.database.config.SqliteDatabase;
import top.mcos.hook.HookHandler;
import top.mcos.command.CommandLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mcos.config.ConfigLoader;
import top.mcos.hook.firework.FireWorkManage;
import top.mcos.listener.PlayerListener;
import top.mcos.message.MessageHandler;
import top.mcos.nms.spi.NmsBuilder;
import top.mcos.nms.spi.NmsProvider;
import top.mcos.scheduler.SchedulerHandler;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.concurrent.Callable;

public final class AesopPlugin extends JavaPlugin {
    private static AesopPlugin instance;

    public static @Nullable HashSet<Runnable> onInstance;
    public static @Nullable NmsProvider nmsProvider;

    public static final @NotNull Logger logger = new Logger("&b&l伊AE&3&l索SOP&6 >>&f ");

    /**
     * 同步标记，使用示例：
     * synchronized (AesopPlugin.sync.intern(“唯一字符串”)) {
     *     do something...
     * }
     */
    public static final Interner<String> sync = Interners.newWeakInterner();
    private boolean pluginActive;

    /**
     * 数据库实例
     */
    private SqliteDatabase database;

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

        // 加载配置数据
        ConfigLoader.load(null);

        NSKeys.init(this);

        //System.out.println("插件目录："+getDataFolder());
        database = new SqliteDatabase(getDataFolder().getPath());

        //注册监听
        getServer().getPluginManager().registerEvents(new PlayerListener(), AesopPlugin.getInstance());
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

        // 注册粒子特效
        FireWorkManage.load();

        // 启动烟花监听
        YanHuaEvent.onFireListen();

        logger.log("&a成功加载插件");
    }

    @Override
    public void onDisable() {
        pluginActive = false;
        SchedulerHandler.shutdown();
        MessageHandler.clearQueue();
        YanHuaEvent.clearQueue();
        MessageHandler.setSendBreak(true);
        FireWorkManage.getInstance().clear();
        logger.log("&c插件已卸载");
    }

    public SqliteDatabase getDatabase() {
        return database;
    }

    public boolean isPluginActive() {
        return pluginActive;
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



    /**
     * 使用数据库事务处理业务逻辑
     * @param callable 业务逻辑回调
     * @throws SQLException sql异常
     */
    public static void callInTransaction(Callable<?> callable) throws SQLException {
        ConnectionSource connectionSource = getInstance().getDatabase().getConnectionSource();
        TransactionManager.callInTransaction(connectionSource, callable);
    }
}
