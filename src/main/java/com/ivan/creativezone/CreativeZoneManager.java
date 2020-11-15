package com.ivan.creativezone;

import com.ivan.creativezone.zone.Zone;
import com.ivan.creativezone.zone.ZoneInventory;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.advancement.Advancement;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.EnderChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.beans.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CreativeZoneManager implements Listener {
    private List<Zone> zones = new ArrayList<>();
    private List<ZoneInventory> inventories = new ArrayList<>();

    private transient JavaPlugin plugin;

    public CreativeZoneManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private void setPlayerInventory(Player player, Inventory inventory) {
        for (int i = 0; i < inventory.getSize(); i++) {
            player.getInventory().setItem(i, inventory.getItem(i));
        }
    }

    private Inventory copyInventory(Inventory inventory) {
        Inventory copiedInventory = Bukkit.createInventory(inventory.getHolder(), inventory.getType());
        for (int i = 0; i < inventory.getSize(); i++) {
            copiedInventory.setItem(i, inventory.getItem(i));
        }
        return copiedInventory;
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        boolean fromIsInZone = false;
        boolean toIsInZone = false;

        Location fromLocation = event.getBlock().getLocation();
        Location toLocation = event.getToBlock().getLocation();

        for (Zone z : zones) {
            if (z.isInZone(fromLocation)) {
                fromIsInZone = true;
            }
            if (z.isInZone(toLocation)) {
                toIsInZone = true;
            }

            if (fromIsInZone && toIsInZone) {
                break;
            }
        }

        if (fromIsInZone != toIsInZone) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityPortal(EntityPortalEvent event) {
        for (Zone z : zones) {
            if (z.isInZone(event.getFrom())) {
                event.setCancelled(true);
                return;
            }
            if (event.getTo() != null && z.isInZone(event.getTo())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getInventory().getHolder() instanceof EnderChest)) {
            return;
        }

        for (Zone z : zones) {
            if (z.isInZone(event.getPlayer().getLocation())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();
        boolean isInZone = false;

        for (Zone z : zones) {
            if (z.isInZone(player.getLocation())) {
                isInZone = true;
                break;
            }
        }

        if (isInZone) {
            Advancement advancement = event.getAdvancement();
            for (String criteria : advancement.getCriteria()) {
                player.getAdvancementProgress(advancement).revokeCriteria(criteria);
            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT ||
        event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            return;
        }
        else if (event.getTo() == null) {
            return;
        }

        boolean isTargetPositionInZone = false;
        boolean isFromPositionInZone = false;

        for (Zone z : zones) {
            if (z.isInZone(event.getTo())) {
                isTargetPositionInZone = true;
            }
            if (z.isInZone(event.getFrom())) {
                isFromPositionInZone = true;
            }

            if (isTargetPositionInZone && isFromPositionInZone) {
                break;
            }
        }

        if (isTargetPositionInZone != isFromPositionInZone) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPortalCreate(PortalCreateEvent event) {
        List<BlockState> portalLocations = event.getBlocks();

        for (Zone z : zones) {
            for (BlockState state : portalLocations) {
                if (z.isInZone(state.getLocation())) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        boolean playerIsInZone = false;
        boolean interactionBlockIsInZone = false;
        Location playerLocation = event.getPlayer().getLocation();
        Location blockLocation = event.getClickedBlock().getLocation();
        for (Zone z : zones) {
            if (z.isInZone(playerLocation)) {
                playerIsInZone = true;
            }
            if (z.isInZone(blockLocation)) {
                interactionBlockIsInZone = true;
            }
            if (playerIsInZone && interactionBlockIsInZone) {
                break;
            }
        }

        if (playerIsInZone != interactionBlockIsInZone) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        boolean playerIsInZone = false;
        boolean entityIsInZone = false;
        Location playerLocation = event.getPlayer().getLocation();
        Location blockLocation = event.getRightClicked().getLocation();
        for (Zone z : zones) {
            if (z.isInZone(playerLocation)) {
                playerIsInZone = true;
            }
            if (z.isInZone(blockLocation)) {
                entityIsInZone = true;
            }
            if (playerIsInZone && entityIsInZone) {
                break;
            }
        }

        if (playerIsInZone != entityIsInZone) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof  Player)) {
            return;
        }

        boolean playerIsInZone = false;
        boolean entityIsInZone = false;

        Location playerLocation = event.getEntity().getLocation();
        Location itemLocation = event.getItem().getLocation();

        for (Zone z : zones) {
            if (z.isInZone(playerLocation)) {
                playerIsInZone = true;
            }
            if (z.isInZone(itemLocation)) {
                entityIsInZone = true;
            }

            if (playerIsInZone && entityIsInZone) {
                break;
            }
        }

        if (playerIsInZone != entityIsInZone) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        boolean playerIsInZone = false;
        boolean targetBlockIsInZone = false;

        Location playerLocation = event.getPlayer().getLocation();
        Location targetBlockLocation = event.getBlockAgainst().getLocation();

        for (Zone z : zones) {
            if (z.isInZone(playerLocation)) {
                playerIsInZone = true;
            }
            if (z.isInZone(targetBlockLocation)) {
                targetBlockIsInZone = true;
            }

            if (playerIsInZone && targetBlockIsInZone) {
                break;
            }
        }

        if (playerIsInZone != targetBlockIsInZone) {
            event.setCancelled(true);
        }
    }

    public void onPistonEvent(BlockPistonEvent event, List<Block> targetBlockLocations) {
        if (targetBlockLocations.isEmpty()) {
            return;
        }
        boolean isPistonInZone = false;
        boolean isAnyTargetBlockInZone = false;

        Location pistonLocation = event.getBlock().getLocation();

        Vector blockVector = event.getDirection().getDirection();

        for (Zone z : zones) {
            if (z.isInZone(pistonLocation)) {
                isPistonInZone = true;
            }
            for (Block block : targetBlockLocations) {
                if (z.isInZone(block.getLocation().add(blockVector))) {
                    isAnyTargetBlockInZone = true;
                    break;
                }
            }

            if (isPistonInZone && isAnyTargetBlockInZone) {
                break;
            }
        }


        if (isPistonInZone != isAnyTargetBlockInZone) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        onPistonEvent(event, event.getBlocks());
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        onPistonEvent(event, event.getBlocks());
    }

    private void removeAllPotionEffects(Player player) {
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getTo() == null) {
            return;
        }

        for (Zone zone : zones) {
            if (zone.isInZone(event.getTo()) && !zone.isInZone(event.getFrom())) {
                removeAllPotionEffects(event.getPlayer());
                event.getPlayer().setGameMode(GameMode.CREATIVE);
                for (ZoneInventory inventory : inventories) {
                    if (inventory.getPlayer().getName().equals(event.getPlayer().getName())) {
                        inventory.setInventory(copyInventory(event.getPlayer().getInventory()));
                        setPlayerInventory(event.getPlayer(), inventory.getCreativeInventory());
                        return;
                    }
                }
                ZoneInventory inventory = new ZoneInventory(copyInventory(event.getPlayer().getInventory()));
                inventories.add(inventory);
                setPlayerInventory(event.getPlayer(), copyInventory(inventory.getCreativeInventory()));
            }
            else if (zone.isInZone(event.getFrom()) && !zone.isInZone(event.getTo())) {
                removeAllPotionEffects(event.getPlayer());
                event.getPlayer().setGameMode(GameMode.SURVIVAL);
                for (ZoneInventory inventory : inventories) {
                    if (inventory.getPlayer().getName().equals(event.getPlayer().getName())) {
                        inventory.setCreativeInventory(copyInventory(event.getPlayer().getInventory()));
                        setPlayerInventory(event.getPlayer(), inventory.getInventory());
                        return;
                    }
                }
            }
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

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public List<ZoneInventory> getInventories() {
        return inventories;
    }

    public void setZones(List<Zone> zones) {
        this.zones = zones;
    }

    public void setInventories(List<ZoneInventory> inventories) {
        this.inventories = inventories;
    }

    public void setPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
    }
}
