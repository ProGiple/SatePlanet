package org.satellite.dev.progiple.sateplanet;

import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.novasparkle.lunaspring.API.menus.items.NonMenuItem;
import org.novasparkle.lunaspring.API.util.service.managers.NBTManager;
import org.novasparkle.lunaspring.API.util.utilities.AnnounceUtils;
import org.satellite.dev.progiple.satecustomitems.itemManager.secondary.BlockPlaceItemComponent;
import org.satellite.dev.progiple.satecustomitems.itemManager.secondary.ClickableItemComponent;
import org.satellite.dev.progiple.sateplanet.configs.Config;

@Getter
public class OxyHelmetComponent implements ClickableItemComponent, BlockPlaceItemComponent {
    private final String id = "oxygen_helmet";

    @Override
    public boolean onClick(PlayerInteractEvent e, ItemStack itemStack) {
        if (!e.getAction().name().contains("RIGHT")) return false;

        Player player = e.getPlayer();
        ItemStack oxyHelmet = player.getInventory().getItemInMainHand().clone();

        ItemStack helmet = player.getInventory().getHelmet();
        if (helmet != null && !helmet.getType().isAir()) {
            if (this.itemIsComponent(helmet)) return true;

            player.getInventory().setItemInMainHand(helmet.clone());
        } else player.getInventory().setItemInMainHand(null);

        AnnounceUtils.sound(player, Sound.ITEM_ARMOR_EQUIP_CHAIN);
        player.getInventory().setHelmet(oxyHelmet);
        return true;
    }

    @Override
    public boolean itemIsComponent(ItemStack itemStack) {
        return NBTManager.hasTag(itemStack, "sateplanet_oxy_helmet");
    }

    @Override
    public NonMenuItem createItem() {
        NonMenuItem item = new NonMenuItem(Config.getSection("oxygen_helmet"));
        NBTManager.setString(item.getItemStack(), "sateplanet_oxy_helmet", "yes");
        return item;
    }

    @Override
    public boolean onPlace(BlockPlaceEvent e, ItemStack itemStack) {
        return true;
    }
}
