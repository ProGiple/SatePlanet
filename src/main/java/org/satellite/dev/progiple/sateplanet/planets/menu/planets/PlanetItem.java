package org.satellite.dev.progiple.sateplanet.planets.menu.planets;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.novasparkle.lunaspring.API.menus.MenuManager;
import org.novasparkle.lunaspring.API.menus.items.Item;
import org.satellite.dev.progiple.sateplanet.planets.VirtualPlanet;
import org.satellite.dev.progiple.sateplanet.planets.menu.TeleportMenu;

public class PlanetItem extends Item {
    private final VirtualPlanet virtualPlanet;
    public PlanetItem(@NotNull ConfigurationSection section, VirtualPlanet virtualPlanet) {
        super(section, section.getInt("slot"));
        this.virtualPlanet = virtualPlanet;
    }

    @Override
    public Item onClick(InventoryClickEvent e) {
        MenuManager.openInventory(new TeleportMenu((Player) e.getWhoClicked(), this.virtualPlanet));
        return this;
    }
}
