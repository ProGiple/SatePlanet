package org.satellite.dev.progiple.sateplanet;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.novasparkle.lunaspring.API.util.utilities.LunaMath;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

@UtilityClass
public class Tools {
    @Setter
    private static ConfigurationSection toughnessSection;
    @Getter
    private static final byte timer = 40;

    public int getToughness(Material material) {
        if (material == null || toughnessSection == null) return 0;
        return toughnessSection.getInt(material.name());
    }

    public int getToughness(ItemStack... itemStacks) {
        return Arrays.stream(itemStacks)
                .filter(i -> i != null && !i.getType().isAir())
                .mapToInt(i -> Tools.getToughness(i.getType()))
                .sum();
    }

    public int getToughnessLevel(int number, Set<String> keys) {
        return keys.stream()
                .map(LunaMath::toInt)
                .min(Comparator.comparingInt(a -> Math.abs(a - number)))
                .orElseThrow(() -> new IllegalArgumentException("Множество значений пусто"));
    }

    public PotionEffect getEffect(String line) {
        String[] split = line.split("-");

        PotionEffectType type = PotionEffectType.getByName(split[0]);
        if (type != null) return new PotionEffect(type, timer + 15,
                split.length == 1 ? 1 : LunaMath.toInt(split[1]) - 1, true, false);
        return null;
    }
}
