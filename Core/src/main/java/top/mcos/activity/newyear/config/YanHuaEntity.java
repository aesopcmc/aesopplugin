package top.mcos.activity.newyear.config;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Setter
@Getter
public class YanHuaEntity implements Delayed{
    private String location;
    private int power;
    private int cellsMode;
    private int delay;
    private List<String> cellKeys;
    private long avaibleTime;

    public YanHuaEntity(String location, int power, int cellsMode, int delay, List<String> cellKeys) {
        this.location = location;
        this.power = power;
        this.cellsMode = cellsMode;
        this.delay = delay;
        this.cellKeys = cellKeys;
        //avaibleTime = 当前时间+ delayTime
        this.avaibleTime = delay + System.currentTimeMillis();
    }

    @Override
    public long getDelay(@NotNull TimeUnit unit) {
        //判断avaibleTime是否大于当前系统时间，并将结果转换成MILLISECONDS
        long diffTime = avaibleTime - System.currentTimeMillis();
        return unit.convert(diffTime,TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(@NotNull Delayed o) {
        //compareTo用在DelayedUser的排序
        return (int)(this.avaibleTime - ((YanHuaEntity) o).getAvaibleTime());
    }
}
