package com.github.boltydawg.horseoverhaul.Listeners;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.world.ChunkLoadEvent;

public class NerfListener implements Listener{
	
	
	public static double divisor;
	public static boolean override;
	
	
	@EventHandler
	public void onSpawn(CreatureSpawnEvent event){
		
		if(event.getEntity() instanceof AbstractHorse) {
			
			if(event.getSpawnReason().equals(SpawnReason.NATURAL)) {
					
				nerf((AbstractHorse)event.getEntity());
			}
			
			else if(override) {
				
				event.getEntity().addScoreboardTag("ho.isNerfed");
				
			}
		}
	}
	
	
	@EventHandler
	public void onNewChunk(ChunkLoadEvent event) {
		
		if(event.isNewChunk()) {
			
			for(Entity e : event.getChunk().getEntities()) {
				
				if(e instanceof AbstractHorse) {
					
					nerf((AbstractHorse)e);
				}
			}
		}
		else if(override) {
			
			for(Entity e : event.getChunk().getEntities()) {
				
				if(e instanceof AbstractHorse) {
					
					if(!e.getScoreboardTags().contains("ho.isNerfed"))
						nerf((AbstractHorse)e);
					
				}
			}
		}
	}
	
	public static void nerf(AbstractHorse horse) {
		
		if(override) {
			
			horse.addScoreboardTag("ho.isNerfed");
			
		}
		
		horse.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue( horse.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() / divisor );
		horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue( horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() / divisor );
		
		horse.setJumpStrength( horse.getJumpStrength() / divisor );
		
	}
}
