package org.satellite.dev.progiple.sateplanet.commands;

import org.bukkit.command.CommandSender;
import org.novasparkle.lunaspring.API.commands.Invocation;
import org.novasparkle.lunaspring.API.commands.annotations.Check;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;
import org.satellite.dev.progiple.sateplanet.configs.Config;
import org.satellite.dev.progiple.sateplanet.configs.PlanetConfig;
import org.satellite.dev.progiple.sateplanet.configs.PlanetMenuConfig;
import org.satellite.dev.progiple.sateplanet.configs.StorageData;

@SubCommand(appliedCommand = "sateplanet", commandIdentifiers = "reload")
@Check(permissions = "sateplanet.admin", flags = {})
public class ReloadSubCommand implements Invocation {
    @Override
    public void invoke(CommandSender sender, String[] strings) {
        Config.reload();
        StorageData.reload();
        PlanetConfig.reload();
        PlanetMenuConfig.reload();
        Config.sendMessage(sender, "reload");
    }
}
