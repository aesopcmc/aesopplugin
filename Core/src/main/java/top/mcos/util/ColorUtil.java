package top.mcos.util;

import org.bukkit.Color;

public class ColorUtil {
    public static Color getByCode(String code) {
        switch (code) {
            case "WHITE": return Color.WHITE;
            case "SILVER": return Color.SILVER;
            case "GRAY": return Color.GRAY;
            case "BLACK": return Color.BLACK;
            case "RED": return Color.RED;
            case "MAROON": return Color.MAROON;
            case "YELLOW": return Color.YELLOW;
            case "OLIVE": return Color.OLIVE;
            case "LIME": return Color.LIME;
            case "GREEN": return Color.GREEN;
            case "AQUA": return Color.AQUA;
            case "BLUE": return Color.BLUE;
            case "NAVY": return Color.NAVY;
            case "FUCHSIA": return Color.FUCHSIA;
            case "PURPLE": return Color.PURPLE;
            case "ORANGE": return Color.ORANGE;
            default: return Color.WHITE;
        }
    }
}
