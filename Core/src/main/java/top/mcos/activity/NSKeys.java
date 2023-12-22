package top.mcos.activity;

import org.bukkit.NamespacedKey;
import top.mcos.AesopPlugin;

/**
 * NamespacedKey
 */
public class NSKeys {
    /**
     * 礼品按钮
     */
    public static NamespacedKey ACTIVITY_GIFT_BUTTON;
    /**
     * 圣诞雪球按钮（收集按钮）
     */
    public static NamespacedKey ACTIVITY_SNOWBALL_BUTTON;
    /**
     * 圣诞树枝
     */
    public static NamespacedKey ACTIVITY_TWIG_ITEM;
    public static NamespacedKey ACTIVITY_COOKIE_ITEM;
    public static NamespacedKey ACTIVITY_APPLE_ITEM;
    public static NamespacedKey ACTIVITY_SDLZ_TAG;
    public static NamespacedKey ACTIVITY_MONEY_AND_POINT;
    public static NamespacedKey ACTIVITY_CREATEKEYID_MYDEPBLUE_ITEM;
    public static NamespacedKey ACTIVITY_CREATEKEYID_MYDEPRED_ITEM;
    public static NamespacedKey ACTIVITY_CREATEKEYID_MYGOLD_ITEM;


    private NSKeys() {}

    public static void init(AesopPlugin instance) {
        ACTIVITY_GIFT_BUTTON = new NamespacedKey(instance, "activity-gift-button");
        ACTIVITY_SNOWBALL_BUTTON = new NamespacedKey(instance, "activity-snowball-button");
        ACTIVITY_TWIG_ITEM = new NamespacedKey(instance, "activity-twig-item");
        ACTIVITY_COOKIE_ITEM = new NamespacedKey(instance, "activity-cookie-item");
        ACTIVITY_APPLE_ITEM = new NamespacedKey(instance, "activity-apple-item");
        ACTIVITY_SDLZ_TAG = new NamespacedKey(instance, "activity-sdlz-tag");
        ACTIVITY_MONEY_AND_POINT = new NamespacedKey(instance, "activity-money-and-point");
        ACTIVITY_CREATEKEYID_MYDEPBLUE_ITEM = new NamespacedKey(instance, "activity_createkeyid_mydepblue_item");
        ACTIVITY_CREATEKEYID_MYDEPRED_ITEM = new NamespacedKey(instance, "activity_createkeyid_mydepred_item");
        ACTIVITY_CREATEKEYID_MYGOLD_ITEM = new NamespacedKey(instance, "activity_createkeyid_mygold_item");
    }
}
