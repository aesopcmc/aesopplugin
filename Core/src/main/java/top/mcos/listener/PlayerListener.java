package top.mcos.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import top.mcos.AesopPlugin;
import top.mcos.config.activitiy.NSKeys;
import top.mcos.util.MessageUtil;

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
            // 监听玩家右键方块
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Block clicked = event.getClickedBlock();

                // event.getClickedBlock() can return nothing, i.e. 'null'.
                // Just in-case we'll check to make sure.
                if (clicked != null) {
                    // The OR logic gate does exist, however, you used it wrong. '||' instead of '<='
                    // The if statement evaluates Booleans (true/false), so you must use OR with booleans.
                    if (clicked.getType() == Material.STONE_BUTTON || clicked.getType() == Material.OAK_BUTTON) {
                        //System.out.println("按下按钮");
                    }
                    // Alternatively, you can do something like this
                    if (clicked.getType().name().endsWith("_BUTTON")) {
                        //display - 》 {Lore:
                        // ['{"extra":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"gold","text":"容量: "}
                        // ,{"italic":false,"color":"yellow","text":"25,600 "},{"italic":false,"color":"yellow","text":"个物品"}],"text":""}'],
                        // Name:'{"extra":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"red","text":"高级"},
                        // {"italic":false,"color":"dark_gray","text":"存储单元"}],"text":""}'}

                        // {Name: '{"extra":[{"italic":false,"color":"dark_gray","text":"测试123"}]}'}
                        //PublicBukkitValues - 》 {"slimefun:slimefun_item":"ADVANCED_STORAGE"}

                        if("Location{world=CraftWorld{name=world},x=26.0,y=64.0,z=16.0,pitch=0.0,yaw=0.0}".equals(clicked.getLocation().toString())) {
                            //AesopPlugin.logger.log(event.getPlayer(), "成功匹配!!!");

                            ItemStack itemStack = new ItemStack(Material.STICK, 1);
                            ItemMeta itemMeta = itemStack.getItemMeta();
                            if(itemMeta!=null) {
                                // 设置显示的名称
                                itemMeta.setDisplayName(MessageUtil.symbol("&a&l⍋&6圣诞树枝&a&l⍋ &8- &5※稀有"));
                                // 设置自定义持久数据
                                itemMeta.getPersistentDataContainer().set(NSKeys.ACTIVITY_TWIG_ITEM, PersistentDataType.BOOLEAN, Boolean.TRUE);
                                // 设置lore
                                List<String> lines = new ArrayList<>();
                                lines.add(" ");
                                lines.add(MessageUtil.symbol("&7一位神秘人在森林里丢失了他的驯鹿座驾，礼物不能及时送达将是致命的，"));
                                lines.add(MessageUtil.symbol("&7他决定把摘下来的树枝作为飞行工具。"));
                                lines.add(" ");
                                lines.add(MessageUtil.symbol("&7- &7攻击时对敌人产生&c&l击退10&7和&c&l1-5秒漂浮&7效果"));
                                lines.add(MessageUtil.symbol("&7- &7获得概率10%"));
                                lines.add(" ");
                                lines.add(MessageUtil.symbol("&b❄== &5圣诞节活动物品&a &b==❄"));
                                itemMeta.setLore(lines);
                                // 设置附魔
                                itemMeta.addEnchant(Enchantment.KNOCKBACK, 10, true);
                                // 添加
                                itemStack.setItemMeta(itemMeta);

                                player.getInventory().addItem(itemStack);
                            }
                        }

                        //CustomBlockData customBlockData = new CustomBlockData(clicked, AesopPlugin.getInstance());
                        //Boolean aBoolean = customBlockData.get(NSKeys.ACTIVITY_BUTTON_FLAG, PersistentDataType.BOOLEAN);
                        //AesopPlugin.logger.log("查看方块数据：" + aBoolean);
                    }
                }
            }

        });

        //    添加物品到玩家库存
        //Player player = evt.getPlayer(); // The player who joined
        //PlayerInventory inventory = player.getInventory(); // The player's inventory
        //ItemStack itemstack = new ItemStack(Material.DIAMOND, 64); // A stack of diamonds
        //
        //if (inventory.contains(itemstack)) {
        //    inventory.addItem(itemstack); // Adds a stack of diamonds to the player's inventory
        //    player.sendMessage("Welcome! You seem to be reeeally rich, so we gave you some more diamonds!");
        //}
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

            //ItemStack itemInHand = e.getItemInHand();
            //ItemMeta itemMeta = itemInHand.getItemMeta();
            //if(itemMeta!=null) {
            //    PersistentDataContainer con = itemMeta.getPersistentDataContainer();
            //    Boolean actBtnFlag = con.get(NSKeys.ACTIVITY_BUTTON_FLAG, PersistentDataType.BOOLEAN);
            //    if (actBtnFlag != null && actBtnFlag) {
            //        // 识别为活动按钮
            //        AesopPlugin.logger.log("手持物品名称：" + itemMeta.getDisplayName());
            //        AesopPlugin.logger.log("放置位置：" + block.getLocation().toString());
            //
            //    }
            //}
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
     * 方块破坏事件
     * @param e
     */
    @EventHandler
    private void onBlockDamage(BlockDamageEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(AesopPlugin.getInstance(), ()->{

        });
    }

}
