package org.satellite.dev.progiple.sateplanet.planets;

import lombok.experimental.UtilityClass;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.satellite.dev.progiple.sateplanet.configs.PlanetConfig;

import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class PlanetManager {
    private final Set<VirtualPlanet> planets = new HashSet<>();

    public void register(VirtualPlanet virtualPlanet) {
        planets.add(virtualPlanet);
    }

    public VirtualPlanet getPlanet(World world) {
        for (VirtualPlanet planet : planets) {
            if (planet.getWorld().equals(world)) return planet;
        }
        return null;
    }

    public VirtualPlanet getPlanet(String id) {
        for (VirtualPlanet planet : planets) {
            if (planet.getCommandId().equalsIgnoreCase(id)) return planet;
        }
        return null;
    }

    public void reload() {
        ConfigurationSection section = PlanetConfig.getSection();

        planets.clear();
        for (String key : section.getKeys(false)) {
            ConfigurationSection planetSection = section.getConfigurationSection(key);

            assert planetSection != null;
            register(new VirtualPlanet(planetSection));
        }
    }
}
