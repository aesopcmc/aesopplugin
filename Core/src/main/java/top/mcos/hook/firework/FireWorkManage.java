package top.mcos.hook.firework;

import com.epicnicity322.epicpluginlib.core.logger.ConsoleLogger;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.TextEffect;
import de.slikey.effectlib.util.DynamicLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import top.mcos.AesopPlugin;
import top.mcos.config.configs.FireworkConfig;
import top.mcos.util.MessageUtil;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class FireWorkManage {
    private final EffectManager effectManager;

    public Map<String, TextEffect> textEffectMap = new HashMap<>();

    public FireWorkManage(AesopPlugin instance, List<FireworkConfig> fireworkConfigs) {
        effectManager = new EffectManager(instance);
        reload(fireworkConfigs);
    }

    public synchronized void reload(List<FireworkConfig> fireworkConfigs) {
        unLoadEffect(null);
        for (FireworkConfig config : fireworkConfigs) {
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

}
