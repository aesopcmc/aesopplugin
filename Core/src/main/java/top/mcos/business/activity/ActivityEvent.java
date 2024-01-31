package top.mcos.business.activity;

import com.epicnicity322.epicpluginlib.core.logger.ConsoleLogger;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.mcos.AesopPlugin;
import top.mcos.business.activity.condition.AndCondition;
import top.mcos.business.activity.config.sub.AConItemConfig;
import top.mcos.business.activity.config.sub.AEventConfig;
import top.mcos.business.activity.config.sub.AGiftConfig;
import top.mcos.business.activity.gift.VirtualGift;
import top.mcos.business.activity.gift.GiftAbs;
import top.mcos.business.activity.gift.ItemGift;
import top.mcos.config.ConfigLoader;
import top.mcos.database.dao.GiftClaimRecordDao;
import top.mcos.database.dao.GiftItemDao;
import top.mcos.database.domain.GiftClaimRecord;
import top.mcos.database.domain.GiftItem;
import top.mcos.util.ItemStackUtil;
import top.mcos.util.MessageUtil;
import top.mcos.util.RandomUtil;

import javax.annotation.CheckForNull;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 活动业务逻辑
 */
public class ActivityEvent {
    public static final String persistentGiftPrefix = "activity-gift";
    public static final String persistentConItemPrefix = "activity-conitem";

    /**
     * 收集物品
     * @param eventKey 活动key
     * @param itemKey 条件物品key
     * @param player 玩家
     */
    public static void collectItem(String eventKey, String itemKey, Player player) throws SQLException {
        AEventConfig eventConfig = null;
        List<AEventConfig> events = ConfigLoader.activityConfig.getEvents();
        for (AEventConfig event : events) {
            if(event.getKey().equals(eventKey)) {
                eventConfig = event;
                break;
            }
        }
        if(eventConfig==null) {
            AesopPlugin.logger.log(player, "&c未知的活动key! " + eventKey);
            return;
        }
        if(!eventConfig.isEnable()) {
            AesopPlugin.logger.log(player, "&c当前活动【"+eventConfig.getEventName()+"&c】不可用!");
            return;
        }

        List<AConItemConfig> itemConfigs = ConfigLoader.activityConfig.getConItemConfigs();
        Map<String, AConItemConfig> conItemMaps = itemConfigs.stream().collect(Collectors.toMap(AConItemConfig::getKey, c -> c));
        AConItemConfig itemConfig = conItemMaps.get(itemKey);
        if(itemConfig==null) {
            AesopPlugin.logger.log(player, "&c未知的物品key！" + itemKey);
            return;
        }

        String playerId = player.getUniqueId().toString();
        GiftClaimRecordDao giftClaimRecordDao = AesopPlugin.getInstance().getDatabase().getGiftClaimRecordDao();
        GiftItemDao giftItemDao = AesopPlugin.getInstance().getDatabase().getGiftItemDao();

        // 若玩家活动数据不存在，保存一次数据库
        GiftClaimRecord record = giftClaimRecordDao.queryByPlayer(eventConfig.getKey(), playerId);
        if(record==null) {
            record = giftClaimRecordDao.saveRecord(playerId,player.getName(),
                    player.getAddress().getHostName(), eventConfig.getKey(),
                    eventConfig.getEventName(), eventConfig.getGiftType());
        }

        // 重复领取验证
        long count = giftItemDao.countByItemKey(record.getId(), 2, itemKey);
        if(count>0) {
            AesopPlugin.logger.log(player, "&c您已经收集过 &a"+itemConfig.getDbName()+"&c 请不要重复操作！");
            return;
        }

        // 创建物品
        ItemStack itemStack = ItemStackUtil.createItemStack(persistentConItemPrefix, itemConfig.getKey(), itemConfig.getMaterial(),
                itemConfig.getDisplayName(), itemConfig.getAmount(), itemConfig.getLore(), itemConfig.getEnchants(), itemConfig.getPotions());

        // 添加物品明细持久化
        GiftItem item = new GiftItem();
        item.setItemKey(itemKey);
        item.setAmount(itemConfig.getAmount());
        item.setGiftName(itemConfig.getDbName());
        item.setItemType(2);// 礼物类型，条件物品
        //item.setPercent();
        item.setRecordId(record.getId());
        int i = giftItemDao.create(item);
        if(i>0) {
            // 推送到玩家库存
            player.getInventory().addItem(itemStack);

            // 显示收集进度
            // 判断物品条件
            AndCondition andCondition = judgmentCondition(eventConfig.getCondition(), record.getId(), conItemMaps);
            AesopPlugin.logger.log(player, "&b获得一个 【&a" + itemConfig.getDbName() + "&b】");
            AesopPlugin.logger.log(player, "&b当前进度：" + andCondition.process());
            AesopPlugin.logger.log(player, " ");
            if(andCondition.success()) {
                AesopPlugin.logger.log(player, "&a恭喜您! 已全部收集完毕。");
                AesopPlugin.logger.log(player, "&a请前往 【&2&l" + eventConfig.getEventName() + "&a】 指定地点领取奖品。");
            }
        } else {
            AesopPlugin.logger.log(player, "&c收集物品失败！");
        }
    }

    /**
     * 领取礼物
     * @param eventKey 活动key
     * @param player 玩家
     * @throws SQLException
     */
    public static void claimGift(String eventKey, Player player) throws SQLException {
        AEventConfig eventConfig = null;
        List<AEventConfig> events = ConfigLoader.activityConfig.getEvents();
        for (AEventConfig event : events) {
            if(event.getKey().equals(eventKey)) {
                eventConfig = event;
                break;
            }
        }
        if(eventConfig==null) {
            AesopPlugin.logger.log(player, "&c未知的活动！");
        }

        if(!eventConfig.isEnable()) {
            AesopPlugin.logger.log(player, "&c当前活动【"+eventConfig.getEventName()+"&c】不可用!");
            return;
        }
        String playerId = player.getUniqueId().toString();
        List<AConItemConfig> conItemConfigs = ConfigLoader.activityConfig.getConItemConfigs();
        Map<String, AConItemConfig> conItemMaps = conItemConfigs.stream().collect(Collectors.toMap(AConItemConfig::getKey, c -> c));

        GiftClaimRecordDao giftClaimRecordDao = AesopPlugin.getInstance().getDatabase().getGiftClaimRecordDao();
        GiftItemDao giftItemDao = AesopPlugin.getInstance().getDatabase().getGiftItemDao();

        // 若玩家活动数据不存在，保存一次数据库
        GiftClaimRecord record = giftClaimRecordDao.queryByPlayer(eventConfig.getKey(), playerId);
        if(record==null) {
            record = giftClaimRecordDao.saveRecord(playerId,player.getName(),
                    player.getAddress().getHostName(), eventConfig.getKey(),
                    eventConfig.getEventName(), eventConfig.getGiftType());
        }

        // =========》 前置条件判断
        // 判断是已经领取过
        if(record.getClaimed()==1) {
            AesopPlugin.logger.log(player, "&c您已经领取过 【"+record.getEventName()+"&c】 礼物，请不要重复操作！");
            return;
        }
        // 判断物品条件
        AndCondition andCondition = judgmentCondition(eventConfig.getCondition(), record.getId(), conItemMaps);
        if(!andCondition.success()) {
            AesopPlugin.logger.log(player, "&c未满足领取条件，请先收集以下物品：" + andCondition.process());
            return;
        }

        // 时间范围限制
        Date beginTime = eventConfig.getBeginTime();
        Date endTime = eventConfig.getEndTime();
        long nowTime = new Date().getTime();
        if(beginTime!=null && nowTime<beginTime.getTime()) {
            AesopPlugin.logger.log(player, "&c当前时间不可领取，活动开始时间为: &e"+
                    DateFormatUtils.format(beginTime, "yyyy年MM月dd日 HH:mm:ss"));
            return;
        }
        if(endTime!=null && nowTime>endTime.getTime()) {
            AesopPlugin.logger.log(player, "&c无法领取，活动已于: &e"+
                    DateFormatUtils.format(endTime, "yyyy年MM月dd日 HH:mm:ss") + " &c结束。");
            return;
        }

        // 检查是否有足够的背包空间
        int slotRequireCount = eventConfig.getGiftKeys()!=null ? eventConfig.getGiftKeys().size() : 0;
        ItemStack[] storageContents = player.getInventory().getStorageContents();
        int emptyClotCount = 0;
        for (ItemStack storageContent : storageContents) {
            if(storageContent==null) emptyClotCount++;
        }
        if(emptyClotCount<slotRequireCount) {
            AesopPlugin.logger.log(player, "&c背包空间不足，请至少留出"+slotRequireCount+"个槽位");
            return;
        }

        // =========》 构建礼物
        List<GiftItem> giftItemDbList = new ArrayList<>();
        List<String> giftKeys = eventConfig.getGiftKeys();
        if(giftKeys==null || giftKeys.size()<1) return;

        List<AGiftConfig> gifts = ConfigLoader.activityConfig.getGifts();
        Map<String, AGiftConfig> giftMaps = gifts.stream().collect(Collectors.toMap(AGiftConfig::getKey, c -> c));

        StringBuilder specialGiftStr = new StringBuilder();
        List<GiftAbs> giftAbsList = new ArrayList<>();
        for (String giftKeyCon : giftKeys) {
            String[] conArr = StringUtils.split(giftKeyCon, ":");

            // 礼物key
            String giftKey = conArr[0];
            AGiftConfig giftConfig = giftMaps.get(giftKey);
            if(giftConfig==null) continue;

            // 命中概率
            int rate = Integer.parseInt(conArr[2]);
            if(!RandomUtil.choiceByRate(rate)) continue;

            // 数量 10-100
            String amountRange = conArr[1];
            int amount;
            if(amountRange.contains("-")) {
                String[] split = StringUtils.split(amountRange, "-");
                amount = RandomUtil.get(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
            } else {
                amount = Integer.parseInt(amountRange);
            }

            // 创建礼物
            GiftAbs gift = createGift(giftConfig, amount);
            if(gift!=null) {
                // 添加礼物明细持久化
                GiftItem item = new GiftItem();
                item.setItemKey(giftKey);
                item.setAmount(amount);
                item.setGiftName(giftConfig.getDbName());
                item.setItemType(1);// 礼物类型
                item.setPercent(rate);
                item.setRecordId(record.getId());
                giftItemDbList.add(item);
                giftAbsList.add(gift);
                // 记录特殊礼物
                if(giftConfig.isBroadcast()) {
                    specialGiftStr.append(giftConfig.getDbName()).append("、");
                }
            } else {
                AesopPlugin.logger.log( "&c礼物【"+giftKey+"】构建失败", ConsoleLogger.Level.ERROR);
            }
        }

        if(giftItemDbList.size()<1) {
            AesopPlugin.logger.log( "&c没有检测到礼物", ConsoleLogger.Level.ERROR);
            return;
        }
        // 数据库持久化
        // 包含进事务块
        GiftClaimRecord finalRecord = record;
        AesopPlugin.callInTransaction(()->{
            // 保存领取的物品条目
            giftItemDao.create(giftItemDbList);
            // 更新主记录领取状态为1
            finalRecord.setClaimed(1);
            giftClaimRecordDao.update(finalRecord);
            return null;
        });

        // ==========》 送出礼物,添加物品到玩家背包
        for (GiftAbs giftAbs : giftAbsList) {
            giftAbs.send(player);
        }

        // 发送消息提醒
        try {
            player.playSound(player, Sound.valueOf(eventConfig.getClaimedSound()), 50, 1);
        } catch (Throwable e) {
            AesopPlugin.logger.log( "&c声音 "+eventConfig.getClaimedSound() + "播放失败", ConsoleLogger.Level.ERROR);
        }
        String claimedMsg = eventConfig.getClaimedMsg();
        claimedMsg =  claimedMsg.replaceAll("\\{eventName\\}", eventConfig.getEventName());
        AesopPlugin.logger.log(player, claimedMsg);
        AesopPlugin.logger.log(player, "&d------=== 礼物列表 ===------");
        int i=1;
        for (GiftItem giftItem : giftItemDbList) {
            AesopPlugin.logger.log(player, "&e" + i + ". " + giftItem.getGiftName() + " x " + giftItem.getAmount() + " （获得概率"+giftItem.getPercent() + "%）");
        }
        AesopPlugin.logger.log(player, "&d---------------------------------");

        // 广播特殊礼物
        if(specialGiftStr.length()>0) {
            String str = specialGiftStr.substring(0, specialGiftStr.length() - 1);
            Bukkit.broadcastMessage(MessageUtil.colorize("&d&l活动广播消息 >> &d玩家 " +player.getName()+" 在"+eventConfig.getEventName()+"收到了一份特殊礼物：&6"+str));
        }

        AesopPlugin.logger.log("玩家"+player.getName()+"成功领取"+eventConfig.getEventName()+"礼物。");
    }

    /**
     * 构建礼物
     * @param giftConfig 礼物配置
     * @param amount 数量
     * @return
     */
    private static @CheckForNull GiftAbs createGift(AGiftConfig giftConfig, int amount) {
        if(giftConfig.getType()==1) {
            // 实体礼物
            ItemStack itemStack = ItemStackUtil.createItemStack(persistentGiftPrefix, giftConfig.getKey(), giftConfig.getMaterial(),
                    giftConfig.getDisplayName(), amount, giftConfig.getLore(), giftConfig.getEnchants(), giftConfig.getPotions());
            return new ItemGift(giftConfig.getKey(), itemStack);
        } else if(giftConfig.getType()==2) {
            // 虚拟礼物
            // 虚拟礼物数量将没有意义 amount
            return new VirtualGift(giftConfig.getKey(), giftConfig.getCommands());
        }
        return null;
    }


    /**
     * 判断是否全部满足条件
     * @param condition 当前条件
     * @param recordId 主记录表id
     * @param conItemMaps 全部条件配置
     * @return
     * @throws SQLException
     */
    private static AndCondition judgmentCondition(List<String> condition, Long recordId, Map<String, AConItemConfig> conItemMaps) throws SQLException {
        GiftItemDao giftItemDao = AesopPlugin.getInstance().getDatabase().getGiftItemDao();

        // 条件为空，表示满足条件
        if(condition==null || condition.size()<1) return new AndCondition(true, "");

        Map<String, Boolean> keyFlag = new HashMap<>();
        StringBuilder collectMsg = new StringBuilder();

        for (String itemKey : condition) {
            long count = giftItemDao.countByItemKey(recordId, 2, itemKey);
            // TODO 暂时是AND的逻辑，即所有条件都满足才可以领取礼物
            AConItemConfig itemConfig = conItemMaps.get(itemKey);
            if(count>0) {
                // 条件满足
                keyFlag.put(itemKey, true);
                collectMsg.append("&a").append(itemConfig.getDbName()).append("、");
            } else {
                // 条件不满足
                keyFlag.put(itemKey, false);
                collectMsg.append("&c").append(itemConfig.getDbName()).append("、");
            }
        }
        String process = collectMsg.substring(0, collectMsg.length() - 1);

        if(keyFlag.containsValue(false)) {
            // 有一个条件不满足，全部不成功
            return new AndCondition(false, process);
        } else {
            // 全部条件满足，返回成功
            return new AndCondition(true, process);
        }
    }
}
