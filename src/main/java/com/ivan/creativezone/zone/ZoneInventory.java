package com.ivan.creativezone.zone;

import com.ivan.creativezone.CreativeZoneManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import java.io.Serializable;
import java.util.Objects;

public class ZoneInventory implements Serializable {
    private Inventory inventory;
    private Inventory creativeInventory;
    private String playerName;
    private int xp;

    public ZoneInventory(Inventory inventory, Inventory creativeInventory, String playerName, int xp) {
        this.inventory = inventory;
        this.creativeInventory = creativeInventory;
        this.playerName = playerName;
    }

    public ZoneInventory(Inventory inventory, int xp) {
        this.playerName = ((Player) inventory.getHolder()).getName();
        this.inventory = inventory;
        creativeInventory = Bukkit.createInventory(inventory.getHolder(), inventory.getType());
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Inventory getCreativeInventory() {
        return creativeInventory;
    }

    public void setCreativeInventory(Inventory creativeInventory) {
        this.creativeInventory = creativeInventory;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }
}
