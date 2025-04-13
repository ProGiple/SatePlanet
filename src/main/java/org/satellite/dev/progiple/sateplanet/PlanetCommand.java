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
import org.novasparkle.lunaspring.API.Menus.Items.NonMenuItem;
import org.novasparkle.lunaspring.API.Util.Service.managers.NBTManager;
import org.novasparkle.lunaspring.API.Util.utilities.Utils;
import org.satellite.dev.progiple.sateplanet.configs.Config;
import org.satellite.dev.progiple.sateplanet.configs.MenuConfig;
import org.satellite.dev.progiple.sateplanet.configs.StorageData;
import org.satellite.dev.progiple.sateplanet.storages.Storage;
import org.satellite.dev.progiple.sateplanet.storages.StorageManager;

import java.util.List;

public class PlanetCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender.hasPermission("sateplanet.admin")) {
            switch (strings[0]) {
                default -> {
                    return false;
                }
                case "reload" -> {
                    Config.reload();
                    MenuConfig.reload();
                    StorageData.reload();
                    Config.sendMessage(commandSender, "reload");
                }
                case "give" -> {
                    Player player = strings.length >= 2 ? Bukkit.getPlayerExact(strings[1]) :
                            (commandSender instanceof Player iP ? iP : null);
                    if (player == null || !player.isOnline()) {
                        Config.sendMessage(commandSender, "unknownPlayer", strings[1]);
                        return true;
                    }

                    ItemStack item = new NonMenuItem(Config.getSection("oxygen_helmet")).getItemStack();
                    NBTManager.setString(item, "lunaspring-item-id", "test");
                    NBTManager.setBool(item, "sateplanet_oxy_helmet", true);
                    player.getInventory().addItem(item);
                }
                case "storage" -> {
                    if (strings.length < 2) return false;

                    if (strings[1].equalsIgnoreCase("set")) {
                        if (!(commandSender instanceof Player player)) {
                            Config.sendMessage(commandSender, "unknownPlayer", commandSender.getName());
                            return true;
                        }

                        Block block = player.getTargetBlock(8);
                        if (block == null) return true;

                        new Storage(block.getLocation());
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
            }
        } else Config.sendMessage(commandSender, "noPermission");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 1) {
            return List.of("reload", "give", "storage");
        }
        else if (strings.length == 2) {
            if (strings[0].equalsIgnoreCase("give"))
                return Utils.getPlayerNicks(strings[1]);
            else if (strings[0].equalsIgnoreCase("storage")) {
                return List.of("set", "update", "addItem");
            }
        }
        else if (strings.length == 3 && strings[0].equalsIgnoreCase("storage")
        && strings[1].equalsIgnoreCase("update")) return List.of("all");
        return List.of();
    }
}
