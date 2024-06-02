package top.mcos.hook.providers;

import com.sk89q.worldguard.WorldGuard;
import top.mcos.hook.HookProvider;

public final class WorldGuardProvider implements HookProvider<WorldGuard> {
    private WorldGuard worldGuard;

    @Override
    public WorldGuardProvider load() throws Exception {
        worldGuard = WorldGuard.getInstance();
        return this;
    }

    @Override
    public boolean isLoaded() {
        return worldGuard !=null;
    }

    @Override
    public WorldGuard getAPI() {
        return worldGuard;
    }

    @Override
    public String getAPIName() {
        return "WorldGuard";
    }
}
