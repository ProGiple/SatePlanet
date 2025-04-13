package org.satellite.dev.progiple.sateplanet.configs;

import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.novasparkle.lunaspring.API.Configuration.IConfig;
import org.novasparkle.lunaspring.API.Events.CooldownPrevent;
import org.satellite.dev.progiple.sateplanet.SatePlanet;
import org.satellite.dev.progiple.sateplanet.listeners.JoinLeaveHandler;

import java.util.List;

@UtilityClass
public class Config {
    private final IConfig config;
    static {
        config = new IConfig(SatePlanet.getINSTANCE());
        JoinLeaveHandler.setToughnessSection(Config.getSection("armor_toughness"));
    }

    public void reload() {
        config.reload(SatePlanet.getINSTANCE());
        JoinLeaveHandler.setToughnessSection(Config.getSection("armor_toughness"));
    }

    public ConfigurationSection getSection(String path) {
        return config.getSection(path);
    }

    public List<String> getList(String path) {
        return config.getStringList(path);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    private final CooldownPrevent<CommandSender> cd = new CooldownPrevent<>(50);
    public void sendMessage(CommandSender sender, String id, String... rpl) {
        if (!cd.isCancelled(null, sender)) config.sendMessage(sender, id, rpl);
    }

    public String getString(String path) {
        return config.getString(path);
    }
}
