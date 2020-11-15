package com.ivan.creativezone.zone;

import org.bukkit.Location;

import java.util.Objects;

public class Zone {
    private double positiveXCoordinate;
    private double negativeXCoordinate;
    private double positiveZCoordinate;
    private double negativeZCoordinate;
    private String name;

    public Zone(double x, double negativeX, double z, double negativeZ, String name) {
        this.positiveXCoordinate = x;
        this.negativeXCoordinate = negativeX;
        this.positiveZCoordinate = z;
        this.negativeZCoordinate = negativeZ;
        this.name = name;
    }

    public boolean isInZone(Location location) {
        return location.getX() <= positiveXCoordinate &&
                location.getX() >= negativeXCoordinate &&
                location.getZ() <= positiveZCoordinate &&
                location.getZ() >= negativeZCoordinate;
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
}
