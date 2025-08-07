package org.satellite.dev.progiple.sateplanet.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.satellite.dev.progiple.sateplanet.SatePlanet;
import org.satellite.dev.progiple.sateplanet.tasks.RadiationTask;
import org.satellite.dev.progiple.sateplanet.tasks.TaskManager;

public class JoinLeaveHandler implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        RadiationTask radiationTask = new RadiationTask(player);
        radiationTask.runTaskAsynchronously(SatePlanet.getINSTANCE());
        TaskManager.register(radiationTask);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        RadiationTask radiationTask = TaskManager.get(e.getPlayer()).orElse(null);
        if (radiationTask == null) return;

        radiationTask.cancel();
        TaskManager.unregister(radiationTask);
    }
}
