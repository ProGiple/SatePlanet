package org.satellite.dev.progiple.sateplanet.storages;

import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.novasparkle.lunaspring.API.Menus.Items.Clickable;
import org.novasparkle.lunaspring.API.Menus.MenuManager;
import org.novasparkle.lunaspring.API.Util.utilities.LunaTask;
import org.satellite.dev.progiple.sateplanet.SatePlanet;
import org.satellite.dev.progiple.sateplanet.configs.Config;
import org.satellite.dev.progiple.sateplanet.configs.StorageData;
import org.satellite.dev.progiple.sateplanet.storages.menu.StorageMenu;
import org.satellite.dev.progiple.sateplanet.storages.menu.LootItem;

import java.util.Set;

@Getter
public class Storage implements Clickable {
    private final Location location;

    private boolean isClaimed = false;
    private LunaTask task;
    public Storage(Location location) {
        this.location = location;

        StorageData.add(location);
        StorageManager.getStorages().add(this);
    }

    public Storage(String strLoc) {
        this.location = StorageData.parseLocation(strLoc);
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
        new LootGetterTask(this.location, lootItems).runTask(SatePlanet.getINSTANCE());
    }

    public void refresh() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }

        this.isClaimed = false;
        this.updateHolo();
    }

    public void updateHolo() {

    }

    public void removeHolo() {

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

                lootItem.drop(this.location);
                this.location.getNearbyPlayers(12).forEach(p ->
                        p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1));
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
            int timer = (int) this.getTicks();

            int t = 0;
            while (t <= timer) {
                t++;

                this.taskSecs = t;
                if (t == timer) this.storage.refresh();
                else this.storage.updateHolo();

                Thread.sleep(1000);
            }
        }
    }
}
