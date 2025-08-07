package org.satellite.dev.progiple.sateplanet.tasks;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.novasparkle.lunaspring.API.util.utilities.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@UtilityClass
public class TaskManager {
    private final List<RadiationTask> tasks = new ArrayList<>();

    public void register(RadiationTask tickableTask) {
        tasks.add(tickableTask);
    }

    public void unregister(RadiationTask tickableTask) {
        tasks.remove(tickableTask);
    }

    public boolean check(RadiationTask tickableTask) {
        return tasks.contains(tickableTask);
    }

    public void stopAll() {
        tasks.forEach(RadiationTask::cancel);
        tasks.clear();
    }

    public Optional<RadiationTask> get(Player player) {
        return Utils.find(tasks, t -> t.getPlayer().equals(player));
    }
}
