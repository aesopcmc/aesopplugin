package top.mcos.hook.firework;

import com.epicnicity322.epicpluginlib.core.logger.ConsoleLogger;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.TextEffect;
import de.slikey.effectlib.util.DynamicLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import top.mcos.AesopPlugin;
import top.mcos.config.ConfigLoader;
import top.mcos.config.configs.subconfig.FireworkConfig;
import top.mcos.config.configs.subconfig.LocationFireworkGroupConfig;
import top.mcos.config.configs.subconfig.PlayerFireworkGroupConfig;
import top.mcos.config.configs.subconfig.TextFireworkConfig;
import top.mcos.database.dao.PlayerFireworkDao;
import top.mcos.database.domain.PlayerFirework;
import top.mcos.util.MessageUtil;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class FireWorkManage {
    //private byte[] fontBytes;
    private static FireWorkManage instance;
    private final EffectManager effectManager;

    private final Map<String, TextEffect> textEffectMap = new HashMap<>();
    /**
     * 所有玩家当前激活的粒子特效，从数据库取出数据缓存，
     */
    private final Map<String, List<PlayerFirework>> playerFireworkCache = new HashMap<>();

    private FireWorkManage() {
        //InputStream fi = FireWorkManage.class.getClassLoader().getResourceAsStream("font/zfxkft_aigei_com.ttf");
        //try {
        //    fontBytes = getBytes(fi);
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}

        this.effectManager = new EffectManager(AesopPlugin.getInstance());
        reload();
        // 监听用户粒子特效生成
        startPlayerListener();
        // 监听固定位置粒子特效生成
        startLocationListener();
    }

    public static void load() {
        if (instance ==null) {
            try {
                instance = new FireWorkManage();
            } catch (Exception e) {
                AesopPlugin.logger.log("&c粒子特效加载失败，已跳过", ConsoleLogger.Level.ERROR);
            }
        }
    }
    public static FireWorkManage getInstance() {
        if (instance ==null) {
            load();
        }
        return instance;
    }

    //private byte[] getBytes(InputStream ins) throws IOException {
    //    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    //    byte[] buffer = new byte[1024];
    //    int len;
    //    while ((len = ins.read(buffer)) > -1 ) {
    //        baos.write(buffer, 0, len);
    //    }
    //    baos.flush();
    //    return baos.toByteArray();
    //}

    public synchronized void reload() {
        unLoadEffect(null);
        for (FireworkConfig config : ConfigLoader.baseConfig.getFireworkConfigs()) {
            try {
                loadEffect(config);
                //TimeUnit.MILLISECONDS.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
                AesopPlugin.logger.log("读取粒子特效 '"+config.getKey()+"' 出错", ConsoleLogger.Level.ERROR);
            }
        }
        AesopPlugin.logger.log("粒子特效加载完成");
    }
    public synchronized void clear() {
        unLoadEffect(null);
    }

    // 定时显示玩家粒子特效组
    private void startPlayerListener() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(AesopPlugin.getInstance(), ()->{
            Map<String, TextFireworkConfig> fireworkKeys = ConfigLoader.fwConfig.getTextFireworks().stream().collect(Collectors.toMap(TextFireworkConfig::getKey, c -> c));
            Map<String, PlayerFireworkGroupConfig> groupKeys = ConfigLoader.fwConfig.getPlayerFireworkGroups().stream().collect(Collectors.toMap(PlayerFireworkGroupConfig::getKey, c -> c));

            Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
            for (Player player : onlinePlayers) {
                List<PlayerFirework> playerFireworkList = playerFireworkCache.get(player.getUniqueId().toString());
                if(playerFireworkList!=null) {
                    for (PlayerFirework playerFirework : playerFireworkList) {
                        Integer enable = playerFirework.getEnable();
                        String groupKey = playerFirework.getPlayerFireworkGroupKey();
                        if (enable == 1) {
                            PlayerFireworkGroupConfig groupConfig = groupKeys.get(groupKey);
                            if (groupConfig!=null && groupConfig.isEnable()) {
                                List<String> pfwKeys = groupConfig.getFireworkKeys();
                                for (String pfwKey : pfwKeys) {
                                    this.spawnPlayerTextEffect(fireworkKeys.get(pfwKey), player, groupConfig.getOffsetY());
                                }
                            }
                        }
                    }
                }
            }
            // TODO period决定粒子生成频率，单位tick
        }, 100, 200);
        AesopPlugin.logger.log("已启动玩家粒子特效监控");
    }

    // 定时显示固定位置粒子组
    private void startLocationListener() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(AesopPlugin.getInstance(), ()->{
            Map<String, TextFireworkConfig> fireworkKeys = ConfigLoader.fwConfig.getTextFireworks().stream().collect(Collectors.toMap(TextFireworkConfig::getKey, c -> c));
            List<LocationFireworkGroupConfig> groupConfigs = ConfigLoader.fwConfig.getLocationFireworkGroups();

            for (LocationFireworkGroupConfig groupConfig : groupConfigs) {
                List<String> keys = groupConfig.getFireworkKeys();
                for (String key : keys) {
                    this.spawnLocationTextEffect(fireworkKeys.get(key), groupConfig.getLocation());
                }
            }
            // TODO period决定粒子生成频率，单位tick
        }, 100, 100);
        AesopPlugin.logger.log("已启动固定位置粒子特效监控");
    }

    private void loadEffect(FireworkConfig config) throws IOException, FontFormatException {
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

        InputStream fi;
        if(MessageUtil.hasChineseChar(config.getText())) {
            fi = FireWorkManage.class.getClassLoader().getResourceAsStream("font/DouyinSansBold.ttf");
        } else {
            fi = FireWorkManage.class.getClassLoader().getResourceAsStream("font/zool-addict-Italic-02.ttf");
        }
        TextEffect effect = new TextEffect(effectManager);
        // 设置位置
        effect.setDynamicOrigin(new DynamicLocation(new Location(Bukkit.getWorld(worldName), x, y, z, xr, yr)));
        // 设置粒子特效（暂时只能选择不需要特效数据的）
        effect.particle = particle;
        // 设置文本
        effect.text = config.getText();
        //effect.color = Color.GREEN;
        // 时间间隔，数值越小，显示越快
        effect.period = config.getPeriod();
        effect.duration = config.getDuration()<=0 ? null : config.getDuration();// 持续时间，持续时间结束后特效消失（当持续时间非空时，优先级会比iterations高）
        effect.iterations = config.getDuration()<=0 ? -1 : 0; // -1永久显示 0默认
        effect.setFont(Font.createFont(Font.PLAIN, fi).deriveFont(Font.PLAIN, config.getTextSize()));
        effect.start();
        textEffectMap.put(config.getKey(), effect);
    }

    private void unLoadEffect(String key) {
        if(key==null) {
            Collection<TextEffect> effects = textEffectMap.values();
            for (TextEffect effect : effects) {
                effect.cancel();
            }
            textEffectMap.clear();
        } else {
            textEffectMap.get(key).cancel();
            textEffectMap.remove(key);
        }
    }

    /**
     * 生成玩家文字粒子
     *
     * @param config       配置
     * @param player       玩家
     * @param groupOffsetY 组Y轴偏移量
     */
    private void spawnPlayerTextEffect(TextFireworkConfig config, Player player, double groupOffsetY) {
        if(config==null || !config.isEnable()) return;
        // 设置位置
        Location location = player.getLocation().add(0, config.getOffsetY() + groupOffsetY, 0);
        if(config.isInverted()) {
            float yaw = location.getYaw();
            yaw = yaw > 0 ? yaw - 180 : yaw + 180;
            location.setYaw(yaw);
        }
        DynamicLocation dynamicLocation = new DynamicLocation(location);
        String particles = config.getParticle();
        String[] split = particles.split(",");
        for (String part : split) {
            spawnTextEffect(config.getText(), config.getTextSize(), part, config.getPeriod(), dynamicLocation, config.getDuration());
        }
    }

    /**
     * 生成固定位置文字粒子
     *
     * @param config         配置
     * @param groupLocation  位置 ，例如：world,17,78,30,0,0， 组成：世界名称,x,y,z,以X平面旋转角度,以Y平面旋转角度
     */
    private void spawnLocationTextEffect(TextFireworkConfig config, String groupLocation) {
        if(config==null || !config.isEnable()) return;
        // 设置位置
        String[] textArray = groupLocation.split(",");
        String worldName = textArray[0];
        double x = Double.parseDouble(textArray[1]);
        double y = Double.parseDouble(textArray[2]);
        double z = Double.parseDouble(textArray[3]);
        float xr = Float.parseFloat(textArray[4]);
        float yr = Float.parseFloat(textArray[5]);
        Location location = new Location(Bukkit.getWorld(worldName), x, y, z, xr, yr);
        // 是否旋转180°
        if(config.isInverted()) {
            float yaw = location.getYaw();
            yaw = yaw > 0 ? yaw - 180 : yaw + 180;
            location.setYaw(yaw);
        }
        DynamicLocation dynamicLocation = new DynamicLocation(location);
        String particles = config.getParticle();
        String[] split = particles.split(",");
        for (String part : split) {
            spawnTextEffect(config.getText(), config.getTextSize(), part, config.getPeriod(), dynamicLocation, config.getDuration());
        }
    }

    /**
     * 生成文字粒子特效
     * @param text 文本
     * @param textSize 文字大小
     * @param particle 粒子特效
     * @param period 显示速率
     * @param location 生成位置
     * @param duration 持续时间，单位tick
     */
    private void spawnTextEffect(String text, float textSize, String particle, int period, DynamicLocation location, long duration) {
        TextEffect effect = new TextEffect(effectManager);
        // 设置位置
        effect.setDynamicOrigin(location);
        //private final BiFunction<Player, Integer, Location> xForwardFromPlayer = (player, x) -> player.getLocation().add(0,2,0).add(player.getLocation().getDirection().multiply(x));
        //effect.setDynamicOrigin(new DynamicLocation(xForwardFromPlayer.apply(player, 9)));
        // 设置文本
        effect.text = text;
        // 设置粒子特效（暂时只能选择不需要特效数据的）
        effect.particle = Particle.valueOf(particle);
        //effect.pitch = 180f;
        // 时间间隔，数值越小，显示越快
        effect.period = period;
        effect.duration = null; //config.getDuration()<=0 ? null : config.getDuration();// 持续时间，持续时间结束后特效消失（当持续时间非空时，优先级会比iterations高）
        effect.iterations = -1;//config.getDuration()<=0 ? -1 : 0; // -1永久显示 0默认
        // TODO 自定义字体
        // 计算读取字体耗时
        //long start = System.currentTimeMillis();
        //InputStream fi = new ByteArrayInputStream(fontBytes);
        InputStream fi = FireWorkManage.class.getClassLoader().getResourceAsStream("font/zfxkft_aigei_com.ttf");
        try {
            effect.setFont(Font.createFont(Font.PLAIN, fi).deriveFont(Font.PLAIN, textSize));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //long end = System.currentTimeMillis();
        //System.out.println("耗时: " + ((end-start) / 1000.0) +"秒");

        effect.start();
        Bukkit.getScheduler().scheduleSyncDelayedTask(AesopPlugin.getInstance(), effect::cancel, duration);
    }

    public void preview(Player player, String effectName, Particle particle) {
        EffectsPreviewer object = new EffectsPreviewer(getEffectManager(), particle);

        Class<?> clazz = object.getClass();
        Method[] methods = clazz.getDeclaredMethods();

        // 获取key值
        for (Method me : methods) {
            me.setAccessible(true);
            if (me.isAnnotationPresent(CommandHook.class)) {
                try {
                    CommandHook annotation = me.getAnnotation(CommandHook.class);
                    String effectNameF = annotation.value();
                    if(effectNameF.equals(effectName)) {
                        me.invoke(object, player);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public EffectManager getEffectManager() {
        return effectManager;
    }

    /**
     * 从数据库加载玩家粒子特效到缓存
     * @param playerId
     */
    public void putPlayerFireworkToCache(String playerId) {
        PlayerFireworkDao playerFireworkDao = AesopPlugin.getInstance().getDatabase().getPlayerFireworkDao();
        List<PlayerFirework> playerFireworks = playerFireworkDao.queryGroupKeys(playerId, null);// 加载玩家所有的粒子特效groupkey
        if(playerFireworks==null || playerFireworks.size()==0) {
            playerFireworkCache.remove(playerId);
        } else {
            playerFireworkCache.put(playerId, playerFireworks);
        }
    }

    /**
     * 移除缓存中玩家的粒子特效
     * @param playerId
     */
    public void removePlayerFireworkFromCache(String playerId) {
        playerFireworkCache.remove(playerId);
    }

    public Map<String, List<PlayerFirework>> getPlayerFireworkCache() {
        return playerFireworkCache;
    }
}
