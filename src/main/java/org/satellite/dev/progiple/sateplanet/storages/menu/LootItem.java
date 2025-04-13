package org.satellite.dev.progiple.sateplanet.storages.menu;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.novasparkle.lunaspring.API.Menus.Items.NonMenuItem;
import org.novasparkle.lunaspring.API.Util.utilities.LunaMath;

import java.util.List;

@Getter
public class LootItem {
    private final ItemStack item;
    public LootItem(ConfigurationSection itemsSection) {
        List<String> keys = itemsSection.getKeys(false).stream().toList();
        String key = keys.get(LunaMath.getRandom().nextInt(keys.size()));

        ConfigurationSection section = itemsSection.getConfigurationSection(key);
        if (section == null) {
            ItemStack sectionItem = itemsSection.getItemStack(key);
            this.item = sectionItem != null ? sectionItem.clone() : null;
        } else this.item = new NonMenuItem(section).getDefaultStack();
    }

    public void drop(Location location) {
        if (this.item == null) return;
        location.getWorld().dropItem(location, this.item);
    }
}
