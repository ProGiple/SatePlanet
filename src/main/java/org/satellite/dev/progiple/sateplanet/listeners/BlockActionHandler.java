package org.satellite.dev.progiple.sateplanet.listeners;

import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.novasparkle.lunaspring.API.util.service.managers.NBTManager;
import org.novasparkle.lunaspring.API.util.utilities.AnnounceUtils;
import org.satellite.dev.progiple.sateplanet.storages.Storage;
import org.satellite.dev.progiple.sateplanet.storages.StorageManager;

public class BlockActionHandler implements Listener {
    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        if (!NBTManager.hasTag(block, "planet-storage")) return;

        Storage storage = StorageManager.getStorage(block);
        if (storage == null) return;

        Player player = e.getPlayer();
        if (player.hasPermission("sateplanet.admin")) {
            ItemStack itemStack = player.getInventory().getItemInMainHand();
            if (itemStack.getType().isAir()) {
                StorageManager.delete(storage);
                return;
            }
        }

        e.setCancelled(true);
        if (storage.isClaimed()) return;

        storage.drop();
        AnnounceUtils.sound(player, Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE);
    }
}
