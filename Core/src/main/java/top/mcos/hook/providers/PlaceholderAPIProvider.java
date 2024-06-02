package top.mcos.hook.providers;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import top.mcos.hook.HookProvider;
import top.mcos.hook.placeholder.statistic.PlayerStatisticsExpansion;

public final class PlaceholderAPIProvider implements HookProvider<Plugin> {
    private Plugin placeholderAPI;

    @Override
    public PlaceholderAPIProvider load() throws Exception {
        placeholderAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
        if(placeholderAPI!=null) {
            // 注册自定义占位符

            // 玩家统计信息
            new PlayerStatisticsExpansion().register();

            // ...

        }
        return this;
    }

    @Override
    public boolean isLoaded() {
        return placeholderAPI !=null;
    }

    @Override
    public Plugin getAPI() {
        return placeholderAPI;
    }

    @Override
    public String getAPIName() {
        return "PlaceholderAPI";
    }
}
