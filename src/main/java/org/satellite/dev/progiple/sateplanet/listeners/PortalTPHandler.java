package org.satellite.dev.progiple.sateplanet.listeners;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.satellite.dev.progiple.sateplanet.configs.Config;

import java.util.Objects;

public class PortalTPHandler implements Listener {
    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent e) {
        Location fromLocation = e.getFrom();

        World fromWorld = fromLocation.getWorld();
        if (fromWorld == null) return;

        World.Environment environment = fromWorld.getEnvironment();
        if (environment == World.Environment.CUSTOM || environment == World.Environment.THE_END) return;

        Location location = fromLocation.clone();
        String value;
        if (environment == World.Environment.NORMAL) {
            value = Config.getString(String.format("nether_link.%s", fromWorld.getName()));
            if (value == null || value.isEmpty()) return;

            location = location.set((double) location.getBlockX() / 8, location.getBlockY(), (double) location.getBlockZ() / 8);
        } else {
            ConfigurationSection section = Config.getSection("nether_link");
            if (section == null) return;

            value = section.getKeys(false)
                    .stream()
                    .filter(k -> Objects.equals(section.getString(k), fromWorld.getName()))
                    .findFirst().orElse(null);
            if (value == null) return;

            location = location.set((double) location.getBlockX() * 8, location.getBlockY(), (double) location.getBlockZ() * 8);
        }

        World targetWorld = Bukkit.getWorld(value);
        if (targetWorld == null) return;

        if (!this.isPortalPresent(location)) {
            this.createNetherPortal(location);
        }

        location.setWorld(targetWorld);
        e.setTo(location);
    }

    private boolean isPortalPresent(Location location) {
        for (int x = -1; x <= 1; x++) {
            for (int y = 0; y <= 3; y++) {
                for (int z = -1; z <= 1; z++) {
                    Block block = location.clone().add(x, y, z).getBlock();
                    if (block.getType() == Material.NETHER_PORTAL) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void createNetherPortal(Location location) {
        for (int x = -1; x <= 1; x++) {
            for (int y = 0; y <= 4; y++) {
                for (int z = -1; z <= 1; z++) {
                    Location blockLocation = location.clone().add(x, y, z);
                    Block block = blockLocation.getBlock();
                    if (x == -1 || x == 1 || y == 0 || y == 4) {
                        block.setType(Material.OBSIDIAN);
                    } else block.setType(Material.AIR);
                }
            }
        }
        location.clone().add(0, 1, 0).getBlock().setType(Material.FIRE);
    }
}
