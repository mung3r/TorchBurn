package com.gmail.rcarretta.torchburn;

import org.bukkit.Material;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;


class TorchBurnBlockListener extends BlockListener {
	private final TorchBurn plugin;
	protected TorchBurnBlockListener(final TorchBurn plugin) {
		this.plugin = plugin;
	}
	@Override
	public void onBlockPlace (BlockPlaceEvent event) {
		if ( plugin.isLit(event.getPlayer()) && event.getItemInHand().getType() == Material.TORCH )
			event.setCancelled(true);
	}
}