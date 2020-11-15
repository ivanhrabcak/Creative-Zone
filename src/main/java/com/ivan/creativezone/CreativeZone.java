package com.ivan.creativezone;

import com.ivan.creativezone.commands.CreateZoneCommand;
import com.ivan.creativezone.commands.RemoveZoneCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class CreativeZone extends JavaPlugin {

    @Override
    public void onEnable() {
        CreativeZoneManager manager = new CreativeZoneManager();
        getServer().getPluginManager().registerEvents(manager, this);
        getServer().getPluginCommand("CreateZone").setExecutor(new CreateZoneCommand(manager));
        getServer().getPluginCommand("RemoveZone").setExecutor(new RemoveZoneCommand(manager));
    }
}
