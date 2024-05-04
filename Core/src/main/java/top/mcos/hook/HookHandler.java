package top.mcos.hook;

import top.mcos.hook.providers.ChunkyProvider;
import top.mcos.hook.providers.MultiverseProvider;
import top.mcos.hook.providers.WorldGuardProvider;

public class HookHandler {

    /**
     * 区块加载支持
     */
    private static ChunkyProvider chunkyProvider;
    /**
     * 多世界支持
     */
    private static MultiverseProvider multiverseProvider;
    /**
     * 世界卫士支持
     */
    private static WorldGuardProvider worldGuardProvider;

    public static void init() {
        chunkyProvider = new ChunkyProvider();
        chunkyProvider.load();
        multiverseProvider = new MultiverseProvider();
        multiverseProvider.load();
        worldGuardProvider = new WorldGuardProvider();
        worldGuardProvider.load();
    }
    public static ChunkyProvider getChunkyProvider() {
        return chunkyProvider;
    }

    public static MultiverseProvider getMultiverseProvider() {
        return multiverseProvider;
    }

    public static WorldGuardProvider getWorldGuardProvider() {
        return worldGuardProvider;
    }
}
