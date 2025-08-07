package org.satellite.dev.progiple.sateplanet.configs;

import lombok.experimental.UtilityClass;
import org.bukkit.configuration.ConfigurationSection;
import org.novasparkle.lunaspring.API.configuration.IConfig;
import org.satellite.dev.progiple.sateplanet.SatePlanet;

import java.io.File;

@UtilityClass
public class PlanetConfig {
    private final IConfig config;
    static {
        config = new IConfig(new File(SatePlanet.getINSTANCE().getDataFolder(), "planets/planets.yml"));
    }

    public void reload() {
        config.reload();
    }

    public ConfigurationSection getSection(String worldName) {
        if (worldName == null) return config.self();
        return config.getSection(worldName);
    }

    public String getString(String path) {
        return config.getString(path);
    }
}
