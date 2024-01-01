package top.mcos.command.subcommands;

import com.epicnicity322.epicpluginlib.bukkit.command.Command;
import com.epicnicity322.epicpluginlib.bukkit.command.CommandRunnable;
import com.epicnicity322.epicpluginlib.bukkit.command.TabCompleteRunnable;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mcos.AesopPlugin;
import top.mcos.activity.NSKeys;
import top.mcos.database.dao.GiftClaimRecordDao;
import top.mcos.database.dao.GiftItemDao;
import top.mcos.database.domain.GiftClaimRecord;
import top.mcos.database.domain.GiftItem;
import top.mcos.database.enums.GiftTypeEnum;
import top.mcos.hook.firework.FireWorkManage;

import java.sql.SQLException;
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
            possibleCompletions.add("give giftBtn"); // 获得礼物按钮
            possibleCompletions.add("give snowballBtn"); // 获得雪球按钮
            possibleCompletions.add("clear ");// 清理玩家数据 clear <playerName>
            possibleCompletions.add("list "); // 查找礼物领取列表 list <playerName>
            possibleCompletions.add("listitem "); // 查找礼物领取详情 listitem <playerName>
            possibleCompletions.add("spawn "); // 生成怪物
            possibleCompletions.add("preview"); // 预览粒子特效
            //if (args.length == 2) {
            //    for (String soundType : SoundType.getPresentSoundNames()) {
            //        if (soundType.startsWith(args[1].toUpperCase(Locale.ROOT))) {
            //            possibleCompletions.add(soundType);
            //        }
            //    }
            //} else if (args.length == 3) {
            //    CommandUtils.addTargetTabCompletion(possibleCompletions, args[2], sender, "playmoresounds.play.others");
            //}
        };
    }

    @Override
    public void run(@NotNull String label, @NotNull CommandSender sender, @NotNull String[] args) {
        if("give".equals(args[1])) {
            if(sender instanceof Player) {
                Player player = (Player) sender;
                String btnType = args.length>=3 && StringUtils.isNotBlank(args[2]) ? args[2] : null;
                if ("giftBtn".equals(btnType)) {
                    ItemStack itemStack = new ItemStack(Material.CRIMSON_BUTTON, 1);
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.getPersistentDataContainer().set(NSKeys.ACTIVITY_GIFT_BUTTON, PersistentDataType.BOOLEAN, true);
                    itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&9【圣诞节&d活&c动&9】 &7| &e-》&a抽取礼物&e《-"));
                    itemStack.setItemMeta(itemMeta);
                    player.getInventory().addItem(itemStack);
                } else if ("snowballBtn".equals(btnType)) {
                    ItemStack itemStack = new ItemStack(Material.STONE_BUTTON, 1);
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.getPersistentDataContainer().set(NSKeys.ACTIVITY_SNOWBALL_BUTTON, PersistentDataType.BOOLEAN, true);
                    itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&9【圣诞节&d活&c动&9】 &7| &e-》&a圣诞雪球&e《-"));
                    itemStack.setItemMeta(itemMeta);
                    player.getInventory().addItem(itemStack);
                }
            }
        } else if("clear".equals(args[1])) {
            try {
                // 清理玩家数据
                String playerName = args.length>=3 && StringUtils.isNotBlank(args[2]) ? args[2] : null;
                if(playerName==null) {
                    AesopPlugin.logger.log(sender, "&c请指定一个玩家名称");
                    return;
                }

                GiftClaimRecordDao giftClaimRecordDao = AesopPlugin.getInstance().getDatabase().getGiftClaimRecordDao();
                GiftItemDao giftItemDao = AesopPlugin.getInstance().getDatabase().getGiftItemDao();

                List<GiftClaimRecord> recordList = giftClaimRecordDao.queryBuilder().selectColumns("id").where().eq("playerName", playerName).query();
                List<Long> recordIds = recordList.stream().map(GiftClaimRecord::getId).toList();

                List<GiftItem> items = giftItemDao.queryBuilder().selectColumns("id").where().in("recordId", recordIds).query();
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
                String playerName = args.length>=3 && StringUtils.isNotBlank(args[2]) ? args[2] : null;
                GiftClaimRecordDao giftClaimRecordDao = AesopPlugin.getInstance().getDatabase().getGiftClaimRecordDao();

                List<GiftClaimRecord> list = giftClaimRecordDao.list(GiftTypeEnum.CHRISTMAS_GIFT, playerName, "createTime", false);
                AesopPlugin.logger.log(sender, "&b =>玩家 "+playerName+" 圣诞礼物领取记录：");
                AesopPlugin.logger.log(sender, "&2 玩家  |  领取时间  |  玩家ip");
                for (GiftClaimRecord record : list) {
                    AesopPlugin.logger.log(sender, "&2 "+record.getPlayerName()+"  |  "+record.getCreateTime()+"  |  "+record.getIpaddress());
                }
                AesopPlugin.logger.log(sender, "&b ------------------------------");
                // TODO 分页交互事件

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if("listitem".equals(args[1])){
            try {
                String playerName = args.length>=3 && StringUtils.isNotBlank(args[2]) ? args[2] : null;
                if(playerName==null) {
                    AesopPlugin.logger.log(sender, "&c只能查询指定玩家");
                    return;
                }
                GiftClaimRecordDao giftClaimRecordDao = AesopPlugin.getInstance().getDatabase().getGiftClaimRecordDao();
                GiftItemDao giftItemDao = AesopPlugin.getInstance().getDatabase().getGiftItemDao();

                List<GiftClaimRecord> list = giftClaimRecordDao.list(GiftTypeEnum.CHRISTMAS_GIFT, playerName, null, true);
                AesopPlugin.logger.log(sender, "&b =>玩家 "+playerName+" 的圣诞礼物领取详情：");
                for (GiftClaimRecord record : list) {
                    AesopPlugin.logger.log(sender, "&2 礼物名称  |  领取数量  |  获得概率% ");
                    List<GiftItem> items = giftItemDao.list(GiftTypeEnum.CHRISTMAS_GIFT, record.getId(), "id", true);
                    for (GiftItem item : items) {
                        AesopPlugin.logger.log(sender, "&2 "+item.getGiftName() + "  |  "+item.getAmount() +"  |  "+item.getPercent());
                    }
                    AesopPlugin.logger.log(sender, "&b ------------------------------");
                }
                AesopPlugin.logger.log(sender, "&b ------------------------------");
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
