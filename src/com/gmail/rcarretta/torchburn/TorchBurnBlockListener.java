package com.gmail.rcarretta.torchburn;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;


class TorchBurnBlockListener implements Listener {
	private final TorchBurn plugin;
	protected TorchBurnBlockListener(final TorchBurn plugin) {
		this.plugin = plugin;
	}
	@EventHandler
	public void onBlockPlace (BlockPlaceEvent event) {
		if ( plugin.isLit(event.getPlayer()) && event.getItemInHand().getType() == Material.TORCH )
			event.setCancelled(true);
	}
}