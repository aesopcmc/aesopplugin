package top.mcos.business;

import top.mcos.business.gbclear.GbClearBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BusRegister {
    public static GbClearBus clearBus;

    private static List<Bus> busList = new ArrayList<>();

    public static void register() {
        clearBus = new GbClearBus();
        clearBus.load();
        busList.add(clearBus);
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
