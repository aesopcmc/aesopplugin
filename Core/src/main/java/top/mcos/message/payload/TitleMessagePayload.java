package top.mcos.message.payload;

import org.bukkit.entity.Player;
import top.mcos.AesopPlugin;
import top.mcos.config.ConfigLoader;
import top.mcos.message.MessagePayload;

/**
 * 标题消息载体
 */
public class TitleMessagePayload implements MessagePayload {
    private Player player;

    private String message;

    private String subMessage;

    public TitleMessagePayload(Player player, String message, String subMessage) {
        this.player = player;
        this.message = message;
        this.subMessage = subMessage;
    }

    /**
     * 发送数据包
     */
    public boolean sendPacket() {
        if(ConfigLoader.baseConfig.getSettingConfig().isNoticeTitleEnabled()) {
            Object[] titlePacket = AesopPlugin.nmsProvider.createTitlePacket(message, subMessage,
                    ConfigLoader.baseConfig.getSettingConfig().getNoticeTitleFadein(),
                    ConfigLoader.baseConfig.getSettingConfig().getNoticeTitleKeep(),
                    ConfigLoader.baseConfig.getSettingConfig().getNoticeTitleFadeout());
            AesopPlugin.nmsProvider.sendTitles(player, titlePacket);
        }
        return true;
    }
}
