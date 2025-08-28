package org.satellite.dev.progiple.sateplanet;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.novasparkle.lunaspring.API.commands.LunaExecutor;
import org.novasparkle.lunaspring.API.util.service.managers.NBTManager;
import org.novasparkle.lunaspring.LunaPlugin;
import org.satellite.dev.progiple.satecustomitems.itemManager.ComponentStorage;
import org.satellite.dev.progiple.sateplanet.configs.StorageData;
import org.satellite.dev.progiple.sateplanet.listeners.*;
import org.satellite.dev.progiple.sateplanet.planets.PlanetManager;
import org.satellite.dev.progiple.sateplanet.storages.Storage;
import org.satellite.dev.progiple.sateplanet.storages.StorageManager;
import org.satellite.dev.progiple.sateplanet.tasks.TaskManager;

@Getter
public final class SatePlanet extends LunaPlugin {
    @Getter private static SatePlanet INSTANCE;
    private OxyHelmetComponent oxyHelmetComponent;
    private boolean inDisabling = false;

    @Override
    public void onEnable() {
        INSTANCE = this;
        super.onEnable();

        saveDefaultConfig();
        this.loadFiles(
                "storages/storage_data.yml",
                "menu/planet_menu.yml",
                "storages/planets.yml",
                "menu/all_planets_menu.yml");

        this.registerListeners(new JoinLeaveHandler(), new BlockActionHandler());
        LunaExecutor.initialize(this, "#.commands");

        StorageData.getList().forEach(s -> {
            Location location = new Storage(s).getLocation();
            if (location != null) NBTManager.setString(location.getBlock(), "planet-storage", "value");
        });

        this.oxyHelmetComponent = new OxyHelmetComponent();
        ComponentStorage.register(this.oxyHelmetComponent);

        try {
            PlanetManager.reload();
        } catch (NullPointerException ignored) {}
    }

    @Override
    public void onDisable() {
        this.inDisabling = true;
        TaskManager.stopAll();
        StorageManager.getStorages()
                .stream()
                .filter(s -> s.getTask() != null)
                .forEach(s -> s.getTask().cancel());
        super.onDisable();
    }
}
