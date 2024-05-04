package top.mcos.hook.providers;

import com.sk89q.worldguard.WorldGuard;
import top.mcos.AesopPlugin;
import top.mcos.hook.HookProviderLoader;

public final class WorldGuardProvider extends Provider<WorldGuard> implements HookProviderLoader {
    private WorldGuard worldGuard;

    @Override
    public void load() {
        try {
            worldGuard = WorldGuard.getInstance();
            if(worldGuard !=null) {
                AesopPlugin.logger.log("&aWorldGuard已挂钩");
            } else {
                AesopPlugin.logger.log("&e未检测到WorldGuard插件，已跳过加载");
            }
        }catch (Throwable e) {
            AesopPlugin.logger.log("&e未检测到WorldGuard插件，已跳过加载");
        }
    }

    @Override
    public boolean isLoaded() {
        return worldGuard !=null;
    }

    @Override
    public WorldGuard getAPI() {
        return worldGuard;
    }
}
