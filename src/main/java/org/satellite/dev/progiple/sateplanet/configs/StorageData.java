package org.satellite.dev.progiple.sateplanet.configs;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.novasparkle.lunaspring.API.configuration.Configuration;
import org.novasparkle.lunaspring.API.util.utilities.LunaMath;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.satellite.dev.progiple.sateplanet.SatePlanet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class StorageData {
    private final Configuration config;
    static {
        config = new Configuration(new File(SatePlanet.getINSTANCE().getDataFolder(), "storages/storage_data.yml"));
    }

    public List<String> getList() {
        return config.getStringList("locations");
    }

    public void add(Location location) {
        String str = parseLocation(location);

        List<String> list = getList();
        if (list.contains(str)) return;

        list.add(str);
        config.setStringList("locations", list);
        config.save();
    }

    public void remove(Location location) {
        List<String> list = getList();
        list.remove(parseLocation(location));
        config.setStringList("locations", list);
        config.save();
    }

    public Location parseLocation(String str) {
        String[] split = str.split(";");
        if (split.length < 4) return null;

        World world = Bukkit.getWorld(split[0]);
        if (world == null) return null;

        return new Location(world, LunaMath.toInt(split[1]), LunaMath.toInt(split[2]), LunaMath.toInt(split[3]));
    }

    public String parseLocation(Location location) {
        return String.format("%s;%s;%s;%s", location.getWorld().getName(),
                location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public @NotNull ConfigurationSection getSection(String path) {
        return config.getSection(path);
    }

    public void reload() {
        List<String> list = new ArrayList<>(getList());
        config.reload();

        config.setStringList("locations", list);
    }

    public void addItem(ItemStack item) {
        config.setItemStack(String.format("items.%s", Utils.getRKey((byte) 32)), item);
        config.save();
    }
}
