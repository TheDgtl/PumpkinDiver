package com.afforess.pumpkindiver;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PumpkinDiverPlayerListener implements Listener {
	private static ArrayList<String> players = new ArrayList<String>(50);

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent event) {
		if (!players.contains(event.getPlayer().getName())) {
			players.add(event.getPlayer().getName());
			doPumpkinDiver(event.getPlayer().getName());
		}
    }
	
	public static void doPumpkinDiver(final String name) {
		Player player = PumpkinDiver.server.getPlayer(name);
		//Check to see if this player is online
		if (player == null) {
			players.remove(name);
			return;
		}
		
		//check to see if this plugin has been disabled
		if (!PumpkinDiver.instance.isEnabled()) {
			return;
		}
		//Check to see if we have changed our helmet
		final int currentAir = player.getRemainingAir();
		final boolean wearingDivingHelmet = (player.getInventory().getHelmet() != null && player.getInventory().getHelmet().getTypeId() == Material.PUMPKIN.getId());
		if (wearingDivingHelmet) {
			PumpkinDiver.server.getPlayer(name).setMaximumAir(3000);
		}
		else {
			PumpkinDiver.server.getPlayer(name).setMaximumAir(300);
		}
		
		Thread update = new Thread() {
			public void run() {
				try {
					Player player = PumpkinDiver.server.getPlayer(name);
					if (currentAir != player.getRemainingAir()) {
						onRemainingAirChange(player, currentAir);
					}
					if (wearingDivingHelmet && player.getInventory().getHelmet().getTypeId() != Material.PUMPKIN.getId()) {
						player.setMaximumAir(300);
						player.setRemainingAir(player.getRemainingAir() > 300 ? 300 : player.getRemainingAir());
					}
					else if (!wearingDivingHelmet && player.getInventory().getHelmet().getTypeId() == Material.PUMPKIN.getId()) {
						player.setMaximumAir(3000);
					}
					doPumpkinDiver(name);
				}
				catch (NullPointerException e) {
					players.remove(name);
				}
			}
		};
		PumpkinDiver.server.getScheduler().scheduleSyncDelayedTask(PumpkinDiver.instance, update, 7);
	}

	public static void onRemainingAirChange(Player player, int old) {
		if (player.getInventory().getHelmet().getTypeId() == Material.PUMPKIN.getId()) {

			//round up
			int oldPercent = (old + 299) / 300;
			int remaining = (player.getRemainingAir() + 299) / 300;
			//Ignore chat messages for when a player just puts on a helmet or just takes one off
			boolean ignore = old == 300 && player.getRemainingAir() == 3000 || old == 3000 && player.getRemainingAir() == 300;
			if (oldPercent != remaining && !ignore) {
				
				String message = "[";
				for (int i = 0; i < 10; i++) {
					if (i < remaining){
						message += ChatColor.BLUE.toString() + "|";
					}
					else {
						message += ChatColor.RED.toString() + "|";
					}
				}
				message += ChatColor.WHITE.toString() + "]";
				message += String.format(" %d%c Air Remaining.", remaining * 10, '%');
				player.sendMessage(message);
			}
		}
	}
}
