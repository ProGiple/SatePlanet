package org.satellite.dev.progiple.sateplanet.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.novasparkle.lunaspring.API.commands.LunaCompleter;
import org.novasparkle.lunaspring.API.commands.annotations.Check;
import org.novasparkle.lunaspring.API.commands.annotations.Permissions;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;
import org.novasparkle.lunaspring.API.menus.items.NonMenuItem;
import org.novasparkle.lunaspring.API.util.service.managers.NBTManager;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.satellite.dev.progiple.satecustomitems.itemManager.ItemComponent;
import org.satellite.dev.progiple.sateplanet.SatePlanet;
import org.satellite.dev.progiple.sateplanet.configs.Config;

import java.util.List;

@SubCommand(appliedCommand = "sateplanet", commandIdentifiers = "give")
@Permissions("sateplanet.admin")
public class GiveSubCommand implements LunaCompleter {
    @Override
    public List<String> tabComplete(CommandSender sender, List<String> list) {
        return list.size() == 1 ? Utils.getPlayerNicks(list.get(0)) : null;
    }

    @Override
    public void invoke(CommandSender sender, String[] strings) {
        Player player = strings.length >= 2 ? Bukkit.getPlayer(strings[1]) :
                (sender instanceof Player iP ? iP : null);
        if (player == null || !player.isOnline()) {
            Config.sendMessage(sender, "unknownPlayer", "player-%-" + strings[1]);
            return;
        }

        ItemComponent itemComponent = SatePlanet.getINSTANCE().getOxyHelmetComponent();
        itemComponent.createItem().give(player);
        Config.sendMessage(sender, "giveOxyHelmet", "player-%-" + player.getName());
    }
}
