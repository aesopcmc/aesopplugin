package top.mcos.hook.providers;

import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.Bukkit;
import top.mcos.AesopPlugin;
import top.mcos.hook.HookProviderLoader;

public final class MultiverseProvider extends Provider<MultiverseCore> implements HookProviderLoader {
    private MultiverseCore multiverseCore;

    @Override
    public void load() {
        try {
            multiverseCore = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
            if(multiverseCore!=null) {
                AesopPlugin.logger.log("MultiverseCore已挂钩");
            } else {
                AesopPlugin.logger.log("未检测到MultiverseCore插件，已跳过加载");
            }
        }catch (Throwable e) {
            AesopPlugin.logger.log("未检测到MultiverseCore插件，已跳过加载");
        }
    }

    @Override
    public boolean isLoaded() {
        return multiverseCore!=null;
    }

    @Override
    public MultiverseCore getAPI() {
        return multiverseCore;
    }
}
