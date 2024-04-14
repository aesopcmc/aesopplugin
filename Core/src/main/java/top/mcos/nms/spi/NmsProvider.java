package top.mcos.nms.spi;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface NmsProvider {
	
	Object createActionbarPacket(String text);
	Object[] createTitlePacket(String title, String subtitle, int in, int keep, int out);
	
	void sendTitles(Player player, Object... packets);
	void sendActionbar(Player player, String[] messagePiles, Long pileDelay);

	/**
	 * 判断是否是掉落物
	 * @param entity 实体
	 * @param namespaceKeyFilters 物品命名空间过滤规则，可选。格式示例 (key:"slimefun:slimefun_item", value:"ANCIENT_RUNE_WATER")
	 * @return true是掉落物 false不是掉落物
	 */
    boolean isCraftItem(@NotNull Entity entity, @Nullable Map<String, String> namespaceKeyFilters);
}
