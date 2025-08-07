package org.satellite.dev.progiple.sateplanet.tasks;

import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.novasparkle.lunaspring.API.util.service.managers.NBTManager;
import org.novasparkle.lunaspring.API.util.utilities.LunaTask;
import org.satellite.dev.progiple.sateplanet.SatePlanet;
import org.satellite.dev.progiple.sateplanet.Tools;
import org.satellite.dev.progiple.sateplanet.configs.PlanetConfig;

import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class RadiationTask extends LunaTask {
    private final Player player;
    public RadiationTask(Player player) {
        super(0);
        this.player = player;
    }

    @Override @SneakyThrows
    @SuppressWarnings("all")
    public void start() {
        while (this.isActive() && TaskManager.check(this)) {
            Thread.sleep(Tools.getTimer());
            String worldName = this.player.getLocation().getWorld().getName();

            ConfigurationSection section = PlanetConfig.getSection(worldName);
            if (section == null) continue;

            PlayerInventory inventory = player.getInventory();

            ItemStack helmet = inventory.getHelmet();
            if (!player.hasPermission("sateplanet.oxygenBypass")) {
                if (helmet == null || !NBTManager.hasTag(helmet, "sateplanet_oxy_helmet")) {
                    this.task(() -> {
                        section.getStringList("disOxygen").forEach(line -> {
                            PotionEffect effect = Tools.getEffect(line);
                            if (effect != null) player.addPotionEffect(effect);
                        });
                    });
                }
            }

            if (player.hasPermission("sateplanet.gravitationBypass")) return;
            ConfigurationSection gravitationSection = section.getConfigurationSection("gravitation");

            int toughness = Tools.getToughness(
                    inventory.getBoots(),
                    inventory.getLeggings(),
                    inventory.getChestplate(),
                    inventory.getHelmet());
            assert gravitationSection != null;
            this.task(() -> {
                player.addPotionEffects(gravitationSection.getStringList(String.valueOf(Tools.getToughnessLevel(
                        toughness, gravitationSection.getKeys(false))))
                        .stream()
                        .map(Tools::getEffect)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet()));
            });
        }
    }

    private void task(Runnable runnable) {
        Bukkit.getScheduler().runTask(SatePlanet.getINSTANCE(), runnable);
    }
}
