package org.satellite.dev.progiple.sateplanet.listeners;

import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.novasparkle.lunaspring.API.Util.Service.managers.NBTManager;
import org.novasparkle.lunaspring.API.Util.utilities.LunaMath;
import org.satellite.dev.progiple.sateplanet.SatePlanet;
import org.satellite.dev.progiple.sateplanet.configs.Config;

import java.util.*;
import java.util.stream.Collectors;

public class JoinLeaveHandler implements Listener {
    private final Map<UUID, Integer> tasks = new HashMap<>();

    private final static int timer = 40;
    @Setter private static ConfigurationSection toughnessSection;

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        int taskId = Bukkit.getScheduler().runTaskTimer(SatePlanet.getINSTANCE(), () -> {
            String worldName = player.getLocation().getWorld().getName();

            ConfigurationSection section = Config.getSection(String.format("gravitation.%s", worldName));
            if (section != null) {
                PlayerInventory inventory = player.getInventory();

                ItemStack helmet = inventory.getHelmet();
                if ((helmet == null || !NBTManager.hasTag(helmet, "sateplanet_oxy_helmet"))
                        && !player.hasPermission("sateplanet.oxygenBypass")) {
                    section.getStringList("disOxygen").forEach(line -> {
                        PotionEffect effect = this.getEffect(line);
                        if (effect != null) player.addPotionEffect(effect);
                    });
                }

                if (!player.hasPermission("sateplanet.gravitationBypass")) {
                    int toughness = this.getAmount(inventory.getBoots()) + this.getAmount(inventory.getLeggings())
                            + this.getAmount(inventory.getChestplate()) + this.getAmount(helmet);
                    player.addPotionEffects(section.getStringList(String.valueOf(this.getToughnessLevel(
                            toughness, section.getKeys(false))))
                            .stream()
                            .map(this::getEffect)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet()));
                }
            }
        }, 15L, timer).getTaskId();
        this.tasks.put(player.getUniqueId(), taskId);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        if (this.tasks.containsKey(uuid)) {
            int taskId = this.tasks.get(uuid);
            if (Bukkit.getScheduler().isCurrentlyRunning(taskId) || Bukkit.getScheduler().isQueued(taskId))
                Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    private int getAmount(ItemStack item) {
        if (item == null || toughnessSection == null) return 0;
        return toughnessSection.getInt(item.getType().name());
    }

    private int getToughnessLevel(int number, Set<String> keys) {
        return keys.stream()
                .filter(v -> {
                    try {
                        Integer.parseInt(v);
                        return true;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })
                .map(LunaMath::toInt)
                .min(Comparator.comparingInt(a -> Math.abs(a - number)))
                .orElseThrow(() -> new IllegalArgumentException("Множество значений пусто"));
    }

    private PotionEffect getEffect(String line) {
        String[] split = line.split("-");

        PotionEffectType type = PotionEffectType.getByName(split[0]);
        if (type != null) return new PotionEffect(type, timer + 15,
                    split.length == 1 ? 1 : LunaMath.toInt(split[1]) - 1, true, false);
        return null;
    }
}
