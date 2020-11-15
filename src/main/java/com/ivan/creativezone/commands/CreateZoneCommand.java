package com.ivan.creativezone.commands;

import com.ivan.creativezone.CreativeZoneManager;
import com.ivan.creativezone.zone.Zone;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class CreateZoneCommand implements CommandExecutor {
    private CreativeZoneManager manager;

    public CreateZoneCommand(CreativeZoneManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }
        else if (!sender.isOp()) {
            sender.sendMessage("You do not have permission to use this command!");
            return true;
        }
        else if (args.length != 5) {
            return false;
        }

        try {
            double x = Double.parseDouble(args[0]);
            double negativeX = Double.parseDouble(args[1]);
            double z = Double.parseDouble(args[2]);
            double negativeZ = Double.parseDouble(args[3]);
            String name = args[4];

            for (Zone zone : manager.getZones()) {
                if (zone.getName().equals(name)) {
                    sender.sendMessage("A zone with that name already exists!");
                    return true;
                }
            }

            Player player = (Player) sender;

            manager.addZone(new Zone(x, negativeX, z, negativeZ, name, player.getWorld(), manager.getPlugin()));
            sender.sendMessage("Zone created!");
            return true;
        }
        catch (NumberFormatException e) {
            sender.sendMessage("One of your coordinates is not a number!");
            return false;
        }

    }
}
