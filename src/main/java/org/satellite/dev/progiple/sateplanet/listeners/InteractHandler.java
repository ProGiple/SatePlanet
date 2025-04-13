package org.satellite.dev.progiple.sateplanet.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.novasparkle.lunaspring.API.Util.Service.managers.NBTManager;
import org.satellite.dev.progiple.sateplanet.storages.Storage;
import org.satellite.dev.progiple.sateplanet.storages.StorageManager;

public class InteractHandler implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (!e.getAction().name().contains("RIGHT")) return;

        PlayerInventory inventory = player.getInventory();
        ItemStack item = inventory.getItemInMainHand();
        if (item.getType() != Material.AIR && NBTManager.hasTag(item, "sateplanet_oxy_helmet")) {

            ItemStack helmet = inventory.getHelmet();
            if (helmet != null && helmet.getType() != Material.AIR && NBTManager.hasTag(helmet, "sateplanet_oxy_helmet"))
                return;

            item = item.clone();
            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
                inventory.setItemInMainHand(item);

                if (helmet != null) inventory.addItem(helmet.clone());
                helmet = item.clone();

                helmet.setAmount(1);
                inventory.setHelmet(helmet);
            } else {
                inventory.setItemInMainHand(helmet);
                inventory.setHelmet(item);
            }
        } else {
            Block block = e.getClickedBlock();
            if (block == null) return;

            Storage storage = StorageManager.getStorage(block);
            if (storage == null) return;

            storage.onClick(player);
        }
    }
}
