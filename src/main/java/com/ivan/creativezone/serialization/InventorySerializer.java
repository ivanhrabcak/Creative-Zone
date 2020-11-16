package com.ivan.creativezone.serialization;

import com.ivan.creativezone.zone.ZoneInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class InventorySerializer {
    /**
     * A method to serialize an inventory to Base64 string.
     *
     * <p />
     *
     * Special thanks to Comphenix in the Bukkit forums or also known
     * as aadnk on GitHub.
     *
     * <a href="https://gist.github.com/aadnk/8138186">Original Source</a>
     *
     * @param inventory to serialize
     * @return Base64 string of the provided inventory
     * @throws IllegalStateException
     */
    public static String toBase64(ZoneInventory inventory, int xp) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write player name
            dataOutput.writeObject(inventory.getPlayerName());

            // Write the size of the inventory
            dataOutput.writeInt(inventory.getInventory().getSize());

            // Save every element in the list
            for (int i = 0; i < inventory.getInventory().getSize(); i++) {
                dataOutput.writeObject(inventory.getInventory().getItem(i));
            }

            dataOutput.writeInt(inventory.getCreativeInventory().getSize());

            for (int i = 0; i < inventory.getCreativeInventory().getSize(); i++) {
                dataOutput.writeObject(inventory.getCreativeInventory().getItem(i));
            }

            dataOutput.writeInt(xp);
            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    /**
     *
     * A method to get an {@link Inventory} from an encoded, Base64, string.
     *
     * <p />
     *
     * Special thanks to Comphenix in the Bukkit forums or also known
     * as aadnk on GitHub.
     *
     * <a href="https://gist.github.com/aadnk/8138186">Original Source</a>
     *
     * @param data Base64 string of data containing an inventory.
     * @return Inventory created from the Base64 string.
     * @throws IOException
     */
    public static ZoneInventory fromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            String playerName = (String) dataInput.readObject();

            dataInput.readInt();
            Inventory inventory = Bukkit.getServer().createInventory(null, InventoryType.PLAYER);

            // Read the serialized inventory
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, (ItemStack) dataInput.readObject());
            }

            dataInput.readInt();
            Inventory creativeInventory = Bukkit.getServer().createInventory(null, InventoryType.PLAYER);

            // Read the serialized inventory
            for (int i = 0; i < inventory.getSize(); i++) {
                creativeInventory.setItem(i, (ItemStack) dataInput.readObject());
            }

            int xp = dataInput.readInt();

            dataInput.close();

            return new ZoneInventory(inventory, creativeInventory, playerName, xp);
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

    public static void writeZoneInventoryList(List<ZoneInventory> zoneList, File file) throws IOException {
        List<String> serializedZones = new ArrayList<>();

        for (ZoneInventory z : zoneList) {
            serializedZones.add(toBase64(z, z.getXp()));
        }

        ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file));

        stream.writeObject(serializedZones);

        stream.flush();
        stream.close();
    }

    public static List<ZoneInventory> readZoneInventoryList(File file) throws IOException, ClassNotFoundException {
        ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file));

        List<String> serializedZones = (List<String>) stream.readObject();

        stream.close();

        List<ZoneInventory> zoneInventories = new ArrayList<>();

        for (String serializedInventory : serializedZones) {
            zoneInventories.add(fromBase64(serializedInventory));
        }

        return zoneInventories;
    }
}
