package com.ivan.creativezone.commands;

import com.ivan.creativezone.CreativeZoneManager;
import com.ivan.creativezone.zone.Zone;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

// set gamemode to everyone in zone

public class RemoveZoneCommand implements CommandExecutor {
    private CreativeZoneManager manager;

    public RemoveZoneCommand(CreativeZoneManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage("You do not have permission to use this command!");
            return true;
        }
        else if (args.length != 1) {
            return false;
        }

        String zoneName = args[0];
        for (Zone z : manager.getZones()) {
            if (z.getName().equals(zoneName)) {
                manager.removeZone(z);
                sender.sendMessage(z.getName() + " was removed!");
                return true;
            }
        }

        sender.sendMessage("That zone does not exist!");
        return true;
    }
}
