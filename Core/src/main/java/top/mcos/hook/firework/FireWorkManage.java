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
import top.mcos.config.configs.FireworkConfig;
import top.mcos.config.configs.PlayerFireworkConfig;
import top.mcos.util.MessageUtil;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class FireWorkManage {
    private final EffectManager effectManager;
    private List<FireworkConfig> fireworkConfigs;

    private List<PlayerFireworkConfig> playerFireworkConfigs;

    public Map<String, TextEffect> textEffectMap = new HashMap<>();

    public FireWorkManage(List<FireworkConfig> fireworkConfigs, List<PlayerFireworkConfig> playerFireworkConfigs) {
        this.effectManager = new EffectManager(AesopPlugin.getInstance());
        this.fireworkConfigs = fireworkConfigs;
        this.playerFireworkConfigs = playerFireworkConfigs;
        reload(fireworkConfigs, playerFireworkConfigs);
        // 监听用户粒子特效
        startPlayerListener();
    }

    private void startPlayerListener() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(AesopPlugin.getInstance(), ()->{
            if(playerFireworkConfigs!=null) {
                Map<String, PlayerFireworkConfig> keyConfigs = playerFireworkConfigs.stream().collect(Collectors.toMap(PlayerFireworkConfig::getKey, c -> c));
                Set<String> keys = keyConfigs.keySet();
                if(keys.size()>0) {
                    Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
                    for (Player player : onlinePlayers) {
                        for (String key : keys) {

                            //TODO OP自动获取了所有权限？？
                            if (player.hasPermission("aesopplugin.pfirework." + key)) {
                                this.spawnPlayerTextEffect(keyConfigs.get(key), player);
                            }
                        }
                    }
                }
            }
        }, 100, 100);
    }

    public synchronized void reload(List<FireworkConfig> fireworkConfigs, List<PlayerFireworkConfig> playerFireworkConfigs) {
        this.fireworkConfigs = fireworkConfigs;
        this.playerFireworkConfigs = playerFireworkConfigs;
        unLoadEffect(null);
        for (FireworkConfig config : this.fireworkConfigs) {
            try {
                loadEffect(config);
                //TimeUnit.MILLISECONDS.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
                AesopPlugin.logger.log("读取特效"+config.getKey()+"出错", ConsoleLogger.Level.ERROR);
            }
        }
        AesopPlugin.logger.log("粒子特效加载完成");
    }
    public synchronized void clear() {
        unLoadEffect(null);
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

    private void spawnPlayerTextEffect(PlayerFireworkConfig config, Player player) {
        if(!config.isEnable()) return;
        String[] offsetStrArr = config.getOffset().split(",");
        double ofx = Double.parseDouble(offsetStrArr[0]);
        double ofy = Double.parseDouble(offsetStrArr[1]);
        double ofz = Double.parseDouble(offsetStrArr[2]);

        TextEffect effect = new TextEffect(effectManager);
        // 设置位置
        Location location = player.getLocation().add(ofx, ofy, ofz);
        if(config.isInverted()) {
            float yaw = location.getYaw();
            yaw = yaw > 0 ? yaw - 180 : yaw + 180;
            location.setYaw(yaw);
        }
        effect.setDynamicOrigin(new DynamicLocation(location));
        //private final BiFunction<Player, Integer, Location> xForwardFromPlayer = (player, x) -> player.getLocation().add(0,2,0).add(player.getLocation().getDirection().multiply(x));
        //effect.setDynamicOrigin(new DynamicLocation(xForwardFromPlayer.apply(player, 9)));
        // 设置文本
        effect.text = config.getText();
        // 设置粒子特效（暂时只能选择不需要特效数据的）
        effect.particle = Particle.valueOf(config.getParticle());
        //effect.pitch = 180f;
        // 时间间隔，数值越小，显示越快
        effect.period = config.getPeriod();
        effect.duration = null; //config.getDuration()<=0 ? null : config.getDuration();// 持续时间，持续时间结束后特效消失（当持续时间非空时，优先级会比iterations高）
        effect.iterations = -1;//config.getDuration()<=0 ? -1 : 0; // -1永久显示 0默认
        InputStream fi = FireWorkManage.class.getClassLoader().getResourceAsStream("font/zfxkft_aigei_com.ttf");
        try {
            effect.setFont(Font.createFont(Font.PLAIN, fi).deriveFont(Font.PLAIN, config.getTextSize()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        effect.start();
        Bukkit.getScheduler().scheduleSyncDelayedTask(AesopPlugin.getInstance(), effect::cancel, config.getDuration());
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
}
