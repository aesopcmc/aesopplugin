package top.mcos.business.activity.gift;

import lombok.Getter;
import lombok.Setter;
import top.mcos.business.activity.config.sub.AGiftConfig;

/**
 *     gift-keys 配置
 */
@Setter
@Getter
public class GiftKey {
    /**
     * 礼物key对应的配置
     */
    private AGiftConfig giftKey;
    /**
     * 数量
     */
    private int amount;
    /**
     * 概率
     */
    private int prob;
    /**
     * 概率分组
     */
    private String pgroup;

    /**
     * 概率是否命中
     */
    private boolean probSuccess;
    public GiftKey(AGiftConfig giftKey, int amount, int prob, String pgroup) {
        this.giftKey = giftKey;
        this.amount = amount;
        this.prob = prob;
        this.pgroup = pgroup;
    }
}