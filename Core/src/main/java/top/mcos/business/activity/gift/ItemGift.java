package top.mcos.business.activity.gift;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * 实体类型礼物
 */
@Setter
@Getter
public class ItemGift extends GiftAbs{
    private ItemStack itemStack;

    public ItemGift(String giftKey, ItemStack itemStack) {
        super(giftKey);
        this.itemStack = itemStack;
    }

    @Override
    public boolean send(Player player) {
        player.getInventory().addItem(itemStack);
        hasSend = true;
        return true;
    }
}
