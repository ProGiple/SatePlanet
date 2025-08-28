package org.satellite.dev.progiple.sateplanet.planets.menu;

import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.novasparkle.lunaspring.API.menus.AMenu;
import org.novasparkle.lunaspring.API.menus.items.Item;
import org.novasparkle.lunaspring.API.util.utilities.LunaTask;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.satellite.dev.progiple.sateplanet.SatePlanet;
import org.satellite.dev.progiple.sateplanet.configs.PlanetConfig;
import org.satellite.dev.progiple.sateplanet.configs.PlanetMenuConfig;
import org.satellite.dev.progiple.sateplanet.planets.VirtualPlanet;

import java.util.*;

public class TeleportMenu extends AMenu {
    private final LunaTask task;
    private boolean isOpened;
    public TeleportMenu(Player player, VirtualPlanet virtualPlanet) {
        super(player, PlanetMenuConfig.getTitle(), PlanetMenuConfig.getSize(), PlanetMenuConfig.getSection("items.decorations"));
        List<Item> itemList = new ArrayList<>();

        ConfigurationSection itemSection = PlanetMenuConfig.getSection("items.animation_item");
        Utils.getSlotList(itemSection.getStringList("slots")).forEach(s -> itemList.add(new Item(itemSection, s)));

        this.task = new Task(virtualPlanet, itemList);
    }

    @Override
    public void onOpen(InventoryOpenEvent e) {
        this.isOpened = true;
        this.task.runTaskAsynchronously(SatePlanet.getINSTANCE());
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        e.setCancelled(true);
    }

    @Override
    public void onClose(InventoryCloseEvent e) {
        this.isOpened = false;
        if (this.task != null) this.task.cancel();
    }

    @Override
    public void onDrag(InventoryDragEvent e) {
        e.setCancelled(true);
    }

    private class Task extends LunaTask {
        private final List<Item> itemList;
        private final VirtualPlanet virtualPlanet;
        public Task(VirtualPlanet virtualPlanet, List<Item> itemList) {
            super(virtualPlanet.getFly_time() * 1000L);
            this.itemList = itemList;
            this.virtualPlanet = virtualPlanet;
        }

        @Override @SneakyThrows @SuppressWarnings("all")
        public void start() {
            int time = (int) (this.getTicks() / this.itemList.size());
            if (time > 0 && !(getPlayer().hasPermission("sateplanet.bypass.teleport")))
                while (!this.itemList.isEmpty()) {
                    if (!this.isActive() || !isOpened) return;

                    Item item = this.itemList.get(0);
                    item.insert(TeleportMenu.this);

                    this.itemList.remove(0);
                    Thread.sleep(time);
                }

            Player player = TeleportMenu.this.getPlayer();
            Bukkit.getScheduler().runTask(SatePlanet.getINSTANCE(), () -> {
                player.closeInventory();
                this.virtualPlanet.teleport(player);
            });
        }
    }
}
