package top.mcos.hook;

import top.mcos.AesopPlugin;
import top.mcos.hook.providers.ChunkyProvider;
import top.mcos.hook.providers.MultiverseProvider;
import top.mcos.hook.providers.PlaceholderAPIProvider;
import top.mcos.hook.providers.WorldGuardProvider;

public final class HookHandler {

    /**
     * 区块加载支持
     */
    public static ChunkyProvider chunkyProvider;
    /**
     * 多世界支持
     */
    public static MultiverseProvider multiverseProvider;
    /**
     * 世界卫士支持
     */
    public static WorldGuardProvider worldGuardProvider;
    /**
     * PlaceholderAPI支持
     */
    public static PlaceholderAPIProvider placeholderAPIProvider;

    public static void init() {
        chunkyProvider = load(new ChunkyProvider());
        multiverseProvider = load(new MultiverseProvider());
        worldGuardProvider = load(new WorldGuardProvider());
        placeholderAPIProvider = load(new PlaceholderAPIProvider());
    }

    private static <H extends HookProvider> H load(H provider) {
        if (provider == null) {
            throw new NullPointerException("Provider cannot be null.");
        }
        // 由于类型擦除，这里无法进行运行时的泛型检查。
        // 调用者需要确保传入的provider的load方法返回类型与T兼容。
        try {
            provider = (H) provider.load();
            if(provider.isLoaded()) {
                AesopPlugin.logger.log("&a插件["+provider.getAPIName()+"]已挂钩");
            } else {
                AesopPlugin.logger.log("&e未检测到插件["+provider.getAPIName()+"]，已跳过加载");
            }
        } catch (Throwable e) {
            AesopPlugin.logger.log("&e插件["+provider.getAPIName()+"]挂钩失败，已跳过加载");
        }

        return provider;
    }
}
