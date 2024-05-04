package top.mcos.business;

import top.mcos.business.gbclear.GbClearBus;
import top.mcos.business.regen.RegenBus;

import java.util.ArrayList;
import java.util.List;

public final class BusRegister {
    public static GbClearBus clearBus;
    public static RegenBus regenBus;

    private static List<Bus> busList = new ArrayList<>();

    public static void register() {
        clearBus = new GbClearBus();
        clearBus.load();
        busList.add(clearBus);

        regenBus = new RegenBus();
        regenBus.load();
        busList.add(regenBus);
    }

    public static void unload() {
        for (Bus bus : busList) {
            bus.unload();
        }
    }

    public static void reload() {
        for (Bus bus : busList) {
            bus.reload();
        }
    }
}
