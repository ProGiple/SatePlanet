package org.satellite.dev.progiple.sateplanet.configs;

import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.novasparkle.lunaspring.API.configuration.IConfig;
import org.satellite.dev.progiple.sateplanet.SatePlanet;
import org.satellite.dev.progiple.sateplanet.Tools;

@UtilityClass
public class Config {
    private final IConfig config;
    static {
        config = new IConfig(SatePlanet.getINSTANCE());
        Tools.setToughnessSection(Config.getSection("armor_toughness"));
    }

    public void reload() {
        config.reload(SatePlanet.getINSTANCE());
        Tools.setToughnessSection(Config.getSection("armor_toughness"));
    }

    public ConfigurationSection getSection(String path) {
        return config.getSection(path);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public void sendMessage(CommandSender sender, String id, String... rpl) {
        config.sendMessage(sender, id, rpl);
    }

    public boolean getBool(String path) {
        return config.getBoolean(path);
    }
}
