package org.satellite.dev.progiple.sateplanet.planets.menu.planets;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.novasparkle.lunaspring.API.menus.AMenu;
import org.novasparkle.lunaspring.API.menus.items.Item;
import org.satellite.dev.progiple.sateplanet.configs.AllPlanetsMenuConfig;
import org.satellite.dev.progiple.sateplanet.planets.PlanetManager;
import org.satellite.dev.progiple.sateplanet.planets.VirtualPlanet;

public class PlanetsMenu extends AMenu {
    public PlanetsMenu(@NotNull Player player) {
        super(player, AllPlanetsMenuConfig.getTitle(), AllPlanetsMenuConfig.getSize(),
                AllPlanetsMenuConfig.getSection("items.decorations"));

        ConfigurationSection section = AllPlanetsMenuConfig.getSection("items.planets");
        for (String key : section.getKeys(false)) {
            ConfigurationSection planetSection = section.getConfigurationSection(key);
            if (planetSection == null) continue;

            VirtualPlanet virtualPlanet = PlanetManager.getPlanet(key);
            if (virtualPlanet == null) continue;

            this.addItems(new PlanetItem(planetSection, virtualPlanet));
        }
    }

    @Override
    public void onOpen(InventoryOpenEvent e) {
        this.insertAllItems();
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        e.setCancelled(true);

        ItemStack itemStack = e.getCurrentItem();
        if (itemStack == null || itemStack.getType().isAir()) return;

        for (Item item : this.getItemList()) {
            if (item.getItemStack().equals(itemStack) && item.getSlot() == e.getSlot()) {
                item.onClick(e);
                return;
            }
        }
    }

    @Override
    public void onClose(InventoryCloseEvent e) {
    }

    @Override
    public void onDrag(InventoryDragEvent e) {
        e.setCancelled(true);
    }
}
