package top.mcos.hook.providers;

import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.Bukkit;
import top.mcos.hook.HookProvider;

public final class MultiverseProvider implements HookProvider<MultiverseCore> {
    private MultiverseCore multiverseCore;

    @Override
    public MultiverseProvider load() throws Exception {
        multiverseCore = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
        return this;
    }

    @Override
    public boolean isLoaded() {
        return multiverseCore!=null;
    }

    @Override
    public MultiverseCore getAPI() {
        return multiverseCore;
    }

    @Override
    public String getAPIName() {
        return "MultiverseCore";
    }
}
