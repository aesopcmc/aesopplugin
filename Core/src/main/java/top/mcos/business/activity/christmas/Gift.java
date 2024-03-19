package top.mcos.business.activity.christmas;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import top.mcos.AesopPlugin;
import top.mcos.database.domain.GiftItem;
import top.mcos.database.enums.GiftTypeEnum;
import top.mcos.util.MessageUtil;
import top.mcos.util.RandomUtil;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public final class Gift {
    public static GiftItem giveItemTwig(List<ItemStack> itemStacks, List<GiftItem> giftItems, int amount) {
        String giftName = "圣诞树枝";
        NamespacedKey nkey = NSKeys.ACTIVITY_TWIG_ITEM;
        ItemStack itemStack = new ItemStack(Material.STICK, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta!=null) {
            // 设置显示的名称
            itemMeta.setDisplayName(MessageUtil.symbol("&a&l⍋&6圣诞树枝&a&l⍋ &8- &5※稀有"));
            // 设置自定义持久数据
            itemMeta.getPersistentDataContainer().set(nkey, PersistentDataType.BOOLEAN, Boolean.TRUE);
            // 设置lore
            List<String> lines = new ArrayList<>();
            lines.add(" ");
            lines.add(MessageUtil.symbol("&7一位神秘人在森林里丢失了他的驯鹿座驾，礼物不能及时送达将是致命的，"));
            lines.add(MessageUtil.symbol("&7他决定把摘下来的树枝作为飞行工具。"));
            lines.add(" ");
            lines.add(MessageUtil.symbol("&7- &7攻击时对敌人产生&c&l击退10&7和&c&l1-5秒漂浮&7效果"));
            lines.add(MessageUtil.symbol("&7- &7获得概率10%"));
            lines.add(" ");
            lines.add(MessageUtil.symbol("&b❄== &5圣诞节礼物&a &b==❄"));
            itemMeta.setLore(lines);
            // 设置附魔
            itemMeta.addEnchant(Enchantment.KNOCKBACK, 10, true);
            itemStack.setItemMeta(itemMeta);
            itemStacks.add(itemStack);

            // 添加数据库条目
            GiftItem item = new GiftItem();
            item.setItemKey(nkey.getKey());
            item.setAmount(amount);
            item.setGiftName(giftName);
            item.setItemType(GiftTypeEnum.CHRISTMAS_GIFT.getIndex());
            giftItems.add(item);
            return item;
        }
        return null;
    }

    public static GiftItem giveItemCookie(List<ItemStack> itemStacks, List<GiftItem> giftItems, int amount) {
        String giftName = "圣诞曲奇";
        NamespacedKey nkey = NSKeys.ACTIVITY_COOKIE_ITEM;
        ItemStack itemStack = new ItemStack(Material.COOKIE, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta!=null) {
            // 设置显示的名称
            itemMeta.setDisplayName(MessageUtil.symbol("&a&l⍋&6圣诞曲奇&a&l⍋"));
            // 设置自定义持久数据
            itemMeta.getPersistentDataContainer().set(nkey, PersistentDataType.BOOLEAN, Boolean.TRUE);
            // 设置lore
            List<String> lines = new ArrayList<>();
            lines.add(MessageUtil.symbol("&7在特殊的节日吃起来肯定更香! ᒄ₍⁽ˆ⁰ˆ⁾₎ᒃ"));
            lines.add(" ");
            lines.add(MessageUtil.symbol("&b❄== &5圣诞节礼物&a &b==❄"));
            itemMeta.setLore(lines);
            itemStack.setItemMeta(itemMeta);
            itemStacks.add(itemStack);

            // 添加数据库条目
            GiftItem item = new GiftItem();
            item.setItemKey(nkey.getKey());
            item.setAmount(amount);
            item.setGiftName(giftName);
            item.setItemType(GiftTypeEnum.CHRISTMAS_GIFT.getIndex());
            giftItems.add(item);
            return item;
        }
        return null;
    }

    public static GiftItem giveItemApple(List<ItemStack> itemStacks, List<GiftItem> giftItems, int amount) {
        String giftName = "平安果";
        NamespacedKey nkey = NSKeys.ACTIVITY_APPLE_ITEM;
        ItemStack itemStack = new ItemStack(Material.APPLE, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta!=null) {
            // 设置显示的名称
            itemMeta.setDisplayName(MessageUtil.symbol("&a&l⍋&6平安果&a&l⍋"));
            // 设置自定义持久数据
            itemMeta.getPersistentDataContainer().set(nkey, PersistentDataType.BOOLEAN, Boolean.TRUE);
            // 设置lore
            List<String> lines = new ArrayList<>();
            lines.add(MessageUtil.symbol("&7在特殊的节日吃起来肯定更香! ᒄ₍⁽ˆ⁰ˆ⁾₎ᒃ"));
            lines.add(" ");
            lines.add(MessageUtil.symbol("&b❄== &5圣诞节礼物&a &b==❄"));
            itemMeta.setLore(lines);
            itemStack.setItemMeta(itemMeta);
            itemStacks.add(itemStack);

            // 添加数据库条目
            GiftItem item = new GiftItem();
            item.setItemKey(nkey.getKey());
            item.setAmount(amount);
            item.setGiftName(giftName);
            item.setItemType(GiftTypeEnum.CHRISTMAS_GIFT.getIndex());
            giftItems.add(item);
            return item;
        }
        return null;
    }

    public static GiftItem giveItemPotion(List<ItemStack> itemStacks, List<GiftItem> giftItems, int amount) {
        String giftName = "圣诞药水";
        NamespacedKey nkey = NSKeys.ACTIVITY_APPLE_ITEM;
        ItemStack itemStack = new ItemStack(Material.SPLASH_POTION, amount);
        PotionEffectType[] effs = PotionEffectType.values();
        int effIdx = RandomUtil.get(0, effs.length-1);
        int duration = RandomUtil.get(100, 2400);// 5s-5min //持续时间，单位tick

        PotionMeta itemMeta = (PotionMeta) itemStack.getItemMeta();
        //ItemMeta itemMeta1 = itemStack.getItemMeta();
        if(itemMeta!=null) {
            // 设置显示的名称
            itemMeta.setDisplayName(MessageUtil.symbol("&a&l⍋&6圣诞药水&a&l⍋"));
            // 设置自定义持久数据
            itemMeta.getPersistentDataContainer().set(nkey, PersistentDataType.BOOLEAN, Boolean.TRUE);
            // 设置lore
            List<String> lines = new ArrayList<>();
            lines.add(MessageUtil.symbol("&7在特殊的节日吃起来肯定更香! ᒄ₍⁽ˆ⁰ˆ⁾₎ᒃ"));
            lines.add(" ");
            lines.add(MessageUtil.symbol("&b❄== &5圣诞节礼物&a &b==❄"));
            itemMeta.setLore(lines);
            itemMeta.addCustomEffect(new PotionEffect(effs[effIdx], duration, 1, true, true), true);
            itemStack.setItemMeta(itemMeta);
            itemStacks.add(itemStack);

            // 添加数据库条目
            GiftItem item = new GiftItem();
            item.setItemKey(nkey.getKey());
            item.setAmount(amount);
            item.setGiftName(giftName);
            item.setItemType(GiftTypeEnum.CHRISTMAS_GIFT.getIndex());
            giftItems.add(item);
            return item;
        }
        return null;
    }

    public static GiftItem giveDeluxeTags(Player player, List<GiftItem> giftItems, int amount) {
        String giftName = "圣诞老祖称号";
        String cmdline = "lp user "+player.getName()+" permission set deluxetags.tag.圣诞老祖 true";

        Bukkit.getScheduler().runTask(AesopPlugin.getInstance(), () -> {
            ConsoleCommandSender consoleSender = Bukkit.getServer().getConsoleSender();
            Bukkit.getServer().dispatchCommand(consoleSender, cmdline);
        });

        // 添加数据库条目
        GiftItem item = new GiftItem();
        item.setItemKey(NSKeys.ACTIVITY_SDLZ_TAG.getKey());
        item.setAmount(amount);
        item.setGiftName(giftName);
        item.setItemType(GiftTypeEnum.CHRISTMAS_GIFT.getIndex());
        giftItems.add(item);
        return item;
    }

    public static GiftItem giveItemCrateMyGoldKey(List<ItemStack> itemStacks, List<GiftItem> giftItems, int amount) {
        //{"excellentcrates:crate_key.id":"my_gold"}
        //{"excellentcrates:crate_key.id":"my_dep_blue"}
        //{"excellentcrates:crate_key.id":"my_dep_red"}
        /*
        Name: '#b3ff5dMy_gold Key'
        Virtual: false
        Item:
          Material: YELLOW_CANDLE
          Name: '&b★ #FFD700&l金色传说 &b★ &8| #DF1019宝箱钥匙'
          Lore:
          - '&8[●] &7可用于#FFD700&l黄金宝箱&7抽奖'
          - '&8[●] &7&o一次性使用'
         */

        String giftName = "金色传说宝箱钥匙";
        NamespacedKey nkey = NSKeys.ACTIVITY_CREATEKEYID_MYGOLD_ITEM;
        // 这里设置的是crates抽奖箱子插件的key
        NamespacedKey crateskey=new NamespacedKey("excellentcrates", "crate_key.id");
        ItemStack itemStack = new ItemStack(Material.YELLOW_CANDLE, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta!=null) {
            // 设置显示的名称
            itemMeta.setDisplayName(MessageUtil.colorize("&b★ &#FFD700&l金色传说 &b★ &8| &#DF1019宝箱钥匙"));
            // 设置自定义持久数据
            itemMeta.getPersistentDataContainer().set(nkey, PersistentDataType.BOOLEAN, Boolean.TRUE);
            itemMeta.getPersistentDataContainer().set(crateskey, PersistentDataType.STRING, "my_gold");
            // 设置lore
            List<String> lines = new ArrayList<>();
            lines.add(MessageUtil.colorize("&8[●] &7可用于&#FFD700&l黄金宝箱&7抽奖"));
            lines.add(MessageUtil.colorize("&8[●] &7&o一次性使用"));
            lines.add(" ");
            lines.add(MessageUtil.colorize("&b❄== &5圣诞节礼物&a &b==❄"));
            itemMeta.setLore(lines);

            itemStack.setItemMeta(itemMeta);
            itemStacks.add(itemStack);

            // 添加数据库条目
            GiftItem item = new GiftItem();
            item.setItemKey(nkey.getKey());
            item.setAmount(amount);
            item.setGiftName(giftName);
            item.setItemType(GiftTypeEnum.CHRISTMAS_GIFT.getIndex());
            giftItems.add(item);
            return item;
        }
        return null;
    }

    public static GiftItem giveItemCrateMyDepBlueKey(List<ItemStack> itemStacks, List<GiftItem> giftItems, int amount) {
        //{"excellentcrates:crate_key.id":"my_dep_blue"}
        /*
        Name: '#0000CDMy_dep_blue Key'
        Virtual: false
        Item:
          Material: BLUE_CANDLE
          Name: '&b★ #0000CD&l珠影古匙 &b★ &8| #DF1019宝箱钥匙'
          Lore:
          - '&8[●] &7可用于#0000CD&l黑珍珠盒&7抽奖'
          - '&8[●] &7&o一次性使用'

         */
        String giftName = "珠影古匙宝箱钥匙";
        NamespacedKey nkey = NSKeys.ACTIVITY_CREATEKEYID_MYDEPBLUE_ITEM;
        // 这里设置的是crates抽奖箱子插件的key
        NamespacedKey crateskey=new NamespacedKey("excellentcrates", "crate_key.id");
        ItemStack itemStack = new ItemStack(Material.BLUE_CANDLE, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta!=null) {
            // 设置显示的名称
            itemMeta.setDisplayName(MessageUtil.colorize("&b★ &#0000CD&l珠影古匙 &b★ &8| &#DF1019宝箱钥匙"));
            // 设置自定义持久数据
            itemMeta.getPersistentDataContainer().set(nkey, PersistentDataType.BOOLEAN, Boolean.TRUE);
            itemMeta.getPersistentDataContainer().set(crateskey, PersistentDataType.STRING, "my_dep_blue");
            // 设置lore
            List<String> lines = new ArrayList<>();
            lines.add(MessageUtil.colorize("&8[●] &7可用于&#0000CD&l黑珍珠盒&7抽奖"));
            lines.add(MessageUtil.colorize("&8[●] &7&o一次性使用"));
            lines.add(" ");
            lines.add(MessageUtil.colorize("&b❄== &5圣诞节礼物&a &b==❄"));
            itemMeta.setLore(lines);

            itemStack.setItemMeta(itemMeta);
            itemStacks.add(itemStack);

            // 添加数据库条目
            GiftItem item = new GiftItem();
            item.setItemKey(nkey.getKey());
            item.setAmount(amount);
            item.setGiftName(giftName);
            item.setItemType(GiftTypeEnum.CHRISTMAS_GIFT.getIndex());
            giftItems.add(item);
            return item;
        }
        return null;
    }

    public static GiftItem giveItemCrateMyDepRedKey(List<ItemStack> itemStacks, List<GiftItem> giftItems, int amount) {
        //{"excellentcrates:crate_key.id":"my_dep_red"}
        /*
        Name: '&4dMy_dep_red Key'
        Virtual: false
        Item:
          Material: RED_CANDLE
          Name: '&b★ &4鬼火古匙 &b★ &8| #DF1019宝箱钥匙'
          Lore:
          - '&8[●] &7可用于&4&l幽魂骨盒&7抽奖'
          - '&8[●] &7&o一次性使用'
         */
        String giftName = "鬼火古匙宝箱钥匙";
        NamespacedKey nkey = NSKeys.ACTIVITY_CREATEKEYID_MYDEPRED_ITEM;
        // 这里设置的是crates抽奖箱子插件的key
        NamespacedKey crateskey=new NamespacedKey("excellentcrates", "crate_key.id");
        ItemStack itemStack = new ItemStack(Material.RED_CANDLE, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta!=null) {
            // 设置显示的名称
            itemMeta.setDisplayName(MessageUtil.colorize("&b★ &4&l鬼火古匙 &b★ &8| &#DF1019宝箱钥匙"));
            // 设置自定义持久数据
            itemMeta.getPersistentDataContainer().set(nkey, PersistentDataType.BOOLEAN, Boolean.TRUE);
            itemMeta.getPersistentDataContainer().set(crateskey, PersistentDataType.STRING, "my_dep_red");
            // 设置lore
            List<String> lines = new ArrayList<>();
            lines.add(MessageUtil.colorize("&8[●] &7可用于&4&l幽魂骨盒&7抽奖"));
            lines.add(MessageUtil.colorize("&8[●] &7&o一次性使用"));
            lines.add(" ");
            lines.add(MessageUtil.colorize("&b❄== &5圣诞节礼物&a &b==❄"));
            itemMeta.setLore(lines);

            itemStack.setItemMeta(itemMeta);
            itemStacks.add(itemStack);

            // 添加数据库条目
            GiftItem item = new GiftItem();
            item.setItemKey(nkey.getKey());
            item.setAmount(amount);
            item.setGiftName(giftName);
            item.setItemType(GiftTypeEnum.CHRISTMAS_GIFT.getIndex());
            giftItems.add(item);
            return item;
        }
        return null;
    }

    public static GiftItem giveMoneyAndPoint(Player player, List<GiftItem> giftItems, int amount) {
        String giftName = "5000金币和10点券";
        String cmdline = "eco give "+player.getName()+" 5000";
        String cmdline2 = "points give "+player.getName()+" 10";
        Bukkit.getScheduler().runTask(AesopPlugin.getInstance(), () -> {
            ConsoleCommandSender consoleSender = Bukkit.getServer().getConsoleSender();
            Bukkit.getServer().dispatchCommand(consoleSender, cmdline);
            Bukkit.getServer().dispatchCommand(consoleSender, cmdline2);
        });

        // 添加数据库条目
        GiftItem item = new GiftItem();
        item.setItemKey(NSKeys.ACTIVITY_MONEY_AND_POINT.getKey());
        item.setAmount(amount);
        item.setGiftName(giftName);
        item.setItemType(GiftTypeEnum.CHRISTMAS_GIFT.getIndex());
        giftItems.add(item);
        return item;
    }

}
