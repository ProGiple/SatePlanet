package org.satellite.dev.progiple.sateplanet.storages;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.novasparkle.lunaspring.API.menus.items.NonMenuItem;
import org.novasparkle.lunaspring.API.util.utilities.LunaMath;

import java.util.List;

@Getter
public class LootItem {
    private final ItemStack item;
    public LootItem(ConfigurationSection itemsSection) {
        List<String> keys = itemsSection.getKeys(false).stream().toList();
        if (keys.isEmpty()) {
            this.item = new ItemStack(Material.STONE, 1);
            return;
        }

        String key = keys.get(LunaMath.getRandomInt(0, keys.size()));

        ConfigurationSection section = itemsSection.getConfigurationSection(key);
        if (section == null) {
            ItemStack sectionItem = itemsSection.getItemStack(key);
            this.item = sectionItem != null ? sectionItem.clone() : new ItemStack(Material.STONE, 1);
        } else this.item = new NonMenuItem(section).getItemStack();

        if (this.item.getAmount() <= 1) return;
        int amount = LunaMath.getRandomInt(1, this.item.getAmount());
        this.item.setAmount(amount);
    }

    public void drop(Location location) {
        if (this.item == null) return;
        location.getWorld().dropItem(location, this.item);
    }
}
