package farn.campfire.listener;

import farn.campfire.CampFireStationAPI;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.item.ItemStack;
import paulevs.bhcreative.listeners.VanillaTabListener;
import paulevs.bhcreative.registry.TabRegistryEvent;

public class CampFireBHCreative {

    @SuppressWarnings("unused")
    @EventListener
    public void registerCampfire(TabRegistryEvent event) {
        VanillaTabListener.tabOtherBlocks.addItem(new ItemStack(CampFireStationAPI.campfire_block));
    }
}
