package top.mcos.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import top.mcos.config.activitiy.NSKeys;
import top.mcos.util.RandomUtil;

import java.util.Random;

public class EntityDamageListener implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();

        if(damager instanceof Player damagerPlayer && entity instanceof Player entityPlayer) {
            // 获得使用的物品
            ItemStack itemInHand = damagerPlayer.getInventory().getItemInMainHand();
            Boolean isTwig = itemInHand.getItemMeta().getPersistentDataContainer().get(NSKeys.ACTIVITY_TWIG_ITEM, PersistentDataType.BOOLEAN);
            if(isTwig!=null && isTwig) {
                //AesopPlugin.logger.log("圣诞树枝攻击事件， damager:" + damager.getName() +", entity:" + entity.getName());
                // 是圣诞树枝，添加药水效果（漂浮1-5秒）
                int duration = RandomUtil.get(20, 100); // 单位tick
                entityPlayer.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, duration, 2, true, true));
            }
        }

        //if("Location{world=CraftWorld{name=world},x=26.0,y=64.0,z=16.0,pitch=0.0,yaw=0.0}".equals(clicked.getLocation().toString())) {
            //player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 100, 2, true, true));
        //}
    }

}
