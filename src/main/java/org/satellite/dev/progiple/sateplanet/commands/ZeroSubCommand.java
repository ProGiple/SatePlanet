package org.satellite.dev.progiple.sateplanet.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.novasparkle.lunaspring.API.commands.Invocation;
import org.novasparkle.lunaspring.API.commands.annotations.Check;
import org.novasparkle.lunaspring.API.commands.annotations.ZeroArgCommand;
import org.novasparkle.lunaspring.API.commands.processor.NoArgCommand;
import org.novasparkle.lunaspring.API.menus.MenuManager;
import org.satellite.dev.progiple.sateplanet.planets.menu.planets.PlanetsMenu;

@ZeroArgCommand("sateplanet")
@Check(permissions = "sateplanet.planet", flags = NoArgCommand.AccessFlag.PLAYER_ONLY)
public class ZeroSubCommand implements Invocation {
    @Override
    public void invoke(CommandSender sender, String[] strings) {
        MenuManager.openInventory(new PlanetsMenu((Player) sender));
    }
}
