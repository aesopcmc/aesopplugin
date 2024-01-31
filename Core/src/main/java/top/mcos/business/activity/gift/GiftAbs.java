package top.mcos.business.activity.gift;

import org.bukkit.entity.Player;

/**
 * 抽象礼物类
 */
public abstract class GiftAbs {
    protected String giftKey;
    protected boolean hasSend = false;

    public GiftAbs(String giftKey) {
        this.giftKey = giftKey;
    }

    /**
     * 玩家获取礼物
     * @param player 玩家
     * @return true成功领取 false领取失败
     */
    public abstract boolean send(Player player);

    public boolean hasSend() {
        return hasSend;
    }

    public String getGiftKey() {
        return giftKey;
    }
}
