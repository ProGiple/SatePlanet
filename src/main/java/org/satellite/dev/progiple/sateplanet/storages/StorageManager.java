package org.satellite.dev.progiple.sateplanet.storages;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.block.Block;
import org.satellite.dev.progiple.sateplanet.configs.StorageData;

import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class StorageManager {
    @Getter private final Set<Storage> storages = new HashSet<>();

    public boolean updateStorage(Block block) {
        Storage storage = getStorage(block);
        if (storage == null) return false;

        storage.refresh();
        return true;
    }

    public void updateStorages() {
        storages.forEach(Storage::refresh);
    }

    public Storage getStorage(Block block) {
        return storages.stream().filter(s -> s.getLocation().equals(block.getLocation())).findFirst().orElse(null);
    }

    public void delete(Storage storage) {
        storage.removeHolo();
        storage.getTask().cancel();

        storages.remove(storage);
        StorageData.remove(storage.getLocation());
    }
}
