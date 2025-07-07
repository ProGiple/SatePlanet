package org.satellite.dev.progiple.sateplanet.storages;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.HologramPage;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.novasparkle.lunaspring.API.Menus.Items.Clickable;
import org.novasparkle.lunaspring.API.Menus.MenuManager;
import org.novasparkle.lunaspring.API.Util.Service.managers.ColorManager;
import org.novasparkle.lunaspring.API.Util.utilities.LunaTask;
import org.novasparkle.lunaspring.API.Util.utilities.Utils;
import org.satellite.dev.progiple.sateplanet.SatePlanet;
import org.satellite.dev.progiple.sateplanet.configs.Config;
import org.satellite.dev.progiple.sateplanet.configs.StorageData;
import org.satellite.dev.progiple.sateplanet.storages.menu.StorageMenu;
import org.satellite.dev.progiple.sateplanet.storages.menu.LootItem;

import java.util.Objects;
import java.util.Set;

@Getter
public class Storage implements Clickable {
    private final String id = Utils.getRKey((byte) 16);
    private final Location location;

    private boolean isClaimed = false;
    private Hologram hologram;
    private LunaTask task;
    public Storage(Location location) {
        this.location = location;
        this.createHolo("isOpened");

        StorageData.add(location);
        StorageManager.getStorages().add(this);
    }

    public Storage(String strLoc) {
        this.location = StorageData.parseLocation(strLoc);
        this.createHolo("isOpened");

        StorageManager.getStorages().add(this);
    }

    @Override
    public void onClick(Player player) {
        if (this.isClaimed) Config.sendMessage(player, "storageIsClaimed");
        else if (MenuManager.getActiveInventories().values().stream()
                .anyMatch(m -> m instanceof StorageMenu menu && menu.getStorage().equals(this))) {
            Config.sendMessage(player, "storageIsOpened");
        } else MenuManager.openInventory(player, new StorageMenu(player, this));
    }

    public void drop(Set<LootItem> lootItems) {
        if (this.isClaimed) return;
        this.isClaimed = true;

        this.task = new StorageTask(this);
        this.task.runTaskAsynchronously(SatePlanet.getINSTANCE());

        if (lootItems == null || lootItems.isEmpty()) return;
        new LootGetterTask(this.location, lootItems).runTaskAsynchronously(SatePlanet.getINSTANCE());
    }

    public void refresh() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }

        this.isClaimed = false;
        this.updateHolo("isOpened");
    }

    public void updateHolo(String linesId, int rpl) {
        if (this.hologram == null) {
            this.createHolo(linesId);
            return;
        }
        ConfigurationSection section = Config.getSection("storages.holograms." + linesId);

        int neededY = this.location.getBlockY() + section.getInt("height");
        if (this.hologram.getLocation().getY() != neededY) this.hologram.getLocation().setY(neededY);

        HologramPage page = this.hologram.getPage(0);
        if (page == null) page = this.hologram.addPage();

        String timer = String.format("%02d:%02d", rpl / 60, rpl % 60);

        byte lineIndex = 0;
        for (String line : section.getStringList("lines")) {
            String reLined = ColorManager.color(line
                    .replace("Material.", "")
                    .replace("{0}", timer));
            if (page.getLine(lineIndex) != null) {
                if (line.startsWith("Material.")) DHAPI.setHologramLine(page, lineIndex, reLined.startsWith("THIS") ?
                        this.location.getBlock().getType() :
                        Objects.requireNonNull(Material.getMaterial(reLined)));
                else DHAPI.setHologramLine(page, lineIndex, reLined);
            } else {
                if (line.startsWith("Material.")) DHAPI.addHologramLine(page, reLined.startsWith("THIS") ?
                        this.location.getBlock().getType() :
                        Objects.requireNonNull(Material.getMaterial(reLined)));
                else DHAPI.addHologramLine(page, reLined);
            }

            lineIndex++;
        }
    }

    private void updateHolo(String linesId) {
        this.updateHolo(linesId, -1);
    }

    public void removeHolo() {
        if (this.hologram != null) DHAPI.removeHologram(this.hologram.getName());
    }

    private void createHolo(String linesId) {
        this.hologram = DHAPI.createHologram("holo-" + this.id, this.location.clone().add(0.5, 0, 0.5));
        this.updateHolo(linesId);
    }

    private static class LootGetterTask extends LunaTask {
        private final Location location;
        private final Set<LootItem> lootItems;
        public LootGetterTask(Location location, Set<LootItem> lootItems) {
            super(0);
            this.location = location.clone().add(0.5, 1, 0.5);
            this.lootItems = lootItems;
        }

        @Override @SneakyThrows
        public void start() {
            for (LootItem lootItem : this.lootItems) {
                Thread.sleep(1000);

                Bukkit.getScheduler().runTask(SatePlanet.getINSTANCE(), () -> {
                    lootItem.drop(this.location);
                    this.location.getNearbyPlayers(12)
                            .forEach(p -> p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1));
                });
            }
        }
    }

    private static class StorageTask extends LunaTask {
        private final Storage storage;
        @Getter private int taskSecs = 0;
        public StorageTask(Storage storage) {
            super(Config.getInt("storages.refreshTime"));
            this.storage = storage;
        }

        @Override @SneakyThrows
        public void start() {
            boolean holoUpdatable = Config.getBool("storages.holograms.inTimer.updatable");
            int timer = (int) this.getTicks();

            int t = 0;
            while (t < timer) {
                t++;

                this.taskSecs = t;
                if (t == timer) this.storage.refresh();
                else if (holoUpdatable) this.storage.updateHolo("inTimer", timer - this.taskSecs);

                Thread.sleep(1000);
            }
        }
    }
}
