package org.satellite.dev.progiple.sateplanet.commands;

import org.bukkit.command.CommandSender;
import org.novasparkle.lunaspring.API.commands.Invocation;
import org.novasparkle.lunaspring.API.commands.annotations.Check;
import org.novasparkle.lunaspring.API.commands.annotations.Permissions;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;
import org.satellite.dev.progiple.sateplanet.configs.*;

@SubCommand(appliedCommand = "sateplanet", commandIdentifiers = "reload")
@Permissions("sateplanet.admin")
public class ReloadSubCommand implements Invocation {
    @Override
    public void invoke(CommandSender sender, String[] strings) {
        Config.reload();
        StorageData.reload();
        PlanetConfig.reload();
        PlanetMenuConfig.reload();
        AllPlanetsMenuConfig.reload();
        Config.sendMessage(sender, "reload");
    }
}
