package org.satellite.dev.progiple.sateplanet.configs;

import lombok.experimental.UtilityClass;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.novasparkle.lunaspring.API.Configuration.IConfig;
import org.satellite.dev.progiple.sateplanet.SatePlanet;

import java.io.File;

@UtilityClass
public class MenuConfig {
    private final IConfig config;
    static {
        config = new IConfig(new File(SatePlanet.getINSTANCE().getDataFolder(), "storage_menu.yml"));
    }

    public void reload() {
        config.reload();
    }

    public String getTitle() {
        String title = config.getString("title");
        return title == null || title.isEmpty() ? "&0" : title;
    }

    public byte getSize() {
        return (byte) (config.getInt("rows") * 9);
    }

    public @NotNull ConfigurationSection getSection(String path) {
        return config.getSection(path);
    }
}
