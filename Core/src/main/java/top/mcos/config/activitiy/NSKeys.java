package top.mcos.config.activitiy;

import org.bukkit.NamespacedKey;
import top.mcos.AesopPlugin;

/**
 * NamespacedKey
 */
public class NSKeys {
    /**
     * 礼品按钮
     */
    public static NamespacedKey ACTIVITY_BUTTON_FLAG;
    /**
     * 圣诞树枝
     */
    public static NamespacedKey ACTIVITY_TWIG_ITEM;

    private NSKeys() {}

    public static void init(AesopPlugin instance) {
        ACTIVITY_BUTTON_FLAG = new NamespacedKey(instance, "activity-button-flag");
        ACTIVITY_TWIG_ITEM = new NamespacedKey(instance, "activity-twig-item");
    }
}
