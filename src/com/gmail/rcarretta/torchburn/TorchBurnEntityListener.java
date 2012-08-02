package com.gmail.rcarretta.torchburn;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;


class TorchBurnEntityListener implements Listener {
	private final TorchBurn plugin;
	
	protected TorchBurnEntityListener(final TorchBurn plugin) {
		this.plugin = plugin;
	}
	@EventHandler
	public void onEntityDeath (PlayerDeathEvent event) {
		if ( plugin.isLit(event.getEntity())) {
			System.out.println("player death");
				plugin.extinguish(event.getEntity());
		}
	}

	@EventHandler
	public void onEntityDamageByEntity (EntityDamageByEntityEvent event) {
		if ( event.getDamager() instanceof Player) {
			if (plugin.getSetFire()) {
				if ( plugin.isLit((Player)event.getDamager()) ) {
					System.out.println("Player attack");
					plugin.extinguish((Player)event.getDamager());
					event.getEntity().setFireTicks(120);
				}
			}
		}
	}
}