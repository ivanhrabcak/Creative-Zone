package com.ivan.creativezone.zone;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Mule;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class Zone implements Serializable {
    private double positiveXCoordinate;
    private double negativeXCoordinate;
    private double positiveZCoordinate;
    private double negativeZCoordinate;
    private String name;

    private transient boolean taskStarted;
    private transient BukkitTask entityRemoverTask;
    private transient World world = null;
    private transient Plugin plugin;

    public Zone(double x, double negativeX, double z, double negativeZ, String name, World world, Plugin plugin) {
        this.positiveXCoordinate = x;
        this.negativeXCoordinate = negativeX;
        this.positiveZCoordinate = z;
        this.negativeZCoordinate = negativeZ;
        this.name = name;
        this.world = world;
        this.plugin = plugin;
    }

    private void attemptToStartTaskIfNotStarted() {
        if (world == null) {
            world = Bukkit.getWorld("world");
        }
        if (plugin == null) {
            plugin = Bukkit.getPluginManager().getPlugin("Creativezone");
        }
        ZoneBorderEntityRemoverTask entityRemover = new ZoneBorderEntityRemoverTask(this, world);
        entityRemoverTask = entityRemover.runTaskTimer(plugin, 0, 1);
        taskStarted = true;
    }

    public boolean isOnBorderBlock(Location location) { // oioioioioioiooi
        if (!taskStarted) {
            attemptToStartTaskIfNotStarted();
        }
        boolean hasCorrectZ = location.getBlockZ() <= positiveZCoordinate
                && location.getBlockZ() >= negativeZCoordinate;

        boolean isOnPositiveXBlock = location.getBlockX() == positiveXCoordinate &&
                hasCorrectZ;
        boolean isOnNegativeXBlock = location.getBlockX() == negativeXCoordinate &&
                hasCorrectZ;

        boolean hasCorrectX = location.getBlockX() <= positiveXCoordinate
                && location.getBlockX() >= negativeXCoordinate;

        boolean isOnPositiveZBlock = location.getBlockZ() == positiveZCoordinate &&
                hasCorrectX;
        boolean isOnNegativeZBlock = location.getBlockZ() == negativeZCoordinate &&
                hasCorrectX;

        return isOnPositiveXBlock || isOnNegativeXBlock || isOnPositiveZBlock || isOnNegativeZBlock;
    }

    public boolean isInZone(Location location) {
        if (!taskStarted) {
            attemptToStartTaskIfNotStarted();
        }

        return location.getX() < positiveXCoordinate &&
                location.getX() > negativeXCoordinate &&
                location.getZ() < positiveZCoordinate &&
                location.getZ() > negativeZCoordinate;
    }

    public double getPositiveXCoordinate() {
        return positiveXCoordinate;
    }

    public void setPositiveXCoordinate(double positiveXCoordinate) {
        this.positiveXCoordinate = positiveXCoordinate;
    }

    public double getNegativeXCoordinate() {
        return negativeXCoordinate;
    }

    public void setNegativeXCoordinate(double negativeXCoordinate) {
        this.negativeXCoordinate = negativeXCoordinate;
    }

    public double getPositiveZCoordinate() {
        return positiveZCoordinate;
    }

    public void setPositiveZCoordinate(double positiveZCoordinate) {
        this.positiveZCoordinate = positiveZCoordinate;
    }

    public double getNegativeZCoordinate() {
        return negativeZCoordinate;
    }

    public void setNegativeZCoordinate(double negativeZCoordinate) {
        this.negativeZCoordinate = negativeZCoordinate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Zone zone = (Zone) o;
        return Objects.equals(name, zone.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public void stopTask() {
        if (taskStarted) {
            entityRemoverTask.cancel();
            taskStarted = false;
        }
    }
}
