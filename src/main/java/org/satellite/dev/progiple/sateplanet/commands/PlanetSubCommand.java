package org.satellite.dev.progiple.sateplanet.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.novasparkle.lunaspring.API.commands.Invocation;
import org.novasparkle.lunaspring.API.commands.LunaCompleter;
import org.novasparkle.lunaspring.API.commands.ZeroArgCommand;
import org.novasparkle.lunaspring.API.commands.annotations.Check;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;
import org.novasparkle.lunaspring.API.menus.MenuManager;
import org.satellite.dev.progiple.sateplanet.planets.menu.planets.PlanetsMenu;

import java.util.List;

@SubCommand(appliedCommand = "sateplanet", commandIdentifiers = "planet")
@Check(permissions = "sateplanet.planet", flags = ZeroArgCommand.AccessFlag.PLAYER_ONLY)
public class PlanetSubCommand implements Invocation {
    @Override
    public void invoke(CommandSender sender, String[] strings) {
        MenuManager.openInventory(new PlanetsMenu((Player) sender));
    }
}
