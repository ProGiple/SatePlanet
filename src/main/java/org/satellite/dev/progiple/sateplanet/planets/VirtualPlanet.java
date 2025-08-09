package org.satellite.dev.progiple.sateplanet.planets;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.novasparkle.lunaspring.API.util.utilities.LunaMath;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class VirtualPlanet {
    private final int fly_time;
    private final Location teleportLocation;
    private final Set<GravitationLevel> gravitationLevels = new HashSet<>();
    private final List<String> disOxygenEffects;
    private final String commandId;
    private final World world;
    public VirtualPlanet(ConfigurationSection section) {
        this.world = Bukkit.getWorld(section.getName());
        if (this.world == null) throw new NullPointerException("Мир не может быть null!");

        this.commandId = section.getString("command");
        this.fly_time = section.getInt("fly_time");

        ConfigurationSection locationSection = section.getConfigurationSection("teleport_location");
        assert locationSection != null;
        this.teleportLocation = new Location(this.world,
                locationSection.getDouble("x"),
                locationSection.getDouble("y"),
                locationSection.getDouble("z"));

        ConfigurationSection gravitationSection = section.getConfigurationSection("gravitation");
        if (gravitationSection != null) for (String key : gravitationSection.getKeys(false)) {
            this.gravitationLevels.add(new GravitationLevel(LunaMath.toInt(key),
                    gravitationSection.getStringList(key)));
        }

        this.disOxygenEffects = section.getStringList("disOxygen");
    }
}
