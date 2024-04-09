package top.mcos.command.subcommands;

import top.mcos.util.epiclib.command.Command;
import top.mcos.util.epiclib.command.CommandRunnable;
import top.mcos.util.epiclib.command.TabCompleteRunnable;
import top.mcos.util.epiclib.logger.ConsoleLogger;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mcos.AesopPlugin;
import top.mcos.business.activity.ActivityEvent;
import top.mcos.business.activity.config.sub.AConItemConfig;
import top.mcos.business.activity.config.sub.AEventConfig;
import top.mcos.config.ConfigLoader;
import top.mcos.database.dao.GiftClaimRecordDao;
import top.mcos.database.dao.GiftItemDao;
import top.mcos.database.domain.GiftClaimRecord;
import top.mcos.database.domain.GiftItem;
import top.mcos.business.firework.FireWorkManage;
import top.mcos.util.PlayerUtil;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * 活动：/xxx act
 */
public final class ActivitySubCommand extends Command implements Helpable {
    @Override
    public @NotNull CommandRunnable onHelp() {
        return (label, sender, args) -> AesopPlugin.logger.log(sender, "&2活动指令: &a/"+label + " "+getName());
    }

    @Override
    public @Nullable String[] getAliases() {
        return new String[]{"act"};
    }

    @Override
    public @NotNull String getName() {
        return "act";
    }

    @Override
    public int getMinArgsAmount() {
        return 2;
    }

    @Override
    public @Nullable String getPermission() {
        return "aesopplugin.admin.activity";
    }

    @Override
    protected @Nullable CommandRunnable getNoPermissionRunnable() {
        return (label, sender, args) -> AesopPlugin.logger.log(sender, "&c您没有执行该命令的权限");
    }

    @Override
    protected @Nullable CommandRunnable getNotEnoughArgsRunnable() {
        return (label, sender, args) -> AesopPlugin.logger.log(sender, "&c参数不全");
    }

    @Override
    protected @Nullable TabCompleteRunnable getTabCompleteRunnable() {
        return (possibleCompletions, label, sender, args) -> {

            if(args.length==2) {
                //possibleCompletions.add("give giftBtn"); // 获得礼物按钮 TODO 废弃
                //possibleCompletions.add("give snowballBtn"); // 获得雪球按钮 TODO 废弃
                possibleCompletions.add("claim");// 领取礼物 act claim <活动key> <playerName>
                possibleCompletions.add("collect");// 收集物品 act collect <活动key> <条件物品key> <playerName>
                possibleCompletions.add("clear");// 清理玩家活动相关的所有数据 act clear <playerName>
                possibleCompletions.add("list"); // 查找玩家礼物领取列表 act list <活动key> <playerName>
                possibleCompletions.add("spawn"); // 生成怪物 TODO 测试
                possibleCompletions.add("preview"); // 预览粒子特效 TODO 测试
            }
            if(args.length==3) {
                if("claim,collect,list,listitem".contains(args[1])) {
                    possibleCompletions.addAll(ConfigLoader.activityConfig.getEvents().stream().map(AEventConfig::getKey).toList());
                }
                if("clear".contains(args[1])) {
                    possibleCompletions.addAll(PlayerUtil.getAllOnlinePlayerName());
                }
            }
            if(args.length==4) {
                if("collect".contains(args[1])) {
                    possibleCompletions.addAll(ConfigLoader.activityConfig.getConItemConfigs().stream().map(AConItemConfig::getKey).toList());
                }
                if("list,listitem".contains(args[1])) {
                    possibleCompletions.addAll(PlayerUtil.getAllOnlinePlayerName());
                }
                if("claim".contains(args[1])) {
                    possibleCompletions.addAll(PlayerUtil.getAllOnlinePlayerName());
                }
            }
            if(args.length==5) {
                if("collect".contains(args[1])) {
                    possibleCompletions.addAll(PlayerUtil.getAllOnlinePlayerName());
                }
            }
        };
    }

    @Override
    public void run(@NotNull String label, @NotNull CommandSender sender, @NotNull String[] args) {
        if("claim".equals(args[1])) {
            String eventKey = args[2];
            String playerName = args[3];
            Player onlinePlayer = PlayerUtil.getOnlinePlayer(playerName);
            if(onlinePlayer==null) {
                AesopPlugin.logger.log("&c玩家不存在", ConsoleLogger.Level.ERROR);
                return;
            }
            try {
                ActivityEvent.claimGift(eventKey, onlinePlayer);
            } catch (SQLException e) {
                e.printStackTrace();
                AesopPlugin.logger.log(sender, "&c领取礼物命令执行出错，完犊子了、、");
                AesopPlugin.logger.log("&c领取礼物命令执行出错");
            }
        } else if("collect".equals(args[1])) {
            String eventKey = args[2];
            String itemKey = args[3];
            String playerName = args[4];
            Player onlinePlayer = PlayerUtil.getOnlinePlayer(playerName);
            if(onlinePlayer==null) {
                AesopPlugin.logger.log("&c玩家不存在", ConsoleLogger.Level.ERROR);
                return;
            }
            try {
                ActivityEvent.collectItem(eventKey, itemKey, onlinePlayer);
            } catch (Exception e) {
                e.printStackTrace();
                AesopPlugin.logger.log(sender, "&c领取礼物命令执行出错，完犊子了、、");
                AesopPlugin.logger.log("&c领取礼物命令执行出错");
            }
        }
        //else if("give".equals(args[1])) {
        //    if(sender instanceof Player player) {
        //        String btnType = args.length>=3 && StringUtils.isNotBlank(args[2]) ? args[2] : null;
        //        if ("giftBtn".equals(btnType)) {
        //            ItemStack itemStack = new ItemStack(Material.CRIMSON_BUTTON, 1);
        //            ItemMeta itemMeta = itemStack.getItemMeta();
        //            itemMeta.getPersistentDataContainer().set(NSKeys.ACTIVITY_GIFT_BUTTON, PersistentDataType.BOOLEAN, true);
        //            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&9【圣诞节&d活&c动&9】 &7| &e-》&a抽取礼物&e《-"));
        //            itemStack.setItemMeta(itemMeta);
        //            player.getInventory().addItem(itemStack);
        //        } else if ("snowballBtn".equals(btnType)) {
        //            ItemStack itemStack = new ItemStack(Material.STONE_BUTTON, 1);
        //            ItemMeta itemMeta = itemStack.getItemMeta();
        //            itemMeta.getPersistentDataContainer().set(NSKeys.ACTIVITY_SNOWBALL_BUTTON, PersistentDataType.BOOLEAN, true);
        //            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&9【圣诞节&d活&c动&9】 &7| &e-》&a圣诞雪球&e《-"));
        //            itemStack.setItemMeta(itemMeta);
        //            player.getInventory().addItem(itemStack);
        //        }
        //    }
        //}
        else if("clear".equals(args[1])) {
            try {
                // 清理玩家数据
                String playerName = args.length>=3 && StringUtils.isNotBlank(args[2]) ? args[2] : null;
                if(playerName==null) {
                    AesopPlugin.logger.log(sender, "&c请指定一个玩家名称");
                    return;
                }

                GiftClaimRecordDao giftClaimRecordDao = AesopPlugin.getInstance().getDatabase().getGiftClaimRecordDao();
                GiftItemDao giftItemDao = AesopPlugin.getInstance().getDatabase().getGiftItemDao();

                List<GiftClaimRecord> recordList = giftClaimRecordDao.queryByPlayerName(playerName);
                List<Long> recordIds = recordList.stream().map(GiftClaimRecord::getId).toList();

                List<GiftItem> items = giftItemDao.queryByRecordIds(recordIds, null, null);
                List<Long> itemIds = items.stream().map(GiftItem::getId).toList();

                int i1 = giftClaimRecordDao.deleteIds(recordIds);
                int i2 = giftItemDao.deleteIds(itemIds);
                if(i1>0 || i2>0) {
                    AesopPlugin.logger.log(sender, "&a已删除玩家" + playerName + "数据.");
                } else {
                    AesopPlugin.logger.log(sender, "&a玩家" + playerName + "数据不存在,无需清理");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else if("list".equals(args[1])) {
            try {
                String eventKey = args.length >= 3 && StringUtils.isNotBlank(args[2]) ? args[2] : null;
                if (eventKey == null) {
                    AesopPlugin.logger.log(sender, "&c缺少活动key");
                    return;
                }
                // 若没有指定玩家，则查询所有
                String playerName = args.length >= 4 && StringUtils.isNotBlank(args[3]) ? args[3] : null;

                GiftClaimRecordDao giftClaimRecordDao = AesopPlugin.getInstance().getDatabase().getGiftClaimRecordDao();
                GiftItemDao giftItemDao = AesopPlugin.getInstance().getDatabase().getGiftItemDao();

                List<GiftClaimRecord> list = giftClaimRecordDao.listByPlayer(eventKey, playerName, false);
                AesopPlugin.logger.log(sender, "&b&l ============> " + (playerName==null? "所有玩家" : playerName) + " 活动领取记录");
                AesopPlugin.logger.log(sender, "&2 玩家  |  活动名称  |  领取时间  |  玩家ip");
                for (GiftClaimRecord record : list) {
                    AesopPlugin.logger.log(sender, "&2 " + record.getPlayerName() + "  |  " + record.getEventName() + "&2  |  " + record.getCreateTime() + "  |  " + record.getIpaddress());
                }
                AesopPlugin.logger.log(sender, " ");

                // 若指定了玩家，则把明细也查询出来
                if (playerName != null && list.size()>0) {
                    GiftClaimRecord record = list.get(0);
                    AesopPlugin.logger.log(sender, "&b&l ============> 礼物领取详情");
                    AesopPlugin.logger.log(sender, "&2 礼物名称  |  领取数量  |  获得概率% ");
                    List<GiftItem> items = giftItemDao.queryByRecordIds(Arrays.asList(record.getId()), 1, true);
                    for (GiftItem item : items) {
                        AesopPlugin.logger.log(sender, "&2 " + item.getGiftName() + "&2  |  " + item.getAmount() + "  |  " + item.getPercent());
                    }
                }

                // TODO 分页交互事件

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if("preview".equals(args[1])){
            Player player = (Player) sender;
            String effectName = args.length>=3 && StringUtils.isNotBlank(args[2]) ? args[2] : null;
            if(effectName==null) return;

            Particle particle;
            String particleName = args.length>=4 && StringUtils.isNotBlank(args[3]) ? args[3] : null;
            if(particleName==null) {
                particle = Particle.FLAME;
            } else {
                particle = Particle.valueOf(particleName);
            }


            FireWorkManage.getInstance().preview(player, effectName, particle);

            //AesopPlugin.getInstance().getFireWorkManage().spawnEffect(player);
        } else if("spawn".equals(args[1])){
            Player player = (Player) sender;
            String text = args.length>=3 && StringUtils.isNotBlank(args[2]) ? args[2] : null;
            String effectName = args.length>=4 && StringUtils.isNotBlank(args[3]) ? args[3] : null;
            if(effectName==null) return;
            //Bukkit.getWorld("world").getChunkAt().getEntities();
            //Spider spider = Bukkit.getWorld("world").spawn(player.getLocation(), Spider.class);
            //spider.setAI(true);
            //spider.setCustomName(MessageUtil.symbol("&c死亡蜘蛛"));
            //spider.setHealth(98000);
            //spider.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER));

            /*
            索尔有两只神奇山羊，分别是坦格里斯尼尔（Tanngrisnir）和坦格乔斯特（Tanngnjostr），它们负责帮助索尔的战车穿越天空。
            当他们带领索尔飞过村庄时是遭受到不明boss攻击，导致山羊群变异而陨落至此。击杀掉所有变异的山羊和boss，拯救村庄
             */

            //Goat goat = Bukkit.getWorld("world").spawn(player.getLocation(), Goat.class);
            //goat.setAI(true);
            //goat.setCustomName(MessageUtil.colorize("&c变异山羊"));
            //goat.setHealth(10);
            //goat.setSeed(10);
            //goat.setLastDamage(30);
            //goat.setScreaming(true); // 频繁叫唤
            //goat.setLeftHorn(true);// 左角
            //goat.setRightHorn(false); // 右角
            //AesopPlugin.getInstance().getFireWorkManage().spawnDragonEffect(text, Particle.valueOf(effectName), player, 300);
        } else {
            AesopPlugin.logger.log(sender, "&c参数有误");
        }
    }
}
