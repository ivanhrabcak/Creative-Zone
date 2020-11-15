package com.ivan.creativezone;

import com.ivan.creativezone.zone.Zone;
import com.ivan.creativezone.zone.ZoneInventory;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.List;

public class CreativeZoneManager implements Listener {
    private final List<Zone> zones = new ArrayList<>();
    private final List<ZoneInventory> inventories = new ArrayList<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getTo() == null) {
            return;
        }

        for (Zone zone : zones) {
            if (zone.isInZone(event.getTo()) && !zone.isInZone(event.getFrom())) {
                event.getPlayer().sendMessage("You entered " + zone.getName());
                event.getPlayer().setGameMode(GameMode.CREATIVE);
            }
            else if (zone.isInZone(event.getFrom()) && !zone.isInZone(event.getTo())) {
                event.getPlayer().sendMessage("You left " + zone.getName());
                event.getPlayer().setGameMode(GameMode.SURVIVAL);
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        ZoneInventory zoneInventory = null;
        boolean hasCreativeInventory = false;
        boolean isInZone = false;

        for (Zone z : zones) {
            if (z.isInZone(event.getPlayer().getLocation())) {
                isInZone = true;
                break;
            }
        }

        for (ZoneInventory inventory : inventories) {
            if (inventory.getPlayer() == event.getPlayer()) {
                zoneInventory = inventory;
                hasCreativeInventory = true;
                break;
            }
        }

        if (!isInZone) {
            event.getPlayer().sendMessage("not opening creative inventory");
        }
        else if (hasCreativeInventory) {
            event.getPlayer().sendMessage("opening creative inventory");
            event.setCancelled(true);
            event.getPlayer().openInventory(zoneInventory.getCreativeInventory());
        }
        else {
            event.getPlayer().sendMessage("opening creative inventory");
            ZoneInventory i = new ZoneInventory(event.getPlayer().getInventory());
            inventories.add(i);
            event.setCancelled(true);
            event.getPlayer().openInventory(i.getCreativeInventory());
        }
    }

    public void addZone(Zone zone) {
        zones.add(zone);
    }

    public void removeZone(Zone zone) {
        zones.remove(zone);
    }

    public List<Zone> getZones() {
        return zones;
    }


}
