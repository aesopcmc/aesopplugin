package top.mcos.nms.spi;

import org.bukkit.entity.Player;

public interface NmsProvider {
	
	Object createActionbarPacket(String text);
	Object[] createTitlePacket(String title, String subtitle, int in, int keep, int out);
	
	void sendTitles(Player player, Object... packets);
	void sendActionbar(Player player, String[] messagePiles, Long pileDelay);
	
}
