package top.mcos.util;

import java.util.Random;

public class RandomUtil {
    /**
     * 给定数值范围获得随机数
     * @param min 开始值 (包含）
     * @param max 结束值 （包含）
     * @return
     */
    public static int get(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }

    /**
     * 根据概率进行判断
     * @param prob 概率 0-100
     * @return 概率为0一定返回false，概率为100一定返回true
     */
    public static boolean choiceByRate(int prob) {
        if (prob==0) return false;
        if (prob==100) return true;
        return get(0, 100) < prob;
    }
}
