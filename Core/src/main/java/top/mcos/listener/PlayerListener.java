package top.mcos.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import top.mcos.AesopPlugin;
import top.mcos.activity.Gift;
import top.mcos.config.ConfigLoader;
import top.mcos.config.activitiy.NSKeys;
import top.mcos.config.configs.BaseConfig;
import top.mcos.database.dao.GiftClaimRecordDao;
import top.mcos.database.dao.GiftItemDao;
import top.mcos.database.domain.GiftClaimRecord;
import top.mcos.database.domain.GiftItem;
import top.mcos.util.MessageUtil;
import top.mcos.util.RandomUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlayerListener implements Listener {

    //@EventHandler(priority = EventPriority.HIGH)
    //public void onPlayerJoin(PlayerJoinEvent event) {
    //    String uniqueId = event.getPlayer().getUniqueId().toString();
    //    playLocks.put(uniqueId, new ReentrantLock());
    //}

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        PlayerLock.removeLock(event.getPlayer().getUniqueId().toString());
    }

    // 玩家点击右键事件
    //@EventHandler
    //public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
    //
    //}

    /**
     * 玩家对物品进行右键或左键交互事件
     * @param event
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskAsynchronously(AesopPlugin.getInstance(), ()->{
            // 以玩家唯一id做同步处理
            synchronized (player.getUniqueId().toString()) {
                // 监听玩家右键方块
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    Block clicked = event.getClickedBlock();

                    // event.getClickedBlock() can return nothing, i.e. 'null'.
                    // Just in-case we'll check to make sure.
                    if (clicked != null) {
                        // 判断按钮类型
                        // The OR logic gate does exist, however, you used it wrong. '||' instead of '<='
                        // The if statement evaluates Booleans (true/false), so you must use OR with booleans.
                        //if (clicked.getType() == Material.STONE_BUTTON || clicked.getType() == Material.OAK_BUTTON) {
                        //System.out.println("按下按钮");
                        //}
                        // Alternatively, you can do something like this
                        if (clicked.getType().name().endsWith("_BUTTON")) {
                            String clickedLocation = clicked.getLocation().toString();
                            BaseConfig baseConfig = ConfigLoader.baseConfig;
                            List<String> locations = baseConfig.getActHandleBlockLocations();
                            List<String> sbLocations = baseConfig.getActHandleSnowballButtonLocations();

                            // 判断是不是礼物按钮坐标
                            if (locations.size() > 0 && locations.contains(clickedLocation)) {
                                handleGiftBtnEvent(player);
                            }
                            // 判断是不是雪球按钮坐标
                            if (sbLocations.size() > 0 && sbLocations.contains(clickedLocation)) {
                                handleSnowballBtnEvent(player, clickedLocation);
                            }
                            //CustomBlockData customBlockData = new CustomBlockData(clicked, AesopPlugin.getInstance());
                            //Boolean aBoolean = customBlockData.get(NSKeys.ACTIVITY_BUTTON_FLAG, PersistentDataType.BOOLEAN);
                            //AesopPlugin.logger.log("查看方块数据：" + aBoolean);
                        }
                    }
                }
            }
        });
    }

    private void handleGiftBtnEvent(Player player) {
        // TODO 添加事务管理
        // 读取数据库判断是否已经领取过
        try {
            GiftClaimRecordDao giftClaimRecordDao = AesopPlugin.getInstance().getDatabase().getGiftClaimRecordDao();
            GiftItemDao giftItemDao = AesopPlugin.getInstance().getDatabase().getGiftItemDao();

            int snowballCount = ConfigLoader.baseConfig.getActSnowballCount();
            // 校验前置条件：收集气N个圣诞雪球
            long collectedCount = giftClaimRecordDao.countBySnowball(player.getUniqueId().toString());
            if(collectedCount < snowballCount && !player.isOp()) {
                AesopPlugin.logger.log(player, "&c请先收集"+snowballCount+"个&b❄圣诞雪球&c，您当前还差&e"+(snowballCount-collectedCount)+"&c个！");
                return;
            }
            // 校验重复领取
            long count = giftClaimRecordDao.countByGift(player.getUniqueId().toString());
            if (count > 0 && !player.isOp()) {
                AesopPlugin.logger.log(player, "&c您已经领取过该礼物了，请不要重复领取哦！~~");
                return;
            }
            // 检查是否有足够的背包空间
            ItemStack[] storageContents = player.getInventory().getStorageContents();
            int emptyClotCount = 0;
            for (ItemStack storageContent : storageContents) {
                if(storageContent==null) emptyClotCount++;
            }
            if(emptyClotCount<8) {
                AesopPlugin.logger.log(player, "&c背包空间不足，请至少留出8个槽位");
                return;
            }
            //ItemStack box = new ItemStack(Material.MAGENTA_SHULKER_BOX, 1);

            List<ItemStack> itemStacks = new ArrayList<>();
            List<GiftItem> giftItems = new ArrayList<>();

            Gift.giveItemCookie(itemStacks, giftItems, 20);
            Gift.giveItemApple(itemStacks, giftItems, 10);
            Gift.giveItemPotion(itemStacks, giftItems, 2);
            Gift.giveItemCrateMyGoldKey(itemStacks, giftItems, 10);
            Gift.giveMoneyAndPoint(player, giftItems, 1);

            //以下奖品，根据概率获得
            int pick = RandomUtil.get(1, 100);
            if(pick>9 && pick<=60) {
                GiftItem it1 = Gift.giveItemCrateMyDepBlueKey(itemStacks, giftItems, 1);
                GiftItem it2 = Gift.giveItemCrateMyDepRedKey(itemStacks, giftItems, 1);
                if(it1!=null)it1.setPercent(50);// 设置中将概率
                if(it2!=null)it2.setPercent(50);// 设置中将概率
            } else if(pick>60 && pick<=80) {
                GiftItem it = Gift.giveDeluxeTags(player, giftItems, 1);
                if(it!=null)it.setPercent(20);// 设置中将概率
            } else if(pick>80 && pick<=100) {
                GiftItem it = Gift.giveItemTwig(itemStacks, giftItems, 1);
                if(it!=null)it.setPercent(20);// 设置中将概率
            }

            // 添加物品到玩家背包
            player.getInventory().addItem(itemStacks.toArray(new ItemStack[]{}));

            // 保存领取记录
            GiftClaimRecord giftlog = giftClaimRecordDao.saveGift(player.getUniqueId().toString(),
                    player.getName(), player.getAddress().getHostName());
            giftItems.forEach(item -> {
                item.setRecordId(giftlog.getId());
            });
            // 保存领取的物品条目
            giftItemDao.create(giftItems);

            AesopPlugin.logger.log(player, "&a恭喜您^_^，成功领取一份圣诞节礼物，请检查你的背包！");
            AesopPlugin.logger.log(player, "&d❄☆·͙̥‧‧̩̥·‧•̥̩̥͙‧·‧̩̥☆❄ ₍⁽ˊᵕˋ⁾₎ ❄☆·͙̥‧‧̩̥·‧•̥̩̥͙‧·‧̩̥☆❄");
            for (GiftItem giftItem : giftItems) {
                boolean isPercent = giftItem.getPercent()!=null && giftItem.getPercent()>0;
                AesopPlugin.logger.log(player, "&d❄==> " + (isPercent ? "&6" : "&e")
                        + giftItem.getGiftName() + " x " + giftItem.getAmount() + " "
                        + (isPercent?" -（获得概率"+giftItem.getPercent() + "%）": ""));
            }
            AesopPlugin.logger.log(player, "&d❄☆·͙̥‧‧̩̥·‧•̥̩̥͙‧·‧̩̥☆❄ ₍⁽ˊᵕˋ⁾₎ ❄☆·͙̥‧‧̩̥·‧•̥̩̥͙‧·‧̩̥☆❄");

            if(ConfigLoader.baseConfig.isDebug()) {
                String hostString = player.getAddress().getHostName();
                AesopPlugin.logger.log(hostString + "玩家"+player.getName()+"领取物品成功。");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleSnowballBtnEvent(Player player, String clickedLocation) {
        try {
            GiftClaimRecordDao giftClaimRecordDao = AesopPlugin.getInstance().getDatabase().getGiftClaimRecordDao();
            String playerId = player.getUniqueId().toString();
            int snowballCount = ConfigLoader.baseConfig.getActSnowballCount();

            long count = giftClaimRecordDao.countBySnowball(playerId, clickedLocation);
            if (count > 0) {
                AesopPlugin.logger.log(player, "&c该位置您已收集过，到其它地方找找看吧！");
                return;
            }

            // 创建一个雪球，该雪球只是一个象征性意义，丢弃不影响收集进度
            ItemStack itemStack = new ItemStack(Material.SNOWBALL, 1);
            ItemMeta itemMeta = itemStack.getItemMeta();
            if(itemMeta!=null) {
                itemMeta.setDisplayName(MessageUtil.symbol("&b❄圣诞雪球"));
                List<String> lore = new ArrayList<>();
                lore.add(MessageUtil.symbol("&7一个象征性意义的雪球，丢弃不影响收集进度"));
                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);
            }
            player.getInventory().addItem(itemStack);
            //记录收集记录
            giftClaimRecordDao.saveSnowball(playerId, player.getName(), player.getAddress().getHostName(), clickedLocation);

            // 统计收集数据
            long collectedCount = giftClaimRecordDao.countBySnowball(player.getUniqueId().toString());
            if(collectedCount>=snowballCount) {
                AesopPlugin.logger.log(player, "&a获得一个&b❄圣诞雪球&a，您已集齐"+snowballCount+"个，&a快去领取圣诞礼物吧~");
            } else {
                AesopPlugin.logger.log(player, "&a获得一个&b❄圣诞雪球&a，当前进度 " + snowballCount + "(&e" + (collectedCount) + "&a).");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * 监听玩家放置方块
     */
    @EventHandler
    private void onBlockPlace(BlockPlaceEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(AesopPlugin.getInstance(), ()->{
            Block block = e.getBlock();
            Player player = e.getPlayer();

            // todo 将block的位置保存到数据库

            ItemStack itemInHand = e.getItemInHand();
            ItemMeta itemMeta = itemInHand.getItemMeta();
            if(itemMeta!=null) {
                BaseConfig baseConfig = ConfigLoader.baseConfig;

                PersistentDataContainer con = itemMeta.getPersistentDataContainer();
                Boolean giftBtn = con.get(NSKeys.ACTIVITY_GIFT_BUTTON, PersistentDataType.BOOLEAN);
                Boolean snowballBtn = con.get(NSKeys.ACTIVITY_SNOWBALL_BUTTON, PersistentDataType.BOOLEAN);
                if (giftBtn != null && giftBtn) {
                    // 识别为活动按钮
                    // 将按钮坐标保存至配置文件
                    List<String> locations = baseConfig.getActHandleBlockLocations();
                    locations.add(block.getLocation().toString());
                    ConfigLoader.saveConfig(baseConfig);
                    //AesopPlugin.logger.log("手持物品名称：" + itemMeta.getDisplayName());
                    //AesopPlugin.logger.log("放置位置：" + block.getLocation().toString());
                }
                if(snowballBtn!=null && snowballBtn) {
                    List<String> locations = baseConfig.getActHandleSnowballButtonLocations();
                    locations.add(block.getLocation().toString());
                    ConfigLoader.saveConfig(baseConfig);
                }
            }
        });

        //Sign sign = (Sign) block.getState();
        //NamespacedKey namespacedKey = new NamespacedKey(AesopPlugin.getInstance(), "display");
        //String s = sign.getPersistentDataContainer().get(namespacedKey, PersistentDataType.STRING);
        //AesopPlugin.logger.log("结果数据：" + s);
        //
        // TODO https://www.spigotmc.org/threads/a-guide-to-1-14-persistentdataholder-api.371200/

        //Sign sign = (Sign) block.getState();
        //sign.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, "some data");
        //sign.update();

        //PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        //if(container.has(key , PersistentDataType.DOUBLE)) {
        //    double foundValue = container.get(key, PersistentDataType.DOUBLE);
        //}

    }

    ///**
    // * 方块破坏事件
    // * @param e
    // */
    //@EventHandler
    //private void onBlockDamage(BlockDamageEvent e) {
    //    Bukkit.getScheduler().runTaskAsynchronously(AesopPlugin.getInstance(), ()->{
    //        // TODO 移除礼物按钮
    //
    //        System.out.println("破坏。。。");
    //        Block block = e.getBlock();
    //        Location location = block.getLocation();
    //        System.out.println("破坏方块："+ location.toString());
    //
    //    });
    //}

}
