package top.mcos.command.subcommands;

import com.epicnicity322.epicpluginlib.bukkit.command.Command;
import com.epicnicity322.epicpluginlib.bukkit.command.CommandRunnable;
import com.epicnicity322.epicpluginlib.bukkit.command.TabCompleteRunnable;
import com.j256.ormlite.stmt.PreparedDelete;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mcos.AesopPlugin;
import top.mcos.config.activitiy.NSKeys;
import top.mcos.database.config.SqliteDatabase;
import top.mcos.database.dao.GiftClaimRecordDao;
import top.mcos.database.dao.GiftItemDao;
import top.mcos.database.domain.GiftClaimRecord;
import top.mcos.database.domain.GiftItem;

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
        return "aesopplugin.activity";
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
            possibleCompletions.add("give giftBtn");
            possibleCompletions.add("give snowballBtn");
            possibleCompletions.add("clear");
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
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if("give".equals(args[1])) {
                if("giftBtn".equals(args[2])) {
                    ItemStack itemStack = new ItemStack(Material.CRIMSON_BUTTON, 1);
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.getPersistentDataContainer().set(NSKeys.ACTIVITY_GIFT_BUTTON, PersistentDataType.BOOLEAN, true);
                    itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&9【圣诞节&d活&c动&9】 &7| &e-》&a抽取礼物&e《-"));
                    itemStack.setItemMeta(itemMeta);
                    player.getInventory().addItem(itemStack);
                } else if("snowballBtn".equals(args[2])) {
                    ItemStack itemStack = new ItemStack(Material.STONE_BUTTON, 1);
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.getPersistentDataContainer().set(NSKeys.ACTIVITY_SNOWBALL_BUTTON, PersistentDataType.BOOLEAN, true);
                    itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&9【圣诞节&d活&c动&9】 &7| &e-》&a圣诞雪球&e《-"));
                    itemStack.setItemMeta(itemMeta);
                    player.getInventory().addItem(itemStack);
                }
            } else if("clear".equals(args[1])) {
                try {
                    // 清理玩家数据
                    String playerName = args[2];
                    GiftClaimRecordDao giftClaimRecordDao = AesopPlugin.getInstance().getDatabase().getGiftClaimRecordDao();
                    GiftItemDao giftItemDao = AesopPlugin.getInstance().getDatabase().getGiftItemDao();

                    List<GiftClaimRecord> recordList = giftClaimRecordDao.queryBuilder().selectColumns("id").where().eq("playerName", playerName).query();
                    List<Long> recordIds = recordList.stream().map(GiftClaimRecord::getId).toList();

                    List<GiftItem> items = giftItemDao.queryBuilder().selectColumns("id").where().in("recordId", recordIds).query();
                    List<Long> itemIds = items.stream().map(GiftItem::getId).toList();

                    int i1 = giftClaimRecordDao.deleteIds(recordIds);
                    int i2 = giftItemDao.deleteIds(itemIds);
                    if(i1>0 || i2>0) {
                        AesopPlugin.logger.log(player, "&a已删除玩家" + playerName + "数据");
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else {
                AesopPlugin.logger.log(player, "&c参数有误");
            }
        }
    }
}
