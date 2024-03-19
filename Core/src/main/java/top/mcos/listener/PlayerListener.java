package top.mcos.listener;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import top.mcos.AesopPlugin;
import top.mcos.business.yanhua.YanHuaEvent;
import top.mcos.business.activity.christmas.NSKeys;
import top.mcos.business.firework.FireWorkManage;
import top.mcos.business.itmebind.ItemEvent;
import top.mcos.config.ConfigLoader;
import top.mcos.util.MessageUtil;
import top.mcos.util.PlayerUtil;
import top.mcos.util.RandomUtil;

import java.util.Collection;
import java.util.Set;

public class PlayerListener implements Listener {

    /**
     * 玩家登录游戏事件
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(AesopPlugin.getInstance(), ()-> {
            //AesopPlugin.logger.log("玩家加入...");
            // 粒子特效缓存
            String uniqueId = event.getPlayer().getUniqueId().toString();
            FireWorkManage.getInstance().putPlayerFireworkToCache(uniqueId);


        });

        // 玩家加入游戏 ，发送消息提醒
        if(ConfigLoader.baseConfig.getSettingConfig().isJoinMessageEnable()) {
            String message = ConfigLoader.baseConfig.getSettingConfig().getJoinMessageMessage();
            message = message.replace("{player}", event.getPlayer().getName());
            event.setJoinMessage(MessageUtil.colorize(message));

            String sound = ConfigLoader.baseConfig.getSettingConfig().getJoinMessageSound();
            if(StringUtils.isNotBlank(sound)) {
                Sound s = Sound.valueOf(sound);
                Collection<? extends Player> players = Bukkit.getOnlinePlayers();
                for (Player player : players) {
                    player.playSound(player, s, 50, 1);
                }
            }
            //Bukkit.broadcastMessage(MessageUtil.colorize(message));
        }
    }

    /**
     * 玩家退出事件
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(AesopPlugin.getInstance(), ()-> {
            //AesopPlugin.logger.log("玩家退出...");
            // 移除消息锁
            String uniqueId = event.getPlayer().getUniqueId().toString();
            PlayerLock.removeLock(uniqueId);
            // 移除粒子特效
            FireWorkManage.getInstance().removePlayerFireworkFromCache(uniqueId);
        });

        // 玩家离开游戏 ，发送消息提醒
        if(ConfigLoader.baseConfig.getSettingConfig().isLeaveMessageEnable()) {
            String message = ConfigLoader.baseConfig.getSettingConfig().getLeaveMessageMessage();
            message = message.replace("{player}", event.getPlayer().getName());
            event.setQuitMessage(MessageUtil.colorize(message));
            //Bukkit.broadcastMessage(MessageUtil.colorize(message));
        }
    }

    // 玩家点击右键事件
    //@EventHandler
    //public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
    //
    //}

    /**
     * 击打伤害事件
     * @param event
     */
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();

        Bukkit.getScheduler().runTaskAsynchronously(AesopPlugin.getInstance(), ()-> {
            if(damager instanceof Player damagerPlayer && entity instanceof Player entityPlayer) {
                // 获得使用的物品
                ItemStack itemInHand = damagerPlayer.getInventory().getItemInMainHand();
                if(itemInHand.getItemMeta() != null) {
                    Boolean isTwig = itemInHand.getItemMeta().getPersistentDataContainer().get(NSKeys.ACTIVITY_TWIG_ITEM, PersistentDataType.BOOLEAN);
                    if (isTwig != null && isTwig) {
                        //AesopPlugin.logger.log("圣诞树枝攻击事件， damager:" + damager.getName() +", entity:" + entity.getName());
                        // 是圣诞树枝，添加药水效果（漂浮1-5秒）
                        int duration = RandomUtil.get(20, 100); // 单位tick
                        Bukkit.getScheduler().runTask(AesopPlugin.getInstance(), ()->{
                            entityPlayer.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, duration, 2, true, true));
                        });
                    }
                }
            }
        });

        //if("Location{world=CraftWorld{name=world},x=26.0,y=64.0,z=16.0,pitch=0.0,yaw=0.0}".equals(clicked.getLocation().toString())) {
        //player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 100, 2, true, true));
        //}
    }

    /**
     * 玩家对物品进行右键或左键交互事件
     * @param event
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // 禁止物品的使用
        //event.setUseItemInHand(Event.Result.DENY);
        //event.setUseInteractedBlock(Event.Result.DENY);
        Bukkit.getScheduler().runTaskAsynchronously(AesopPlugin.getInstance(), ()->{
            // 以玩家唯一id做同步处理
            String playerId = player.getUniqueId().toString();
            synchronized (AesopPlugin.sync.intern(playerId)) {
                // TODO 监听给玩家事件
                ItemEvent.triggerEvent(player, event);

                // 监听玩家右键方块 TODO 已重构 至 -> ItemEvent.triggerEvent(player, event);
                //if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                //    Block clicked = event.getClickedBlock();
                //
                //    // event.getClickedBlock() can return nothing, i.e. 'null'.
                //    // Just in-case we'll check to make sure.
                //    if (clicked != null) {
                //        // 判断按钮类型
                //        // The OR logic gate does exist, however, you used it wrong. '||' instead of '<='
                //        // The if statement evaluates Booleans (true/false), so you must use OR with booleans.
                //        //if (clicked.getType() == Material.STONE_BUTTON || clicked.getType() == Material.OAK_BUTTON) {
                //        //System.out.println("按下按钮");
                //        //}
                //        // Alternatively, you can do something like this
                //        if (clicked.getType().name().endsWith("_BUTTON")) {
                //            String clickedLocation = clicked.getLocation().toString();
                //            BaseConfig baseConfig = ConfigLoader.baseConfig;
                //            List<String> locations = baseConfig.getSettingConfig().getActHandleBlockLocations();
                //            List<String> sbLocations = baseConfig.getSettingConfig().getActHandleSnowballButtonLocations();
                //
                //            // 判断是不是礼物按钮坐标
                //            if (locations.size() > 0 && locations.contains(clickedLocation)) {
                //                handleGiftBtnEvent(player);
                //            }
                //            // 判断是不是雪球按钮坐标
                //            if (sbLocations.size() > 0 && sbLocations.contains(clickedLocation)) {
                //                handleSnowballBtnEvent(player, clickedLocation);
                //            }
                //            //CustomBlockData customBlockData = new CustomBlockData(clicked, AesopPlugin.getInstance());
                //            //Boolean aBoolean = customBlockData.get(NSKeys.ACTIVITY_BUTTON_FLAG, PersistentDataType.BOOLEAN);
                //            //AesopPlugin.logger.log("查看方块数据：" + aBoolean);
                //        }
                //    }
                //}
            }
        });
    }

    /**
     * 监听玩家放置方块
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(AesopPlugin.getInstance(), ()->{
            Block block = e.getBlock();
            //Player player = e.getPlayer();

            // todo 将block的位置保存到数据库

            ItemStack itemInHand = e.getItemInHand();
            ItemMeta itemMeta = itemInHand.getItemMeta();
            if(itemMeta!=null) {
                //BaseConfig baseConfig = ConfigLoader.baseConfig;

                PersistentDataContainer container = itemMeta.getPersistentDataContainer();
                Set<NamespacedKey> namespacedKeys = container.getKeys();
                for (NamespacedKey namespace : namespacedKeys) {
                    if (ItemEvent.persistentKeyPrefix.equalsIgnoreCase(namespace.getKey())) {
                        // 识别为物品绑定事件
                        ItemEvent.onBlockPlace(block, itemInHand, namespace);
                    } else if (YanHuaEvent.persistentKeyPrefix.equalsIgnoreCase(namespace.getKey())) {
                        // 识别为烟花桩
                        YanHuaEvent.onBlockPlace(block, itemInHand, namespace);
                    }
                    //if(NSKeys.ACTIVITY_GIFT_BUTTON.getKey().equalsIgnoreCase(namespace.getKey())) {
                    //    // 识别为活动按钮
                    //    Boolean giftBtn = container.get(NSKeys.ACTIVITY_GIFT_BUTTON, PersistentDataType.BOOLEAN);
                    //    if (giftBtn != null && giftBtn) {
                    //        // 将按钮坐标保存至配置文件
                    //        List<String> locations = baseConfig.getSettingConfig().getActHandleBlockLocations();
                    //        locations.add(block.getLocation().toString());
                    //        ConfigLoader.saveConfig(baseConfig);
                    //        //AesopPlugin.logger.log("手持物品名称：" + itemMeta.getDisplayName());
                    //        //AesopPlugin.logger.log("放置位置：" + block.getLocation().toString());
                    //    }
                    //} else if(NSKeys.ACTIVITY_SNOWBALL_BUTTON.getKey().equalsIgnoreCase(namespace.getKey())) {
                    //    Boolean snowballBtn = container.get(NSKeys.ACTIVITY_SNOWBALL_BUTTON, PersistentDataType.BOOLEAN);
                    //    if(snowballBtn!=null && snowballBtn) {
                    //        // 识别为活动雪球按钮
                    //        List<String> locations = baseConfig.getSettingConfig().getActHandleSnowballButtonLocations();
                    //        locations.add(block.getLocation().toString());
                    //        ConfigLoader.saveConfig(baseConfig);
                    //    }
                    //}

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

    /**
     * 方块打碎事件
     */
    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent e) {
        //System.out.println("监听打碎");
        //Player p = e.getPlayer();
        Block block = e.getBlock();

        Bukkit.getScheduler().runTaskAsynchronously(AesopPlugin.getInstance(), ()->{
            // 移除烟花发射点
            YanHuaEvent.onBlockBreakEvent(block);
            // 移除物品按钮绑定事件
            ItemEvent.onBlockBreakEvent(block);
            // TODO 移除礼物按钮

        });

    }

    ///**
    // * 方块破坏损坏事件
    // */
    //@EventHandler
    //public void onBlockDamage(BlockDamageEvent e) {
    //    //Block block = e.getBlock();
    //}

    //private void handleGiftBtnEvent(Player player) {
    //    // 读取数据库判断是否已经领取过
    //    try {
    //        GiftClaimRecordDao giftClaimRecordDao = AesopPlugin.getInstance().getDatabase().getGiftClaimRecordDao();
    //        GiftItemDao giftItemDao = AesopPlugin.getInstance().getDatabase().getGiftItemDao();
    //        BaseConfig baseConfig = ConfigLoader.baseConfig;
    //
    //        int snowballCount = ConfigLoader.baseConfig.getSettingConfig().getActSnowballCount();
    //        // 校验前置条件：收集气N个圣诞雪球
    //        long collectedCount = giftClaimRecordDao.countBySnowball(player.getUniqueId().toString());
    //        if(collectedCount < snowballCount && !player.isOp()) {
    //            AesopPlugin.logger.log(player, "&c请先收集"+snowballCount+"个&b❄圣诞雪球&c，您当前还差&e"+(snowballCount-collectedCount)+"&c个！");
    //            return;
    //        }
    //        // 校验重复领取
    //        long count = giftClaimRecordDao.countByGift(player.getUniqueId().toString());
    //        if (count > 0 && !player.isOp()) {
    //            AesopPlugin.logger.log(player, "&c您已经领取过该礼物了，请不要重复领取哦！~~");
    //            return;
    //        }
    //        // 检查是否有足够的背包空间
    //        ItemStack[] storageContents = player.getInventory().getStorageContents();
    //        int emptyClotCount = 0;
    //        for (ItemStack storageContent : storageContents) {
    //            if(storageContent==null) emptyClotCount++;
    //        }
    //        if(emptyClotCount<8) {
    //            AesopPlugin.logger.log(player, "&c背包空间不足，请至少留出8个槽位");
    //            return;
    //        }
    //        //ItemStack box = new ItemStack(Material.MAGENTA_SHULKER_BOX, 1);
    //
    //        List<ItemStack> itemStacks = new ArrayList<>();
    //        List<GiftItem> giftItems = new ArrayList<>();
    //
    //        Gift.giveItemCookie(itemStacks, giftItems, 20);
    //        Gift.giveItemApple(itemStacks, giftItems, 10);
    //        Gift.giveItemPotion(itemStacks, giftItems, 2);
    //        Gift.giveItemCrateMyGoldKey(itemStacks, giftItems, 10);
    //        Gift.giveMoneyAndPoint(player, giftItems, 1);
    //
    //        //以下奖品，根据概率获得
    //        int pick = RandomUtil.get(1, 100);
    //        if(pick>9 && pick<=60) {
    //            GiftItem it1 = Gift.giveItemCrateMyDepBlueKey(itemStacks, giftItems, 1);
    //            GiftItem it2 = Gift.giveItemCrateMyDepRedKey(itemStacks, giftItems, 1);
    //            if(it1!=null)it1.setPercent(50);// 设置中将概率
    //            if(it2!=null)it2.setPercent(50);// 设置中将概率
    //        } else if(pick>60 && pick<=80) {
    //            GiftItem it = Gift.giveDeluxeTags(player, giftItems, 1);
    //            if(it!=null)it.setPercent(20);// 设置中将概率
    //        } else if(pick>80 && pick<=100) {
    //            GiftItem it = Gift.giveItemTwig(itemStacks, giftItems, 1);
    //            if(it!=null)it.setPercent(20);// 设置中将概率
    //        }
    //
    //        // 包含进事务块
    //        AesopPlugin.callInTransaction(()->{
    //            // 保存领取记录 TODO
    //            GiftClaimRecord giftlog = giftClaimRecordDao.saveRecord(player.getUniqueId().toString(),
    //                    player.getName(), player.getAddress().getHostName(), null, "圣诞礼物", GiftTypeEnum.CHRISTMAS_GIFT.getIndex());
    //            giftItems.forEach(item -> {
    //                item.setRecordId(giftlog.getId());
    //            });
    //            // 保存领取的物品条目
    //            giftItemDao.create(giftItems);
    //
    //            // 添加物品到玩家背包
    //            player.getInventory().addItem(itemStacks.toArray(new ItemStack[]{}));
    //
    //            return null;
    //        });
    //
    //        try {
    //            player.playSound(player, Sound.valueOf(baseConfig.getSettingConfig().getClaimedSound()), 50, 1);
    //        } catch (Throwable e) {
    //            AesopPlugin.logger.log( "&c声音 "+baseConfig.getSettingConfig().getClaimedSound() + "播放失败", ConsoleLogger.Level.ERROR);
    //        }
    //
    //        // 发送领取信息
    //        StringBuilder specialGiftStr = new StringBuilder();
    //        AesopPlugin.logger.log(player, "&a恭喜您^_^，成功领取一份圣诞节礼物，请检查你的背包！");
    //        AesopPlugin.logger.log(player, "&d❄☆·͙̥‧‧̩̥·‧•̥̩̥͙‧·‧̩̥☆❄ ₍⁽ˊᵕˋ⁾₎ ❄☆·͙̥‧‧̩̥·‧•̥̩̥͙‧·‧̩̥☆❄");
    //        for (GiftItem giftItem : giftItems) {
    //            boolean isPercent = giftItem.getPercent()!=null && giftItem.getPercent()>0;
    //            AesopPlugin.logger.log(player, "&d❄==> " + (isPercent ? "&6" : "&e")
    //                    + giftItem.getGiftName() + " x " + giftItem.getAmount() + " "
    //                    + (isPercent?" -（获得概率"+giftItem.getPercent() + "%）": ""));
    //            if(isPercent) {
    //                specialGiftStr.append(giftItem.getGiftName()).append("、");
    //            }
    //        }
    //        AesopPlugin.logger.log(player, "&d❄☆·͙̥‧‧̩̥·‧•̥̩̥͙‧·‧̩̥☆❄ ₍⁽ˊᵕˋ⁾₎ ❄☆·͙̥‧‧̩̥·‧•̥̩̥͙‧·‧̩̥☆❄");
    //
    //        // 广播特殊礼物
    //        if(specialGiftStr.length()>0) {
    //            String str = specialGiftStr.substring(0, specialGiftStr.length() - 1);
    //            Bukkit.broadcastMessage(MessageUtil.colorize("&d玩家 " +player.getName()+" 在圣诞节活动中收到了一份特殊礼物："+str));
    //        }
    //
    //        if(ConfigLoader.baseConfig.getSettingConfig().isDebug()) {
    //            String hostString = player.getAddress().getHostName();
    //            AesopPlugin.logger.log(hostString + "玩家"+player.getName()+"领取物品成功。");
    //        }
    //
    //    } catch (SQLException e) {
    //        e.printStackTrace();
    //    }
    //}

    //private void handleSnowballBtnEvent(Player player, String clickedLocation) {
    //    try {
    //        // 开启事务
    //        AesopPlugin.callInTransaction(()->{
    //            String playerId = player.getUniqueId().toString();
    //            GiftClaimRecordDao giftClaimRecordDao = AesopPlugin.getInstance().getDatabase().getGiftClaimRecordDao();
    //            int snowballCount = ConfigLoader.baseConfig.getSettingConfig().getActSnowballCount();
    //
    //            long count = giftClaimRecordDao.countBySnowball(playerId, clickedLocation);
    //            if (count > 0) {
    //                AesopPlugin.logger.log(player, "&c该位置您已收集过，到其它地方找找看吧！");
    //                return null;
    //            }
    //
    //            // 创建一个雪球，该雪球只是一个象征性意义，丢弃不影响收集进度
    //            ItemStack itemStack = new ItemStack(Material.SNOWBALL, 1);
    //            ItemMeta itemMeta = itemStack.getItemMeta();
    //            if (itemMeta != null) {
    //                itemMeta.setDisplayName(MessageUtil.symbol("&b❄圣诞雪球"));
    //                List<String> lore = new ArrayList<>();
    //                lore.add(MessageUtil.symbol("&7一个象征性意义的雪球，丢弃不影响收集进度"));
    //                itemMeta.setLore(lore);
    //                itemStack.setItemMeta(itemMeta);
    //            }
    //            player.getInventory().addItem(itemStack);
    //            try {
    //                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 50, 1);
    //            } catch (Throwable e) {
    //                AesopPlugin.logger.log("&c声音 BLOCK_NOTE_BLOCK_BELL 播放失败", ConsoleLogger.Level.ERROR);
    //            }
    //            //记录收集记录
    //            giftClaimRecordDao.saveSnowball(playerId, player.getName(), player.getAddress().getHostName(), clickedLocation);
    //
    //            // 统计收集数据
    //            long collectedCount = giftClaimRecordDao.countBySnowball(player.getUniqueId().toString());
    //            if (collectedCount >= snowballCount) {
    //                AesopPlugin.logger.log(player, "&a获得一个&b❄圣诞雪球&a，您已集齐" + snowballCount + "个，&a快去领取圣诞礼物吧~");
    //            } else {
    //                AesopPlugin.logger.log(player, "&a获得一个&b❄圣诞雪球&a，当前进度 " + snowballCount + "(&e" + (collectedCount) + "&a).");
    //            }
    //            return null;
    //        });
    //    } catch (SQLException e) {
    //        e.printStackTrace();
    //    }
    //}


}
