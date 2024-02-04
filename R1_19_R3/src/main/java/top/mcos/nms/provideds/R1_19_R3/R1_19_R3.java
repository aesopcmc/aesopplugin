package top.mcos.nms.provideds.R1_19_R3;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;
import top.mcos.message.MessageHandler;
import top.mcos.nms.spi.NmsProvider;

import java.lang.reflect.Field;

public class R1_19_R3 implements NmsProvider {
	
	private final Field networkManagerH;
	
	public R1_19_R3() {
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
	
	private NetworkManager getNetworkManager(Player player) {
		try {
			return (NetworkManager) networkManagerH.get(((CraftPlayer) player).getHandle().b);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
}