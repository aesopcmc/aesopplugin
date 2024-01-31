package top.mcos.business.activity.gift;

import org.bukkit.entity.Player;
import top.mcos.command.CommandUtil;

import java.util.List;


/**
 * 虚拟礼物（命令类型礼物）
 */
public class VirtualGift extends GiftAbs{
    private List<String> commands;

    public VirtualGift(String giftKey, List<String> commands) {
        super(giftKey);
        this.commands = commands;
    }

    @Override
    public boolean send(Player player) {
        if(commands!=null && commands.size()>0) {
            CommandUtil.executeCommand(commands, player);
            hasSend = true;
            return true;
        }
        return false;
    }
}
