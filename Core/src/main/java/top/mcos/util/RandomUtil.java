package top.mcos.util;

import java.util.Random;

public class RandomUtil {
    /**
     * 给定数值范围获得随机数
     * @param min 开始值
     * @param max 结束值
     * @return
     */
    public static int get(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }
}
