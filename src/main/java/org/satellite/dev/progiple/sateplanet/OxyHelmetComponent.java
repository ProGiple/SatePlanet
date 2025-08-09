package org.satellite.dev.progiple.sateplanet;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.novasparkle.lunaspring.API.menus.items.NonMenuItem;
import org.novasparkle.lunaspring.API.util.service.managers.NBTManager;
import org.satellite.dev.progiple.satecustomitems.itemManager.secondary.ClickableItemComponent;
import org.satellite.dev.progiple.sateplanet.configs.Config;

public class OxyHelmetComponent implements ClickableItemComponent {
    @Override
    public boolean onClick(PlayerInteractEvent e) {
        if (!e.getAction().name().contains("RIGHT")) return false;

        Player player = e.getPlayer();
        ItemStack oxyHelmet = player.getInventory().getItemInMainHand().clone();

        ItemStack helmet = player.getInventory().getHelmet();
        if (helmet != null && !helmet.getType().isAir()) {
            if (this.itemIsComponent(helmet)) {
                e.setCancelled(true);
                return false;
            }

            player.getInventory().setItemInMainHand(helmet.clone());
        }

        player.getInventory().setHelmet(oxyHelmet);
        return false;
    }

    @Override
    public boolean itemIsComponent(ItemStack itemStack) {
        return NBTManager.hasTag(itemStack, "sateplanet_oxy_helmet");
    }

    @Override
    public NonMenuItem createItem() {
        NonMenuItem item = new NonMenuItem(Config.getSection("oxygen_helmet"));
        NBTManager.setBool(item.getItemStack(), "sateplanet_oxy_helmet", true);
        return item;
    }
}
