package org.satellite.dev.progiple.sateplanet;

import lombok.Getter;
import org.bukkit.Location;
import org.novasparkle.lunaspring.API.Util.Service.managers.NBTManager;
import org.novasparkle.lunaspring.LunaPlugin;
import org.satellite.dev.progiple.sateplanet.configs.StorageData;
import org.satellite.dev.progiple.sateplanet.listeners.*;
import org.satellite.dev.progiple.sateplanet.storages.Storage;

public final class SatePlanet extends LunaPlugin {
    @Getter private static SatePlanet INSTANCE;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        INSTANCE = this;
        this.initialize();

        this.loadFiles(true, "storage_menu.yml", "storage_data.yml");
        this.registerListeners(
                new JoinLeaveHandler(),
                new InteractHandler(),
                new BlockPlaceHandler(),
                new PortalTPHandler(),
                new BlockBreakHandler());
        this.registerTabExecutor(new PlanetCommand(), "sateplanet");

        StorageData.getList().forEach(s -> {
            Location location = new Storage(s).getLocation();
            if (location != null) NBTManager.setString(location.getBlock(), "planet-storage", "value");
        });
    }
}
