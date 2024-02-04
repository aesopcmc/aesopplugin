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

    public static Color getByInteger(int order) {
        switch (order) {
            case 1: return Color.WHITE;
            case 2: return Color.SILVER;
            case 3: return Color.GRAY;
            case 4: return Color.BLACK;
            case 5: return Color.RED;
            case 6: return Color.MAROON;
            case 7: return Color.YELLOW;
            case 8: return Color.OLIVE;
            case 9: return Color.LIME;
            case 10: return Color.GREEN;
            case 11: return Color.AQUA;
            case 12: return Color.BLUE;
            case 13: return Color.NAVY;
            case 14: return Color.FUCHSIA;
            case 15: return Color.PURPLE;
            case 16: return Color.ORANGE;
            case 17: return Color.fromBGR(230, 176, 170);
            case 18: return Color.fromBGR(245, 183, 177 );
            case 19: return Color.fromBGR(215, 189, 226);
            case 20: return Color.fromBGR(210, 180, 222);
            case 21: return Color.fromBGR(169, 204, 227);
            case 22: return Color.fromBGR(174, 214, 241);
            case 23: return Color.fromBGR(163, 228, 215);
            case 24: return Color.fromBGR(162, 217, 206);
            case 25: return Color.fromBGR(169, 223, 191);
            case 26: return Color.fromBGR(171, 235, 198);
            case 27: return Color.fromBGR(249, 231, 159);
            case 28: return Color.fromBGR(250, 215, 160);
            case 29: return Color.fromBGR(245, 203, 167);
            case 30: return Color.fromBGR(237, 187, 153);
            case 31: return Color.fromBGR(192, 57, 43);
            case 32: return Color.fromBGR(203, 67, 53);
            case 33: return Color.fromBGR(136, 78, 160);
            case 34: return Color.fromBGR(125, 60, 152);
            case 35: return Color.fromBGR(36, 113, 163);
            case 36: return Color.fromBGR(46, 134, 193);
            case 37: return Color.fromBGR(23, 165, 137);
            case 38: return Color.fromBGR(19, 141, 117);
            case 39: return Color.fromBGR(34, 153, 84);
            case 40: return Color.fromBGR(40, 180, 99);
            case 41: return Color.fromBGR(212, 172, 13);
            case 42: return Color.fromBGR(214, 137, 16);
            case 43: return Color.fromBGR(202, 111, 30);
            case 44: return Color.fromBGR(186, 74, 0);
            default: return Color.WHITE;
        }
    }
}
