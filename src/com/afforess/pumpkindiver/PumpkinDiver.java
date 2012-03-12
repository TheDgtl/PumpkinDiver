package com.afforess.pumpkindiver;

import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class PumpkinDiver extends JavaPlugin {
	public static Logger log;
	public static Server server;
	public static Plugin instance;
	public static PluginDescriptionFile description;
	
	
	public void onEnable() {
		log = Logger.getLogger("Minecraft");
		server = this.getServer();
		description = this.getDescription();
		instance = this;
        getServer().getPluginManager().registerEvents(new PumpkinDiverPlayerListener(), this);
        PluginDescriptionFile pdfFile = this.getDescription();
        log.info( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
	}
	
	public void onDisable() {
		
	}
}
