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
    private transient Player player;


    public ZoneInventory(Inventory inventory) {
        this.player = player;
        this.inventory = inventory;
        creativeInventory = Bukkit.createInventory(inventory.getHolder(), inventory.getType());
        player = (Player) inventory.getHolder();
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

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
