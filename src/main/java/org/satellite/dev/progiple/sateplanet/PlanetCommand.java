package org.satellite.dev.progiple.sateplanet;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.novasparkle.lunaspring.API.menus.MenuManager;
import org.novasparkle.lunaspring.API.menus.items.NonMenuItem;
import org.novasparkle.lunaspring.API.util.service.managers.NBTManager;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.satellite.dev.progiple.sateplanet.configs.*;
import org.satellite.dev.progiple.sateplanet.planets.PMenu;
import org.satellite.dev.progiple.sateplanet.storages.Storage;
import org.satellite.dev.progiple.sateplanet.storages.StorageManager;

import java.util.List;

public class PlanetCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender.hasPermission("sateplanet.admin")) {
            switch (strings[0]) {
                case "reload" -> {
                    Config.reload();
                    StorageData.reload();
                    PlanetConfig.reload();
                    PlanetMenuConfig.reload();
                    Config.sendMessage(commandSender, "reload");
                }
                case "give" -> {
                    Player player = strings.length >= 2 ? Bukkit.getPlayerExact(strings[1]) :
                            (commandSender instanceof Player iP ? iP : null);
                    if (player == null || !player.isOnline()) {
                        Config.sendMessage(commandSender, "unknownPlayer", strings[1]);
                        return true;
                    }

                    ItemStack item = new NonMenuItem(Config.getSection("oxygen_helmet")).getDefaultStack();
                    NBTManager.setBool(item, "sateplanet_oxy_helmet", true);
                    player.getInventory().addItem(item);
                }
                case "planet" -> {
                    if (strings.length < 2) return false;
                    Player player = strings.length >= 3 ? Bukkit.getPlayerExact(strings[2]) : null;

                    if (player == null || !player.isOnline()) {
                        if (player == null && commandSender instanceof Player) player = (Player) commandSender;
                        else {
                            Config.sendMessage(commandSender, "unknownPlayer", commandSender.getName());
                            return true;
                        }
                    }

                    String sectionName = PlanetConfig.getSection(null).getKeys(false).stream()
                            .filter(k -> strings[1].equalsIgnoreCase(PlanetConfig.getString(k + ".command")))
                            .findFirst()
                            .orElse(null);
                    if (sectionName == null) return false;

                    MenuManager.openInventory(new PMenu(player, sectionName));
                }
                case "storage" -> {
                    if (strings.length < 2) return false;

                    if (strings[1].equalsIgnoreCase("set")) {
                        if (!(commandSender instanceof Player player)) {
                            Config.sendMessage(commandSender, "unknownPlayer", commandSender.getName());
                            return true;
                        }

                        Block block = player.getTargetBlock(9);
                        if (block == null) return true;

                        new Storage(block.getLocation());
                        NBTManager.setString(block, "planet-storage", "value");

                        Config.sendMessage(player, "setStorage");
                        return true;
                    }

                    if (strings[1].equalsIgnoreCase("update")) {
                        if (strings.length >= 3 && strings[2].equalsIgnoreCase("all")) {
                            StorageManager.updateStorages();
                            Config.sendMessage(commandSender, "updateStorage");
                            return true;
                        }

                        if (!(commandSender instanceof Player player)) {
                            Config.sendMessage(commandSender, "unknownPlayer", commandSender.getName());
                            return true;
                        }

                        Block block = player.getTargetBlock(8);
                        if (block == null) return true;

                        if (StorageManager.updateStorage(block)) Config.sendMessage(commandSender, "updateStorage");
                        return true;
                    }

                    if (strings[1].equalsIgnoreCase("addItem")) {
                        if (!(commandSender instanceof Player player)) {
                            Config.sendMessage(commandSender, "unknownPlayer", commandSender.getName());
                            return true;
                        }

                        ItemStack item = player.getInventory().getItemInMainHand();
                        if (item.getType() != Material.AIR) {
                            StorageData.addItem(item);
                            Config.sendMessage(commandSender, "addItem");
                        }
                    }
                }
                default -> {
                    return false;
                }
            }
        } else Config.sendMessage(commandSender, "noPermission");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 1) {
            return Utils.tabCompleterFiltering(List.of("reload", "give", "storage", "planet"), strings[0]);
        }
        else if (strings.length == 2) {
            if (strings[0].equalsIgnoreCase("give"))
                return Utils.getPlayerNicks(strings[1]);
            else if (strings[0].equalsIgnoreCase("storage")) {
                return Utils.tabCompleterFiltering(List.of("set", "update", "addItem"), strings[1]);
            }
            else if (strings[0].equalsIgnoreCase("planet")) {
                return Utils.tabCompleterFiltering(PlanetConfig.getSection(null).getKeys(false)
                        .stream()
                        .map(k -> PlanetConfig.getString(k + ".command"))
                        .toList(), strings[1]);
            }
        }
        else if (strings.length == 3) {
            if (strings[0].equalsIgnoreCase("storage")
                    && strings[1].equalsIgnoreCase("update")) return List.of("all");
            else if (strings[0].equalsIgnoreCase("planet")) Utils.getPlayerNicks(strings[2]);
        }
        return List.of();
    }
}
