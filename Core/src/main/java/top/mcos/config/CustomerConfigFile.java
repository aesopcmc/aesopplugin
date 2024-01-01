package top.mcos.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import top.mcos.AesopPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class CustomerConfigFile {
    private FileConfiguration customConfig = null;
    private final File customConfigFile;

    public CustomerConfigFile(String fileName) {
        customConfigFile = new File(AesopPlugin.getInstance().getDataFolder(), fileName);
    }

    /**
     * 如果配置文件不存在，将从jar文件中拷贝至data目录
     */
    public void saveDefaultConfig() {
        if (!customConfigFile.exists()) {
            AesopPlugin.getInstance().saveResource(customConfigFile.getName(), false);
        }
    }

    /**
     * 重载配置。从data目录配置中加载到缓存
     */
    public void reloadConfig() {
        customConfig = YamlConfiguration.loadConfiguration(customConfigFile);

        // Look for defaults in the jar
        Reader defConfigStream = null;
        try {
            defConfigStream = new InputStreamReader(AesopPlugin.getInstance().getResource(customConfigFile.getName()), "UTF8");
        } catch (Exception e) {
            AesopPlugin.logger.log("读取配置失败：" + customConfigFile.getName());
        }
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            customConfig.setDefaults(defConfig);
        }
    }

    /**
     * 获得配置
     * @return FileConfiguration
     */
    public FileConfiguration getConfig() {
        if (customConfig == null) {
            reloadConfig();
        }
        return customConfig;
    }

    /**
     * 保存配置。从缓存中保存配置到data目录配置
     */
    public void saveConfig() {
        if (customConfig == null || customConfigFile == null) {
            return;
        }
        try {
            getConfig().save(customConfigFile);
        } catch (IOException ex) {
            AesopPlugin.logger.log("无法保存配置：" + customConfigFile.getName());
        }
    }

}
