package top.mcos.nms.provideds.R1_20_R1;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftItem;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftChatMessage;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mcos.message.MessageHandler;
import top.mcos.nms.spi.NmsProvider;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

public class R1_20_R1 implements NmsProvider {
	
	private final Field networkManagerH;
	
	public R1_20_R1() {
		try {
			networkManagerH = PlayerConnection.class.getDeclaredField("h");
			networkManagerH.setAccessible(true);
			
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Object createActionbarPacket(String text) {
		if (text.isEmpty()) text = " ";
		return new ClientboundSetActionBarTextPacket(CraftChatMessage.fromStringOrNull(text));
	}
	
	@Override
	public Object[] createTitlePacket(String title, String subtitle, int in, int keep, int out) {
		if (title==null || title.isEmpty()) title = " ";
		if (subtitle==null || subtitle.isEmpty()) subtitle = " ";
		title = ChatColor.translateAlternateColorCodes('&', title);
		subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);

		ClientboundSetTitlesAnimationPacket animation = new ClientboundSetTitlesAnimationPacket(in, keep, out);
		ClientboundSetTitleTextPacket text = new ClientboundSetTitleTextPacket(CraftChatMessage.fromStringOrNull(title));
		ClientboundSetSubtitleTextPacket subtext = new ClientboundSetSubtitleTextPacket(CraftChatMessage.fromStringOrNull(subtitle));
		return new Object[] {animation, text, subtext};
	}
	
	@Override
	public void sendTitles(Player player, Object... packets) {
		NetworkManager manager = getNetworkManager(player);
		manager.a((Packet<?>) packets[0]);
		manager.a((Packet<?>) packets[1]);
		manager.a((Packet<?>) packets[2]);
	}

	@Override
	public void sendActionbar(Player player, String[] messagePiles, Long pileDelay) {
		NetworkManager manager = getNetworkManager(player);

		for (String messagePile : messagePiles) {
			if(MessageHandler.isSendBreak()) break;
			String s = ChatColor.translateAlternateColorCodes('&', messagePile);
			var packet = new ClientboundSetActionBarTextPacket(CraftChatMessage.fromStringOrNull(s));
			manager.a((Packet<?>) packet);

			try {
				Thread.sleep(pileDelay);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

	}

	@Override
	public boolean isCraftItem(@NotNull Entity entity, @Nullable Map<String, String> namespaceKeyFilters) {
		// CraftItem ： 识别为凋落物，而不是其它生物
		// c.getPickupDelay() : 获取该物品可供玩家拾取之前的延迟时间
		if(entity instanceof CraftItem c && c.getPickupDelay()<=0) {

			// 根据命名空间过滤
			if(namespaceKeyFilters!=null) {
				if(c.getItemStack().getItemMeta()!=null) {
					Set<NamespacedKey> namespacedKeys = c.getItemStack().getItemMeta().getPersistentDataContainer().getKeys();
					Set<String> nkFilters = namespaceKeyFilters.keySet();
					for (String nkf : nkFilters) {
						for (NamespacedKey namespacedKey : namespacedKeys) {
							String namespaceKeyString = namespacedKey.getNamespace() + ":" + namespacedKey.getKey();
							if (namespaceKeyString.contains(nkf)) {
								String nvf = namespaceKeyFilters.get(nkf);

								// 过滤生效情况1 namespace:key -> 空|*|''
								if(nvf==null || nvf.trim().equals("*") || nvf.trim().isBlank()) {
									return false;
								}
								// 过滤生效情况2 namespace:key -> 一个匹配的值
								if(nvf.equals(c.getItemStack().getItemMeta().getPersistentDataContainer().get(namespacedKey, PersistentDataType.STRING))) {
									return false;
								}
							}
						}
					}
				}
			}

			// 清除掉落物
			return true;
		}
		return false;
	}

	private NetworkManager getNetworkManager(Player player) {
		try {
			return (NetworkManager) networkManagerH.get(((CraftPlayer) player).getHandle().c);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
}