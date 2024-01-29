package top.mcos.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CollectionUtils {
    /**
     * 从集合中随机取出N个不重复的元素
     * @param list 需要被取出数据的集合
     * @param n 取出的元素数量
     * @return
     */
    private List<Integer> createRandoms(List<Integer> list, int n) {
        Map<Integer,String> map = new HashMap();
        List<Integer> news = new ArrayList();
        if (list.size() <= n) {
            return list;
        } else {
            while (map.size() < n) {
                int random = (int)(Math.random() * list.size());
                if (!map.containsKey(random)) {
                    map.put(random, "");
                    news.add(list.get(random));
                }
            }
            return news;
        }
    }

    /**
     * 随机抽取随机数量的集合
     */
    public static <T> List<T> randPickup(List<T> list) {
        int size = list.size();
        int loopCount = RandomUtil.get(1, size);
        List<T> randList = new ArrayList<>();
        Random random = new Random();
        for (int i=0;i<loopCount;i++) {
            int randIndex = random.nextInt(size);
            if(!randList.contains(list.get(randIndex))) {
                randList.add(list.get(randIndex));
            }
        }
        return randList;
    }

}
