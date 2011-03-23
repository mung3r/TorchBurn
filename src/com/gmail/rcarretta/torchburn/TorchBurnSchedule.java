package com.gmail.rcarretta.torchburn;

import org.bukkit.Material;
import org.bukkit.entity.Player;


class TorchBurnSchedule implements Runnable {
	Player player;
	private final TorchBurn plugin;
	
	protected TorchBurnSchedule(final TorchBurn plugin, Player newPlayer) { 
		this.plugin = plugin;
		this.player = newPlayer;
	}
	
	@Override
	public void run() {
		if ( plugin.isLit(player) ) {
			if ( player.getInventory().getItemInHand().getType() == Material.TORCH ) {
				player.getInventory().getItemInHand().setDurability((short)(player.getInventory().getItemInHand().getDurability()+1));
				if ( player.getInventory().getItemInHand().getDurability() >= 32 ) {
					plugin.extinguish(player);
					// remove the torch from the player's inventory and return light levels
				}
				else {
					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new TorchBurnSchedule(plugin, player), (int)(plugin.getDuration()*20/32));
				} 	
			}
		}
	}
}