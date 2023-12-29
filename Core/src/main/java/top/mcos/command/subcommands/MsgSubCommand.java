package top.mcos.command.subcommands;

import com.epicnicity322.epicpluginlib.bukkit.command.Command;
import com.epicnicity322.epicpluginlib.bukkit.command.CommandRunnable;
import com.epicnicity322.epicpluginlib.bukkit.command.TabCompleteRunnable;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.popcraft.chunky.api.ChunkyAPI;
import top.mcos.AesopPlugin;

/**
 * 消息命令：/xxx msg
 */
public final class MsgSubCommand extends Command implements Helpable {
    @Override
    public @NotNull CommandRunnable onHelp() {
        return (label, sender, args) -> AesopPlugin.logger.log(sender, "&2定时消息: &a/"+label + " "+getName());
    }

    @Override
    public @Nullable String[] getAliases() {
        return new String[]{"sendmessage"};
    }

    @Override
    public @NotNull String getName() {
        return "msg";
    }

    @Override
    public int getMinArgsAmount() {
        return 2;
    }

    @Override
    public @Nullable String getPermission() {
        return "aesopplugin.admin.msg";
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
            possibleCompletions.add("send");
            possibleCompletions.add("out");
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
            if("send".equals(args[1])) {
                //MessageHandler.sendAllOnlinePlayers("测试");
                //Bukkit.broadcastMessage("&a服务器即将&c关机: " + args[2]);

                // 测试发送center消息
                //NetworkManager networkManager = null;
                //try {
                //    networkManager = (NetworkManager) MessageHandler.networkManagerH.get(((CraftPlayer) player).getHandle().b);
                //} catch (IllegalAccessException e) {
                //    AesopPlugin.logger.log("实例化网络管理器出错", ConsoleLogger.Level.ERROR);
                //    e.printStackTrace();
                //}
                //String title = "主标题";
                //String subtitle = "子标题xxx";
                //ClientboundSetTitleTextPacket text = new ClientboundSetTitleTextPacket(CraftChatMessage.fromStringOrNull(title));
                //ClientboundSetSubtitleTextPacket subtext = new ClientboundSetSubtitleTextPacket(CraftChatMessage.fromStringOrNull(subtitle));
                //networkManager.a((Packet<?>) text);
                //networkManager.a((Packet<?>) subtext);

                /*
                多世界
                 */
                //MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
                //MVWorldManager mvWorldManager = core.getMVWorldManager();
                ////long seed = (new Random()).nextLong();
                //mvWorldManager.regenWorld("zy", true, true, null, true);
                //AesopPlugin.logger.log("世界已重置");

                /*
                区块生成
                 */
                //ChunkyAPI chunky = Bukkit.getServer().getServicesManager().load(ChunkyAPI.class);
                //AesopPlugin.logger.log("开始加载区块...");
                //chunky.startTask("zy", "square", 0, 0, 600, 600, "concentric");

                /*
                执行命令
                 */
                //String cmdline = "gamerule doDaylightCycle "+args[2];
                //ConsoleCommandSender consoleSender = Bukkit.getServer().getConsoleSender();
                //Bukkit.getServer().dispatchCommand(consoleSender, cmdline);

                /**
                 * 获取插件路径
                 */
                //Location location = player.getLocation();
                //AesopPlugin.getInstance().getServer().getWorld("world").playEffect(location, );

                /**
                 * EffectLib 类库使用示例：
                 * https://github.com/u9g/effectlib-visualizer/blob/main/src/main/java/dev/u9g/effectspreviewer/Commands.java
                 */
                //EffectManager effectManager = new EffectManager(AesopPlugin.getInstance());
                ////TextEffect textEffect = new TextEffect(effectManager);
                ////textEffect.text = "我就是特效哈哈哈";
                ////textEffect.size=90;
                ////textEffect.color= Color.BLUE;
                ////textEffect.delay=10;
                ////textEffect.duration=10;
                ////textEffect.setFont(new Font("", Font.BOLD, 30));
                ////textEffect.setTargetEntity(player);
                ////textEffect.start();
                //
                //TextEffect effect = new TextEffect(effectManager);
                //World world = Bukkit.getWorld("world");
                //Location location = new Location(world, -4, 67, 61, 45, 0);
                //
                //// 设置位置
                //effect.setDynamicOrigin(new DynamicLocation(location));
                ////private final BiFunction<Player, Integer, Location> xForwardFromPlayer = (player, x) -> player.getLocation().add(0,2,0).add(player.getLocation().getDirection().multiply(x));
                ////effect.setDynamicOrigin(new DynamicLocation(xForwardFromPlayer.apply(player, 9)));
                //
                //// 设置粒子特效（暂时只能选择不需要特效数据的）
                ////effect.particle = Particle.ELECTRIC_SPARK;
                //effect.particle = Particle.SOUL_FIRE_FLAME;
                ////effect.particleData=''
                //
                //// 设置文本
                //effect.text = "圣诞节快乐hello";
                //
                //// 时间间隔，数值越小，显示越快
                //effect.period = 10;
                //try {
                //    InputStream fi = MsgSubCommand.class.getClassLoader().getResourceAsStream("font/DouyinSansBold.ttf");
                //    Font font = Font.createFont(Font.PLAIN, fi);
                //    font = font.deriveFont(Font.PLAIN, 30);
                //    effect.setFont(font);
                //    //effect.size = 16;
                //} catch (Exception e) {
                //    e.printStackTrace();
                //}
                //
                //effect.start();
                ////effect.cancel();
                //

                /**
                 * 自定义NBT
                 * https://www.spigotmc.org/threads/1-8-x-1-20-x-v7-19-0-maven-single-class-nbt-editor-for-items-skulls-mobs-and-tile-entities.269621/
                 */
                ItemStack itemStack = new ItemStack(Material.REDSTONE_BLOCK, 1);
                //ItemMeta itemMeta = itemStack.getItemMeta();
                //itemMeta.setDisplayName("sfee");
                //itemStack.setItemMeta(itemMeta);
                //ItemStack itemStack1 = new ItemStack(itemStack);
                ////ItemStack set = NBTEditor.set(itemStack, "any custom string key",  "item", "owner");
                //player.getInventory().addItem(itemStack1);

                //NBTItem nbtItem = new NBTItem(itemStack);
                //nbtItem.setString("PublicBukkitValues", "{'chao':'123'}");
                //ItemStack item = nbtItem.getItem();
                //item.getItemMeta().getPersistentDataContainer();
                //player.getInventory().addItem(item);
                //boolean publicBukkitValues = nbtItem.hasTag("PublicBukkitValues");
                //System.out.println("存在" +publicBukkitValues);
                //NBTEntity ne = new NBTEntity();


                //TODO 在此示例中，我们将 PI 的值存储在容器实例中，并通过将 ItemMeta 反馈回 ItemStack，即使在重新启动时我们也可以存储它。
                //NamespacedKey key = new NamespacedKey(AesopPlugin.getInstance(), "display");
                //ItemMeta itemMeta = itemStack.getItemMeta();
                //itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING,  "-》抽取礼物《-");
                //itemStack.setItemMeta(itemMeta);

                player.getInventory().addItem(itemStack);

                AesopPlugin.logger.log("结束指令");
            } else if ("out".equals(args[1])) {
                ChunkyAPI chunky = Bukkit.getServer().getServicesManager().load(ChunkyAPI.class);
                AesopPlugin.logger.log(player, "isRunning:"+chunky.isRunning("zy"));
                AesopPlugin.logger.log(player, "out");
            } else {
                AesopPlugin.logger.log(player, "&c参数有误");
            }
        }
    }
}
