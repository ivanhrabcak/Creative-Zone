package com.ivan.creativezone.zone;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

public class ZoneBorderEntityRemoverTask extends BukkitRunnable {
    private final Zone zone;
    private final World world;

    public ZoneBorderEntityRemoverTask(Zone zone, World world) {
        this.zone = zone;
        this.world = world;
    }


    @Override
    public void run() {
        Collection<Entity> affectedEntities = world.getEntitiesByClasses(Entity.class);
        for (Entity entity : affectedEntities) {
            if (entity instanceof Player) {
                continue;
            }
            if (zone.isOnBorderBlock(entity.getLocation())) {
                entity.remove();
            }
        }
    }
}
