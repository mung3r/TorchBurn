package com.gmail.rcarretta.torchburn;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.entity.Player;
import org.bukkit.craftbukkit.CraftWorld;
import net.minecraft.server.EnumSkyBlock;
import org.bukkit.util.Vector;
import org.bukkit.Location;

public class TorchBurn extends JavaPlugin {
	private int intensity = 15;
	private int falloff = 3;
	private int duration = 120;
	private boolean requireSneaking = true;
	private boolean allowUnderwater = false;
	private boolean setFire = false;
	private boolean fastServer = false;

	private final TorchBurnPlayerListener playerListener = new TorchBurnPlayerListener(this);
	private final TorchBurnEntityListener entityListener = new TorchBurnEntityListener(this);
	private final TorchBurnBlockListener blockListener = new TorchBurnBlockListener(this);
	private final TorchBurnConfig config = new TorchBurnConfig(this);
	
	// used to reduce calls to lightArea(), instead of on every playermove, only when block changes.
	private static HashMap<Player, TorchBurnSimplePlayerLoc> playerLoc = new HashMap<Player, TorchBurnSimplePlayerLoc>();

	private static HashMap<Location, TorchBurnLightLevelOwner> prevState = new HashMap<Location, TorchBurnLightLevelOwner>();
	private static HashMap<Player, List<Location>> playerBlocks = new HashMap<Player, List<Location>>();
	
	@Override
	public void onEnable() {
		config.configRead();
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(playerListener, this);
		pm.registerEvents(entityListener, this);
		pm.registerEvents(blockListener, this);
	}
	
	@Override
	public void onDisable() {
		
	}
	
	protected void extinguish ( Player player, int slot ) {
		// remove torches, restore light, etc.
		if ( player.getInventory().getItem(slot).getAmount() > 1 ) {
			// decrement stack
			player.getInventory().getItem(slot).setDurability((short)0);
			player.getInventory().getItem(slot).setAmount(player.getInventory().getItem(slot).getAmount()-1);
		}
		else {
			// last torch
			player.getInventory().clear(slot);
		}
		removePlayerLoc(player);
		if ( playerBlocks.containsKey(player) ) {
			unLightarea(player);
			playerBlocks.remove(player);
		}
	}
	
	// i am so lazy
	protected void extinguishNoRemove ( Player player ) {
		int slot = player.getInventory().getHeldItemSlot();
		// remove torches, restore light, etc.
		if ( player.getInventory().getItemInHand().getAmount() > 1 ) {
			// decrement stack
			player.getInventory().getItem(slot).setDurability((short)0);
		}
		else {
			// last torch
			player.getInventory().clear(slot);
		}
		removePlayerLoc(player);
		if ( playerBlocks.containsKey(player) ) {
			unLightarea(player);
			playerBlocks.remove(player);
		}
	}
	
	protected void extinguish ( Player player ) {
		int slot = player.getInventory().getHeldItemSlot();
		// remove torches, restore light, etc.
		if ( player.getInventory().getItemInHand().getAmount() > 1 ) {
			// decrement stack
			player.getInventory().getItem(slot).setDurability((short)0);
			player.getInventory().getItem(slot).setAmount(player.getInventory().getItem(slot).getAmount()-1);
		}
		else {
			// last torch
			player.getInventory().clear(slot);
		}
		removePlayerLoc(player);
		if ( playerBlocks.containsKey(player) ) {
			unLightarea(player);
			playerBlocks.remove(player);
		}
	}
	
	protected int getDuration() { return duration; }
	protected int getIntensity() { return intensity; }
	protected int getFalloff() { return falloff; }
	protected boolean getRequireSneaking() { return requireSneaking; }
	protected boolean getAllowUnderwater() { return allowUnderwater; }
	protected boolean getSetFire() { return setFire; }
	protected boolean getFastServer() { return fastServer; } 
	
	protected void setBurnDuration(int newDuration) {
		this.duration = newDuration;
	}
	protected void setLightIntensity(int newIntensity) {
		this.intensity = newIntensity;
	}
	protected void setLightFalloff(int newFalloff) {
		this.falloff = newFalloff;
	}
	protected void setRequireSneaking(boolean newRequireSneaking) {
		this.requireSneaking = newRequireSneaking;
	}
	protected void setAllowUnderwater(boolean newAllowUnderwater) {
		this.allowUnderwater = newAllowUnderwater;
	}
	protected void setSetFire(boolean newSetFire) {
		this.setFire = newSetFire;
	}
	protected void setFastServer(boolean newFastServer) {
		this.fastServer = newFastServer;
	}
	
	protected boolean updatePlayerLoc (Player player) {
		Location loc = player.getLocation();
		TorchBurnSimplePlayerLoc tbLoc = playerLoc.get(player);
		
		if ( tbLoc == null )
			return false;
		
		if ( tbLoc.equals(loc) )
			return false;
		
		tbLoc.set(loc.clone());
		return true;
	}
	
	protected boolean isLit (Player player) {
		return ( playerLoc.containsKey(player) );
	}
	
	private void addPlayerLoc (Player player) {
		Location loc = player.getLocation();
		playerLoc.put(player, new TorchBurnSimplePlayerLoc(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
	}
	
	private void removePlayerLoc (Player player) {
		playerLoc.remove(player);
	}
	
	protected void lightTorch ( Player player ) {
		lightArea(player, intensity, falloff);
		addPlayerLoc(player);
		if (getDuration() > 0)
			getServer().getScheduler().scheduleSyncDelayedTask(this, new TorchBurnSchedule(this, player), 0);
	}
	
	protected void lightArea ( Player player, int intensity, int falloff ) {
		assert ( intensity >= 0 );
		assert ( intensity <= 15 );
		assert ( falloff > 0 );
		assert ( falloff <= 15 );
		CraftWorld world = (CraftWorld)player.getWorld();
		int radius = intensity / falloff;
		int blockX = player.getLocation().getBlockX();
		int blockY = player.getLocation().getBlockY();
		int blockZ = player.getLocation().getBlockZ();
				
		// first reset all light around
		if ( playerBlocks.containsKey(player) ) {
			unLightarea(player);
			playerBlocks.remove(player);
		}
		
		List <Location> blockList = new ArrayList<Location>();
		
		for ( int x = -radius; x <= radius; x++ )
			for ( int y = -radius; y <= radius; y++ )
				for ( int z = -radius; z <= radius; z++ ) {
					int newIntensity;
					int curIntensity = world.getHandle().getLightLevel(blockX+x, blockY+y, blockZ+z);
					
					if ( fastServer == false ) {
						// this is fast
						newIntensity = (intensity-(Math.abs(x)+Math.abs(y)+Math.abs(z))) < 0 ? 0 : intensity-(Math.abs(x)+Math.abs(y)+Math.abs(z));
					}
					else {	
						// this is slow, but nicer
						Vector origin = new Vector(blockX, blockY, blockZ);
						Vector v = new Vector(blockX+x, blockY+y, blockZ+z);
						if ( v.isInSphere(origin, radius) ) {
							// looks like the entry is within the radius
							double distanceSq = v.distanceSquared(origin);
							newIntensity = (int)(((intensity-Math.sqrt(distanceSq)*falloff)*100+0.5)/100);
						}
						else {
							newIntensity = curIntensity;
						}
					}
					
					TorchBurnLightLevelOwner prevIntensity;
					Location l = new Location(world, blockX+x, blockY+y, blockZ+z);
					prevIntensity = TorchBurn.prevState.get(l);
					int worldIntensity = world.getHandle().getLightLevel(blockX+x, blockY+y, blockZ+z);
					if ( prevIntensity != null ) {
						// this area was in the map already. see if we are brightening and if it belongs to us
						if ( prevIntensity.getLevel() < newIntensity && !(prevIntensity.getPlayer().equals(player))) {
							// we are brightening, remove the other guy's entry and add our own 
							TorchBurn.prevState.remove(l);
							TorchBurn.prevState.put(l, new TorchBurnLightLevelOwner(player, worldIntensity));
						}
					}
					else {
						// add the current world's light level to the map
						TorchBurn.prevState.put(l, new TorchBurnLightLevelOwner(player, world.getHandle().getLightLevel(blockX+x, blockY+y, blockZ+z)));
					}
					// light 'em up! 
					if ( newIntensity > worldIntensity ) {
//						if my pull request to bukkit gets accepted
//						l.getBlock().setLightLevel(newIntensity);
						world.getHandle().a(EnumSkyBlock.BLOCK, blockX+x, blockY+y, blockZ+z, newIntensity);
					}
					
					blockList.add(l);
				}	
		playerBlocks.put(player, blockList);
	}

	private void unLightarea ( Player player ) {
		TorchBurnLightLevelOwner lightLevelOwner;
		for ( Location l : playerBlocks.get(player) ) {
			lightLevelOwner = prevState.get(l);
			if ( lightLevelOwner != null ) {
				if ( lightLevelOwner.getPlayer().equals(player)) {
// this is if my pull request to bukkit gets accepted
//					l.getBlock().setLightLevel(lightLevelOwner.getLevel());
					((CraftWorld)(player.getWorld())).getHandle().a(EnumSkyBlock.BLOCK, l.getBlockX(), l.getBlockY(), l.getBlockZ(), lightLevelOwner.getLevel());
					prevState.remove(l);
				}
			}
		}
	}
}
