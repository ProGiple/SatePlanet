package org.satellite.dev.progiple.sateplanet.tasks;

import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.novasparkle.lunaspring.API.util.utilities.LunaTask;
import org.satellite.dev.progiple.satecustomitems.itemManager.ItemComponent;
import org.satellite.dev.progiple.sateplanet.SatePlanet;
import org.satellite.dev.progiple.sateplanet.Tools;
import org.satellite.dev.progiple.sateplanet.planets.GravitationLevel;
import org.satellite.dev.progiple.sateplanet.planets.PlanetManager;
import org.satellite.dev.progiple.sateplanet.planets.VirtualPlanet;

import java.util.Comparator;
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
        ItemComponent itemComponent = SatePlanet.getINSTANCE().getOxyHelmetComponent();
        while (this.isActive() && TaskManager.check(this)) {
            Thread.sleep(Tools.getTimer() * 50L);

            VirtualPlanet virtualPlanet = PlanetManager.getPlanet(this.player.getWorld());
            if (virtualPlanet == null) continue;

            PlayerInventory inventory = player.getInventory();

            ItemStack helmet = inventory.getHelmet();
            if (!player.hasPermission("sateplanet.bypass.oxygen")) {
                if (helmet == null || helmet.getType().isAir() || !itemComponent.itemIsComponent(helmet)) {
                    this.task(() -> {
                        virtualPlanet.getDisOxygenEffects().forEach(line -> {
                            PotionEffect effect = Tools.getEffect(line);
                            if (effect != null) player.addPotionEffect(effect);
                        });
                    });
                }
            }

            if (player.hasPermission("sateplanet.bypass.gravitation")) return;
            int toughness = Tools.getToughness(
                    inventory.getBoots(),
                    inventory.getLeggings(),
                    inventory.getChestplate(),
                    inventory.getHelmet());

            GravitationLevel gravitationLevel = virtualPlanet.getGravitationLevels().stream()
                    .filter(l -> l.getLevel() <= toughness)
                    .max(Comparator.comparingInt(GravitationLevel::getLevel))
                    .orElse(null);
            if (gravitationLevel == null) return;

            this.task(() -> {
                player.addPotionEffects(gravitationLevel.getEffectList()
                        .stream()
                        .map(Tools::getEffect)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet()));
            });
        }
    }

    private void task(Runnable runnable) {
        if (!SatePlanet.getINSTANCE().isInDisabling())
            Bukkit.getScheduler().runTask(SatePlanet.getINSTANCE(), runnable);
    }
}
