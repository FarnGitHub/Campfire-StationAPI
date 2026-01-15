package farn.campfire.config;

import net.glasslauncher.mods.gcapi3.api.ConfigEntry;
import net.glasslauncher.mods.gcapi3.api.ConfigRoot;

public class GCAPIHandler {
    @ConfigRoot(value="campfire_config", visibleName="Campfire Configuration")
    public static final Instance instance = new Instance();

    public static class Instance {
        @ConfigEntry(name="Cooking Duration", maxValue = Integer.MAX_VALUE, minValue = 20, multiplayerSynced = true)
        public Integer cookDuration = 600;
    }
}
