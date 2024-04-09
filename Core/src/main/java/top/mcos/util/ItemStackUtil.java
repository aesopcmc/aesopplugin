package top.mcos.util;

import top.mcos.util.epiclib.logger.ConsoleLogger;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import top.mcos.AesopPlugin;

import java.util.ArrayList;
import java.util.List;

public class ItemStackUtil {
    /**
     * 创建自定义物品 ItemStack
     * @param persistentPrefix 命名空间
     * @param dataKey 命名空间下存放的 数据key
     * @param material 材质名 {@link Material}
     * @param displayName 显示名
     * @param amount 数量
     * @param lore 描述
     * @param enchants 附魔
     * @param potions 药水效果
     * @return ItemStack
     */
    public static ItemStack createItemStack(@NotNull String persistentPrefix, @NotNull String dataKey, @NotNull String material, String displayName, int amount, List<String> lore, List<String> enchants, List<String> potions) {
        NamespacedKey namespacedKey = new NamespacedKey(AesopPlugin.getInstance(), persistentPrefix);
        ItemStack itemStack = new ItemStack(Material.valueOf(material.toUpperCase()), amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta!=null) {
            // 设置自定义持久数据 ： gifts->key
            itemMeta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, dataKey);
            // 设置显示的名称
            itemMeta.setDisplayName(MessageUtil.colorize(displayName));
            // 设置lore
            if(lore!=null) {
                List<String> lores = new ArrayList<>();
                for (String desc : lore) {
                    lores.add(MessageUtil.colorize(desc));
                }
                itemMeta.setLore(lores);
            }
            // 设置附魔
            if(enchants!=null && enchants.size()>0){
                for (String enchant : enchants) {
                    String[] split = StringUtils.split(enchant, ":");
                    Enchantment enc = Enchantment.getByKey(NamespacedKey.minecraft(split[0]));// 附魔名称
                    if(enc==null) continue;
                    String[] levelArr = StringUtils.split(split[1], "-"); // 附魔等级
                    boolean ignoreLevelRestriction = Boolean.parseBoolean(split[2]); // 等级限制
                    int prob = Integer.parseInt(split[3]);// 概率
                    if(!RandomUtil.probRandom(prob)) continue;

                    int level;
                    if(levelArr.length>1) {
                        level = Integer.parseInt(levelArr[RandomUtil.get(0, levelArr.length-1)]);
                    }else {
                        level = Integer.parseInt(levelArr[0]);
                    }

                    itemMeta.addEnchant(enc, level, ignoreLevelRestriction);
                }
            }
            // 设置药水效果
            if(potions!=null && potions.size()>0) {
                try {
                    PotionMeta potionMeta = (PotionMeta) itemMeta;
                    for (String potion : potions) {
                        String[] split = StringUtils.split(potion, ":");
                        String potionName = split[0]; // 药水名称
                        String[] durationArr = StringUtils.split(split[1], "-"); // 持续时间
                        int prob = Integer.parseInt(split[2]);// 概率
                        if(!RandomUtil.probRandom(prob)) continue;

                        PotionEffectType potionType = PotionEffectType.getByName(potionName);
                        if(potionType==null) continue;

                        int duration;
                        if(durationArr.length>1) {
                            duration = Integer.parseInt(durationArr[RandomUtil.get(0, durationArr.length-1)]);
                        }else {
                            duration = Integer.parseInt(durationArr[0]);
                        }
                        // TODO 药水其它属性扩展 配置
                        potionMeta.addCustomEffect(new PotionEffect(potionType, duration, 1, true, true), true);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    AesopPlugin.logger.log("&c物品【"+dataKey+"】无法设置附魔", ConsoleLogger.Level.ERROR);
                }
            }

            itemStack.setItemMeta(itemMeta);
        }

        return itemStack;
    }
}
