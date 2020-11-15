package com.ivan.creativezone.tabcompleters;

import com.ivan.creativezone.CreativeZone;
import com.ivan.creativezone.CreativeZoneManager;
import com.ivan.creativezone.zone.Zone;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RemoveZoneTabCompleter implements TabCompleter {
    private CreativeZoneManager manager;

    public RemoveZoneTabCompleter(CreativeZoneManager manager) {
        this.manager = manager;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("removezone")) {
            if (args.length > 1) {
                return new ArrayList<>();
            }

            List<String> zoneNames = new ArrayList<>();
            for (Zone zone : manager.getZones()) {
                zoneNames.add(zone.getName());
            }

            return zoneNames;
        }
        return null;
    }
}
