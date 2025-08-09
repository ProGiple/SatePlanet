package org.satellite.dev.progiple.sateplanet.configs;

import lombok.experimental.UtilityClass;
import org.bukkit.configuration.ConfigurationSection;
import org.novasparkle.lunaspring.API.configuration.IConfig;
import org.satellite.dev.progiple.sateplanet.SatePlanet;
import org.satellite.dev.progiple.sateplanet.planets.PlanetManager;

import java.io.File;

@UtilityClass
public class PlanetConfig {
    private final IConfig config;
    static {
        config = new IConfig(new File(SatePlanet.getINSTANCE().getDataFolder(), "storages/planets.yml"));
    }

    public void reload() {
        config.reload();
        PlanetManager.reload();
    }

    public ConfigurationSection getSection() {
        return config.self();
    }

    public String getString(String path) {
        return config.getString(path);
    }
}
