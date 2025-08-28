package org.satellite.dev.progiple.sateplanet.commands;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.novasparkle.lunaspring.API.commands.LunaCompleter;
import org.novasparkle.lunaspring.API.commands.annotations.Check;
import org.novasparkle.lunaspring.API.commands.annotations.Permissions;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;
import org.novasparkle.lunaspring.API.util.service.managers.NBTManager;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.satellite.dev.progiple.sateplanet.Tools;
import org.satellite.dev.progiple.sateplanet.configs.Config;
import org.satellite.dev.progiple.sateplanet.storages.Storage;
import org.satellite.dev.progiple.sateplanet.storages.StorageManager;

import java.util.List;

@SubCommand(appliedCommand = "sateplanet", commandIdentifiers = "storage")
@Permissions("sateplanet.admin")
public class StorageSubCommand implements LunaCompleter {
    // /planet storage set

    @Override
    public List<String> tabComplete(CommandSender sender, List<String> list) {
        return list.size() == 1 ? Utils.tabCompleterFiltering(List.of("edit", "update", "set"), list.get(0)) :
                list.size() == 2 && list.get(0).equals("update") ? List.of("all") : null;
    }

    @Override
    public void invoke(CommandSender sender, String[] strings) {
        if (strings.length < 2) return;

        switch (strings[1]) {
            case "set" -> {
                if (!(sender instanceof Player player)) {
                    Config.sendMessage(sender, "unknownPlayer", "player-%-" + sender.getName());
                    return;
                }

                Block block = player.getTargetBlock(9);
                if (block == null) return;

                new Storage(block.getLocation());
                NBTManager.setString(block, "planet-storage", "value");

                Config.sendMessage(player, "setStorage");
            }
            case "update" -> {
                if (strings.length >= 3) {
                    StorageManager.updateStorages();
                    Config.sendMessage(sender, "updateStorage");
                    return;
                }

                if (!(sender instanceof Player player)) {
                    Config.sendMessage(sender, "unknownPlayer", "player-%-" + sender.getName());
                    return;
                }

                Block block = player.getTargetBlock(8);
                if (block == null) return;

                if (StorageManager.updateStorage(block)) Config.sendMessage(sender, "updateStorage");
            }
            default -> {
                if (!(sender instanceof Player player)) {
                    Config.sendMessage(sender, "unknownPlayer", "player-%-" + sender.getName());
                    return;
                }

                Tools.openEditMenu(player, 0);
            }
        }
    }
}
