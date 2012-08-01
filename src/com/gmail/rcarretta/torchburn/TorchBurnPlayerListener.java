package com.gmail.rcarretta.torchburn;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.PlayerInventory;


class TorchBurnPlayerListener implements Listener {
	private final TorchBurn plugin;
	
	protected TorchBurnPlayerListener(final TorchBurn plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerMove (PlayerMoveEvent event) {
			Player player = event.getPlayer();

			if ( plugin.isLit(player) ) {
				if (!plugin.getAllowUnderwater())
					if ( player.getRemainingAir() < player.getMaximumAir() )
						plugin.extinguish(player);
			}
			
			if ( plugin.updatePlayerLoc(player) ) {
				plugin.lightArea(player, plugin.getIntensity(), plugin.getFalloff()); // values for a torch
			}
	}
		
	@EventHandler
	public void onItemHeldChange (PlayerItemHeldEvent event) {
		if ( plugin.isLit(event.getPlayer())) {
			plugin.extinguish(event.getPlayer(), event.getPreviousSlot());
		}
	}

	@EventHandler
	public void onPlayerDropItem (PlayerDropItemEvent event) {
		if ( plugin.isLit(event.getPlayer())) {
			if ( event.getItemDrop().getItemStack().getType() == Material.TORCH ) {
				plugin.extinguishNoRemove(event.getPlayer());
				event.getItemDrop().getItemStack().setDurability((short)0);
				if ( event.getItemDrop().getItemStack().getAmount() <= 1 ) {
					event.getItemDrop().remove();
				}
				else {
					event.getItemDrop().getItemStack().setAmount(event.getItemDrop().getItemStack().getAmount()-1);
				}
			}
		}
	}
		
	@EventHandler
	public void onPlayerInteract (PlayerInteractEvent event) {
		// check if a torch to light
		if ( event.getAction() != Action.RIGHT_CLICK_AIR )
			return;
		
		if ( plugin.isLit(event.getPlayer()) ) {
			// player already has lit torch
			return;
		}
		PlayerInventory inv = event.getPlayer().getInventory();
		if ( inv.getItemInHand().getType() == Material.TORCH ) {
			if (plugin.getRequireSneaking()) {
				if (event.getPlayer().isSneaking()) {
					plugin.lightTorch(event.getPlayer());
				}
			}
			else {
				plugin.lightTorch(event.getPlayer());
			}
		}
	}
		
	@EventHandler
	public void onPlayerQuit (PlayerQuitEvent event) {
		if ( plugin.isLit(event.getPlayer()) ) {
			plugin.extinguish(event.getPlayer());
		}
	}
}
