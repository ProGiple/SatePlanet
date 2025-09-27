package org.satellite.dev.progiple.sateplanet.storages;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.HologramPage;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.novasparkle.lunaspring.API.util.service.managers.ColorManager;
import org.novasparkle.lunaspring.API.util.utilities.LunaTask;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.novasparkle.lunaspring.API.util.utilities.rarities.RarityManager;
import org.novasparkle.lunaspring.API.util.utilities.rarities.StackRandomizer;
import org.satellite.dev.progiple.sateplanet.SatePlanet;
import org.satellite.dev.progiple.sateplanet.configs.Config;
import org.satellite.dev.progiple.sateplanet.configs.StorageData;

import java.util.HashSet;
import java.util.Set;

@Getter
public class Storage {
    private final String id = Utils.getRKey((byte) 16);
    private final Location location;
    private final Material blockType;

    private boolean isClaimed = false;
    private Hologram hologram;
    private LunaTask task;
    public Storage(Location location) {
        this.location = location;
        this.blockType = this.location.getBlock().getType();
        this.createHolo("isOpened");

        StorageData.add(location);
        StorageManager.getStorages().add(this);
    }

    public Storage(String strLoc) {
        this.location = StorageData.parseLocation(strLoc);
        this.blockType = this.location.getBlock().getType();
        this.createHolo("isOpened");

        StorageManager.getStorages().add(this);
    }

    public void drop() {
        if (this.isClaimed) return;
        this.isClaimed = true;

        Set<ItemStack> lootItems = new HashSet<>();
        ConfigurationSection section = StorageData.getSection("items");
        for (int i = 0; i < Config.getInt("storages.uses"); i++) {
            ItemStack stack = RarityManager.calculateItemStack(section,
                    StackRandomizer.ADVANCED_DURABILITY,
                    StackRandomizer.ADVANCED_ENCHANTS,
                    StackRandomizer.ADVANCED_AMOUNT);
            lootItems.add(stack);
        }

        this.task = new StorageTask(this);
        this.task.runTaskAsynchronously(SatePlanet.getINSTANCE());

        if (lootItems.isEmpty()) return;
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

        double neededY = this.location.getBlockY() + section.getDouble("height");
        if (this.hologram.getLocation().getY() != neededY) this.hologram.getLocation().setY(neededY);

        HologramPage page = this.hologram.getPage(0);
        if (page == null) page = this.hologram.addPage();

        String timer = String.format("%02d:%02d", rpl / 60, rpl % 60);

        byte lineIndex = 0;
        for (String line : section.getStringList("lines")) {
            String reLined = ColorManager.color(line
                    .replace("Material.", "")
                    .replace("[timer]", timer));

            Material material = reLined.startsWith("THIS") ?
                    this.blockType :
                    Material.getMaterial(reLined);
            material = material == null ? Material.GLASS : material;

            if (page.getLine(lineIndex) != null) {
                if (line.startsWith("Material.")) DHAPI.setHologramLine(page, lineIndex, material);
                else DHAPI.setHologramLine(page, lineIndex, reLined);
            } else {
                if (line.startsWith("Material.")) DHAPI.addHologramLine(page, material);
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
        private final Set<ItemStack> lootItems;
        public LootGetterTask(Location location, Set<ItemStack> lootItems) {
            super(0);
            this.location = location.clone().add(0.5, 1, 0.5);
            this.lootItems = lootItems;
        }

        @Override @SneakyThrows
        public void start() {
            World world = location.getWorld();
            for (ItemStack lootItem : this.lootItems) {
                Thread.sleep(1000);

                Bukkit.getScheduler().runTask(SatePlanet.getINSTANCE(), () -> {
                    world.dropItem(this.location, lootItem);
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
            do {
                if (!storage.isClaimed) return;
                t++;

                this.taskSecs = t;
                if (holoUpdatable) this.storage.updateHolo("inTimer", timer - this.taskSecs);

                Thread.sleep(1000);
            } while (t < timer);

            this.storage.refresh();
        }
    }
}
