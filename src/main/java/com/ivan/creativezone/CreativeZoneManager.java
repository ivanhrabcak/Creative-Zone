package com.ivan.creativezone;

import com.ivan.creativezone.zone.Zone;
import com.ivan.creativezone.zone.ZoneInventory;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.advancement.Advancement;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.EnderChest;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
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
        }
    }


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!event.getKeepInventory()) {
            for (ZoneInventory inventory : inventories) {
                if (event.getEntity().getName().equals(inventory.getPlayerName())) {
                    inventory.setInventory(copyInventory(event.getEntity().getInventory()));
                }
            }
        }
        if (!event.getKeepLevel()) {
            for (ZoneInventory inventory : inventories) {
                if (event.getEntity().getName().equals(inventory.getPlayerName())) {
                    inventory.setXp(event.getNewLevel());
                }
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
        onPlayerMove(new PlayerMoveEvent(event.getPlayer(), event.getFrom(), event.getTo()));
    }

    @EventHandler
    public void onPortalCreate(PortalCreateEvent event) {
        List<BlockState> portalLocations = event.getBlocks();

        for (Zone z : zones) {
            for (BlockState state : portalLocations) {

                if (z.isInZone(state.getLocation()) && state.getChunk().getWorld().getName().equals("world")) {
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

        if (playerIsInZone && event.getClickedBlock().getType() == Material.ENDER_CHEST
                && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
        }
        else if (playerIsInZone && event.getClickedBlock().getBlockData() instanceof Bed &&
        event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
        }
        else if (playerIsInZone != interactionBlockIsInZone) {
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

        boolean firstBlockIsInZone = false;

        for (Zone z : zones) {
            boolean isFirst = true;
            for (Block block : targetBlockLocations) {
                if (z.isInZone(block.getLocation().add(blockVector))) {
                    isAnyTargetBlockInZone = true;
                }
                if (z.isInZone(pistonLocation)) {
                    isPistonInZone = true;
                }

                if (z.isInZone(block.getLocation().add(blockVector)) && isFirst) {
                    isFirst = false;

                    firstBlockIsInZone = z.isInZone(block.getLocation().add(blockVector));
                }
                else if (z.isInZone(block.getLocation().add(blockVector)) != firstBlockIsInZone) {
                    event.setCancelled(true);
                    return;
                }
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
        if (!event.getTo().getChunk().getWorld().getName().equals("world")) {
            return;
        }
        if (!event.getFrom().getChunk().getWorld().getName().equals("world")) {
            return;
        }

        for (Zone zone : zones) {
            if (zone.isInZone(event.getTo()) && !zone.isInZone(event.getFrom())) {
                removeAllPotionEffects(event.getPlayer());
                event.getPlayer().setGameMode(GameMode.CREATIVE);
                for (ZoneInventory inventory : inventories) {
                    if (inventory.getPlayerName().equals(event.getPlayer().getName())) {
                        inventory.setInventory(copyInventory(event.getPlayer().getInventory()));
                        inventory.setXp(event.getPlayer().getLevel());
                        setPlayerInventory(event.getPlayer(), inventory.getCreativeInventory());
                        return;
                    }
                }
                ZoneInventory inventory = new ZoneInventory(copyInventory(event.getPlayer().getInventory()), event.getPlayer().getLevel());
                inventories.add(inventory);
                setPlayerInventory(event.getPlayer(), copyInventory(inventory.getCreativeInventory()));
            }
            else if (zone.isInZone(event.getFrom()) && !zone.isInZone(event.getTo())) {
                removeAllPotionEffects(event.getPlayer());
                event.getPlayer().setGameMode(GameMode.SURVIVAL);
                for (ZoneInventory inventory : inventories) {
                    if (inventory.getPlayerName().equals(event.getPlayer().getName())) {
                        inventory.setCreativeInventory(copyInventory(event.getPlayer().getInventory()));
                        event.getPlayer().setLevel(inventory.getXp());
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
        zone.stopTask();
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
