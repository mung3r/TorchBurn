package com.gmail.rcarretta.torchburn;

import org.bukkit.Location;

class TorchBurnSimplePlayerLoc {
	private int x;
	private int y;
	private int z;
	
	protected TorchBurnSimplePlayerLoc(int newX, int newY, int newZ) {
		this.x = newX;
		this.y = newY;
		this.z = newZ;
	}
	
	protected boolean equals (Location loc) {
		return (loc.getBlockX() == x && loc.getBlockY() == y & loc.getBlockZ() == z);
	}
	
	protected void set(Location loc) {
		x = loc.getBlockX();
		y = loc.getBlockY();
		z = loc.getBlockZ();
	}
}