package com.gmail.rcarretta.torchburn;

import org.bukkit.entity.Player;

class TorchBurnLightLevelOwner {
	private Player owner;
	private Integer level;
	
	TorchBurnLightLevelOwner(Player newOwner, Integer newLevel) {
		owner = newOwner;
		level = newLevel;
	}
	
	protected Player getPlayer() { return owner; }
	protected Integer getLevel() { return level; }
}