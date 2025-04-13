package org.satellite.dev.progiple.sateplanet.listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.satellite.dev.progiple.sateplanet.storages.Storage;
import org.satellite.dev.progiple.sateplanet.storages.StorageManager;

public class BlockBreakHandler implements Listener {
    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Block block = e.getBlock();

        Storage storage = StorageManager.getStorage(block);
        if (storage == null) return;

        Player player = e.getPlayer();
        if (player.hasPermission("sateplanet.admin")) {
            StorageManager.delete(storage);
        } else e.setCancelled(true);
    }
}
