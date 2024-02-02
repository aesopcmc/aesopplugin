package top.mcos.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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
    public static boolean probRandom(int prob) {
        if (prob==0) return false;
        if (prob==100) return true;
        return get(0, 100) < prob;
    }


    /**
     * 权重随机
     * 使用示例：
     *          Map<String, Integer> map = new HashMap<>();
     *         map.put("8元", 25);
     *         map.put("10元", 70);
     *         map.put("15元", 5);
     *         String selectedKey = weightRandom(map);
     */
    public static String weightRandom(Map<String, Integer> map) {
        Set<String> keySet = map.keySet();
        List<String> weights = new ArrayList<>();
        for (Iterator<String> it = keySet.iterator(); it.hasNext(); ) {
            String weightStr = it.next();
            int weight = map.get(weightStr);
            for (int i = 0; i <= weight; i++) {
                weights.add(weightStr);
            }
        }
        int idx = new Random().nextInt(weights.size());
        return weights.get(idx);
    }
}
