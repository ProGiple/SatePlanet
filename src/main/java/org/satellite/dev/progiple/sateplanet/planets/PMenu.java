package org.satellite.dev.progiple.sateplanet.planets;

import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.novasparkle.lunaspring.API.Menus.AMenu;
import org.novasparkle.lunaspring.API.Menus.IMenu;
import org.novasparkle.lunaspring.API.Menus.Items.Item;
import org.novasparkle.lunaspring.API.Util.utilities.LunaMath;
import org.novasparkle.lunaspring.API.Util.utilities.LunaTask;
import org.satellite.dev.progiple.sateplanet.SatePlanet;
import org.satellite.dev.progiple.sateplanet.configs.PlanetConfig;
import org.satellite.dev.progiple.sateplanet.configs.PlanetMenuConfig;

import java.util.*;

public class PMenu extends AMenu {
    private final LunaTask task;
    public PMenu(Player player, String planetName) {
        super(player, PlanetMenuConfig.getTitle(), PlanetMenuConfig.getSize(), PlanetMenuConfig.getSection("items.decorations"));
        List<Item> itemList = new ArrayList<>();

        ConfigurationSection itemSection = PlanetMenuConfig.getSection("items.animation_item");
        this.getSlotList(itemSection.getStringList("slots")).forEach(s -> itemList.add(new Item(itemSection, s)));

        this.task = new Task(PlanetConfig.getSection(planetName), this, itemList);
    }

    @Override
    public void onOpen(InventoryOpenEvent e) {
        this.task.runTaskAsynchronously(SatePlanet.getINSTANCE());
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        e.setCancelled(true);
    }

    @Override
    public void onClose(InventoryCloseEvent e) {
        if (this.task != null) this.task.cancel();
    }

    private List<Integer> getSlotList(Collection<String> slotList) {
        List<Integer> list = new ArrayList<>();
        for (String line : slotList) {
            if (line.contains("-")) {
                String[] split = line.split("-");
                for (int i = LunaMath.toInt(split[0]); i <= LunaMath.toInt(split[1]); i++) list.add(i);
            } else if (line.contains(",")) {
                String[] split = line.split(",");
                for (String string : split) list.add(LunaMath.toInt(string.replace(" ", "")));
            } else list.add(LunaMath.toInt(line));
        }
        return list;
    }

    private static class Task extends LunaTask {
        private final IMenu iMenu;
        private final List<Item> itemList;
        private final ConfigurationSection section;
        public Task(ConfigurationSection worldSection, IMenu iMenu, List<Item> itemList) {
            super(worldSection.getInt("fly_time") * 1000L);
            this.iMenu = iMenu;
            this.itemList = itemList;
            this.section = worldSection;
        }

        @Override @SneakyThrows @SuppressWarnings("all")
        public void start() {
            int time = (int) (this.getTicks() / this.itemList.size());
            if (time > 0)
                while (!this.itemList.isEmpty()) {
                    if (!this.isActive()) return;

                    Item item = this.itemList.get(0);
                    item.insert(this.iMenu);

                    this.itemList.remove(0);
                    Thread.sleep(time);
                }

            World world = Bukkit.getWorld(this.section.getName());
            if (world == null) return;

            ConfigurationSection locSection = this.section.getConfigurationSection("teleport_location");
            Location location = new Location(world, locSection.getInt("x"), locSection.getInt("y"), locSection.getInt("z"));
            if (location == null) return;

            Bukkit.getScheduler().runTask(SatePlanet.getINSTANCE(), () -> {
                this.iMenu.getPlayer().closeInventory();
                this.iMenu.getPlayer().teleport(location);
            });
        }
    }
}
