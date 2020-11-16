package com.ivan.creativezone;

import com.ivan.creativezone.commands.CreateZoneCommand;
import com.ivan.creativezone.commands.RemoveZoneCommand;
import com.ivan.creativezone.serialization.InventorySerializer;
import com.ivan.creativezone.tabcompleters.RemoveZoneTabCompleter;
import com.ivan.creativezone.zone.Zone;
import com.ivan.creativezone.zone.ZoneInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.util.List;

public final class CreativeZone extends JavaPlugin {
    private CreativeZoneManager manager;

    private void loadDataIfExists() throws IOException, ClassNotFoundException {
        File zones = new File(getDataFolder().getPath() + "/ZoneData.dat");
        File inventories = new File(getDataFolder().getPath() + "/InventoryData.dat");

        if (zones.exists()) {
            ObjectInputStream zoneInputStream = new ObjectInputStream(new FileInputStream(zones));
            List<Zone> zoneList = (List<Zone>) zoneInputStream.readObject();
            zoneInputStream.close();

            manager.setZones(zoneList);
        }
        if (inventories.exists()) {
            List<ZoneInventory> inventoryList = InventorySerializer.readZoneInventoryList(inventories);
            manager.setInventories(inventoryList);
        }
    }

    @Override
    public void onEnable() {
        manager = new CreativeZoneManager(this);

        try {
            loadDataIfExists();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        getServer().getPluginManager().registerEvents(manager, this);
        getServer().getPluginCommand("CreateZone").setExecutor(new CreateZoneCommand(manager));
        getServer().getPluginCommand("RemoveZone").setExecutor(new RemoveZoneCommand(manager));
        getServer().getPluginCommand("RemoveZone").setTabCompleter(new RemoveZoneTabCompleter(manager));
    }

    @Override
    public void onDisable() {
        File zones = new File(getDataFolder().getPath() + "/ZoneData.dat");
        File inventories = new File(getDataFolder().getPath() + "/InventoryData.dat");

        if (zones.exists()) {
            zones.delete();
        }
        if (inventories.exists()) {
            inventories.delete();
        }

        zones.getParentFile().mkdirs();
        inventories.getParentFile().mkdirs();

        try {
            zones.createNewFile();
            inventories.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ObjectOutputStream zoneStream = new ObjectOutputStream(new FileOutputStream(zones));
            zoneStream.writeObject(manager.getZones());

            zoneStream.flush();
            zoneStream.close();

            InventorySerializer.writeZoneInventoryList(manager.getInventories(), inventories);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
