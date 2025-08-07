package org.satellite.dev.progiple.sateplanet.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.novasparkle.lunaspring.API.util.service.managers.NBTManager;

public class BlockPlaceHandler implements Listener {
    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        ItemStack item = e.getItemInHand();
        if (item.getType().equals(e.getBlockPlaced().getType())) {
            if (NBTManager.hasTag(item, "sateplanet_oxy_helmet")) e.setCancelled(true);
        }
    }
}
