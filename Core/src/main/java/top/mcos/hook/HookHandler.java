package top.mcos.hook;

import top.mcos.hook.providers.ChunkyProvider;
import top.mcos.hook.providers.MultiverseProvider;

public class HookHandler {

    /**
     * 区块加载支持
     */
    private static ChunkyProvider chunkyProvider;
    /**
     * 多世界支持
     */
    private static MultiverseProvider multiverseProvider;

    public static void init() {
        chunkyProvider = new ChunkyProvider();
        chunkyProvider.load();
        multiverseProvider = new MultiverseProvider();
        multiverseProvider.load();
    }
    public static ChunkyProvider getChunkyProvider() {
        return chunkyProvider;
    }

    public static MultiverseProvider getMultiverseProvider() {
        return multiverseProvider;
    }
}
