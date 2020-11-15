package com.ivan.creativezone.zone;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.Objects;

public class ZoneInventory {
    private PlayerInventory inventory;
    private PlayerInventory creativeInventory;
    private Player player;

    public ZoneInventory(PlayerInventory inventory) {
        this.inventory = inventory;
        creativeInventory = (PlayerInventory) Bukkit.createInventory(inventory.getHolder(), inventory.getType());
        creativeInventory.clear();
        player = (Player) inventory.getHolder();
    }

    public PlayerInventory getInventory() {
        return inventory;
    }

    public void setInventory(PlayerInventory inventory) {
        this.inventory = inventory;
    }

    public PlayerInventory getCreativeInventory() {
        return creativeInventory;
    }

    public void setCreativeInventory(PlayerInventory creativeInventory) {
        this.creativeInventory = creativeInventory;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
