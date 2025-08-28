package org.satellite.dev.progiple.sateplanet.planets;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.novasparkle.lunaspring.API.util.utilities.LunaMath;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class VirtualPlanet {
    private final int fly_time;
    private final int centerX;
    private final int centerZ;
    private final int radius;
    private final Set<GravitationLevel> gravitationLevels = new HashSet<>();
    private final List<String> disOxygenEffects;
    private final String commandId;
    private final World world;
    public VirtualPlanet(ConfigurationSection section) {
        this.world = Bukkit.getWorld(section.getName());
        if (this.world == null) throw new NullPointerException("Мир не может быть null!");

        this.commandId = section.getString("command");
        this.fly_time = section.getInt("fly_time");

        ConfigurationSection teleportSection = section.getConfigurationSection("teleport_settings");
        assert teleportSection != null;

        String stringCenter = teleportSection.getString("center");
        if (stringCenter == null || stringCenter.isEmpty()) stringCenter = "0;0";

        String[] split = stringCenter.split(";");
        this.centerX = LunaMath.toInt(split[0]);
        this.centerZ = split.length >= 2 ? LunaMath.toInt(split[1]) : this.centerX;
        this.radius = teleportSection.getInt("radius");

        ConfigurationSection gravitationSection = section.getConfigurationSection("gravitation");
        if (gravitationSection != null) for (String key : gravitationSection.getKeys(false)) {
            this.gravitationLevels.add(new GravitationLevel(LunaMath.toInt(key),
                    gravitationSection.getStringList(key)));
        }

        this.disOxygenEffects = section.getStringList("disOxygen");
    }

    public void teleport(Player player) {
        int x = LunaMath.getRandomInt(-this.centerX - this.radius, this.centerX + this.radius);
        int z = LunaMath.getRandomInt(-this.centerZ - this.radius, this.centerZ + this.radius);

        int y = this.world.getHighestBlockYAt(x, z);
        player.teleport(new Location(this.world, x, y + 1, z));
    }
}
